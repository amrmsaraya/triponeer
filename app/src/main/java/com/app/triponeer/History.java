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

import java.util.ArrayList;


public class History extends Fragment {
    RecyclerView rvHistory;
    HistoryAdapter historyAdapter;
    ArrayList<Trip> historyTrips;
    SwipeRefreshLayout swipeRefreshLayoutHistory;
    ArrayList<String> notes;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                        .replace(R.id.fragment_container, History.this).commit();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        View view = inflater.inflate(R.layout.history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        swipeRefreshLayoutHistory = view.findViewById(R.id.swipeRefreshLayoutHistory);
        swipeRefreshLayoutHistory.setColorSchemeResources(R.color.colorPrimary);
        historyTrips = new ArrayList<>();
        notes = new ArrayList<>();

        swipeRefreshLayoutHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutHistory.setRefreshing(false);
            }
        });

        Trip trip = new Trip();
        trip.setName("Mansoura");
        trip.setDescription("Going to ITI using Superjet");
        trip.setSource("ITI Suez", "address", 0, 0);
        trip.setDestination("ITI Mansoura", "address", 0, 0);
        trip.setDate("8-1-2020");
        trip.setTime("9:00 AM");
        trip.setStatus("Completed");
        trip.setDistance(150);
        trip.setType("Round");
        notes.add("Buy Coffee");
        notes.add("Call manager");
        notes.add("Check Email");
        trip.setNotes(notes);
        Trip trip1 = new Trip();
        trip1.setName("Mansoura");
        trip1.setDescription("Going to ITI using Superjet");
        trip1.setSource("ITI Ismalia", "address", 0, 0);
        trip1.setDestination("Pyramids", "address", 0, 0);
        trip1.setDate("8-1-2020");
        trip1.setTime("9:00 AM");
        trip1.setStatus("Cancelled");
        trip1.setDistance(67);
        trip1.setType("One Way");
        historyTrips.add(trip);
        historyTrips.add(trip1);
        trip1.setNotes(notes);

        historyAdapter = new HistoryAdapter();
        rvHistory.setAdapter(historyAdapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        setDataSource();

        return view;
    }

    private void setDataSource() {
        if (historyAdapter != null) {
            historyAdapter.setData(historyTrips);
        }
    }

}