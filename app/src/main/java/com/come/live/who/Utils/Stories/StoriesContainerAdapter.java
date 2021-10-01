package com.come.live.who.Utils.Stories;

import android.annotation.SuppressLint;
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
import androidx.viewpager.widget.ViewPager;

import com.come.live.R;
import com.come.live.who.Activities.Profile.UserProfile;

import com.come.live.who.Interfaces.StoriesOnComplete;
import com.come.live.who.Modules.Stories.Stories;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;


public class StoriesContainerAdapter extends RecyclerView.Adapter<StoriesContainerAdapter.mHolder> {
    List<Stories> StoriesData;
    Context mContext;
    Boolean SkipByClick = false;
    int position = 0;
    int DataPosition = 0;
    StoriesOnComplete storiesOnComplete;
    StoriesContentAdapter storiesViewAdapter;

    public StoriesContainerAdapter(List<Stories> storiesData, StoriesOnComplete storiesOnComplete) {
        StoriesData = storiesData;
        this.storiesOnComplete = storiesOnComplete;

    }

    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new mHolder(
                LayoutInflater.from(
                        parent.getContext()).inflate(
                        R.layout.view_stories_container_layout, parent, false
                )
        );

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull mHolder holder, int position) {
        holder.setIsRecyclable(false);
//
//        holder.screenPager.setOffscreenPageLimit(1);
//        DataPosition = StoriesData.get(position).getContent().size();
//        holder.storiesProgressView.setStoriesCount(DataPosition); // <- set stories
//        holder.storiesProgressView.setStoriesCountWithDurations(GetDurations(position));
//        storiesViewAdapter = new StoriesContentAdapter(mContext, StoriesData.get(position).getContent(),
//                new onStoriesProgressChanges() {
//                    @Override
//                    public void onStart() {
//                        holder.storiesProgressView.startStories();
//                        holder.storiesProgressView.resume();
//                    }
//
//                    @Override
//                    public void onPause() {
//                        Toast.makeText(mContext, "pause", Toast.LENGTH_SHORT).show();
//                        holder.storiesProgressView.stopNestedScroll();
//                    }
//
//                    @Override
//                    public void next() {
//                        SkipByClick = true;
//                        NEXT(holder);
//                        holder.storiesProgressView.skip();
//                    }
//                });
//
//        holder.screenPager.setAdapter(storiesViewAdapter);
//        holder.storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
//            @Override
//            public void onNext() {
//                if (!SkipByClick) {
//                    NEXT(holder);
//                }
//                SkipByClick = false;
//
//            }
//
//            @Override
//            public void onPrev() {
//                // Call when finished revserse animation.
//                if (!SkipByClick) {
//                    PERV(holder);
//                }
//                SkipByClick = false;
//            }
//
//            @Override
//            public void onComplete() {
//                storiesOnComplete.Complete(position);
//            }
//        }); // <- set listener
//        holder.left.setOnClickListener(v -> {
//            SkipByClick = true;
//            PERV(holder);
//            holder.storiesProgressView.reverse();
//        });
//        holder.Right.setOnClickListener(v -> {
//            SkipByClick = true;
//            NEXT(holder);
//            holder.storiesProgressView.skip();
//        });
//        holder.screenPager.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    // Start
//                    holder.storiesProgressView.pause();
//                    try {
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//
//                case MotionEvent.ACTION_UP:
//                    // End
//                    holder.storiesProgressView.resume();
//                    try {
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//            }
//            return false;
//        });
        try {
            Picasso.get().load(StoriesData.get(position).getImg()).into(holder.img);
        } catch (Exception ignored) {

        }
        holder.name.setText(StoriesData.get(position).getName());
        holder.Profile.setOnClickListener(v -> {
            Intent i = new Intent(mContext, UserProfile.class);
            i.putExtra("userID", StoriesData.get(position).getId());
            mContext.startActivity(i);
        });


    }


    @Override
    public void onViewRecycled(@NonNull mHolder holder) {
        super.onViewRecycled(holder);
//        storiesViewAdapter.releasePlayer();
    }



    private void NEXT(mHolder holder) {
        position = holder.screenPager.getCurrentItem();
        if (position <= StoriesData.get(holder.getAdapterPosition()).getContent().size()) {
            position = position + 1;
            holder.screenPager.setCurrentItem(position);
        } else {
            // TODO : change the stories Array to the next
            storiesOnComplete.Complete(holder.getAdapterPosition());

        }
    }

    private void PERV(mHolder holder) {
        position = holder.screenPager.getCurrentItem();
        if (position > 0) {
            position = position - 1;
            holder.screenPager.setCurrentItem(position);
        }
        if (position == 0) {
            // TODO : change the stories Array to the next
            storiesOnComplete.PERV(holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return StoriesData.size();
    }



    static class mHolder extends RecyclerView.ViewHolder {
        StoriesProgressView storiesProgressView;
        View left, Right;
        LinearLayout linearLayout;
        LinearLayout Profile;
        ViewPager screenPager;
        ImageView img;
        TextView name;

        public mHolder(@NonNull View itemView) {
            super(itemView);

            storiesProgressView = itemView.findViewById(R.id.stories);
            left = itemView.findViewById(R.id.left);
            Right = itemView.findViewById(R.id.right);
            screenPager = itemView.findViewById(R.id.PagerMedia);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            Profile = itemView.findViewById(R.id.Profile);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);

        }
    }

}