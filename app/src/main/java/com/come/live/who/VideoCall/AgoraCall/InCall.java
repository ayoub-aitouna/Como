package com.come.live.who.VideoCall.AgoraCall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.come.live.R;
import com.come.live.who.Activities.Payments;
import com.come.live.who.Global;
import com.come.live.who.Retrofit.Retrofit;

import org.json.JSONObject;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InCall extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID = 22;
    private static final String TAG = "InCall";
    private String token;
    int UserID;
    private String channelName;
    private RtcEngine mRtcEngine;
    int CallDuration = 1;
    public Socket mSocket;
    Handler Transaction = new Handler();
    Handler TimeTransaction = new Handler();
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call);
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            channelName = getIntent().getStringExtra("channel");
            token = getIntent().getStringExtra("Token");
            UserID = getIntent().getIntExtra("userId", 0) != Global.GetUserID(this)
                    ? getIntent().getIntExtra("userId", 0)
                    : getIntent().getIntExtra("receiverId", 0);
            ConnectSocket();
            initializeAndJoinChannel();
            Actions();
            MakeTransaction();
            Timer();
        }
    }

    private void Timer() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                CallDuration += 1;
                TimeTransaction.postDelayed(this, 1000);
            }
        };
        TimeTransaction.postDelayed(run, 1000);
    }

    private void ConnectSocket() {
        JSONObject data = new JSONObject();
        try {
            mSocket = IO.socket(Global.URL);
            data.put("UserId", Global.GetUserID(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.emit("Connect", data);
        mSocket.on("EnCall", (args) -> {
            Log.d(TAG, "ConnectSocket: ");
            finish();
        });
    }

    private void MakeTransaction() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
                retrofit2.Call<String> CallTransaction = service.MakeTransaction(
                        getIntent().getIntExtra("userId", 0),
                        getIntent().getIntExtra("receiverId", 0), 120);
                CallTransaction.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() != 200) {
                            Transaction.removeCallbacksAndMessages(null);
                            TimeTransaction.removeCallbacksAndMessages(null);
                            mRtcEngine.leaveChannel();
                            startActivity(new Intent(getApplicationContext(), Payments.class));
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Transaction.removeCallbacksAndMessages(null);
                        mRtcEngine.leaveChannel();
                        finish();
                    }
                });
                Transaction.postDelayed(this, 60 * 1000);
            }
        };
        Transaction.postDelayed(run, 60 * 1000);
    }

    private void Actions() {
        findViewById(R.id.exit).setOnClickListener(v -> {
            mRtcEngine.leaveChannel();
            endCall();
            TimeTransaction.removeCallbacksAndMessages(null);
            Transaction.removeCallbacksAndMessages(null);
            finish();
        });
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
//            runOnUiThread(() -> finish());
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            runOnUiThread(() -> finish());
            super.onLeaveChannel(stats);

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            findViewById(R.id.progress).setVisibility(View.GONE);
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

    };

    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), Global.appId, mRtcEventHandler);
        } catch (Exception e) {
            Log.d(TAG, "initializeAndJoinChannel: " + e.getMessage());
        }
        mRtcEngine.enableVideo();
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }


    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    protected void onDestroy() {
        super.onDestroy();
        endCall();
        Transaction.removeCallbacksAndMessages(null);
        TimeTransaction.removeCallbacksAndMessages(null);
        try {
            mRtcEngine.leaveChannel();
            mSocket.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RtcEngine.destroy();
    }

    void endCall() {
        TimeTransaction.removeCallbacksAndMessages(null);
        JSONObject CallInfo = new JSONObject();
        try {
            Log.d(TAG, "endCall: " + getIntent().getIntExtra("userId", 0));
            CallInfo.put("SenderId", getIntent().getIntExtra("userId", 0));
            CallInfo.put("receiverId", getIntent().getIntExtra("receiverId", 0));
            CallInfo.put("Duration", CallDuration);
            mSocket.emit("EnCall", CallInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}