//package com.come.live.who.VideoCall;
//
//import android.annotation.SuppressLint;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.View;
//import android.webkit.PermissionRequest;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.come.live.R;
//import com.come.live.who.Global;
//
//import java.net.URISyntaxException;
//import java.util.UUID;
//
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
//
//public class callActivity extends AppCompatActivity {
//    String username = "";
//    String OtherUsername = "";
//    boolean IsPeerConnected = false;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference("Users");
//    boolean IsVideo = true;
//    boolean IsAudio = true;
//    WebView mWebView;
//    RelativeLayout call_layout;
//    String UniqueID = "";
//    private Socket mSocket;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_call);
//        FirebaseApp.initializeApp(this);
//        SharedPreferences sharedPref = PreferenceManager
//                .getDefaultSharedPreferences(this);
//        try {
//            mSocket = IO.socket(Global.URL);
//        } catch (URISyntaxException e) {
//        }
//        mSocket.connect();
//        mSocket.on("Call", onNewMessage);
//
//        username = sharedPref.getString(Global.User.getName(), "None");
//        call_layout = findViewById(R.id.call_layout);
//        findViewById(R.id.toggleAudio).setOnClickListener(v -> {
//            IsAudio = !IsAudio;
//            callJavaScriptFunction("javascript:toggleAudio('" + IsAudio + "')");
//            findViewById(R.id.toggleAudio).setBackgroundResource(IsAudio ? R.drawable.mic_on : R.drawable.mic_off);
//        });
//        findViewById(R.id.toggleVideo).setOnClickListener(v -> {
//            IsVideo = !IsVideo;
//            callJavaScriptFunction("javascript:toggleVideo('" + IsVideo + "')");
//            findViewById(R.id.toggleVideo).setBackgroundResource(IsVideo ? R.drawable.videocam : R.drawable.videocam_off);
//        });
//        findViewById(R.id.btn).setOnClickListener(v -> {
//            mSocket.emit("Call", UniqueID);
//            OtherUsername = ((EditText) findViewById(R.id.Otherusername)).getText().toString();
//            sendCallRequest();
//        });
//        findViewById(R.id.end).setOnClickListener(v -> {
//            EndCall();
//        });
//        setUpWebView();
//
//    }
//
//    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String data = (String) args[0];
//                    SwitchControolLayout();
//                    callJavaScriptFunction("javascript:startCall('" + data + "')");
//                }
//            });
//        }
//    };
//
//    private void sendCallRequest() {
//        if (!IsPeerConnected) {
//            Toast.makeText(this, "Error None Connected", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            myRef.child(OtherUsername).child("incoming").setValue(username);
////            myRef.child(OtherUsername).child("isAvailable").addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                    if (snapshot.getValue() == null) return;
////                    if (snapshot.getValue(boolean.class)) {
////                        ListenForCallID();
////                    }
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError error) {
////
////                }
////            });
//        }
//    }
//
//    private void ListenForCallID() {
//        myRef.child(OtherUsername).child("connid").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue() == null) {
//                    return;
//                }
//                SwitchControolLayout();
//                callJavaScriptFunction("javascript:startCall('" + snapshot.getValue(String.class) + "')");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        myRef.child(OtherUsername).child("End").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue() == null) {
//                    return;
//                }
//                EndCall();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void EndCall() {
//        finish();
//    }
//
//    private void setUpWebView() {
//        mWebView = findViewById(R.id.webview);
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onPermissionRequest(PermissionRequest request) {
//                assert request != null;
//                request.grant(request.getResources());
//            }
//        });
//
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setMediaPlaybackRequiresUserGesture(false);
//        mWebView.addJavascriptInterface(new javascriptCallback(this), "Android");
//        loadVideoCall();
//    }
//
//    private void loadVideoCall() {
////        String filepath = "file:android_asset/call.html";
//        String filepath = "file:android_asset/call.html";
//        mWebView.loadUrl(filepath);
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                initializePeer();
//            }
//        });
//    }
//
//
//    private void CloseCall() {
//        myRef.child(username).child("End").setValue(true);
//        EndCall();
//    }
//
//    private void initializePeer() {
//        UniqueID = GetUniqueId();
//        callJavaScriptFunction("javascript:init('" + UniqueID + "')");
//        myRef.child(username).child("incoming").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                callRequest(snapshot.getValue(String.class));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void callRequest(String caller) {
//        if (caller == null) return;
//        call_layout.setVisibility(View.VISIBLE);
//        ((TextView) findViewById(R.id.caller)).setText(caller + "Is calling you");
//        findViewById(R.id.start_call).setOnClickListener(v -> {
//            myRef.child(username).child("connid").setValue(UniqueID);
//            myRef.child(username).child("isAvailable").setValue(true);
//            call_layout.setVisibility(View.GONE);
//            SwitchControolLayout();
//
//        });
//        findViewById(R.id.end_call).setOnClickListener(v -> {
//            myRef.child(username).child("incoming").setValue(null);
//            call_layout.setVisibility(View.GONE);
//        });
//
//    }
//
//    private void SwitchControolLayout() {
//        findViewById(R.id.input_layout).setVisibility(View.GONE);
//        findViewById(R.id.controler).setVisibility(View.VISIBLE);
//    }
//
//    private String GetUniqueId() {
//        return UUID.randomUUID().toString();
//    }
//
//    private void callJavaScriptFunction(String Function) {
//        mWebView.post(() -> mWebView.evaluateJavascript(Function, null));
//    }
//
//    public void onPeerConnected() {
//        IsPeerConnected = true;
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        myRef.child(username).setValue(null);
//        mWebView.loadUrl("about:blank");
//        super.onBackPressed();
//    }
//
//
//    protected void onDestroy() {
//        super.onDestroy();
//        mSocket.disconnect();
//        mSocket.off("Call", onNewMessage);
//    }
//}