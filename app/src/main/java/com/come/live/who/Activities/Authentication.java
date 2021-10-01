package com.come.live.who.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.come.live.R;
import com.come.live.who.Fragments.Auth.Singin;
import com.come.live.who.Global;


public class Authentication extends AppCompatActivity {
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    int RequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!IsPermissionGranted()) {
                RequestPermission();
            }
        }
        SharedPreferences sharedPref = getSharedPreferences("Auth", 0);
        Global.UserId = sharedPref.getInt(getString(R.string.User_ID), -1);
        if (Global.UserId != -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        ReplaceFragment(new Singin());
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

    private void ReplaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("singin");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!IsPermissionGranted()) {
                RequestPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}