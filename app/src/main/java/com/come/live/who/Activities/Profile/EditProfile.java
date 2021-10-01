package com.come.live.who.Activities.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.Modules.Users;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    public DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "EditProfile";
    int Age = Global.User.getAge();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        assignValues();
        Actions();
    }

    private void assignValues() {
        ((EditText) findViewById(R.id.name)).setText(Global.User.getName());
        ((EditText) findViewById(R.id.about)).setText(Global.User.getAbout());
        ((TextView) findViewById(R.id.education)).setText(Global.User.getEducation());
        ((TextView) findViewById(R.id.select)).setText(String.valueOf(Global.User.getAge()));
        ((EditText) findViewById(R.id.job)).setText(Global.User.getJob());
        if (Global.User.getProfileImage() != null)
            Picasso.get().load(Global.User.getProfileImage()).into(((ImageView) findViewById(R.id.img)));
    }

    private void UpdateUser() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        Users user = Global.User;
        user.setAbout(((EditText) findViewById(R.id.about)).getText().toString());
        user.setName(((EditText) findViewById(R.id.name)).getText().toString());
        user.setAge(Age);
        user.setEducation(((EditText) findViewById(R.id.education)).getText().toString());
        user.setJob(((EditText) findViewById(R.id.job)).getText().toString());
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<Users> call = service.UpdateUser(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.code() == 200 && response.body() != null) {
                    Global.User = response.body();
                    assignValues();
                } else {
                    Toast.makeText(EditProfile.this, "Error Please Try again ", Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                Toast.makeText(EditProfile.this, "Error Please Try again ", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void Actions() {
        findViewById(R.id.select).setOnClickListener(v -> DataSelected());
        findViewById(R.id.done).setOnClickListener(v -> UpdateUser());
    }


    private void DataSelected() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month1 + "/" + dayOfMonth + "/" + year1);
            String date = month1 + "/" + dayOfMonth + "/" + year1;
            GetAge(year1, month1, dayOfMonth);
            ((TextView) findViewById(R.id.select)).setText(date);
        };
    }
    private void GetAge(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        int T_year = cal.get(Calendar.YEAR);
        int T_month = cal.get(Calendar.MONTH);
        int T_day = cal.get(Calendar.DAY_OF_MONTH);
        int age = T_year - year;
        if (T_month < month) {
            Age = age - 1;
        } else if ((T_month == month && T_day > day) || T_month > month) {
            Age = age + 1;
        }
        Toast.makeText(this, "" + Age, Toast.LENGTH_SHORT).show();
    }


}
