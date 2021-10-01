package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Modules.GiftModule;
import com.come.live.who.Modules.LiveStreamChatModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.come.live.who.Global.GiftList;

public class RandomChatAdapter extends RecyclerView.Adapter<RandomChatAdapter.Holder> {
    ArrayList<LiveStreamChatModule> Data;

    public RandomChatAdapter(ArrayList<LiveStreamChatModule> Data) {
        this.Data = Data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.random_chat_item_layout, parent, false));
    }
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        if (Data.get(position).getGift() > 0) {
            GiftModule gift = GetGiftfromAmount(Data.get(position).getGift());
            holder.giftimg.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(gift.getImg())
                    .into(holder.giftimg);
            holder.message.setText("Gifted you" + Data.get(position).getGift());
        }
        holder.name.setText(Data.get(position).getUsername());
    }
    private GiftModule GetGiftfromAmount(int giftCoins) {
        GiftModule data = new GiftModule();
        data.setImg("https://www.kindpng.com/picc/m/249-2490070_transparent-christmas-present-png-gold-gift-box-png.png");
        for (GiftModule item : GiftList) {
            if (item.getAmount() == giftCoins) return item;
        }
        return data;
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView message;
        TextView name;
        ImageView giftimg;

        public Holder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            name = itemView.findViewById(R.id.name);
            giftimg = itemView.findViewById(R.id.giftimg);

        }
    }

}
