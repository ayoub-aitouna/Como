package com.come.live.who.Fragments.Calls;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
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

public class Incoming extends Fragment {
    videoCall videoCall;
    String name;
    String img;
    int id;
    int ReceiverId;
    View view;
    Socket mSocket;
    MediaPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoCall = (videoCall) getActivity();
        name = videoCall.Name;
        img = videoCall.img;
        id = videoCall.Id;
        ReceiverId = videoCall.ReceiverId;

        player = videoCall.player;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.incoming_call_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        JSONObject data = new JSONObject();
        try {
            mSocket = IO.socket(Global.URL);
            data.put("UserId", Global.UserId);
        } catch (Exception ignored) {
        }
        mSocket.connect();
        mSocket.emit("Connect", data);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        try {
            Picasso.get().load(img).into((ImageView) view.findViewById(R.id.img));
            BackgroundImg();
        } catch (Exception ignored) {
        }
        Action();
        PlayRingtone();


    }

    private void PlayRingtone() {
        player = MediaPlayer.create(getActivity(), Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
        player.setOnCompletionListener(mp -> player.start());
    }

    void BackgroundImg() {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bitmap bitmap = Global.getBitmapFromURL(img);
        Bitmap blurred = Global.blurRenderScript(bitmap, 12, getActivity());//second parametre is radius
        ((ImageView) view.findViewById(R.id.backimg)).setImageBitmap(blurred);
    }

    private void Action() {
        view.findViewById(R.id.start_call).setOnClickListener(v -> StartCall());
        view.findViewById(R.id.end_call).setOnClickListener(v -> EndCall());
    }

    private void EndCall() {
        JSONObject data = new JSONObject();
        try {
            data.put("userId", id);
            data.put("receiverId", ReceiverId);
            data.put("Answer", false);
            mSocket.emit("CallResponse", data);
            player.stop();
        } catch (Exception ignored) {
        }
        getActivity().finish();
    }

    private void StartCall() {
        Log.d("StartCall", "StartCall: StartCall");
        JSONObject data = new JSONObject();
        try {
            data.put("userId", id);
            data.put("receiverId", ReceiverId);
            data.put("Answer", true);
            player.stop();
        } catch (Exception ignored) {
        }
        mSocket.emit("CallResponse", data);

    }



}


