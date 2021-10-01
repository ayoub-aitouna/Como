package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.ChatRoom.ChatRoom;
import com.come.live.who.Global;
import com.come.live.who.Modules.ChatModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Messages_adapter extends RecyclerView.Adapter<Messages_adapter.Holder> {
    ArrayList<ChatModule> Data;
    Context context;
    private static final String TAG = "Messages_adapter";

    public Messages_adapter(ArrayList<ChatModule> Data) {
        this.Data = Data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_dashboard_item_layout, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.itemView.setOnClickListener(v -> {
                Data.get(position).setSeen(1);
                Data.get(position).setUnseenNumber(0);
                notifyDataSetChanged();
                Intent i = new Intent(context, ChatRoom.class);
                i.putExtra("Hash_id", Data.get(position).getHash());
                i.putExtra("ReceiverId", Data.get(position).getReceiverId() == Global.UserId ? Data.get(position).getSenderId() :
                        Data.get(position).getReceiverId());
                context.startActivity(i);
            });
            holder.name.setText(Data.get(position).getName());
            Picasso.get().load(Data.get(position).getProfileImage()).into(holder.img);
            if (Data.get(position).getIsAvailable() == 1) {
                holder.online.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
            } else {
                holder.online.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.acsnt_color));
            }
            holder.lastMessage.setText(Data.get(position).getLastMessage());
            holder.seenContainer.setVisibility(View.GONE);
            holder.Container.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } catch (Exception ignored) {
        }
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, lastMessage, seen;
        View online;
        LinearLayout seenContainer;
        CardView Container;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            online = itemView.findViewById(R.id.online);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            Container = itemView.findViewById(R.id.Container);
        }
    }
}
