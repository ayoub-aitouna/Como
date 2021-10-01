package com.come.live.who.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.R;
import com.come.live.who.Global;
import com.come.live.who.Modules.ChatModule;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.Messages_adapter;
import com.come.live.who.Utils.Users_Message_adapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Message extends Fragment {
    private static final String TAG = "Messages";
    ArrayList<ChatModule> data = new ArrayList<>();
    ArrayList<Users> users = new ArrayList<>();
    private Users_Message_adapter User_adapter;
    private Messages_adapter Message_adapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.message_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initUsers(view);
        initMessages(view);

    }


    private void initUsers(View view) {
        User_adapter = new Users_Message_adapter(users);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(User_adapter);
        GET();
    }

    private void initMessages(View view) {
        Global.GetUserID(requireActivity());
        Message_adapter = new Messages_adapter(data);
        RecyclerView recyclerView = view.findViewById(R.id.messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(Message_adapter);
        GetData();
    }

    private void GetData() {
        Retrofit.Messages service = Retrofit.getRetrofitInstance().create(Retrofit.Messages.class);
        Log.d(TAG, "GetData: " + Global.GetUserID(requireActivity()));
        retrofit2.Call<ArrayList<ChatModule>> call = service.Messages(Global.GetUserID(requireActivity()));
        call.enqueue(new Callback<ArrayList<ChatModule>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ChatModule>> call, @NonNull Response<ArrayList<ChatModule>> response) {
                if (response.body() != null) {
                    data.addAll(response.body());
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                    Message_adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ChatModule>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void GET() {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<Users>> call = service.matchHistory(Global.GetUserID(requireActivity()));
        call.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<Users>> call, @NonNull Response<ArrayList<Users>> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 200 && response.body() != null) {
                    users.addAll(response.body());
                    User_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Users>> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
            }
        });
    }

}
