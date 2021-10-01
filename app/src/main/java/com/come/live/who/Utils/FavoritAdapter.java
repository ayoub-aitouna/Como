package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.come.live.R;
import com.come.live.who.Activities.Profile.UserProfile;
import com.come.live.who.Global;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritAdapter extends RecyclerView.Adapter<FavoritAdapter.Holder> {
    ArrayList<Users> Users;
    Context mContext;
    private static final String TAG = "FAVORITE_Adapter";

    public FavoritAdapter(ArrayList<Users> users) {
        Users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            holder.imageButton.setOnClickListener(v -> ShowDialog(position));
            holder.name.setText(Users.get(position).getName());
            Picasso.get().load(Users.get(position).getProfileImage()).into(holder.img);
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(mContext, UserProfile.class);
                i.putExtra("userID", Users.get(position).getIdUser());
                mContext.startActivity(i);
            });
        } catch (Exception ignored) {
        }


    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    private void ShowDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setMessage("Remove From Friends List");
        builder.setPositiveButton("Delete",
                (dialog, which) -> {
                    Detele(position);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Detele(int position) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<String> call = service.RemoveUserToFriends(Global.User.getIdUser(), Users.get(position).getIdUser());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<String> call, @NonNull Response<String> response) {
                if (response.code() == 200) {
                    Users.remove(position);
                    notifyDataSetChanged();
                } else {
                    ShowErorr();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ShowErorr();
            }
        });
    }

    private void ShowErorr() {
        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView name;
        ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.delet);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);

        }


    }
}
