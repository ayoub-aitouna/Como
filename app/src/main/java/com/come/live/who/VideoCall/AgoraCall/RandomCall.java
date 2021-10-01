package com.come.live.who.VideoCall.AgoraCall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.come.live.R;
import com.come.live.who.Activities.Payments;
import com.come.live.who.Fragments.Dialog.GiftDialog;
import com.come.live.who.Global;

import com.come.live.who.Modules.LiveStreamChatModule;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.RandomChatAdapter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomCall extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID = 22;
    private static final String TAG = "RandomCall";
    private String token;
    private int RandomPersonUserID;
    private String channelName;
    private RtcEngine mRtcEngine;
    private Socket mSocket;
    private RandomChatAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<LiveStreamChatModule> ChatData = new ArrayList<>();
    Handler Transaction;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_call);
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            try {
                mSocket = IO.socket(Global.URL);
            } catch (URISyntaxException ignored) {
            }
            mSocket.connect();
            mSocket.on("CallQueueRes", CallQueueRes);
            mSocket.on("Gift", args -> {
                try {
                    Log.d(TAG, "Gift: " + args[0]);
                    JSONObject data = (JSONObject) args[0];
                    LiveStreamChatModule item = new LiveStreamChatModule();
                    item.setGift(data.getInt("amount"));
                    item.setUsername(data.getString("username"));
                    item.setSenderId(data.getInt("id"));
                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                    ChatData.add(item);
                    adapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(ChatData.size() - 1);
                } catch (Exception e) {
                    Log.d(TAG, "GiftFailled: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            mSocket.on("ExitQueue", (args -> new Handler(Looper.getMainLooper()).post(() -> {
                Log.d(TAG, "Other User left Call : ");
                recreate();
                //END();
                //RandomCall.this.finish();
                //RandomCall.this.startActivity(getIntent());
                //DestroyAgora();
                //Connect();
            })));
            Connect();
            Action();
            Log.d(TAG, "mScocket Is Active: "+mSocket.isActive());

        }
    }

    private void intitAdapter() {
        adapter = new RandomChatAdapter(ChatData);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
//        this.recreate();
        super.onRestart();
    }

    private void Action() {
        findViewById(R.id.next).setOnClickListener(v -> {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
//            Disconnect();
//            Connect();
            recreate();
        });
        findViewById(R.id.gift).setOnClickListener(v -> OpenGiftDialog());
        findViewById(R.id.exit).setOnClickListener(v -> {
//            Disconnect();
            finish();
        });
    }

    private void AddToFavList() {
        Global.AddToFavList(Global.GetUserID(this), RandomPersonUserID, "Random", new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                UpdateUI(response.code() == 200);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {

            }
        });
    }

    private void UpdateUI(boolean b) {
        if (b) {
            findViewById(R.id.fav).setVisibility(View.GONE);
        } else {
            findViewById(R.id.fav).setVisibility(View.VISIBLE);
        }
    }

    private void OpenGiftDialog() {
        GiftDialog reportDialog = new GiftDialog(this, amount -> {
            try {
                JSONObject data = new JSONObject();
                data.put("amount", amount);
                data.put("username", Global.User.getName());
                data.put("id", Global.GetUserID(this));
                data.put("reciver", RandomPersonUserID);
                Log.d(TAG, "OpenGiftDialog: " + amount);
                mSocket.emit("Gift", data);
            } catch (Exception e) {
                Log.d(TAG, "OpenGiftDialog: Failled" + e.getMessage());
                e.printStackTrace();
            }
        });
        reportDialog.getWindow().setGravity(Gravity.BOTTOM);
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportDialog.show();
    }

    private void Connect() {
        intitAdapter();
        if (Transaction != null) Transaction.removeCallbacksAndMessages(null);
        JSONObject data = new JSONObject();
        try {
            data.put("id", Global.GetUserID(this));
            data.put("into", getIntent().getStringExtra("into"));
            data.put("UserGender", Global.User.getGender());
        } catch (Exception ignored) {

        }
        mSocket.emit("CallQueue", data);
    }


    private void MakeTransactionToSystem() {
        Transaction = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
                retrofit2.Call<String> CallTransaction = service.MakeTransactionToSystem(
                        Global.GetUserID(RandomCall.this), 60);
                CallTransaction.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() != 200) {
                            try {
                                mRtcEngine.leaveChannel();
                                Transaction.removeCallbacksAndMessages(null);
                                startActivity(new Intent(getApplicationContext(), Payments.class));
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        recreate();
                    }
                });
                Transaction.postDelayed(this, 60 * 1000);
            }
        };
        Transaction.postDelayed(run, 15 * 1000);
    }

    private final Emitter.Listener CallQueueRes = args -> new Handler(Looper.getMainLooper()).post(() -> {
        findViewById(R.id.progress).setVisibility(View.GONE);
        JSONObject data = (JSONObject) args[0];
        try {
            RandomPersonUserID = data.getInt("FUserID") != Global.GetUserID(this) ?
                    data.getInt("FUserID") :
                    data.getInt("SUserID");
            token = data.getString("Token");
            channelName = data.getString("Channel");
            try {
                initializeAndJoinChannel();
            } catch (Exception e) {
                e.printStackTrace();
                recreate();
            }
            MakeTransactionToSystem();
            CheckUserFriend(RandomPersonUserID);
            findViewById(R.id.fav).setOnClickListener(v -> AddToFavList());
        } catch (Exception e) {
            recreate();
        }

    });
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            recreate();
            super.onLeaveChannel(stats);
        }
    };

    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), Global.appId, mRtcEventHandler);
        } catch (Exception e) {
            recreate();
        }
        mRtcEngine.enableVideo();
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        mRtcEngine.joinChannel(token, channelName, "", 0);

    }

    // Java
    private void setupRemoteVideo(int uid) {
        FrameLayout RemoteContainer = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        RemoteContainer.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }
    private void CheckUserFriend(int UserId) {
        Global.CheckFavList(UserId, "TAG", this, new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                UpdateUI(response.code() == 200);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    // Java
    protected void onDestroy() {
        Disconnect();
        mSocket.disconnect();
        Log.d(TAG, "mScocket Is Active: "+mSocket.isActive());
        DestroyAgora();
        super.onDestroy();

    }

    void DestroyAgora() {
        if (Transaction != null) Transaction.removeCallbacksAndMessages(null);
        try {
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
        } catch (Exception ignored) {
        }
        RtcEngine.destroy();
    }

    void Disconnect() {
        if (Transaction != null) Transaction.removeCallbacksAndMessages(null);
        JSONObject data = new JSONObject();
        try {
            data.put("id", Global.GetUserID(this));
        } catch (Exception ignored) {
        }
        mSocket.emit("ExitQueue", data);
    }
}