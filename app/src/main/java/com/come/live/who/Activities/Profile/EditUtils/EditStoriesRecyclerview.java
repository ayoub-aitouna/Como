package com.come.live.who.Activities.Profile.EditUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.come.live.who.Global;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Activities.StoryMainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditStoriesRecyclerview extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Stories";
    ArrayList<Stories> stories;
    OnAddClicked clicked;
    Context mContext;

    public EditStoriesRecyclerview(ArrayList<Stories> stories, OnAddClicked clicked) {
        this.stories = stories;
        this.clicked = clicked;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storiesImg;
        ProgressBar progress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            storiesImg = itemView.findViewById(R.id.storiesImg);
            progress = itemView.findViewById(R.id.progress);
        }
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
                    "Shoot Video", "Choose From Photos", "Cancel"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Please Select");
            builder.setItems(options, (dialog, which) -> {
                if (options[0].equals(options[which])) {
                    clicked.TakeVideo(progressBar);
                } else if (options[1].equals(options[which])) {
                    clicked.ChoosePicture(progressBar);
                } else if (options[2].equals(options[which])) {
                    builder.show().dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        if (viewType == 0)
            return new AddHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_stories_item_layout, parent, false), clicked);
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_item_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            try {
                Glide.with(mContext).load(stories.get(position).getContent().get(stories.get(position).getContent().size() - 1).getMediaUrl()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ((MyViewHolder)holder).progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ((MyViewHolder)holder).progress.setVisibility(View.GONE);
                        return false;
                    }
                }).into( ((MyViewHolder)holder).storiesImg);
            } catch (Exception e) {
                try {
                    Picasso.get().load(
                            stories.get(position)
                                    .getImg())
                            .into( ((MyViewHolder)holder).storiesImg);
                } catch (Exception ignored) {
                }
            }
            holder.itemView.setOnClickListener(v -> {
                Intent viewStory = new Intent(mContext, StoryMainActivity.class);
                viewStory.putExtra("position", position - 1);
                Log.d(TAG, "onBindViewHolder: size " + Global.stories.get(0).getContent().size() + "position " + position);
                Log.d(TAG, "onBindViewHolder: size " + Global.stories.get(0).getContent().get(0).getMediaUrl() + "position " + position);
                mContext.startActivity(viewStory);
            });
        }


    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


}
