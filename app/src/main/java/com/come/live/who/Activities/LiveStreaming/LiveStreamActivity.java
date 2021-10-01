package com.come.live.who.Activities.LiveStreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.come.live.R;
import com.come.live.who.Activities.Profile.UserProfile;
import com.come.live.who.Fragments.Dialog.GiftDialog;
import com.come.live.who.Global;
import com.come.live.who.Modules.Token;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.LiveStreamChatAdapter;
import com.come.live.who.Modules.LiveStreamChatModule;

import com.come.live.who.VideoCall.videoCall;
import com.squareup.picasso.Picasso;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveStreamActivity extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID = 22;
    private static final String TAG = "LiveStreamActivity";
    FrameLayout RemoteContainer;
    FrameLayout container;
    String token = "";
    String channel = "";
    int TotalGiftsAmount = 0;
    RtcEngine mRtcEngine;
    Socket mSocket;
    int UserID;
    LiveStreamChatAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<LiveStreamChatModule> LiveStreamChatData;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
        UserID = getIntent().getIntExtra("UserId", 0);
        LiveStreamChatData = new ArrayList<>();
        RemoteContainer = findViewById(R.id.remote_video_view_container);
        container = findViewById(R.id.local_video_view_container);

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
//            GetToken();
            ConnectToSocketByChannel();
            GetData();
            CheckFav();
            Actions();
            initChatRoom();

        }
    }

    private void ConnectToSocketByChannel() {
        try {
            mSocket = IO.socket(Global.URL);
        } catch (URISyntaxException ignored) {
        }
        mSocket.connect();
        mSocket.emit("StreamChat", String.valueOf(UserID));
        mSocket.emit("join", Global.GetUserID(this));
        mSocket.on("join", args -> {
            JSONObject arg = (JSONObject) args[0];
            try {
                token = arg.getString("Token");
                channel = arg.getString("Channel");
                if (arg.getBoolean("isActive")) {
                    ShowLiveEnd();
                }
                initializeAndJoinChannel();
            } catch (Exception e) {
                e.printStackTrace();

            }
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

                if (num == -1) {
                    ShowLiveEnd();
                }
            } catch (JSONException e) {
                Log.d(TAG, "ConnectToSocketByChannel: " + e.getMessage());
                e.printStackTrace();
            }
        }));
    }

    private void ShowLiveEnd() {
        findViewById(R.id.end).setVisibility(View.GONE);
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

    private void initChatRoom() {
        adapter = new LiveStreamChatAdapter(LiveStreamChatData);
        recyclerView = findViewById(R.id.chat_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }
    };

    private void GetToken() {
        Retrofit.GetDataService service = Retrofit.getRetrofitInstance().create(Retrofit.GetDataService.class);
        Call<Token> call = service.getToken(false, String.valueOf(getIntent().getIntExtra("UserId", 0)));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                token = response.body().getToken();
                initializeAndJoinChannel();
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                Log.i(TAG, "onResponse: " + t.getMessage());
                Toast.makeText(LiveStreamActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), Global.appId, mRtcEventHandler);
            findViewById(R.id.progress).setVisibility(View.GONE);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        RemoteContainer.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mRtcEngine.enableVideo();
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        mRtcEngine.joinChannel(token, String.valueOf(UserID), "", 0);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private void GetData() {
        Users user = new Users();
        user.setIdUser(UserID);
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.code() != 403 || response.body() != null) {
                    assert response.body() != null;
                    ((TextView) findViewById(R.id.name)).setText(response.body().getName());
                    ((TextView) findViewById(R.id.end_name)).setText(response.body().getName());

                    try {
                        Picasso.get().load(response.body().getProfileImage()).into(((ImageView) findViewById(R.id.img)));
                        Picasso.get().load(response.body().getProfileImage()).into(((ImageView) findViewById(R.id.End_img)));
                    } catch (Exception ignored) {
                    }
                    findViewById(R.id.call).setOnClickListener(v -> {
                        Intent i = new Intent(getApplicationContext(), videoCall.class);
                        i.putExtra("isOutGoing", true);
                        assert response.body() != null;
                        i.putExtra("img", response.body().getProfileImage());
                        i.putExtra("name", response.body().getName());
                        i.putExtra("ReceiverId", response.body().getIdUser());
                        i.putExtra("id", Global.GetUserID(LiveStreamActivity.this));
                        startActivity(i);
                        finish();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {

            }
        });
    }

    private void CheckFav() {
        //noinspection rawtypes
        Global.CheckFavList(UserID, TAG, this, new Callback() {
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
            findViewById(R.id.addFav_end).setVisibility(View.GONE);
        } else {
            findViewById(R.id.fav).setVisibility(View.VISIBLE);
            findViewById(R.id.addFav_end).setVisibility(View.VISIBLE);
        }
    }

    private void Actions() {
        findViewById(R.id.fav).setOnClickListener(v -> Global.AddToFavList(
                Global.GetUserID(this), UserID, TAG, new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        UpdateUI(response.code() == 200);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    }
                }));
        findViewById(R.id.addFav_end).setOnClickListener(v -> Global.AddToFavList(
                Global.GetUserID(this), UserID, TAG, new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        UpdateUI(response.code() == 200);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    }
                }));
        findViewById(R.id.img).setOnClickListener(v -> OpenProfile());
        findViewById(R.id.name).setOnClickListener(v -> OpenProfile());
        findViewById(R.id.close).setOnClickListener(v -> finish());
        findViewById(R.id.gift).setOnClickListener(v -> OpenGiftDialog());
        findViewById(R.id.send).setOnClickListener(v -> {
            String message = ((TextView) findViewById(R.id.input_Text)).getText().toString().trim();
            SendComment(message, 0);
        });
    }

    private void SendComment(String message, int gift) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("Message", message);
            jsonData.put("Username", Global.User.getName());
            jsonData.put("Gift", gift);
            jsonData.put("SenderId", Global.GetUserID(this));
            mSocket.emit("Comment", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void OpenProfile() {
        Intent i = new Intent(getApplicationContext(), UserProfile.class);
        i.putExtra("userID", UserID);
        startActivity(i);
    }

    private void OpenGiftDialog() {
        GiftDialog giftDialog = new GiftDialog(this, R.style.DialogSlideAnim, Amount -> {
            Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
            retrofit2.Call<String> CallTransaction = service.MakeTransaction(
                    Global.GetUserID(LiveStreamActivity.this), UserID, Amount);
            CallTransaction.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.code() != 200) {
                        SendComment("", Amount);
                    } else {
                        Toast.makeText(LiveStreamActivity.this, "you don't have enough", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Toast.makeText(LiveStreamActivity.this, "sorry something went wrong please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });

        });
        giftDialog.getWindow().setGravity(Gravity.BOTTOM);
        giftDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        giftDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        giftDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initChatRoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            mRtcEngine.leaveChannel();
            RtcEngine.destroy();
            mSocket.emit("leave", Global.User.getIdUser());
            mSocket.disconnect();
            mSocket.off("Comment", comments -> {
            });
            mSocket.off("NumberOfAudience", numbers -> {
            });
        } catch (Exception ignored) {
        }
    }


}