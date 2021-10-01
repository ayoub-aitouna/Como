package com.come.live.who.Fragments.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.come.live.R;

public class CallDialog extends Dialog {

    public Dialog dialog;
    Context mcontext;

    public CallDialog(@NonNull Context context) {
        super(context);
        this.mcontext = context;
    }

    public CallDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mcontext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.call_dialog);

    }

}
