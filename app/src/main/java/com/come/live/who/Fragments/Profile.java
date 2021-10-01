package com.come.live.who.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.come.live.R;
import com.come.live.who.Activities.LiveStreaming.OutGoingLiveStream;
import com.come.live.who.Activities.Profile.BlockedUsers;
import com.come.live.who.Activities.Payments;
import com.come.live.who.Activities.Profile.Edit;
import com.come.live.who.Activities.Profile.Settings;
import com.come.live.who.Global;
import com.come.live.who.Modules.Users;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.come.live.who.Global.getImageUri;
import static com.come.live.who.Global.getRealPathFromURI;


public class Profile extends Fragment {
    View view;
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESULT_LOAD_IMG = 5421;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pofile_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        progressBar = view.findViewById(R.id.progress);
        ((TextView) view.findViewById(R.id.coins)).setText(String.valueOf(Global.User.getCoins()));
        Actions();
        GetData();
    }

    private void Actions() {

        view.findViewById(R.id.makePayments).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Payments.class)));

        view.findViewById(R.id.settings).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Settings.class)));

        view.findViewById(R.id.blockedUsers).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), BlockedUsers.class)));

        view.findViewById(R.id.GoLive).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), OutGoingLiveStream.class)));

        view.findViewById(R.id.edit).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Edit.class)));
        view.findViewById(R.id.add).setOnClickListener(v ->
                ShowDialog());


    }


    private void GetData() {
        Users user = new Users();
        user.setIdUser(Global.GetUserID(getActivity()));
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        retrofit2.Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(retrofit2.Call<Users> call, Response<Users> response) {
                if (response.code() != 403 && response.body() != null) {
                    if (response.body().getProfileImage() != null && !response.body().getProfileImage().isEmpty())
                        Picasso.get().load(response.body().getProfileImage()).into(((ImageView) view.findViewById(R.id.img)));
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = Global.getImageUri(getActivity(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri, getActivity()));
            UpdateProfile(finalFile.getPath());
        }

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Uri tempUri = getImageUri(getActivity(), selectedImage);
                File finalFile = new File(getRealPathFromURI(tempUri, getActivity()));
                UpdateProfile(finalFile.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void UpdateProfile(String path) {
        File file = new File(path);
        Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Global.GetUserID(getActivity())));
        com.come.live.who.Retrofit.Retrofit.Data uploadApis = retrofit.create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = uploadApis.UpdateProfileImage(parts, someData);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                GetData();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG_upload", "Failed: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void ShowDialog() {
        final String[] options = {
                "Shoot Photo", "Choose From Photos", "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please Select");
        builder.setItems(options, (dialog, which) -> {
            if (options[0].equals(options[which])) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else if (options[1].equals(options[which])) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

            } else if (options[2].equals(options[which])) {
                builder.show().dismiss();
            }
        });
        builder.show();
    }
}
