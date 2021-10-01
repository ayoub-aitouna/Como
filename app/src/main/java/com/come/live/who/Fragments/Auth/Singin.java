package com.come.live.who.Fragments.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.come.live.R;
import com.come.live.who.Activities.MainActivity;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Singin extends Fragment {
    private static final int RC_SIGN_IN = 5524;
    View view;
    ProgressBar progressBar;
    private boolean IsEmail = false;
    private static final String TAG = "SING IN";
    GoogleSignInClient mGoogleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.singin_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        view.setEnabled(true);
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        SignInButton signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(v -> signIn());
        progressBar = requireActivity().findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        Actions();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void Actions() {
        view.findViewById(R.id.singup).setOnClickListener(v -> Replace());
        view.findViewById(R.id.singin).setOnClickListener(v -> {
            Users user = new Users();
            ((TextView) view.findViewById(R.id.ErrorHolder)).setText("");
            String EmailOrNum = ((EditText) view.findViewById(R.id.email)).getText().toString();
            if (!TextUtils.isEmpty(EmailOrNum) && Patterns.EMAIL_ADDRESS.matcher(EmailOrNum).matches()) {
                IsEmail = true;
                user.setEmail(EmailOrNum);
            } else if (!TextUtils.isEmpty(EmailOrNum) && Patterns.PHONE.matcher(EmailOrNum).matches()) {
                IsEmail = false;
                user.setPhoneNumber(EmailOrNum);
                user.setEmail("");
            } else {
                ShowError(IsEmail ? "Email" : "Phone number");
            }
            String password = ((EditText) view.findViewById(R.id.pass)).getText().toString();
            user.setPassword(password);
            ShowError("Password Shorter then 8");
            SingIn(user);
        });

    }

    private void Replace() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new Singup())
                .addToBackStack("singup").commit();
    }

    void SingIn(Users user) {
        progressBar.setVisibility(View.VISIBLE);
        view.setEnabled(false);
        Retrofit.Auth service = Retrofit.getRetrofitInstance().create(Retrofit.Auth.class);
        Call<Users> call = service.SingIn(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 403 || response.body() == null) {
                    ShowError(IsEmail ? "Email" : "Phone number");
                    progressBar.setVisibility(View.GONE);
                } else {
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("Auth", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.User_ID), response.body().getIdUser());
                    editor.apply();
                    requireActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                    progressBar.setVisibility(View.VISIBLE);
                }
                view.setEnabled(true);

            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                ShowError(IsEmail ? "Email" : "Phone number");
                progressBar.setVisibility(View.VISIBLE);
                view.setEnabled(true);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void ShowError(String message) {
        ((TextView) view.findViewById(R.id.ErrorHolder)).setText(message + "incorrect");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Users user = new Users();
            user.setName(account.getDisplayName());
            String Image = account.getPhotoUrl() != null
                    ? account.getPhotoUrl().toString()
                    : "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";
            user.setProfileImage(Image);
            user.setPassword(account.getIdToken());
            user.setCountry(GetCountryName());
            user.setPhoneNumber("");
            GoogleAuth(user);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void GoogleAuth(Users user) {
        progressBar.setVisibility(View.VISIBLE);
        view.setEnabled(false);
        Retrofit.Auth service = Retrofit.getRetrofitInstance().create(Retrofit.Auth.class);
        Call<String> call = service.GoogleAuth(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 403 || response.body() == null) {
                    ShowError("Something wentWrong");
                } else {
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("Auth", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.User_ID), Integer.parseInt(response.body()));
                    editor.apply();
                    requireActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();

                }
                view.setEnabled(true);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                ShowError("" + t.getMessage()+" ");
                progressBar.setVisibility(View.GONE);
                view.setEnabled(true);
            }
        });
    }


    private String GetCountryName() {
        String country_name = "Unknown";
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getActivity());
        for (String provider : lm.getAllProviders()) {
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        country_name = addresses.get(0).getCountryName();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return country_name;
    }

}
