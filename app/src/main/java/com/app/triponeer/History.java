package com.app.triponeer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class History extends Fragment {
    RecyclerView rvHistory;
    HistoryAdapter historyAdapter;
    ArrayList<Trip> historyTrips;
    SwipeRefreshLayout swipeRefreshLayoutHistory;
    ArrayList<String> notes;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;
    Trip trip;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        swipeRefreshLayoutHistory = view.findViewById(R.id.swipeRefreshLayoutHistory);
        swipeRefreshLayoutHistory.setColorSchemeResources(R.color.colorPrimary);
        historyTrips = new ArrayList<>();
        notes = new ArrayList<>();
        user = mAuth.getCurrentUser();
        trip = new Trip();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        getData();

        swipeRefreshLayoutHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayoutHistory.setRefreshing(false);
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
        if (historyAdapter != null) {
            historyAdapter.setData(historyTrips);
        }
    }

    private void getData() {
        historyTrips.clear();
        reference.child(user.getUid()).child("trips").child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        historyTrips.add(trip);
                    }
                    historyAdapter = new HistoryAdapter();
                    if (!historyTrips.isEmpty()) {
                        rvHistory.setAdapter(historyAdapter);
                        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
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