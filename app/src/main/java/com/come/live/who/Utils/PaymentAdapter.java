package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.PaymentModule;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.Holder> {
    ArrayList<PaymentModule> Data;
    int index = 0;
    ItemCallBack itemCallBack;
    Context context;

    public PaymentAdapter(ArrayList<PaymentModule> Data, ItemCallBack itemCallBack) {
        this.Data = Data;
        this.itemCallBack = itemCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item_layout, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(Data.get(position).getTitle());
        holder.price.setText(String.valueOf(Data.get(position).getAmount()));
        holder.amount.setText(String.valueOf(Data.get(position).getPrice()));
        holder.itemView.setOnClickListener(v -> {
            index = position;
            notifyDataSetChanged();
            itemCallBack.onItem(position);
        });
        if (index == position) {
            holder.container.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.highlight_red));
            holder.title.setVisibility(View.VISIBLE);
        } else {
            holder.container.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_grey));
            holder.title.setVisibility(View.GONE);

        }
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        TextView title;
        TextView amount;
        TextView price;

        public Holder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            price = itemView.findViewById(R.id.price);

        }
    }
}
