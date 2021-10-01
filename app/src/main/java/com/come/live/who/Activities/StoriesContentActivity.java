package com.come.live.who.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


import com.come.live.R;
import com.come.live.who.Animation.ZoomOutPageTransformer;
import com.come.live.who.Global;
import com.come.live.who.Interfaces.StoriesOnComplete;
import com.come.live.who.Utils.Stories.StoriesContainerAdapter;

public class StoriesContentActivity extends AppCompatActivity implements StoriesOnComplete {

    private static final String TAG = "Stories_content";
    private ViewPager2 screenPager;
    StoriesContainerAdapter storiesContainerAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stories_content);
        // setup viewpager
        screenPager = findViewById(R.id.PagerMedia);
        storiesContainerAdapter = new StoriesContainerAdapter(Global.stories, this);
        screenPager.setPageTransformer(new ZoomOutPageTransformer());
        screenPager.setAdapter(storiesContainerAdapter);
        screenPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }


    @Override
    public void Complete(int position) {
        if (position < Global.stories.size() - 1) {
            screenPager.setCurrentItem(position + 1, true);
        } else
            finish();
    }

    @Override
    public void PERV(int position) {
        screenPager.setCurrentItem(position - 1, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}