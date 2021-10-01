package com.come.live.who.Activities.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.come.live.R;
import com.come.live.who.Global;

public class Settings extends AppCompatActivity {
    RadioGroup Genders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Genders = findViewById(R.id.gender);
        ((TextView) findViewById(R.id.id)).setText(String.valueOf(Global.User.getIdUser()));
        switch (Global.User.getGender()) {
            case "Male":
                ((RadioButton) Genders.getChildAt(0)).setChecked(true);
                break;
            case "Famel":
                ((RadioButton) Genders.getChildAt(1)).setChecked(true);
                break;
            default:
                ((RadioButton) Genders.getChildAt(2)).setChecked(true);
                break;
        }
        findViewById(R.id.Legal).setOnClickListener(v -> {
            OpenUrl("https://comoapp.herokuapp.com/");
        });
        findViewById(R.id.policy).setOnClickListener(v -> {
            OpenUrl("https://comoapp.herokuapp.com/");
        });
        findViewById(R.id.terms).setOnClickListener(v -> {
            OpenUrl("https://comoapp.herokuapp.com/");
        });
        findViewById(R.id.guideline).setOnClickListener(v -> {
            OpenUrl("https://comoapp.herokuapp.com/");
        });
        findViewById(R.id.Subscription).setOnClickListener(v -> {
            OpenUrl("https://comoapp.herokuapp.com/");
        });


    }

    private void OpenUrl(String s) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(s));
        startActivity(i);
    }
}