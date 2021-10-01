package com.come.live.who.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.come.live.R;


import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.come.live.who.Activities.Profile.UserProfile;
import com.come.live.who.Global;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Stories.StoriesContent;
import com.come.live.who.Modules.Stories.StoriesData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryMainActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private VideoView videoPlayer;
    private static final String TAG = "StoryMainActivity";
    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private RelativeLayout mVideoViewLayout;
    private int counter = 0;
    ArrayList<StoriesData> mStoriesList = new ArrayList<>();
    ArrayList<View> mediaPlayerArrayList = new ArrayList<>();
    long pressTime = 0L;
    long limit = 500L;
    ImageView thumbnail;


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    try {
                        videoPlayer.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    try {
                        videoPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.stories_activity_main);
        mProgressBar = findViewById(R.id.progressBar);
        mVideoViewLayout = findViewById(R.id.videoView);
        storiesProgressView = findViewById(R.id.stories);
        prepareStoriesList();
        initInfo();
        storiesProgressView.setStoriesCount(mStoriesList.size());
        storiesProgressView.setStoriesListener(this);
        for (int i = 0; i < mStoriesList.size(); i++) {
            if (mStoriesList.get(i).mimeType.contains("video")) {
                mediaPlayerArrayList.add(getVideoView(i));
            } else if (mStoriesList.get(i).mimeType.contains("image")) {
                mediaPlayerArrayList.add(getImageView());
            }
        }
        setStoryView(counter);
        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);
        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);
    }

    private void initInfo() {
        Stories item = Global.stories.get(getIntent().getIntExtra("position", 0));
        ((TextView) findViewById(R.id.name)).setText(item.getName());
        if ((item.getImg() != null || !(item.getImg().isEmpty())))
            Picasso.get().load(item.getImg()).into(((ImageView) findViewById(R.id.img)));
        findViewById(R.id.profile).setOnClickListener(v -> {
            if (Global.GetUserID(this) == item.getId()) finish();
            else
                startActivity(new Intent(this, UserProfile.class)
                        .putExtra("userID", item.getId()));

        });
    }

    private void setStoryView(final int counter) {
        if (counter < mediaPlayerArrayList.size() && counter >= 0) {
            final View view = mediaPlayerArrayList.get(counter);
            mVideoViewLayout.addView(view);
            if (view instanceof VideoView) {
                Thumbnail();
                videoPlayer = (VideoView) view;
                final VideoView video = (VideoView) view;
                storiesProgressView.setStoryDuration(45 * 1000);
                storiesProgressView.startStories();
                video.setOnPreparedListener(mediaPlayer -> {
                    mediaPlayer.setOnInfoListener((mediaPlayer1, i, i1) -> {
                        Log.d("mediaStatus", "onInfo: =============>>>>>>>>>>>>>>>>>>>" + i);
                        switch (i) {
                            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                                mProgressBar.setVisibility(View.GONE);
                                if (thumbnail != null) thumbnail.setVisibility(View.GONE);
                                storiesProgressView.resume();
                                return true;
                            }
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:
                            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                mProgressBar.setVisibility(View.VISIBLE);
                                storiesProgressView.pause();
                                return true;
                            }
                        }
                        return false;
                    });
                    float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                    float screenRatio = video.getWidth() / (float)
                            video.getHeight();
                    float scaleX = videoRatio / screenRatio;
                    if (scaleX >= 1f) {
                        video.setScaleX(scaleX);
                    } else {
                        video.setScaleY(1f / scaleX);
                    }
                    video.start();
                    mProgressBar.setVisibility(View.GONE);
                    storiesProgressView.setStoryDuration(mediaPlayer.getDuration());
                    storiesProgressView.startStories(counter);
                });
            } else if (view instanceof ImageView) {
                final ImageView image = (ImageView) view;
                mProgressBar.setVisibility(View.GONE);
                if (thumbnail != null) {
                    thumbnail.setVisibility(View.GONE);
                }
                Glide.with(this).load(mStoriesList.get(counter).mediaUrl).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        storiesProgressView.setStoryDuration(5000);
                        storiesProgressView.startStories(counter);
                        return false;
                    }
                }).into(image);
            }
        } else {
            finish();
        }
    }

    private void Thumbnail() {
        thumbnail = findViewById(R.id.thumbnail);
        thumbnail.setVisibility(View.VISIBLE);
        Glide.with(this).load(mStoriesList.get(counter).mediaUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(thumbnail);
    }

    private void prepareStoriesList() {
        mStoriesList = new ArrayList<>();
        for (StoriesContent item : Global.stories.get(getIntent().getIntExtra("position", 0)).getContent()) {
            Log.d(TAG, item.getMediaUrl() + " :prepareStoriesList: :" + item.getMimeType());
            mStoriesList.add(new StoriesData(item.getMediaUrl(), item.getMimeType()));
        }
        mStoriesList.size();
    }

    private VideoView getVideoView(int position) {
        final VideoView video = new VideoView(this);
        Log.d(TAG, " :getVideoView: :" + mStoriesList.get(position).mediaUrl);
        video.setVideoPath(mStoriesList.get(position).mediaUrl);
        video.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return video;
    }

    private ImageView getImageView() {
        final ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void onNext() {
        storiesProgressView.destroy();
        mVideoViewLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(++counter);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        storiesProgressView.destroy();
        mVideoViewLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(--counter);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
