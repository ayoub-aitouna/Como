package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.come.live.R;
import com.come.live.who.Interfaces.ItemCallBack;
import com.come.live.who.Modules.Countries;

import java.util.ArrayList;

public class DiscoverCategories extends RecyclerView.Adapter<DiscoverCategories.Holder> {
    ArrayList<Countries> Data;
    int index = 0;
    ItemCallBack itemCallBack;

    public DiscoverCategories(ArrayList<Countries> Data, ItemCallBack itemCallBack) {
        this.Data = Data;
        this.itemCallBack = itemCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_category_item_layout, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.content.setText(Data.get(position).getCountry());
        holder.itemView.setOnClickListener(v -> {
            index = position;
            notifyDataSetChanged();
            itemCallBack.onItem(position);
        });
        if (index == position) {
            holder.container.setBackgroundResource(R.drawable.category_fill);
            holder.content.setTextColor(Color.WHITE);
        } else {
            holder.container.setBackgroundResource(R.drawable.category_raduis);
            holder.content.setTextColor(Color.BLACK);
        }
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView content;

        public Holder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            content = itemView.findViewById(R.id.content);

        }
    }
}
