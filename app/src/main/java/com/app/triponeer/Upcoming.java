package com.app.triponeer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class Upcoming extends Fragment {
    RecyclerView rvUpcoming;
    UpcomingAdapter upcomingAdapter;
    ArrayList<Trip> upcomingTrips;
    Button btnAdd;
    SwipeRefreshLayout swipeRefreshLayoutUpcoming;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    NormalUser normalUser;
    SocialMediaUser socialMediaUser;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.upcoming, container, false);
        btnAdd = view.findViewById(R.id.btnAddTrip);
        swipeRefreshLayoutUpcoming = view.findViewById(R.id.swipeRefreshLayoutUpcoming);
        swipeRefreshLayoutUpcoming.setColorSchemeResources(R.color.colorPrimary);
        normalUser = NormalUser.getInstance();
        socialMediaUser = SocialMediaUser.getInstance();
        saveDataToNormalUserClass();

        setBtnAddAction();

        swipeRefreshLayoutUpcoming.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutUpcoming.setRefreshing(false);
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        upcomingTrips = new ArrayList<>();

        ArrayList<String> notes = new ArrayList<>();
        ArrayList<String> repeatDays = new ArrayList<>();
        Trip trip = new Trip();
        trip.setName("Ismalia");
        trip.setDescription("Going to ITI using Superjet");
        trip.setDate("12-1-2020");
        trip.setTime("6:00 PM");
        trip.setSource("ITI Suez", "address", 0, 0);
        trip.setDestination("Barcelo Cairo Pyramids", "address", 29.999875853836755, 31.176258131389357);
        repeatDays.add("Friday");
        repeatDays.add("Tuesday");
        repeatDays.add("Monday");
        trip.setRepeatDays(repeatDays);
        trip.setType("Round");
        trip.setDistance(70.59);
        notes.add("Buy Coffee");
        notes.add("Call manager");
        notes.add("Check Email");
        trip.setNotes(notes);
        Trip trip1 = new Trip();
        trip1.setName("Mansoura");
        trip1.setDescription("Going to ITI using Superjet");
        trip1.setDate("12-1-2020");
        trip1.setTime("4:45 AM");
        trip1.setSource("Alexandria", "address", 0, 0);
        trip1.setDestination("معهد تكنولوجيا المعلومات", "address", 30.071253966638995, 31.020768397551034);
        trip1.setNotes(notes);
        trip1.setType("One Way");
        trip1.setDistance(10.5);
        upcomingTrips.add(trip);
        upcomingTrips.add(trip1);
        upcomingAdapter = new UpcomingAdapter(getContext());
        rvUpcoming.setAdapter(upcomingAdapter);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        setDataSource();

        return view;
    }

    private void setDataSource() {
        if (upcomingAdapter != null) {
            upcomingAdapter.setData(upcomingTrips);
        }
    }

    void setBtnAddAction() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                        .replace(R.id.fragment_container, new AddTrip("new")).addToBackStack(null).commit();
            }
        });
    }

    public void saveDataToNormalUserClass() {
        saving = getActivity().getSharedPreferences(Login.LOGIN_DATA, 0);
        if (saving.getBoolean(Login.IS_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                Log.i("MainActivity", "SharePref: " + saving.getString(Login.LOGIN_NAME, ""));
                normalUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                normalUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                normalUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
                if (saving.getBoolean(Login.IS_NEW_PICTURE, false)) {
                    edit = saving.edit();
                    edit.putBoolean(Login.IS_NEW_PICTURE, false);
                    edit.apply();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = normalUser.getImageUrl();
                            Bitmap image = downloadImage(url);
                            try {
                                FileOutputStream stream = getContext().openFileOutput(normalUser.getEmail() + ".png", getContext().MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                stream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        } else if (saving.getBoolean(Login.IS_FACEBOOK_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                Log.i("MainActivity", "SharePref: " + saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                socialMediaUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
            }
        } else if (saving.getBoolean(Login.IS_GOOGLE_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                Log.i("MainActivity", "SharePref: " + saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                socialMediaUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
            }
        }
    }

    public Bitmap downloadImage(String url) {
        Bitmap image = null;
        URL urlObj = null;
        HttpsURLConnection connection;
        InputStream inputStream;
        try {
            urlObj = new URL(url);
            connection = (HttpsURLConnection) urlObj.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            image = BitmapFactory.decodeStream(inputStream);
            connection.disconnect();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean isFileExists(String filename) {
        File file = getContext().getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}