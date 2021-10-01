package com.come.live.who.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.FavoritAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Favorite extends Fragment {
    private static final String TAG = "Favorite";
    private ArrayList<Users> users = new ArrayList<>();
    FavoritAdapter adapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.favorite_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        GET();
        adapter = new FavoritAdapter(users);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);
    }


    private void GET() {
        Users user = new Users();
        user.setIdUser(Global.GetUserID(getActivity()));
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<Users>> call = service.GetUserFriends(user);
        call.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Users>> call, Response<ArrayList<Users>> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 403 || response.body() == null) {
                    ShowErorr();
                } else {
                    users.addAll(response.body());
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Users>> call, Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                ShowErorr();
            }
        });
    }

    private void ShowErorr() {
    }

}
