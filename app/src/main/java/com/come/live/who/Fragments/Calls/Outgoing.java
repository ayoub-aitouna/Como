package com.come.live.who.Fragments.Calls;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.VideoCall.videoCall;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Outgoing extends Fragment {
    videoCall videoCall;
    String name;
    String img;
    int id;
    int ReceiverId;
    Socket mSocket;
    View view;
    MediaPlayer player;
    Handler EndHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoCall = (videoCall) getActivity();
        name = videoCall.Name;
        img = videoCall.img;
        id = videoCall.Id;
        ReceiverId = videoCall.ReceiverId;
        player = videoCall.player;

        return inflater.inflate(R.layout.outgoing_call_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        ((TextView) view.findViewById(R.id.name)).setText(name);
        try {
            Picasso.get().load(img).into(((ImageView) view.findViewById(R.id.img)));
        } catch (Exception e) {
            Log.d("Picasso", "onViewCreated: " + e.getMessage());
        }
        JSONObject data = new JSONObject();
        try {
            mSocket = IO.socket(Global.URL);
            data.put("UserId", Global.GetUserID(getActivity()));
        } catch (Exception ignored) {

        }
        mSocket.connect();
        mSocket.emit("Connect", data);
        StartCall();
        Action(view);
        BackgroundImg();
        PlayRingtone();
        EndHandler.postDelayed(this::EndCall, 45 * 1000);
    }

    private void StartCall() {
        JSONObject data = new JSONObject();
        try {
            data.put("userId", id);
            data.put("ProfileImg", img);
            data.put("ReceiverId", ReceiverId);
            data.put("Username", name);
            mSocket.emit("newCall", data);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPause() {
        try {
            player.stop();
        } catch (Exception ignored) {
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            player.stop();
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    private void Action(View view) {
        view.findViewById(R.id.end_call).setOnClickListener(v -> EndCall());
    }

    void BackgroundImg() {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Bitmap bitmap = Global.getBitmapFromURL(img);
            Bitmap blurred = Global.blurRenderScript(bitmap, 8, getActivity());//second parametre is radius
            ((ImageView) view.findViewById(R.id.backimg)).setImageBitmap(blurred);
        } catch (Exception ignored) {
        }
    }

    private void PlayRingtone() {
        player = MediaPlayer.create(getActivity(), R.raw.ringtone);
        player.start();
        player.setOnCompletionListener(mp -> player.start());
    }

    private void EndCall() {
        EndHandler.removeCallbacksAndMessages(null);
        JSONObject data = new JSONObject();
        try {
            data.put("userId", id);
            data.put("receiverId", ReceiverId);
            data.put("Answer", false);
            mSocket.emit("CallResponse", data);
            player.stop();
            getActivity().finish();
        } catch (Exception ignored) {
        }

    }


}
