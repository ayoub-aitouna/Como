package com.come.live.who.Activities.LiveStreaming;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.Modules.Token;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.LiveStreamChatAdapter;
import com.come.live.who.Modules.LiveStreamChatModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutGoingLiveStream extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID = 22;
    private static final String TAG = "LiveStreamActivity";
    private FrameLayout container;
    int TotalGiftsAmount = 0;
    private Socket mSocket;
    LiveStreamChatAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<LiveStreamChatModule> LiveStreamChatData;
    private String token = "";
    private RtcEngine mRtcEngine;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private String channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_live_stream);
        LiveStreamChatData = new ArrayList<>();

        try {
            mSocket = IO.socket(Global.URL);
        } catch (URISyntaxException ignored) {
        }
        initChatRoom();
        container = findViewById(R.id.local_video_view_container);
        findViewById(R.id.End).setOnClickListener(v -> {
            finish();
        });
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            SetAlive();

        }
        setUi();
    }

    private void setUi() {
        Picasso.get().load(Global.User.getProfileImage()).into((CircleImageView) findViewById(R.id.img));
        ((TextView) findViewById(R.id.name)).setText(Global.User.getName());
    }

    private void SetAlive() {
        Retrofit.UpdateUserStates service = Retrofit.getRetrofitInstance().create(Retrofit.UpdateUserStates.class);
        Call<String> call = service.UpdateUserToLive(Global.GetUserID(this), true);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    ConnectToSocketByChannel();
                } else {
                    recreate();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        mSocket.emit("StreamChat", Global.User.getIdUser());
    }

    private void initChatRoom() {
        adapter = new LiveStreamChatAdapter(LiveStreamChatData);
        recyclerView = findViewById(R.id.chat_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

//    private void ConnectToSocketByChannel() {
//        try {
//            mSocket = IO.socket(Global.URL);
//        } catch (URISyntaxException ignored) {
//        }
//        mSocket.connect();
//        mSocket.emit("Send", "message");
//        mSocket.on("Comment", Comments -> new Handler(Looper.getMainLooper()).post(() -> {
//            JSONObject data = (JSONObject) Comments[0];
//            LiveStreamChatModule comments = new LiveStreamChatModule();
//            try {
//                comments.setUsername(data.getString("Username"));
//                comments.setMessage(data.getString("Message"));
//                comments.setGift(data.getInt("Gift"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            SetGiftAmountUi(TotalGiftsAmount += comments.getGift());
//
//            if (comments.getSenderId() != Global.GetUserID(this))
//                SentComment(comments);
//        }));
//        mSocket.on("NumberOfAudience", NumberOfAudience -> new Handler(Looper.getMainLooper()).post(() -> {
//            JSONObject data = (JSONObject) NumberOfAudience[0];
//            try {
//                int num = data.getInt("NumberOfAudience");
//                ((TextView) findViewById(R.id.NumberOfAudience)).setText(String.valueOf(num));
//                List<LiveStreamChatModule> commentList = new Gson().fromJson(data.getJSONArray("comments").toString(), new TypeToken<List<LiveStreamChatModule>>() {
//                }.getType());
//                for (LiveStreamChatModule item : commentList) {
//                    TotalGiftsAmount += item.getGift();
//                    SetGiftAmountUi(TotalGiftsAmount);
//                }
//                LiveStreamChatData.addAll(commentList);
//                adapter.notifyDataSetChanged();
//                if (LiveStreamChatData.size() > 1)
//                    recyclerView.smoothScrollToPosition(LiveStreamChatData.size() - 1);
//
//            } catch (JSONException e) {
//                Log.d(TAG, "ConnectToSocketByChannel: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }));
//    }

    private void ConnectToSocketByChannel() {
        try {
            mSocket = IO.socket(Global.URL);
        } catch (URISyntaxException ignored) {
        }
        mSocket.connect();
        mSocket.emit("StreamChat", Global.User.getIdUser());
        mSocket.emit("join", Global.GetUserID(this));
        mSocket.on("join", args -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                findViewById(R.id.progress).setVisibility(View.GONE);
                JSONObject arg = (JSONObject) args[0];
                try {
                    token = arg.getString("Token");
                    channel = arg.getString("Channel");
                    initializeAndJoinChannel();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            });
        });
        mSocket.on("Comment", Comments -> new Handler(Looper.getMainLooper()).post(() -> {
            JSONObject data = (JSONObject) Comments[0];
            LiveStreamChatModule comments = new LiveStreamChatModule();
            try {
                comments.setUsername(data.getString("Username"));
                comments.setMessage(data.getString("Message"));
                comments.setGift(data.getInt("Gift"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SetGiftAmountUi(TotalGiftsAmount += comments.getGift());

            if (comments.getSenderId() != Global.GetUserID(this))
                SentComment(comments);
        }));
        mSocket.on("NumberOfAudience", NumberOfAudience -> new Handler(Looper.getMainLooper()).post(() -> {
            JSONObject data = (JSONObject) NumberOfAudience[0];
            try {
                int num = data.getInt("NumberOfAudience");
                ((TextView) findViewById(R.id.NumberOfAudience)).setText(String.valueOf(num));
                List<LiveStreamChatModule> commentList = new Gson().fromJson(data.getJSONArray("comments").toString(), new TypeToken<List<LiveStreamChatModule>>() {
                }.getType());
                for (LiveStreamChatModule item : commentList) {
                    TotalGiftsAmount += item.getGift();
                    SetGiftAmountUi(TotalGiftsAmount);
                }
                LiveStreamChatData.addAll(commentList);
                adapter.notifyDataSetChanged();
                if (LiveStreamChatData.size() > 1)
                    recyclerView.smoothScrollToPosition(LiveStreamChatData.size() - 1);
            } catch (JSONException e) {
                Log.d(TAG, "ConnectToSocketByChannel: " + e.getMessage());
                e.printStackTrace();
            }
        }));
    }

    private void SetGiftAmountUi(int i) {
        ((TextView) findViewById(R.id.amount)).setText(String.valueOf(i));
    }

    private void SentComment(LiveStreamChatModule comment) {
        LiveStreamChatData.add(comment);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(LiveStreamChatData.size() - 1);
        ((TextView) findViewById(R.id.input_Text)).setText("");
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
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid);
            });
        }
    };

    private void GetToken() {
        initializeAndJoinChannel();
        Retrofit.GetDataService service = Retrofit.getRetrofitInstance().create(Retrofit.GetDataService.class);
        Call<Token> call = service.getToken(true, String.valueOf(Global.GetUserID(this)));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                token = response.body().getToken();
                Log.d(TAG, "onResponse: " + response.body().getToken());
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i(TAG, "onResponse: " + t.getMessage());
                Toast.makeText(OutGoingLiveStream.this, "Get Token Failed", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), Global.appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.enableVideo();
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        mRtcEngine.joinChannel(token, channel, "", 0);

    }

    private void setupRemoteVideo(int uid) {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
//        container.addView(surfaceView);
//        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }


    protected void onDestroy() {
        super.onDestroy();
        try {
            mRtcEngine.leaveChannel();
            mRtcEngine.destroy();
            mSocket.disconnect();
        } catch (Exception e) {
        }
    }


}