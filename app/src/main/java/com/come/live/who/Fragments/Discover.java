package com.come.live.who.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.come.live.who.Activities.Payments;
import com.come.live.who.Global;
import com.come.live.who.Modules.Countries;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Users;
import com.come.live.R;
import com.come.live.who.Retrofit.Retrofit;
import com.come.live.who.Utils.DiscoverAdapter;
import com.come.live.who.Utils.DiscoverCategories;
import com.come.live.who.Activities.StoryMainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Discover extends Fragment {
    private static final String TAG = "Discover";
    ArrayList<Users> users = new ArrayList<>();
    ArrayList<Countries> Countries = new ArrayList<>();
    DiscoverAdapter adapter;
    DiscoverCategories categoryAdapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.discover_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        GET(null);
        Global.GetUserData(Global.GetUserID(requireActivity()), new Callback<Users>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Users> call, @NonNull Response<Users> response) {
                if (response.body() != null)
                    ((TextView) requireActivity().findViewById(R.id.coinNumber)).setText(String.valueOf(response.body().getCoins()));
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Users> call, @NonNull Throwable t) {

            }
        });
        view.findViewById(R.id.makePayments).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Payments.class)));
        adapter = new DiscoverAdapter(users, position -> {
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            Global.GetStories(users.get(position).getIdUser(), new Callback<ArrayList<Stories>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Stories>> call, @NonNull Response<ArrayList<Stories>> response) {
                    if (response.body() != null) {
                        Global.stories.clear();
                        Global.stories.addAll(response.body());
                        Intent i = new Intent(getActivity(), StoryMainActivity.class);
                        requireActivity().startActivity(i);
                    }
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Stories>> call, @NonNull Throwable t) {
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);


        categoryAdapter = new DiscoverCategories(Countries, position -> GET(Countries.get(position).getCountry()));
        RecyclerView categoryRecyclerView = view.findViewById(R.id.filterRecycler);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
        GetCountries();

    }

    private void GET(String country) {
        users.clear();
        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<Users>> call = service.GetUsers(country, Global.GetUserID(getActivity()));
        call.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<Users>> call, @NonNull Response<ArrayList<Users>> response) {
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
            public void onFailure(@NonNull Call<ArrayList<Users>> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                ShowErorr();
            }
        });

    }

    void GetCountries() {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        Call<ArrayList<Countries>> call = service.GetAllCountries(Global.GetUserID(getActivity()));
        call.enqueue(new Callback<ArrayList<Countries>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Countries>> call, @NonNull Response<ArrayList<Countries>> response) {
                if (response.body() != null) {
                    Countries.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Countries>> call, @NonNull Throwable t) {

            }
        });

    }

    private void ShowErorr() {
    }


}
