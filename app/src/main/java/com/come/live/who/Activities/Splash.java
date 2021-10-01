package com.come.live.who.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.Modules.GiftModule;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Service.Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Splash extends AppCompatActivity {
    VideoView logoContainer;
    private Service mService;
    private static String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoContainer = findViewById(R.id.videoPlayer);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_video);
        logoContainer.setVideoURI(uri);
        Log.d(TAG, "onCreate: User id is " + Global.GetUserID(this));
        findViewById(R.id.btn).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Authentication.class));
            finish();
        });
        if (Global.GetUserID(this) == -1) {
            Log.d(TAG, "onCreate: User Not Found");
            findViewById(R.id.connectionError).setVisibility(View.GONE);
            findViewById(R.id.dots_progress).setVisibility(View.GONE);
            findViewById(R.id.background).setVisibility(View.VISIBLE);
            findViewById(R.id.logo).setVisibility(View.GONE);
            logoContainer.start();
            logoContainer.setOnCompletionListener(mp -> logoContainer.start());

        } else {
            Log.d(TAG, "onCreate: User  Found fetching Data ");
            Users user = new Users();
            user.setIdUser(Global.GetUserID(this));
            Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
            retrofit2.Call<Users> call = service.GetProfileInfo(user);
            call.enqueue(new Callback<Users>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Users> call, @NonNull Response<Users> response) {
                    Log.d(TAG, "onResponce Code " + response.code());
                    if (response.code() == 200 && response.body() != null) {
                        Log.d(TAG, "onCreate: fetching Data Succeeded");
                        if (response.body() != null) {
                            Global.User = response.body();
                            ReadyToStartApp();
                            GETGIFTLIST();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Users> call, @NonNull Throwable t) {
                    Log.d(TAG, "onCreate: fetching Data Failed");
                    findViewById(R.id.connectionError).setVisibility(View.VISIBLE);
                }
            });

        }


    }

    private void GETGIFTLIST() {
        Global.GiftList(new Callback<ArrayList<GiftModule>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<GiftModule>> call, @NonNull Response<ArrayList<GiftModule>> response) {
                assert response.body() != null;
                Global.GiftList.addAll(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<GiftModule>> call, @NonNull Throwable t) {
                Log.d(TAG, "onCreate: fetching Data Failled " + t.getMessage());

            }
        });

    }

    private void ReadyToStartApp() {
        findViewById(R.id.connectionError).setVisibility(View.GONE);
        findViewById(R.id.dots_progress).setVisibility(View.GONE);
        findViewById(R.id.background).setVisibility(View.VISIBLE);
        findViewById(R.id.logo).setVisibility(View.GONE);
        logoContainer.start();
        logoContainer.setOnCompletionListener(mp -> logoContainer.start());
        findViewById(R.id.btn).setOnClickListener(v -> {
            if (Global.User.getConfirmationStatus() == 0)
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            else
                startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
            finish();
        });
    }


}