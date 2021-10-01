package com.come.live.who.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.come.live.R;
import com.come.live.who.Activities.Payments;
import com.come.live.who.Fragments.Dialog.ChoseGender;
import com.come.live.who.Global;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.VideoCall.AgoraCall.RandomCall;

import retrofit2.Callback;
import retrofit2.Response;


public class Call extends Fragment {
    private String PreferenceGender = "both";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.call_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Actions(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            animate(view);

        Global.GetUserData(Global.GetUserID(requireActivity()), new Callback<Users>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Users> call, @NonNull Response<Users> response) {
                assert response.body() != null;
                try {

                    ((TextView) requireActivity().findViewById(R.id.coins)).setText(String.valueOf(response.body().getCoins()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Users> call, @NonNull Throwable t) {

            }
        });
    }

    private void Actions(View view) {
        view.findViewById(R.id.payments).setOnClickListener(v -> startActivity(new Intent(getActivity(), Payments.class)));
        view.findViewById(R.id.start).setOnClickListener(v -> DepositPayment());
        view.findViewById(R.id.chose).setOnClickListener(v -> showDialog());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void animate(View view) {
        ImageView v = view.findViewById(R.id.background);
        @SuppressLint("UseCompatLoadingForDrawables") AnimatedVectorDrawable animatedVectorDrawable =
                (AnimatedVectorDrawable) requireActivity().getDrawable(R.drawable.avd_anim);
        v.setImageDrawable(animatedVectorDrawable);
        animatedVectorDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                animatedVectorDrawable.start();
            }
        });
        animatedVectorDrawable.start();
    }

    void showDialog() {
        String[] genders = {"Female", "Male", "Both"};
        ChoseGender promotionDialog = new ChoseGender(requireActivity(),
                position -> {
                    ((TextView) requireActivity().findViewById(R.id.gender)).setText(genders[position]);
                    PreferenceGender = genders[position];
                });
        promotionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        promotionDialog.show();
    }

    void DepositPayment() {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<String> CallTransaction = service.MakeTransactionToSystem(
                Global.GetUserID(requireActivity()), PreferenceGender.equals("Female") || PreferenceGender.equals("Male") ? 9 : 0);
        CallTransaction.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<String> call, @NonNull Response<String> response) {
                Intent Start;
                if (response.code() == 200) {
                    Start = new Intent(requireActivity(), RandomCall.class);
                    Start.putExtra("into", PreferenceGender);
                } else {
                    Start = new Intent(requireActivity(), Payments.class);
                }
                requireActivity().startActivity(Start);
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<String> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "sorry something went wrong please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
