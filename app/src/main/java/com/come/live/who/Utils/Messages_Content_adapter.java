package com.come.live.who.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Activities.Profile.ViewPost;
import com.come.live.who.Global;
import com.come.live.who.Modules.ChatContent;
import com.come.live.who.Modules.GiftModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.come.live.who.Global.GiftList;

public class Messages_Content_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final String TAG = "msgContent";
    private static final int NO_POSITION = -20;
    ArrayList<ChatContent> Data;
    Context context;
    int currentPlayingPosition;
    SeekBarUpdater seekBarUpdater;
    RecyclerView.ViewHolder playingHolder;
    MediaPlayer mediaPlayer;

    public Messages_Content_adapter(ArrayList<ChatContent> Data) {
        this.Data = Data;
        seekBarUpdater = new SeekBarUpdater();
        mediaPlayer = new MediaPlayer();
        this.currentPlayingPosition = NO_POSITION;
    }

    @Override
    public int getItemViewType(int position) {
        if (Data.get(position).getCallsDuration() > 0) return 1;
        if (Data.get(position).getSenderId() == Global.UserId) return 0;
        return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if (viewType == 0)
            return new SenderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_sender_item_layout, parent, false));
        if (viewType == 1)
            return new callInfo(LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_item_layout, parent, false));
        return new ReceiverHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_receiver_item_layout, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onCreateViewHolder: " + Data.get(position).getCallsDuration());
        if (Data.get(position).getCallsDuration() > 0) {
            ((callInfo) holder).content.setText("Call Duration " + Data.get(position).getCallsDuration() + "S - " + Data.get(position).getSendTime());
        } else if (Data.get(position).getSenderId() == Global.UserId) {
            BindSenderHolder((SenderHolder) holder, position);
        } else {
            BindReceiverHolder((ReceiverHolder) holder, position);
        }
    }


    @SuppressLint("SetTextI18n")
    private void BindReceiverHolder(ReceiverHolder holder, int position) {
        boolean IsImageContent = Data.get(position).getContentImage() != null
                && !Data.get(position).getContentImage().trim().isEmpty();
        boolean IsAudioContent = Data.get(position).getContentAudio() != null
                && !Data.get(position).getContentAudio().trim().isEmpty();
        holder.content.setVisibility(View.VISIBLE);
        if (Data.get(position).getGiftCoins() > 0) {
            GiftModule gift = GetGiftfromAmount(Data.get(position).getGiftCoins());
            holder.giftimg.setVisibility(View.VISIBLE);
            holder.content.setText(" you Gifted " + Data.get(position).getGiftCoins() + " Coins");
            Picasso.get()
                    .load(gift.getImg())
                    .into(holder.giftimg);
        } else {
            holder.giftimg.setVisibility(View.GONE);
            holder.content.setText(String.valueOf(Data.get(position).getContentText()));
        }
        holder.time.setText(String.valueOf(Data.get(position).getSendTime()));
        if (IsImageContent) {
            holder.img.setVisibility(View.VISIBLE);
            holder.Audio.setVisibility(View.GONE);
            Picasso.get().load(Data.get(position).getContentImage()).into(holder.img);
        } else if (IsAudioContent) {
            holder.content.setVisibility(View.GONE);
            holder.Audio.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            if (holder.getAdapterPosition() == currentPlayingPosition) {
                playingHolder = holder;
                updatePlayingView(holder.sbProgress, holder.playBtn);
            } else {
                updateNonPlayingView(holder.sbProgress, holder.playBtn);
            }
        } else {
            holder.img.setVisibility(View.GONE);
            holder.Audio.setVisibility(View.GONE);
        }
        holder.img.setOnClickListener(v -> {
            Intent View = new Intent(context, ViewPost.class);
            View.putExtra("Url", Data.get(position).getContentImage());
            View.putExtra("isOther", false);
            context.startActivity(View);
        });
    }

    private GiftModule GetGiftfromAmount(int giftCoins) {
        GiftModule data = new GiftModule();
        data.setImg("https://www.kindpng.com/picc/m/249-2490070_transparent-christmas-present-png-gold-gift-box-png.png");
        for (GiftModule item : GiftList) {
            if (item.getAmount() == giftCoins) return item;
        }
        return data;
    }

    @SuppressLint("SetTextI18n")
    private void BindSenderHolder(SenderHolder holder, int position) {
        boolean IsImageContent = Data.get(position).getContentImage() != null
                && !Data.get(position).getContentImage().trim().isEmpty();
        boolean IsAudioContent = Data.get(position).getContentAudio() != null
                && !Data.get(position).getContentAudio().trim().isEmpty();
        holder.content.setVisibility(View.VISIBLE);
        holder.time.setText(String.valueOf(Data.get(position).getSendTime()));

        if (Data.get(position).getGiftCoins() > 0) {
            GiftModule gift = GetGiftfromAmount(Data.get(position).getGiftCoins());
            holder.giftimg.setVisibility(View.VISIBLE);
            holder.content.setText(" you Gifted " + Data.get(position).getGiftCoins() + " Coins");
            Picasso.get()
                    .load(gift.getImg())
                    .into(holder.giftimg);
        } else {
            holder.giftimg.setVisibility(View.GONE);
            holder.content.setText(String.valueOf(Data.get(position).getContentText()));
        }
        if (IsAudioContent) {
            holder.Audio.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            holder.content.setVisibility(View.GONE);
            if (holder.getAdapterPosition() == currentPlayingPosition) {
                playingHolder = holder;
                updatePlayingView(holder.sbProgress, holder.playBtn);
            } else {
                updateNonPlayingView(holder.sbProgress, holder.playBtn);
            }
        } else if (IsImageContent) {
            holder.img.setVisibility(View.VISIBLE);
            holder.Audio.setVisibility(View.GONE);
            Picasso.get().load(Data.get(position).getContentImage()).fit().into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
            holder.Audio.setVisibility(View.GONE);
        }
        holder.img.setOnClickListener(v -> {
            Intent View = new Intent(context, ViewPost.class);
            View.putExtra("Url", Data.get(position).getContentImage());
            View.putExtra("isOther", false);
            context.startActivity(View);
        });
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (currentPlayingPosition == holder.getAdapterPosition()) {
            if (playingHolder instanceof ReceiverHolder)
                updateNonPlayingView(((ReceiverHolder) playingHolder).sbProgress, ((ReceiverHolder) playingHolder).playBtn);
            else if (playingHolder instanceof SenderHolder)
                updateNonPlayingView(((SenderHolder) playingHolder).sbProgress, ((SenderHolder) playingHolder).playBtn);
            playingHolder = null;
        }
    }

    private void updateNonPlayingView(SeekBar sbProgress, ImageView ivPlayPause) {
        sbProgress.removeCallbacks(seekBarUpdater);
        sbProgress.setEnabled(false);
        sbProgress.setProgress(0);
        ivPlayPause.setImageResource(R.drawable.exo_controls_play);

    }

    private void updatePlayingView(SeekBar sbProgress, ImageView ivPlayPause) {
        sbProgress.setMax(mediaPlayer.getDuration());
        sbProgress.setProgress(mediaPlayer.getCurrentPosition());
        sbProgress.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            sbProgress.postDelayed(seekBarUpdater, 100);
            ivPlayPause.setImageResource(R.drawable.pause);
        } else {
            sbProgress.removeCallbacks(seekBarUpdater);
            ivPlayPause.setImageResource(R.drawable.exo_controls_play);
        }
    }

    public void stopPlayer() {
        if (null != mediaPlayer) {
            releaseMediaPlayer();
        }
    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (playingHolder != null) {
                if (playingHolder instanceof SenderHolder) {
                    ((SenderHolder) playingHolder).sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                    ((SenderHolder) playingHolder).sbProgress.postDelayed(this, 100);
                } else if (playingHolder instanceof ReceiverHolder) {
                    ((ReceiverHolder) playingHolder).sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                    ((ReceiverHolder) playingHolder).sbProgress.postDelayed(this, 100);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }


    class SenderHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {
        TextView content, time;
        ImageView img, playBtn, giftimg;
        RelativeLayout Audio;
        ProgressBar progressBar;
        SeekBar sbProgress;
        boolean F = true;

        public SenderHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.img);
            Audio = itemView.findViewById(R.id.Audio);
            playBtn = itemView.findViewById(R.id.playBtn);
            giftimg = itemView.findViewById(R.id.giftimg);
            sbProgress = itemView.findViewById(R.id.audioTrack);
            progressBar = itemView.findViewById(R.id.progressBar);
            sbProgress.setOnSeekBarChangeListener(this);
            playBtn.setOnClickListener(v -> {
                if (getAdapterPosition() != currentPlayingPosition) {
                    releaseMediaPlayer();
                }
                if (mediaPlayer != null && !F) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playBtn.setImageResource(R.drawable.exo_controls_play);
                    } else {
                        mediaPlayer.start();
                        playBtn.setImageResource(R.drawable.pause);
                    }

                } else {
                    startMediaPlayer(Data.get(getAdapterPosition()).getContentAudio(), sbProgress, playBtn, progressBar, this);
                    F = false;
                }
            });
        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class ReceiverHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {
        TextView content, time;
        ImageView img, playBtn, giftimg;
        RelativeLayout Audio;
        SeekBar sbProgress;
        ProgressBar progressBar;
        boolean F = true;

        public ReceiverHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.img);
            Audio = itemView.findViewById(R.id.Audio);
            giftimg = itemView.findViewById(R.id.giftimg);
            playBtn = itemView.findViewById(R.id.playBtn);
            sbProgress = itemView.findViewById(R.id.audioTrack);
            progressBar = itemView.findViewById(R.id.progressBar);
            sbProgress.setOnSeekBarChangeListener(this);
            playBtn.setOnClickListener(v -> {
                if (getAdapterPosition() != currentPlayingPosition) {
                    releaseMediaPlayer();
                }
                if (mediaPlayer != null && !F) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playBtn.setImageResource(R.drawable.exo_controls_play);
                    } else {
                        mediaPlayer.start();
                        playBtn.setImageResource(R.drawable.pause);
                    }

                } else {
                    startMediaPlayer(Data.get(getAdapterPosition()).getContentAudio(), sbProgress, playBtn, progressBar, this);
                    F = false;
                }
            });
        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class callInfo extends RecyclerView.ViewHolder {
        TextView content;

        public callInfo(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.call_info);
        }
    }


    private void startMediaPlayer(String url, SeekBar sbProgress, ImageView playBtn, ProgressBar progressBar, RecyclerView.ViewHolder holder) {
        try {
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            progressBar.setVisibility(View.VISIBLE);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                playingHolder = holder;
                currentPlayingPosition = holder.getAdapterPosition();
                mediaPlayer.start();
                updatePlayingView(sbProgress, playBtn);
            });
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());

        } catch (Exception e) {
            releaseMediaPlayer();
            Log.d(TAG, "startMediaPlayer: " + e.getMessage());
        }
    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            if (playingHolder instanceof ReceiverHolder)
                updateNonPlayingView(((ReceiverHolder) playingHolder).sbProgress, ((ReceiverHolder) playingHolder).playBtn);
            else if (playingHolder instanceof SenderHolder)
                updateNonPlayingView(((SenderHolder) playingHolder).sbProgress, ((SenderHolder) playingHolder).playBtn);
        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer = null;
        currentPlayingPosition = NO_POSITION;
    }
}
