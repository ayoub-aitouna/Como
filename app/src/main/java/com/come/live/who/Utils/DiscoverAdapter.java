package com.come.live.who.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Activities.LiveStreaming.LiveStreamActivity;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.Holder> {
    ArrayList<Users> Users;
    Context mContext;
    ItemCallBack itemCallBack;

    public DiscoverAdapter(ArrayList<Users> users, ItemCallBack callBack) {
        Users = users;
        this.itemCallBack = callBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (Users.get(position).getIsStreaming() == 1) {
            holder.live.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(mContext, LiveStreamActivity.class);
                i.putExtra("UserId", Users.get(position).getIdUser());
                mContext.startActivity(i);
            });
        } else {
            holder.live.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                itemCallBack.onItem(position);
            });
        }
        holder.name.setText(Users.get(position).getName());
        holder.online.setVisibility(Users.get(position).getIsAvailable() == 1 ? View.VISIBLE : View.GONE);
        try {
            Picasso.get()
                    .load(Users.get(position).getProfileImage())
                    .into(holder.img, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png")
                                    .into(holder.img);
                        }
                    });
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout live, online;
        ImageView img;
        TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            live = itemView.findViewById(R.id.live);
            online = itemView.findViewById(R.id.online);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }
}
