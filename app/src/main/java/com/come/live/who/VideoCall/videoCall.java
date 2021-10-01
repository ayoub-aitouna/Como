package com.come.live.who.VideoCall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.come.live.R;
import com.come.live.who.Fragments.Calls.Incoming;
import com.come.live.who.Fragments.Calls.Outgoing;
import com.come.live.who.Global;
import com.come.live.who.VideoCall.AgoraCall.InCall;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class videoCall extends AppCompatActivity {
    public String Name;
    public String img;
    public int Id;
    public int ReceiverId;
    public Socket mSocket;
    public MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        Name = getIntent().getStringExtra("name");
        Id = getIntent().getIntExtra("id", 0);
        ReceiverId = getIntent().getIntExtra("ReceiverId", 0);
        img = getIntent().getStringExtra("img");
        NotificationManagerCompat.from(this).cancel(Id);
        JSONObject data = new JSONObject();
        try {
            mSocket = IO.socket(Global.URL);
            data.put("UserId", Global.UserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSocket.on("CallResponse", CallResponse);
        mSocket.connect();
        mSocket.emit("Connect", data);
        if (getIntent().getBooleanExtra("isOutGoing", false)) {
            Replace(new Outgoing());
        } else {
            Replace(new Incoming());
        }
    }

    public String getName() {
        return Name;
    }

    void Replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fram, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private final Emitter.Listener CallResponse = args -> new Handler(Looper.getMainLooper()).post(() -> {
        Log.d("CallResponse", "run: Call");
        JSONObject Res = (JSONObject) args[0];
        try {
            NotificationManagerCompat.from(this).cancel(Res.getInt("receiverId"));
        } catch (Exception ignored) {
        }

        try {
            if (Res.getBoolean("Answer")) {
                Intent i = new Intent(getApplicationContext(), InCall.class);
                i.putExtra("Token", Res.getString("Token"));
                i.putExtra("channel", Res.getString("channel"));
                i.putExtra("userId", Res.getInt("userId"));
                i.putExtra("receiverId", Res.getInt("receiverId"));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            Log.d("CallResponse", "NotFication: " + Res.getBoolean("Answer"));
        } catch (Exception e) {
            Log.d("CallResponse", "NotFication: " + e.getMessage());
        }
        finish();
    });

    @Override
    protected void onDestroy() {
        try {
            player.stop();
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }
}