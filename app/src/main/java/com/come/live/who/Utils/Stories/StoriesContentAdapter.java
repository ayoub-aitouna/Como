package com.come.live.who.Utils.Stories;

import android.content.Context;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.come.live.R;
import com.come.live.who.Interfaces.onStoriesProgressChanges;
import com.come.live.who.Modules.Stories.StoriesContent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class StoriesContentAdapter extends PagerAdapter {
    Context mContext;
    View StoriesView;
    List<StoriesContent> mListScreen;
    VideoView videoView;
    ProgressBar progressBar;
    onStoriesProgressChanges StoriesProgressChanges;
    ImageView StoryContainer;

    public StoriesContentAdapter(Context mContext, List<StoriesContent> mListScreen, onStoriesProgressChanges StoriesProgressChanges) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
        this.StoriesProgressChanges = StoriesProgressChanges;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        StoriesView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_stories_item_layout, container, false);
        initView();
        if (mListScreen.get(position).getMimeType().contains("video")) {
            isImageOrVideo(false);
            loadVideo("http://192.168.1.129:3000/VideoPlayer/video?videoName=VID_20210910_232105.mp4");
        } else if (mListScreen.get(position).getMimeType().contains("image")) {
            isImageOrVideo(true);
                Picasso.get().load(mListScreen.get(position).getMediaUrl()).into(StoryContainer, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(GONE);
                        StoriesProgressChanges.onStart();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        }
        container.addView(StoriesView);
        return StoriesView;
    }

    private void loadVideo(String s) {
        StoriesProgressChanges.onPause();
        Uri videoUri = Uri.parse("http://192.168.1.129:3000/VideoPlayer/video?videoName=VID_20210910_232105.mp4");
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mb -> {
            mb.start();
//            StoriesProgressChanges.onStart();
        });
        videoView.setOnCompletionListener(mp -> StoriesProgressChanges.onStart());
    }

    public void releasePlayer() {
        videoView.stopPlayback();
    }

    private void isImageOrVideo(boolean b) {
        if (b) {
            StoryContainer.setVisibility(VISIBLE);
            videoView.setVisibility(GONE);
        } else {
            StoryContainer.setVisibility(GONE);
            videoView.setVisibility(VISIBLE);
        }
    }


    private void initView() {
        videoView = StoriesView.findViewById(R.id.Video);
        progressBar = StoriesView.findViewById(R.id.progressBar);
        StoryContainer = StoriesView.findViewById(R.id.ContentMedia);

    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        releasePlayer();
        container.removeView((View) object);
    }


}
