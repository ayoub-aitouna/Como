package com.come.live.who.Fragments.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.come.live.R;

public class ReportDialog extends Dialog {
    public Dialog dialog;
    Context mcontext;

    public ReportDialog(@NonNull Context context) {
        super(context);
        this.mcontext = context;
    }

    public ReportDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.report_dialog);
    }
}
