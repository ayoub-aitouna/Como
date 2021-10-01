package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Activities.Profile.UserProfile;
import com.come.live.who.Modules.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Users_Message_adapter extends RecyclerView.Adapter<Users_Message_adapter.Holder> {
    ArrayList<Users> users;
    public Users_Message_adapter(ArrayList<Users> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_users_item_layout, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(holder.itemView.getContext(), UserProfile.class);
                i.putExtra("userID", users.get(position).getIdUser());
                holder.itemView.getContext().startActivity(i);
            });
            Picasso.get().load(users.get(position).getProfileImage()).into(holder.img);
            holder.name.setText(String.valueOf(users.get(position).getName()));
        } catch (Exception ignored) {

        }
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }
}
