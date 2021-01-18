package com.app.triponeer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


public class Upcoming extends Fragment {
    RecyclerView rvUpcoming;
    UpcomingAdapter upcomingAdapter;
    ArrayList<Trip> upcomingTrips;
    Button btnAdd;
    SwipeRefreshLayout swipeRefreshLayoutUpcoming;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.upcoming, container, false);
        btnAdd = view.findViewById(R.id.btnAddTrip);
        swipeRefreshLayoutUpcoming = view.findViewById(R.id.swipeRefreshLayoutUpcoming);
        swipeRefreshLayoutUpcoming.setColorSchemeResources(R.color.colorPrimary);
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
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_left)
                        .replace(R.id.fragment_container, new Upcoming()).commit();
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

}