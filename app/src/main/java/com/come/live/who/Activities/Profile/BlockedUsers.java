package com.come.live.who.Activities.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.come.live.R;
import com.come.live.who.Modules.Users;
import com.come.live.who.Utils.FavoritAdapter;

import java.util.ArrayList;

public class BlockedUsers extends AppCompatActivity {
    private ArrayList<Users> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);
        initRecycler();
    }

    private void initRecycler() {
        FavoritAdapter adapter = new FavoritAdapter(users);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

    }
}