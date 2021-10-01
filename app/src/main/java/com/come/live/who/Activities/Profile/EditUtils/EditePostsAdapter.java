package com.come.live.who.Activities.Profile.EditUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Activities.Profile.ViewPost;
import com.come.live.who.Modules.Posts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditePostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Posts> posts;
    Context mContext;
    OnAddClicked clicked;

    public EditePostsAdapter(ArrayList<Posts> posts, OnAddClicked clicked) {
        this.posts = posts;
        this.clicked = clicked;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == 1)
            return new AddHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_posts_adapter, parent, false), clicked);
        else
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_adapter, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {

        } else {
            try {
                Picasso.get()
                        .load(posts.get(position).getContentImg())
                        .resize(300, 250)
                        .centerCrop()
                        .into(((Holder) holder).img);
            } catch (Exception e) {
            }
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(mContext, ViewPost.class);
                i.putExtra("Url", posts.get(position).getContentImg());
                i.putExtra("isOther", false);
                mContext.startActivity(i);
            });
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public static class AddHolder extends RecyclerView.ViewHolder {
        OnAddClicked clicked;
        ProgressBar progressBar;

        public AddHolder(@NonNull View itemView, OnAddClicked clicked) {
            super(itemView);
            this.clicked = clicked;
            progressBar = itemView.findViewById(R.id.progress);
            itemView.setOnClickListener(v -> ShowDialog(itemView.getContext()));
        }

        private void ShowDialog(Context context) {
            final String[] options = {
                    "Shoot Photo", "Choose From Photos", "Cancel"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Please Select");
            builder.setItems(options, (dialog, which) -> {
                if (options[0].equals(options[which])) {
                    clicked.TakePicture(progressBar);
                } else if (options[1].equals(options[which])) {
                    clicked.ChoosePicture(progressBar);
                } else if (options[2].equals(options[which])) {
                    builder.show().dismiss();
                }
            });
            builder.show();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        Context mContext;
        ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            img = itemView.findViewById(R.id.img);
        }
    }
}
