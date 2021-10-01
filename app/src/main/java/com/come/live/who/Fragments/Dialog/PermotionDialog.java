package com.come.live.who.Fragments.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.come.live.R;
import com.come.live.who.Activities.Payments;

public class PermotionDialog extends Dialog {

    public Dialog dialog;
    Context mcontext;

    public PermotionDialog(@NonNull Context context) {
        super(context);
        this.mcontext = context;
    }

    public PermotionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mcontext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.promotion_dialog);
        VideoView videoContainer = findViewById(R.id.video_player);
        Uri uri = Uri.parse("android.resource://" + mcontext.getPackageName() + "/" + R.raw.start_video);
        videoContainer.setVideoURI(uri);
        videoContainer.start();
        ImageButton close = findViewById(R.id.close);
        TextView makePayments = findViewById(R.id.makePayments);
        close.setOnClickListener(v -> dismiss());
        makePayments.setOnClickListener(v -> mcontext.startActivity(new Intent(mcontext, Payments.class)));
    }

}
