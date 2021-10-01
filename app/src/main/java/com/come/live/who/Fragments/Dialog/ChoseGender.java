package com.come.live.who.Fragments.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.come.live.R;
import com.come.live.who.Activities.Payments;
import com.come.live.who.Global;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.VideoCall.AgoraCall.RandomCall;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoseGender extends Dialog {
    private static final String TAG = "ChoseGender";
    public Dialog dialog;
    Activity mContext;
    RadioButton btn1, btn2, btn3;
    TextView coins;
    ArrayList<RadioButton> btns = new ArrayList<>();
    String PreferredGender = "Female";
    ItemCallBack itemCallBack;

    public ChoseGender(@NonNull Activity context, ItemCallBack itemCallBack) {
        super(context);
        this.mContext = context;
        this.itemCallBack = itemCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_gender_dialog);
        LinkViews();
        findViewById(R.id.Continue).setOnClickListener(v -> DepositPayment());
        ControlRadioBtns();
    }

    private void LinkViews() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        coins = findViewById(R.id.coins);
        coins.setText(String.valueOf(Global.User.getCoins()));
        btn3 = findViewById(R.id.btn3);
        btns.add(btn1);
        btns.add(btn2);
        btns.add(btn3);
    }
    void DepositPayment() {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<String> CallTransaction = service.MakeTransactionToSystem(
                Global.GetUserID(mContext), PreferredGender.equals("Female") || PreferredGender.equals("Male") ? 9 : 0);
        CallTransaction.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Intent Start;
                if (response.code() == 200) {
                    Start = new Intent(mContext, RandomCall.class);
                    Start.putExtra("into", PreferredGender);
                } else {
                    Start = new Intent(mContext, Payments.class);
                }
                mContext.startActivity(Start);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(mContext, "sorry something went wrong please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ControlRadioBtns() {
        btn1.setOnClickListener(v -> SelectionChange(btn1));
        btn2.setOnClickListener(v -> SelectionChange(btn2));
        btn3.setOnClickListener(v -> SelectionChange(btn3));
    }
    private void SelectionChange(RadioButton btn) {
        int i = 0;
        for (RadioButton item : btns) {
            Log.d(TAG, "SelectionChange: " + btn.getTag());
            PreferredGender = btn.getTag().toString();
            if (btn != item) {
                item.setChecked(false);
            } else {
                itemCallBack.onItem(i);
            }
            i++;
        }
    }
}
