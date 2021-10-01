package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Modules.LiveStreamChatModule;


import java.util.ArrayList;

public class LiveStreamChatAdapter extends RecyclerView.Adapter<LiveStreamChatAdapter.Holder> {
    ArrayList<LiveStreamChatModule> Data;

    public LiveStreamChatAdapter(ArrayList<LiveStreamChatModule> Data) {
        this.Data = Data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.live_stram_chat_item_layout, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        if (Data.get(position).getGift() > 0) {
            holder.message.setText("Gifted you" + Data.get(position).getGift());
        } else {
            holder.message.setText(Data.get(position).getMessage());
        }
        holder.name.setText(Data.get(position).getUsername());
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView message;
        TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            name = itemView.findViewById(R.id.name);

        }
    }
}
