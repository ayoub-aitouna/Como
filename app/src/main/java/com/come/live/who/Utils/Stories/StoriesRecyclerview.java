package com.come.live.who.Utils.Stories;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.come.live.R;

import com.come.live.who.Activities.StoryMainActivity;
import com.come.live.who.Modules.Stories.Stories;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoriesRecyclerview extends RecyclerView.Adapter<StoriesRecyclerview.MyViewHolder> {
    Context context;
    ArrayList<Stories> stories;

    public StoriesRecyclerview(ArrayList<Stories> stories) {
        this.stories = stories;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progress;
        CircleImageView storiesImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            storiesImg = itemView.findViewById(R.id.storiesImg);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.stories_item_layout, parent, false);
        // Return a new holder instance
        return new MyViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Glide.with(context).load(stories.get(position).getContent().get(stories.get(position).getContent().size() - 1).getMediaUrl()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progress.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progress.setVisibility(View.GONE);
                    return false;
                }
            }).into((holder).storiesImg);
        } catch (Exception e) {
            try {
                Picasso.get().load(
                        stories.get(position)
                                .getImg())
                        .into((holder).storiesImg);
            } catch (Exception ignored) {
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent viewStory = new Intent(context, StoryMainActivity.class);
            viewStory.putExtra("position", position);
            context.startActivity(viewStory);
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


}
