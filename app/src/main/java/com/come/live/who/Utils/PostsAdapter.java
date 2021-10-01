package com.come.live.who.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.Posts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.Holder> {
    ArrayList<Posts> Posts;
    Context mContext;
    ItemCallBack itemCallBack;

    public PostsAdapter(ArrayList<Posts> Posts, ItemCallBack itemCallBack) {
        this.Posts = Posts;
        this.itemCallBack = itemCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            Picasso.get()
                    .load(Posts.get(position).getContentImg())
                    .resize(300, 250)
                    .centerCrop()
                    .into(holder.img);
        } catch (Exception e) {
        }
        holder.itemView.setOnClickListener(v -> itemCallBack.onItem(position));

    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout live;
        ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
