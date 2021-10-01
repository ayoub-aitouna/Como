package com.come.live.who.Fragments.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.come.live.R;
import com.come.live.who.Activities.MainActivity;
import com.come.live.who.Modules.CountriesNumberCode;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Singup extends Fragment {
    private static final String TAG = "SING UP";
    View view;
    Spinner spinner, countriesCode;
    ProgressBar progressBar;
    Users users;
    ArrayList<String> names = new ArrayList<>();
    TextView CodeNumber;
    private String SelectedCountry = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.singup_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        progressBar = requireActivity().findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        view.findViewById(R.id.singin).setOnClickListener(v -> Replace());
        view.findViewById(R.id.singup).setOnClickListener(v -> SingUp());
        view.findViewById(R.id.goback).setOnClickListener(v -> GoBack());
        fillSpinner(view);
    }

    private void GoBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void fillSpinner(View view) {
        spinner = (Spinner) view.findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        /***/
        GetCountriesCode();

    }

    private void GetCountriesCode() {
        ArrayList<CountriesNumberCode> countries = new ArrayList<>();
        countriesCode = (Spinner) view.findViewById(R.id.countriesCode);
        CodeNumber = view.findViewById(R.id.code);
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        Call<ArrayList<CountriesNumberCode>> call = service.GetCountriesNumberCode();
        call.enqueue(new Callback<ArrayList<CountriesNumberCode>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CountriesNumberCode>> call, @NonNull Response<ArrayList<CountriesNumberCode>> response) {
                if (response.body() != null) {
                    countries.addAll(response.body());
                    names = GetStringFromCountriesCodeArray(countries);
                    ArrayAdapter adapterCountriesCode;
                    adapterCountriesCode = new ArrayAdapter(requireContext(),
                            android.R.layout.simple_spinner_item, names);
                    adapterCountriesCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    countriesCode.setAdapter(adapterCountriesCode);
                    countriesCode.setSelection(0);
                    countriesCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != -1) {
                                CodeNumber.setText(countries.get(position).getDial_code());
                                SelectedCountry = countries.get(position).getName();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CountriesNumberCode>> call, @NonNull Throwable t) {

            }
        });
    }

    ArrayList<String> GetStringFromCountriesCodeArray(ArrayList<CountriesNumberCode> list) {
        ArrayList<String> names = new ArrayList<>();
        for (CountriesNumberCode item : list) {
            names.add(item.getDial_code() + " - " + item.getName());
        }
        return names;
    }

    private void Replace() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new Singin())
                .addToBackStack("singin").commit();
    }

    void SingUp() {
        progressBar.setVisibility(View.VISIBLE);
        view.setEnabled(false);
        if (fillObject()) {
            Retrofit.Auth service = Retrofit.getRetrofitInstance().create(Retrofit.Auth.class);
            Call<String> call = service.SingUp(users);
            call.enqueue(new Callback<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: " + response.body());
                        SharedPreferences sharedPref = requireActivity().getSharedPreferences("Auth", 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        try {
                            editor.putInt(getString(R.string.User_ID), Integer.parseInt(response.body()));
                            editor.apply();
                            requireActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                            requireActivity().finish();
                        } catch (Exception e) {
                            ShowMessage("Signing up Failed");
                        }
                    } else {
                        ShowMessage("Signing up Failed");
                    }
                    view.setEnabled(true);
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                    view.setEnabled(true);
                }
            });
        } else {
            ShowMessage("Signing up Failed Please check your inputs");
        }
    }

    private void ShowMessage(String message) {
        ((TextView) view.findViewById(R.id.ErrorHolder)).setText(message);
        progressBar.setVisibility(View.GONE);

    }

    private boolean fillObject() {
        users = new Users();
        String EmailOrNum = ((EditText) view.findViewById(R.id.numOrEmail)).getText().toString();
        if (!TextUtils.isEmpty(EmailOrNum) && Patterns.PHONE.matcher(EmailOrNum).matches()) {
            users.setPhoneNumber(CodeNumber + EmailOrNum);
            users.setEmail("");
        } else {
            return false;
        }
        users.setGender(spinner.getSelectedItem().toString());
        String pass = ((EditText) view.findViewById(R.id.pass)).getText().toString();
        if (pass.trim().length() >= 8)
            users.setPassword(pass);
        else
            return false;
        String name = ((EditText) view.findViewById(R.id.name)).getText().toString();
        if (!name.trim().isEmpty())
            users.setName(name);
        else return false;
        users.setCountry(SelectedCountry);
        users.setProfileImage("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png");
        return true;
    }


}
