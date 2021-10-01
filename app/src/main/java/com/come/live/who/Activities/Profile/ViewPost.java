package com.come.live.who.Activities.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.come.live.R;
import com.come.live.who.Fragments.Dialog.ReportDialog;
import com.come.live.who.Global;
import com.come.live.who.VideoCall.videoCall;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Actions();
        if (!getIntent().getBooleanExtra("isOther", true)) {
            findViewById(R.id.StartCall).setVisibility(View.GONE);
            findViewById(R.id.report).setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(getIntent().getStringExtra("Url")).into(((ImageView) findViewById(R.id.imgContainer)), new Callback() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception ignored) {
        }
    }

    private void Actions() {
        findViewById(R.id.goback).setOnClickListener(v -> finish());
        findViewById(R.id.StartCall).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), videoCall.class);
            i.putExtra("isOutGoing", true);
            i.putExtra("img", getIntent().getStringExtra("img"));
            i.putExtra("name", getIntent().getStringExtra("name"));
            i.putExtra("ReceiverId", getIntent().getIntExtra("id", 0));
            i.putExtra("id", Global.GetUserID(ViewPost.this));
            startActivity(i);
        });
        findViewById(R.id.report).setOnClickListener(v -> ShowDialog());
    }

    private void ShowDialog() {
        ReportDialog reportDialog = new ReportDialog(this, R.style.DialogSlideAnim);
        reportDialog.getWindow().setGravity(Gravity.BOTTOM);
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportDialog.show();
    }
}