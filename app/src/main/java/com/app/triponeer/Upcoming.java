package com.app.triponeer;

import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class Upcoming extends Fragment {
    RecyclerView rvUpcoming;
    UpcomingAdapter upcomingAdapter;
    ArrayList<Trip> upcomingTrips;
    Trip trip;
    Button btnAdd;
    SwipeRefreshLayout swipeRefreshLayoutUpcoming;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    NormalUser normalUser;
    SocialMediaUser socialMediaUser;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;

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
        user = mAuth.getCurrentUser();
        trip = new Trip();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        saveDataToNormalUserClass();
        setBtnAddAction();

        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        upcomingTrips = new ArrayList<>();

        getData();

        swipeRefreshLayoutUpcoming.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayoutUpcoming.setRefreshing(false);
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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

    private void getData() {
        upcomingTrips.clear();
        reference.child(user.getUid()).child("trips").child("upcoming").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        upcomingTrips.add(trip);
                    }
                    upcomingAdapter = new UpcomingAdapter(getContext());
                    if (!upcomingTrips.isEmpty()) {
                        rvUpcoming.setAdapter(upcomingAdapter);
                        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
                        System.out.println(trip);
                    }
                }
                setDataSource();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}