package com.come.live.who.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.GiftModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.Holder> {
    ArrayList<GiftModule> Gifts;
    Context mContext;
    ItemCallBack itemCallBack;

    public GiftAdapter(ArrayList<GiftModule> Gifts, ItemCallBack itemCallBack) {
        this.itemCallBack = itemCallBack;
        this.Gifts = Gifts;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.amount.setText(String.valueOf(Gifts.get(position).getAmount()));
        try {
            Picasso.get().load(Gifts.get(position).getImg()).into(holder.img);
        } catch (Exception ignored) {
        }
        holder.itemView.setOnClickListener(v -> itemCallBack.onItem(position));
    }

    @Override
    public int getItemCount() {
        return Gifts.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        TextView amount;
        ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            img = itemView.findViewById(R.id.img);
        }
    }
}
