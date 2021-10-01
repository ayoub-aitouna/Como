package com.come.live.who.Activities.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.come.live.R;
import com.come.live.who.Activities.Profile.EditUtils.EditStoriesRecyclerview;
import com.come.live.who.Activities.Profile.EditUtils.EditePostsAdapter;
import com.come.live.who.Activities.Profile.EditUtils.OnAddClicked;
import com.come.live.who.Global;
import com.come.live.who.Modules.Posts;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Users;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Edit extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int RESULT_LOAD_IMG = 5421;
    private String filePath = "";
    private ProgressBar ProgressBar;
    private final ArrayList<Posts> posts = new ArrayList<>();
    private final ArrayList<Stories> Stories = new ArrayList<>();
    private EditePostsAdapter PostsAdapter;
    private int Upload_mode;
    private final int STORIES_MODE = 1;
    private final int POSTs_MODE = 2;
    private final int PROFILE_MODE = 3;

    EditStoriesRecyclerview storiesRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
            }
        GetData();
        Actions();
        GETNumberOfFlowers();
        initPosts();
        GetStoriesData();
        GetPosts();
    }

    private void GETNumberOfFlowers() {
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = service.NumberOfFlowers(Global.GetUserID(this));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ((TextView) findViewById(R.id.FollowersNumber)).setText(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                ((TextView) findViewById(R.id.FollowersNumber)).setText(String.valueOf(0));
            }
        });

    }

    private void GetData() {
        Users user = new Users();
        user.setIdUser(Global.GetUserID(this));
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.code() != 403 && response.body() != null) {
                    ((TextView) findViewById(R.id.name)).setText(response.body().getName());
                    ((TextView) findViewById(R.id.country)).setText(response.body().getCountry());
                    ((TextView) findViewById(R.id.about)).setText(response.body().getAbout());
                    if (response.body().getProfileImage() != null && !response.body().getProfileImage().isEmpty())
                        Picasso.get().load(response.body().getProfileImage()).into(((ImageView) findViewById(R.id.img)));
                }

            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {

            }
        });
    }


    private void GetPosts() {
        posts.clear();
        posts.add(new Posts());
        Global.GetPosts("GetPosts", Global.GetUserID(this), new Callback<ArrayList<Posts>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Posts>> call, @NonNull Response<ArrayList<Posts>> response) {
                Log.d("POSTS", "onResponse: " + response.body());
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<Posts> Res = response.body();
                    Collections.reverse(Res);
                    posts.addAll(Res);
                    PostsAdapter.notifyDataSetChanged();
                    findViewById(R.id.noPosts).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.noPosts).setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                findViewById(R.id.noPosts).setVisibility(View.VISIBLE);
            }
        });
    }

    private void Actions() {
        findViewById(R.id.goback).setOnClickListener(v -> finish());
        findViewById(R.id.updateProfileButton).setOnClickListener(v -> ShowDialog());
        findViewById(R.id.EditProfile).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), EditProfile.class)));
    }

    private void initPosts() {
        PostsAdapter = new EditePostsAdapter(posts, new OnAddClicked() {
            @Override
            public void TakePicture(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = POSTs_MODE;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

            @Override
            public void ChoosePicture(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = POSTs_MODE;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }

            @Override
            public void TakeVideo(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = POSTs_MODE;
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
            }
        });


        RecyclerView posts = findViewById(R.id.recycler);
        posts.setLayoutManager(new GridLayoutManager(this, 2));
        posts.setHasFixedSize(true);
        posts.setNestedScrollingEnabled(false);
        posts.setAdapter(PostsAdapter);


        storiesRecyclerview = new EditStoriesRecyclerview(Stories, new OnAddClicked() {
            @Override
            public void TakePicture(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = STORIES_MODE;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

            @Override
            public void ChoosePicture(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = STORIES_MODE;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }

            @Override
            public void TakeVideo(ProgressBar progressBar) {
                ProgressBar = progressBar;
                Upload_mode = STORIES_MODE;
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
            }
        });
        RecyclerView stories = findViewById(R.id.stories);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stories.setLayoutManager(layoutManager);
        stories.setAdapter(storiesRecyclerview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ProgressBar.setVisibility(View.VISIBLE);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            filePath = finalFile.getPath();
            if (Upload_mode == POSTs_MODE)
                uploadImage();
            else if (Upload_mode == STORIES_MODE)
                UploadStorie(false);
            else if (Upload_mode == PROFILE_MODE)
                UpdateProfile(filePath);
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            ProgressBar.setVisibility(View.VISIBLE);
            Uri videoUri = data.getData();
            File finalFile = new File(getRealPathFromURI(videoUri));
            filePath = finalFile.getPath();
            UploadStorie(true);
        }
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                ProgressBar.setVisibility(View.VISIBLE);
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), selectedImage);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                filePath = finalFile.getPath();
                if (Upload_mode == POSTs_MODE)
                    uploadImage();
                else if (Upload_mode == STORIES_MODE)
                    UploadStorie(false);
                else if (Upload_mode == PROFILE_MODE)
                    UpdateProfile(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                ProgressBar.setVisibility(View.GONE);
            }
        }

    }

    private void UpdateProfile(String path) {
        File file = new File(path);
        Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Global.GetUserID(this)));
        com.come.live.who.Retrofit.Retrofit.Data uploadApis = retrofit.create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = uploadApis.UpdateProfileImage(parts, someData);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                GetData();
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("TAG_upload", "Failed: " + t.getMessage());
                ProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private void ShowDialog() {
        Upload_mode = PROFILE_MODE;
        this.ProgressBar = findViewById(R.id.progress);
        final String[] options = {
                "Shoot Photo", "Choose From Photos", "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void UploadStorie(boolean b) {
        File file = new File(filePath);
        Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Global.GetUserID(this)));
        RequestBody mimeType = RequestBody.create(MediaType.parse("text/plain"), b ? "video" : "image");
        com.come.live.who.Retrofit.Retrofit.Data uploadApis = retrofit.create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = uploadApis.uploadStory(parts, someData, mimeType);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                GetStoriesData();
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG_upload", "Failed: " + t.getMessage());
                ProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private void uploadImage() {
        File file = new File(filePath);
        Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Global.GetUserID(this)));
        com.come.live.who.Retrofit.Retrofit.Data uploadApis = retrofit.create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = uploadApis.uploadImage(parts, someData);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                GetPosts();
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("TAG_upload", "Failed: " + t.getMessage());
                ProgressBar.setVisibility(View.GONE);
            }
        });

    }


    //utils
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "BlogImage" + UUID.randomUUID(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }


    private void GetStoriesData() {
        Global.stories.clear();
        Stories.clear();
        Stories.add(new Stories());
        int Id = Global.GetUserID(this);
        Global.GetStories(Global.GetUserID(this), new Callback<ArrayList<Stories>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Stories>> call, @NonNull Response<ArrayList<Stories>> response) {
                if (response.body() != null) {
                    Global.stories.addAll(response.body());
                    if (response.body().size() == 0) {
                        findViewById(R.id.noStories).setVisibility(View.VISIBLE);
                    } else {
                        Collections.reverse(Global.stories);
                        Stories.addAll(Global.stories);
                        storiesRecyclerview.notifyDataSetChanged();
                    }
                } else {
                    findViewById(R.id.noStories).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.d("GetStoriesData", "onFailure: " + t.getMessage());
                findViewById(R.id.noStories).setVisibility(View.VISIBLE);

            }
        });
    }
}

