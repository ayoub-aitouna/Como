package com.come.live.who.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.come.live.R;
import com.come.live.Receiver.CallReceiver;
import com.come.live.who.ChatRoom.ChatRoom;
import com.come.live.who.Global;
import com.come.live.who.Modules.ChatContent;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.VideoCall.videoCall;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.come.live.App.CHANNEL_1_ID;
import static com.come.live.App.CHANNEL_2_ID;

public class Service extends android.app.Service {
    private static final String TAG = "Service";
    private NotificationManagerCompat notificationManager;
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = new Handler();
        JSONObject data = new JSONObject();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Connect();
                handler.postDelayed(this, 600000);
            }
        };
        handler.post(run);
        try {
            mSocket = IO.socket(Global.URL);
        } catch (URISyntaxException ignored) {
        }
        try {
            data.put("UserId", Global.UserId);
        } catch (Exception ignored) {

        }
        mSocket.connect();
        mSocket.on("Send", onNewMessage);
        mSocket.on("newCall", onNewCall);
        mSocket.emit("Connect", data);
        return super.onStartCommand(intent, flags, startId);
    }

    private void Connect() {
        Retrofit.UpdateUserStates service = Retrofit.getRetrofitInstance().create(Retrofit.UpdateUserStates.class);
        Call<String> call = service.UpdateUserToOnline(Global.UserId, true);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "Connect: " + response.code());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private final Emitter.Listener onNewMessage = args -> new Handler(Looper.getMainLooper()).post(() -> {
        JSONObject object = (JSONObject) args[0];
        ChatContent chatContent = new ChatContent();
        String name = "";
        try {
            chatContent.setContentImage(object.getString("contentImage"));
            chatContent.setContentAudio(object.getString("contentAudio"));
            chatContent.setContentText(object.getString("contentText"));
            chatContent.setGiftCoins(object.getInt("giftCoins"));
            chatContent.setSenderId(object.getInt("SenderId"));
            chatContent.setHash_id(object.getString("Hash_id"));
            name = object.getString("Sender_name");
            Log.d(TAG, "Socket Hash: " + object.getString("Hash_id"));
        } catch (Exception e) {
            Log.d(TAG, "onNewMessage Fail: " + e.getMessage());
        }
        if (chatContent.getHash_id() != null && !chatContent.getHash_id().equals(Global.Room))
            ShowNotification(chatContent, name);
    });
    private final Emitter.Listener onNewCall = args -> new Handler(Looper.getMainLooper()).post(() -> {
        Log.d(TAG, "run: New Call");
        JSONObject object = (JSONObject) args[0];
        String name = "", img = "";
        int id = 0, ResId = 0;
        try {
            id = object.getInt("userId");
            ResId = object.getInt("ReceiverId");
            name = object.getString("Username");
            img = object.getString("ProfileImg");
        } catch (Exception e) {
            Log.d(TAG, "NotFication: " + e.getMessage());
        }
        ShowCallNotify(id, ResId, img, name);

    });

    private void ShowCallNotify(int id, int ReceiverId, String img, String name) {
        Uri soundUri = Uri.parse("android.resource://" + Service.this.getPackageName() + "/" + R.raw.notification_sound);
        Intent StartCall = new Intent(Service.this, videoCall.class);
        StartCall.putExtra("name", name);
        StartCall.putExtra("id", id);
        StartCall.putExtra("ReceiverId", ReceiverId);
        StartCall.putExtra("img", img);

        PendingIntent StartCallPendingIntent = PendingIntent.getActivity(Service.this,
                0, StartCall, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent dismiss = new Intent(Service.this, CallReceiver.class);
        dismiss.putExtra("id", id);
        dismiss.putExtra("ReceiverId", ReceiverId);

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(Service.this, 0, dismiss, 0);
        Notification notification = new NotificationCompat.Builder(Service.this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.call_icon)
                .setContentTitle(name + " Calling you")
                .setContentText("Calling")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_baseline_call_end_24, "Dismiss", dismissPendingIntent)
                .addAction(R.drawable.ic_baseline_call_24, "Answer", StartCallPendingIntent)
                .setContentIntent(StartCallPendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setColor(Color.GREEN)
                .build();
        notificationManager.notify(id, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ShowNotification(ChatContent content, String name) {
        String msg = "";
        Intent i = new Intent(getApplicationContext(), ChatRoom.class);
        i.putExtra("SendMode", 0);
        i.putExtra("ReceiverId", content.getSenderId());
        PendingIntent chatIntent = PendingIntent.getActivity(Service.this, 0, i, 0);
        if (content.getGiftCoins() > 0) {
            msg = "Gifted you " + content.getGiftCoins();
        } else if (content.getContentText() != null && !content.getContentText().isEmpty()) {
            msg = content.getContentText();
        } else if (content.getContentAudio() != null && !content.getContentAudio().isEmpty()) {
            msg = "Sent Audio";
        } else if (content.getContentImage() != null && !content.getContentImage().isEmpty()) {
            msg = "Sent an Image";
        }
        Uri soundUri = Uri.parse("android.resource://" + Service.this.getPackageName() + "/" + R.raw.notification_sound);
        Notification notification = new NotificationCompat.Builder(Service.this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle("New Message from " + name)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setColor(Color.BLUE)
                .setContentIntent(chatIntent)
                .build();
        notificationManager.notify(new Random().nextInt(61) + 20, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        mSocket.off("newCall", onNewCall);
        Log.d(TAG, "onDestroy: called.");

    }
}
