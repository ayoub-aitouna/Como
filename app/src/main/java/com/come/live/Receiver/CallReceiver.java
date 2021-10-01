package com.come.live.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.come.live.who.Global;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

public class CallReceiver extends BroadcastReceiver {
    private Socket mSocket;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallReceiver", "onReceive: "+ intent.getIntExtra("id", 0)+" : "+ intent.getIntExtra("ReceiverId", 0));
        NotificationManagerCompat.from(context).cancel(intent.getIntExtra("id", 0));
        JSONObject UserId = new JSONObject();

        try {
            mSocket = IO.socket(Global.URL);
            UserId.put("UserId", Global.UserId);
        } catch (Exception e) {

        }
        mSocket.connect();
        mSocket.emit("Connect",UserId);
        int id = intent.getIntExtra("id", 0);
        int ReceiverId = intent.getIntExtra("ReceiverId", 0);
        JSONObject data = new JSONObject();
        try {
            data.put("userId", id);
            data.put("receiverId", ReceiverId);
            data.put("Answer", false);
            mSocket.emit("CallResponse", data);
        } catch (Exception ignored) {
        }
    }
}
