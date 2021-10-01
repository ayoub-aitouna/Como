package com.come.live.who.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.come.live.R;
import com.come.live.who.Fragments.Call;
import com.come.live.who.Fragments.Dialog.PermotionDialog;
import com.come.live.who.Fragments.Discover;
import com.come.live.who.Fragments.Favorite;
import com.come.live.who.Fragments.Message;
import com.come.live.who.Fragments.Profile;
import com.come.live.who.Global;

import com.come.live.who.Service.Service;


public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int RequestCode = 1;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav_view);
        ShowBadge(bottomNavigationView);
        frameLayout = findViewById(R.id.fram);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!IsPermissionGranted()) {
                RequestPermission();
            }
        }
        if (!isMyServiceRunning()) {
            Global.GetUserID(this);
            Intent serviceIntent = new Intent(this, Service.class);
            startService(serviceIntent);
        }
        if (savedInstanceState == null) {
            Replace(new Call());
            PermotionDialog permotionDialog = new PermotionDialog(this, android.R.style.Theme_Light);
            permotionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            permotionDialog.getWindow().setGravity(Gravity.CENTER);
            permotionDialog.show();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.call:
                    Replace(new Call());
                    break;
                case R.id.discover:
                    Replace(new Discover());

                    break;
                case R.id.favorite:
                    Replace(new Favorite());

                    break;
                case R.id.message:
                    Replace(new Message());

                    break;
                case R.id.profile:
                    Replace(new Profile());
                    break;
            }
            return true;
        });

    }

    private void ShowBadge(BottomNavigationView bottomNavigationView) {
        BadgeDrawable Badge = bottomNavigationView.getOrCreateBadge(R.id.message);
    }

    void Replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fram, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequestPermission() {
        requestPermissions(permissions, RequestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean IsPermissionGranted() {
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }


        return true;
    }

    @Override
    protected void onDestroy() {
        DisConnect();
        super.onDestroy();
    }

    private void DisConnect() {

    }


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Service.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}