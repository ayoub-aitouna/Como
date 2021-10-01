package com.come.live.who.Activities.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.come.live.R;
import com.come.live.who.ChatRoom.ChatRoom;
import com.come.live.who.Fragments.Dialog.ReportDialog;
import com.come.live.who.Global;
import com.come.live.who.Modules.Posts;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;

import com.come.live.who.Utils.PostsAdapter;
import com.come.live.who.Utils.Stories.StoriesRecyclerview;
import com.come.live.who.VideoCall.videoCall;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity {
    public static String TAG = "USERPROFILE";
    private PostsAdapter adapter;
    ImageView img;
    TextView name, country, numFollowers, about;
    Users users;
    ArrayList<Posts> Posts = new ArrayList<>();
    StoriesRecyclerview Stories_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (getIntent().getIntExtra("userID", 0) == Global.User.getIdUser()) {
            startActivity(new Intent(this, Edit.class));
            finish();
        }
        LinkUI();
        GETNumberOfFlowers();
        GetData();
        findViewById(R.id.goback).setOnClickListener(v -> finish());
    }

    private void GetStoriesData(int id) {
        Global.stories.clear();
        Global.GetStories(id, new Callback<ArrayList<Stories>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Stories>> call, @NonNull Response<ArrayList<Stories>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    findViewById(R.id.noStories).setVisibility(View.GONE);
                    Global.stories.addAll(response.body());
                    Collections.reverse(Global.stories);
                    Stories_adapter.notifyDataSetChanged();
                } else {
                    findViewById(R.id.noStories).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                findViewById(R.id.noStories).setVisibility(View.VISIBLE);

            }
        });
    }

    private void CheckUserFriend() {
        Global.CheckFavList(users.getIdUser(), TAG, this, new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                UpdateUI(response.code() == 200);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {

            }
        });
    }

    private void LinkUI() {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        country = findViewById(R.id.country);
        about = findViewById(R.id.about);
    }

    private void GetData() {
        Users user = new Users();
        ProgressBar progressBar = findViewById(R.id.progress);
        LinearLayout container = findViewById(R.id.Container);
        container.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        user.setIdUser(getIntent().getIntExtra("userID", 0));
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 403 || response.body() == null) {
                    ShowErorr();
                } else {
                    users = response.body();
                    Actions(response.body());
                    CheckUserFriend();
                    AsingData();
                    initPosts();
                    GetStoriesData(getIntent().getIntExtra("userID", 0));
                    GetPosts();

                }
                progressBar.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                ShowErorr();
                progressBar.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
            }
        });
    }

    private void GETNumberOfFlowers() {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        Call<String> call = service.NumberOfFlowers(getIntent().getIntExtra("userID", 0));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "onResponse: " + response.body());
                ((TextView) findViewById(R.id.FollowersNumber)).setText(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                numFollowers.setText(String.valueOf(0));
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void AsingData() {
        name.setText(users.getName());
        country.setText(users.getCountry());
        about.setText(users.getAbout());
        try {
            Picasso.get().load(users.getProfileImage()).into(img);
        } catch (Exception e) {
            Log.d(TAG, "AsingData: " + e.getMessage());
        }

    }


    private void ShowErorr() {
    }


    private void initPosts() {
        adapter = new PostsAdapter(Posts, position -> {
            Intent i = new Intent(UserProfile.this, ViewPost.class);
            i.putExtra("Url", Posts.get(position).getContentImg());
            i.putExtra("isOther", true);
            i.putExtra("img", users.getProfileImage());
            i.putExtra("name", users.getName());
            i.putExtra("id", users.getIdUser());
            startActivity(i);
        });
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        //init Stories
        Stories_adapter = new StoriesRecyclerview(Global.stories);
        RecyclerView stories = findViewById(R.id.Stories);
        stories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        stories.setAdapter(Stories_adapter);

    }

    private void Actions(Users body) {
        findViewById(R.id.chat).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ChatRoom.class);
            i.putExtra("SendMode", 0);
            i.putExtra("ReceiverId", users.getIdUser());
            startActivity(i);
        });
        findViewById(R.id.StartCall).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), videoCall.class);
            i.putExtra("isOutGoing", true);
            i.putExtra("img", body.getProfileImage());
            i.putExtra("name", body.getName());
            i.putExtra("ReceiverId", body.getIdUser());
            i.putExtra("id", Global.GetUserID(UserProfile.this));
            startActivity(i);
        });
        findViewById(R.id.report).setOnClickListener(v -> ShowDialog());
        findViewById(R.id.fav).setOnClickListener(v ->
                Global.AddToFavList(Global.GetUserID(this), users.getIdUser(), TAG, new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        UpdateUI(response.code() == 200);
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                        Toast.makeText(UserProfile.this, "Error Please Try Again ...", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void UpdateUI(boolean b) {
        if (b) {
            findViewById(R.id.fav).setVisibility(View.GONE);
        } else {
            findViewById(R.id.fav).setVisibility(View.VISIBLE);
        }
    }

    private void ShowDialog() {
        ReportDialog reportDialog = new ReportDialog(this, R.style.DialogSlideAnim);
        reportDialog.getWindow().setGravity(Gravity.BOTTOM);
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportDialog.show();
    }

    private void GetPosts() {
        Posts.clear();
        Global.GetPosts("GetPosts", users.getIdUser(), new Callback<ArrayList<Posts>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Posts>> call, @NonNull Response<ArrayList<Posts>> response) {
                Log.d("POSTS", "onResponse: " + response.body());
                if (response.body() != null && response.body().size() > 0) {
                    findViewById(R.id.noPosts).setVisibility(View.GONE);
                    Posts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    findViewById(R.id.noPosts).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                findViewById(R.id.noPosts).setVisibility(View.VISIBLE);
            }
        });
    }
}