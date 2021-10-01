package com.come.live.who.Fragments.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.GiftModule;
import com.come.live.who.Utils.GiftAdapter;

import java.util.ArrayList;

import static com.come.live.who.Global.GiftList;

public class GiftDialog extends Dialog {
    public Dialog dialog;
    Context mcontext;
    ItemCallBack itemCallBack;

    public GiftDialog(@NonNull Context context, int themeResId, ItemCallBack itemCallBack) {
        super(context, themeResId);
        this.mcontext = context;
        this.itemCallBack = itemCallBack;
    }

    public GiftDialog(@NonNull Context context, ItemCallBack itemCallBack) {
        super(context);
        this.mcontext = context;
        this.itemCallBack = itemCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gift_dialog);
        initRecycler();

    }

    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(mcontext,
                3, GridLayoutManager.HORIZONTAL, false));
        GiftAdapter adapter = new GiftAdapter(GiftList, position -> {
            itemCallBack.onItem(GiftList.get(position).getAmount());
            this.dismiss();
        });
        recyclerView.setAdapter(adapter);


    }

}
