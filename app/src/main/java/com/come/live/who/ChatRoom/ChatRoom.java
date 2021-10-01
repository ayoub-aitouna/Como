package com.come.live.who.ChatRoom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.come.live.R;
import com.come.live.who.Activities.Profile.UserProfile;
import com.come.live.who.Fragments.Dialog.GiftDialog;
import com.come.live.who.Global;
import com.come.live.who.Interfaces.MessageCallback;
import com.come.live.who.Modules.ChatContent;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.Messages_Content_adapter;
import com.come.live.who.VideoCall.videoCall;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.come.live.who.Global.getImageUri;

public class ChatRoom extends AppCompatActivity {
    //
    private static final String TAG = "ChatRoom";
    private final ArrayList<ChatContent> Data = new ArrayList<>();
    boolean IsLoading = false;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESULT_LOAD_IMG = 5421;
    private String FileUri;
    private String AudioFilePath;
    private EditText input;
    private RecyclerView Messages_recyclerView;
    private Messages_Content_adapter adapter;
    private final Retrofit.Messages service = Retrofit.getRetrofitInstance().create(Retrofit.Messages.class);
    private Socket mSocket;
    private MediaRecorder recorder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        try {
            mSocket = IO.socket(Global.URL);
        } catch (URISyntaxException ignored) {
        }
        Global.Room = getIntent().getStringExtra("Hash_id");
        SwitchView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
            }
        SwitchLoadedState(0);
        input = findViewById(R.id.Text_input);
        GetData();
        Actions();
        SetRead();
        HandelRecord();
        initAdapter();
        GetMessages();
        SocketConnect(content -> {
            if (content.getSenderId() != Global.GetUserID(this)) {
                sent(content);
            }
        });
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        AudioFilePath = getExternalCacheDir().getAbsolutePath();
        AudioFilePath += "/MessageAudion" + UUID.randomUUID() + ".3gp";
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(AudioFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException ignored) {
        }
        recorder.start();
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
        } catch (Exception ignored) {
        }
        recorder = null;
    }

    private void HandelRecord() {
        RecordView recordView = findViewById(R.id.record_view);
        RecordButton recordButton = findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                FileUri = null;
                SwitchRecordView(false);
                onRecord(true);
            }

            @Override
            public void onCancel() {
                onRecord(false);
                SwitchRecordView(true);

            }

            /**
             *Stop Recording..
             *limitReached to determine if the Record was finished when time limit reached.
             */
            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                onRecord(false);
                uploadAudio(AudioFilePath, content -> SendAudioMessage(content.getContentImage()));
                SwitchRecordView(true);
            }

            @Override
            public void onLessThanSecond() {
                onRecord(false);
                SwitchRecordView(true);
            }
        });
        recordView.setRecordPermissionHandler(() -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
            if (recordPermissionAvailable) {
                return true;
            }

            ActivityCompat.
                    requestPermissions(ChatRoom.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            0);
            return false;

        });
        recordView.setSmallMicColor(Color.parseColor("#c2185b"));
        recordView.setSlideToCancelText("Cancel Recording");
        recordView.setSoundEnabled(false);
        recordView.setLessThanSecondAllowed(false);
        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);
        recordView.setSlideToCancelTextColor(Color.parseColor("#ff0000"));
        recordView.setSlideToCancelArrowColor(Color.parseColor("#ff0000"));
        recordView.setCounterTimeColor(Color.parseColor("#ff0000"));
        recordView.setShimmerEffectEnabled(true);
        recordView.setTimeLimit(30000);//30 sec
        recordView.setTrashIconColor(Color.parseColor("#fff000"));
        recordView.setRecordButtonGrowingAnimationEnabled(true);
    }

    private void SendAudioMessage(String AudioUri) {
        JSONObject data = new JSONObject();
        SimpleDateFormat current_data_time = new SimpleDateFormat("HHmm", Locale.getDefault());
        String Time = current_data_time.format(new Date());
        ChatContent chatContent = new ChatContent();
        chatContent.setSendTime(Time);
        chatContent.setContentAudio(AudioUri);
        chatContent.setContentImage(null);
        chatContent.setHash_id(Global.Room);
        chatContent.setReceiverId(getIntent().getIntExtra("ReceiverId", 0));
        chatContent.setSenderId(Global.GetUserID(this));
        try {
            data.put("contentAudio", AudioUri);
            data.put("SenderId", Global.GetUserID(this));
            data.put("ReceiverId", chatContent.getReceiverId());
            data.put("contentImage", "");
            data.put("contentText", "");
            data.put("Hash_id", Global.Room);
            data.put("SocketConnect", Global.User.getName());
        } catch (Exception ignored) {
        }

        if (getIntent().getIntExtra("SendMode", -1) == 0) {
            Call<String> call = service.SendMessages(chatContent);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.code() == 200) {
                        mSocket.emit("Send", data);

                    } else {
                        Toast.makeText(ChatRoom.this, " Error please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: + " + t.getMessage());
                    Toast.makeText(ChatRoom.this, "Error please try again...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mSocket.emit("Send", data);
        }
        sent(chatContent);
    }

    private void SwitchRecordView(boolean b) {
        if (b) {
            findViewById(R.id.record_view).setVisibility(View.GONE);
            findViewById(R.id.input).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.record_view).setVisibility(View.VISIBLE);
            findViewById(R.id.input).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        Global.Room = null;
        try {
            adapter.stopPlayer();
        } catch (Exception ignored) {

        }
        super.onDestroy();
    }

    private void SetRead() {
        Retrofit.Messages service = Retrofit.getRetrofitInstance().create(Retrofit.Messages.class);
        retrofit2.Call<String> call = service.MessagesRead(Global.Room);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    void LoadImageDialog() {
        final String[] options = {
                "Take a picture", "from gallery", "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select");
        builder.setItems(options, (dialog, which) -> {
            if (options[0].equals(options[which])) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else if (options[1].equals(options[which])) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            } else if (options[2].equals(options[which])) {
                builder.show().dismiss();
            }
        });
        builder.show();
    }

    private void initAdapter() {
        Global.GetUserID(this);
        adapter = new Messages_Content_adapter(Data);
        Messages_recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        Messages_recyclerView.setLayoutManager(layoutManager);
        Messages_recyclerView.setAdapter(adapter);
    }

    private void GetMessages() {
        retrofit2.Call<ArrayList<ChatContent>> call = service.MessagesContent(getIntent().getStringExtra("Hash_id")
                , Global.GetUserID(this)
                , getIntent().getIntExtra("ReceiverId", -1));
        call.enqueue(new Callback<ArrayList<ChatContent>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ChatContent>> call, @NonNull Response<ArrayList<ChatContent>> response) {
                assert response.body() != null;
                Data.addAll(response.body());
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ChatContent>> call, @NonNull Throwable t) {
            }
        });

    }

    private void Actions() {
        findViewById(R.id.goback).setOnClickListener(v -> finish());
        findViewById(R.id.gift).setOnClickListener(v -> ShowDialog());
        findViewById(R.id.send).setOnClickListener(v -> SendMessage(0, input.getText().toString()));
        findViewById(R.id.loadImg).setOnClickListener(v -> LoadImageDialog());
        findViewById(R.id.unLoadImg).setOnClickListener(v -> UnLoadImg());
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SwitchView(input.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void StartProfileActivity(int UserId) {
        Intent i = new Intent(getApplicationContext(), UserProfile.class);
        i.putExtra("userID", UserId);
        startActivity(i);
    }

    private void UnLoadImg() {
        SwitchLoadedState(0);
        IsLoading = false;
        SwitchView(true);

    }

    private void uploadAudio(String filePath, MessageCallback callback) {
        IsLoading = true;
        File file = new File(filePath);
        retrofit2.Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "Audio");
        Retrofit.Messages uploadApis = retrofit.create(Retrofit.Messages.class);
        Call<ChatContent> call = uploadApis.uploadImage(parts, Type);
        call.enqueue(new Callback<ChatContent>() {
            @Override
            public void onResponse(@NonNull Call<ChatContent> call, @NonNull Response<ChatContent> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                if (response.body() != null) {
                    callback.onMessage(response.body());
                } else {
                    SwitchLoadedState(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatContent> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                SwitchLoadedState(0);
            }
        });

    }

    private void LoadImg(String filePath) {
        IsLoading = true;
        File file = new File(filePath);
        retrofit2.Retrofit retrofit = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("fileName", file.getName(), requestBody);
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "Img");
        Retrofit.Messages uploadApis = retrofit.create(Retrofit.Messages.class);
        Call<ChatContent> call = uploadApis.uploadImage(parts, Type);
        call.enqueue(new Callback<ChatContent>() {
            @Override
            public void onResponse(@NonNull Call<ChatContent> call, @NonNull Response<ChatContent> response) {
                Log.d("TAG_upload", "onResponse: " + response.body());
                IsLoading = false;
                if (response.body() != null) {
                    FileUri = response.body().getContentImage();
                    SwitchLoadedState(2);
                    SwitchView(false);
                } else {
                    FileUri = null;
                    SwitchLoadedState(0);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ChatContent> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                SwitchLoadedState(0);
            }
        });

    }

    private void SendMessage(int GiftCoins, String messageText) {
        JSONObject data = new JSONObject();
        SimpleDateFormat current_data_time = new SimpleDateFormat("HHmm", Locale.getDefault());
        String Time = current_data_time.format(new Date());
        ChatContent chatContent = new ChatContent();
        chatContent.setContentText(messageText);
        chatContent.setGiftCoins(GiftCoins);
        chatContent.setSendTime(Time);
        chatContent.setContentImage(FileUri);
        chatContent.setHash_id(Global.Room == null ? "" : Global.Room);
        chatContent.setReceiverId(getIntent().getIntExtra("ReceiverId", 0));
        chatContent.setSenderId(Global.GetUserID(this));
        try {
            data.put("contentImage", FileUri == null ? "" : FileUri);
            data.put("contentText", input.getText().toString());
            data.put("contentAudio", "");
            data.put("giftCoins", GiftCoins);
            data.put("SenderId", Global.GetUserID(this));
            data.put("ReceiverId", chatContent.getReceiverId());
            data.put("Hash_id", Global.Room == null ? "" : Global.Room);
            data.put("Sender_name", Global.User.getName());
        } catch (Exception ignored) {
        }
        Log.d(TAG, "SendMessage: " + Global.Room);
        if (!IsLoading) {
            if (getIntent().getIntExtra("SendMode", -1) == 0) {
                Call<String> call = service.SendMessages(chatContent);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200) {
                            mSocket.emit("Send", data);
                        } else {
                            Toast.makeText(ChatRoom.this, "Res Error please try again...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: + " + t.getMessage());
                        Toast.makeText(ChatRoom.this, "Error please try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mSocket.emit("Send", data);
            }
            sent(chatContent);
        } else {
            Toast.makeText(this, "please wait tell finish uploading image", Toast.LENGTH_SHORT).show();
        }
        FileUri = null;
    }

    private void sent(ChatContent chatContent) {
        Data.add(chatContent);
        adapter.notifyDataSetChanged();
        Messages_recyclerView.smoothScrollToPosition(Data.size() - 1);
        input.setText("");
        SwitchLoadedState(0);
        SwitchView(true);
    }

    private void ShowDialog() {
        GiftDialog reportDialog = new GiftDialog(this, R.style.DialogSlideAnim, Amount -> {
            Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
            retrofit2.Call<String> CallTransaction = service.MakeTransaction(Global.GetUserID(ChatRoom.this),
                    getIntent().getIntExtra("ReceiverId", 0), Amount);
            CallTransaction.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.code() == 200) {
                        FileUri = "";
                        SendMessage(Amount, "");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                }
            });
        });
        reportDialog.getWindow().setGravity(Gravity.BOTTOM);
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportDialog.show();

    }

    private void ShowMenu(Users user) {
        final String[] options = {
                "Delete conversation", "Report And Block", "View Profile", "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select");
        builder.setItems(options, (dialog, which) -> {
            if (options[0].equals(options[which])) {
                DeleteConversation();
            } else if (options[1].equals(options[which])) {
                Report();
            } else if (options[2].equals(options[which])) {
                StartProfileActivity(user.getIdUser());
            } else if (options[3].equals(options[which])) {
                builder.show().dismiss();
            }
        });
        builder.show();
    }

    private void Report() {
    }

    private void DeleteConversation() {
    }

    void SwitchView(boolean isAudio) {
        if (isAudio) {
            findViewById(R.id.record_button).setVisibility(View.VISIBLE);
            findViewById(R.id.send).setVisibility(View.GONE);
        } else {
            findViewById(R.id.record_button).setVisibility(View.GONE);
            findViewById(R.id.send).setVisibility(View.VISIBLE);
        }
    }

    void SwitchLoadedState(int IsImgLoaded) {
        if (IsImgLoaded == 0) {
            findViewById(R.id.loadImg).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.unLoadImg).setVisibility(View.GONE);
        } else if (IsImgLoaded == 1) {
            findViewById(R.id.loadImg).setVisibility(View.GONE);
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            findViewById(R.id.unLoadImg).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loadImg).setVisibility(View.GONE);
            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.unLoadImg).setVisibility(View.VISIBLE);
        }
    }

    private void GetData() {
        Users user = new Users();
        user.setIdUser(getIntent().getIntExtra("ReceiverId", 0));
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() != 403 || response.body() != null) {
                    //Action
                    findViewById(R.id.Profile).setOnClickListener(v -> {
                        assert response.body() != null;
                        StartProfileActivity(response.body().getIdUser());
                    });
                    findViewById(R.id.call).setOnClickListener(v -> {
                        Intent i = new Intent(getApplicationContext(), videoCall.class);
                        i.putExtra("isOutGoing", true);
                        assert response.body() != null;
                        i.putExtra("img", response.body().getProfileImage());
                        i.putExtra("name", response.body().getName());
                        i.putExtra("ReceiverId", response.body().getIdUser());
                        i.putExtra("id", Global.GetUserID(ChatRoom.this));
                        startActivity(i);
                    });


                    findViewById(R.id.menu).setOnClickListener(v -> ShowMenu(response.body()));
                    try {
                        assert response.body() != null;
                        Picasso.get().load(response.body().getProfileImage()).into(((ImageView) findViewById(R.id.img)));
                    } catch (Exception e) {
                        Log.d(TAG, "Picasso: " + e.getMessage());
                    }
                    assert response.body() != null;
                    ((TextView) findViewById(R.id.name)).setText(response.body().getName());
                    if (response.body().getIsAvailable() == 0)
                        findViewById(R.id.isOnline).setVisibility(View.GONE);
                    else
                        findViewById(R.id.isOnline).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(Global.getRealPathFromURI(tempUri, this));
            SwitchLoadedState(1);
            LoadImg(finalFile.getPath());
        }
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), selectedImage);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(Global.getRealPathFromURI(tempUri, this));
                SwitchLoadedState(1);
                LoadImg(finalFile.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }


    void SocketConnect(MessageCallback callback) {
        JSONObject data = new JSONObject();
        int ReceiverId = getIntent().getIntExtra("ReceiverId", 0);
        try {
            data.put("hashId", getIntent().getStringExtra("Hash_id"));
            data.put("ReceiverId", ReceiverId == 0 ? null : ReceiverId);
        } catch (Exception ignored) {
        }
        mSocket.connect();
        mSocket.emit("Connect", data);
        mSocket.on("Send", args -> new Handler(Looper.getMainLooper()).post(() -> {
            JSONObject object = (JSONObject) args[0];
            ChatContent chatContent = new ChatContent();
            try {
                chatContent.setContentImage(object.getString("contentImage"));
                chatContent.setContentAudio(object.getString("contentAudio"));
                chatContent.setContentText(object.getString("contentText"));
                chatContent.setGiftCoins(object.getInt("giftCoins"));
                chatContent.setSenderId((object.getInt("SenderId")));
                chatContent.setReceiverId(object.getInt("ReceiverId"));
                chatContent.setSendTime(object.getString("sendTime"));
            } catch (Exception ignored) {

            }
            callback.onMessage(chatContent);
            Log.d(TAG, "SocketConnect: " + chatContent.getContentAudio());
        }));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] != PERMISSION_GRANTED) {
                finish();
            }
        }
    }


}