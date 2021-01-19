package com.app.triponeer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.ArrayList;
import java.util.Calendar;


public class AddTrip extends Fragment implements AddNote.OnSaveNote, RepeatDays.OnApplyRepeat {
    EditText edtTextTripName;
    EditText edtTextTripDescription;
    EditText edtTextSource;
    EditText edtTextDestination;
    EditText edtTextNote;
    TextView txtViewDate;
    TextView txtViewTime;
    TextView txtViewError;
    Spinner spinnerRepeat;
    Spinner spinnerType;
    TextView txtViewNotesEmpty;
    ImageButton btnAddNote;
    ImageButton btnRepeatDays;
    RecyclerView recyclerView;
    Button btnSaveAddTrip;
    ProgressBar progressBarAddTrip;
    Trip trip;
    Trip modifiedTrip;
    String job;
    String type;
    String date;
    String time;
    int selected_year;
    int selected_month;
    int selected_day;
    int selected_hour;
    int selected_minute;

    LatLng sourceLatLng;
    LatLng destLatLng;
    String sourceAddress;
    String destAddress;
    String sourceName;
    String destName;

    ArrayList<String> notes;
    NotesAdapter notesAdapter;
    ArrayList<String> repeatedDays;
    String repeat;

    public AddTrip(String job) {
        this.trip = new Trip();
        this.job = job;
    }

    public AddTrip(Trip trip, String job) {
        this.trip = trip;
        this.modifiedTrip = trip;
        this.job = job;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_trip, container, false);
        initComponent(view);
        if (job.equals("edit") && trip != null) {
            edtTextTripName.setText(trip.getName());
            edtTextTripDescription.setText(trip.getDescription());
            edtTextSource.setText(trip.getSourceName());
            edtTextDestination.setText(trip.getDestinationName());
            date = trip.getDate();
            time = trip.getTime();
            selected_year = trip.getYear();
            selected_month = trip.getMonth();
            selected_day = trip.getDay();
            selected_hour = trip.getHour();
            selected_minute = trip.getMinute();
            sourceLatLng = new LatLng(trip.getSourceLat(), trip.getSourceLong());
            destLatLng = new LatLng(trip.getDestLat(), trip.getDestLong());
            notes.addAll(trip.getNotes());
            notesAdapter.notifyDataSetChanged();
            txtViewDate.setText(date);
            txtViewTime.setText(time);
            repeat = trip.getRepeatPattern();
            repeatedDays = trip.getRepeatDays();
            if (notes.size() != 0) {
                txtViewNotesEmpty.setText("");
            }
        }

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNote addNote = new AddNote();
                addNote.setTargetFragment(AddTrip.this, 1);
                addNote.show(getFragmentManager(), "AddNote");
            }
        });


        btnRepeatDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatDays repeatDays = new RepeatDays(repeatedDays);
                repeatDays.setTargetFragment(AddTrip.this, 2);
                repeatDays.show(getFragmentManager(), "RepeatDays");
            }
        });


        edtTextSource.setFocusable(false);
        edtTextDestination.setFocusable(false);
        edtTextSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken("pk.eyJ1IjoiaXRpcHJvamVjdDIwMjEiLCJhIjoiY2tqdWhodjcyMWhpYTJ4cWhheW4zdG84OSJ9.aELz12r7NTIjzOxXQOSFVw")
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#FFFFFF"))
                                .limit(7)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                PlaceAutocomplete.clearRecentHistory(getContext());
                startActivityForResult(intent, 1);
            }
        });

        edtTextDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken("pk.eyJ1IjoiaXRpcHJvamVjdDIwMjEiLCJhIjoiY2tqdWhodjcyMWhpYTJ4cWhheW4zdG84OSJ9.aELz12r7NTIjzOxXQOSFVw")
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#FFFFFF"))
                                .limit(7)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                PlaceAutocomplete.clearRecentHistory(getContext());
                startActivityForResult(intent, 2);
            }
        });

        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeat = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selected_hour = hourOfDay;
                                selected_minute = minute;
                                setTxtViewTime(hourOfDay, minute);
                            }
                        }, selected_hour, selected_minute, false);
                timePickerDialog.show();
            }
        });

        txtViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selected_year = year;
                                selected_month = monthOfYear + 1;
                                selected_day = dayOfMonth;
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                txtViewDate.setText(date);
                            }
                        }, selected_year, selected_month, selected_day);
                datePickerDialog.show();
            }
        });


        btnSaveAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTextTripName.getText().toString();
                String description = edtTextTripDescription.getText().toString();
                String source = edtTextSource.getText().toString();
                String destination = edtTextDestination.getText().toString();

                if (name.isEmpty() || description.isEmpty() || source.isEmpty() || destination.isEmpty()) {
                    txtViewError.setText("* Please fill all fields");
                } else {
                    if (job.equals("new")) {

                        trip.setName(name);
                        trip.setDescription(description);
                        trip.setSource(sourceName, sourceAddress, sourceLatLng.getLatitude(), sourceLatLng.getLongitude());
                        trip.setDestination(destName, destAddress, destLatLng.getLatitude(), destLatLng.getLongitude());
                        trip.setDate(date);
                        trip.setDate(selected_day, selected_month, selected_year);
                        trip.setTime(time);
                        trip.setTime(selected_hour, selected_minute);
                        trip.setRepeatPattern(repeat.toLowerCase());
                        trip.setRepeatDays(repeatedDays);
                        trip.setNotes(notes);
                        trip.setStatus("upcoming");
                        trip.setType(type);
                        trip.setDistance(sourceLatLng.distanceTo(destLatLng) / 1000);

                        progressBarAddTrip.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).child("trips").child("upcoming").child(trip.getName()).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBarAddTrip.setVisibility(View.GONE);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                        .replace(R.id.fragment_container, new Upcoming()).commit();
                            }
                        });

                    } else if (job.equals("edit")) {

                        if (modifiedTrip == null) {
                            modifiedTrip = new Trip();
                        }

                        modifiedTrip.setName(name);
                        modifiedTrip.setDescription(description);
                        modifiedTrip.setSource(sourceName, sourceAddress, sourceLatLng.getLatitude(), sourceLatLng.getLongitude());
                        modifiedTrip.setDestination(destName, destAddress, destLatLng.getLatitude(), destLatLng.getLongitude());
                        modifiedTrip.setDate(date);
                        modifiedTrip.setDate(selected_day, selected_month, selected_year);
                        modifiedTrip.setTime(time);
                        modifiedTrip.setTime(selected_hour, selected_minute);
                        modifiedTrip.setRepeatPattern(repeat.toLowerCase());
                        modifiedTrip.setRepeatDays(repeatedDays);
                        modifiedTrip.setNotes(notes);
                        modifiedTrip.setStatus("upcoming");
                        modifiedTrip.setType(type);
                        modifiedTrip.setDistance(sourceLatLng.distanceTo(destLatLng) / 1000);

                        progressBarAddTrip.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).child("trips").child("upcoming").child(modifiedTrip.getName()).setValue(modifiedTrip).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBarAddTrip.setVisibility(View.GONE);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                        .replace(R.id.fragment_container, new Upcoming()).commit();
                            }
                        });
                    }

                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                        .replace(R.id.fragment_container, new Upcoming()).commit();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == 1) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            sourceLatLng = new LatLng(((Point) feature.geometry()).latitude(),
                    ((Point) feature.geometry()).longitude());
            sourceAddress = feature.placeName();
            sourceName = feature.text();
            edtTextSource.setText(sourceName);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == 2) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            destLatLng = new LatLng(((Point) feature.geometry()).latitude(),
                    ((Point) feature.geometry()).longitude());
            destAddress = feature.placeName();
            destName = feature.text();
            edtTextDestination.setText(destName);
        }
    }

    private void initComponent(View view) {
        edtTextTripName = view.findViewById(R.id.edtTextTripName);
        edtTextTripDescription = view.findViewById(R.id.edtTextTripDescription);
        edtTextSource = view.findViewById(R.id.edtTextSource);
        edtTextDestination = view.findViewById(R.id.edtTextDestination);
        txtViewDate = view.findViewById(R.id.txtViewDate);
        txtViewTime = view.findViewById(R.id.txtViewTime);
        spinnerRepeat = view.findViewById(R.id.spinnerRepeat);
        spinnerType = view.findViewById(R.id.spinnerType);
        btnAddNote = view.findViewById(R.id.btnAddNote);
        edtTextNote = view.findViewById(R.id.edtTextNote);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnRepeatDays = view.findViewById(R.id.btnRepeatDays);
        txtViewError = view.findViewById(R.id.txtViewError);
        btnSaveAddTrip = view.findViewById(R.id.btnSaveAddTrip);
        txtViewNotesEmpty = view.findViewById(R.id.txtViewNotesEmpty);
        progressBarAddTrip = view.findViewById(R.id.progressBarAddTrip);
        repeatedDays = new ArrayList<String>();
        repeat = "none";
        notes = new ArrayList<String>();
        notesAdapter = new NotesAdapter(getContext(), notes);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        txtViewNotesEmpty.setText("Press \"+\" to add notes");

        if (job.equals("new")) {
            // View current time and date
            Calendar c = Calendar.getInstance();
            selected_year = c.get(Calendar.YEAR);
            selected_month = c.get(Calendar.MONTH);
            selected_day = c.get(Calendar.DAY_OF_MONTH);
            selected_hour = c.get(Calendar.HOUR_OF_DAY);
            selected_minute = c.get(Calendar.MINUTE);
            date = selected_day + "-" + (selected_month + 1) + "-" + selected_year;
            txtViewDate.setText(date);
            setTxtViewTime(selected_hour, selected_minute);
        }
    }


    void setTxtViewTime(int hour, int minute) {
        if (hour == 0) {
            if (minute >= 10) {
                time = "12:" + minute + " AM";
                txtViewTime.setText(time);
            } else {
                time = "12:0" + minute + " AM";
                txtViewTime.setText(time);
            }
        } else if (hour < 12 && hour > 0) {
            if (minute >= 10) {
                time = hour + ":" + minute + " AM";
                txtViewTime.setText(time);
            } else {
                time = hour + ":0" + minute + " AM";
                txtViewTime.setText(time);
            }
        } else if (hour == 12) {
            if (minute >= 10) {
                time = "12:" + minute + " PM";
                txtViewTime.setText(time);
            } else {
                time = "12:0" + minute + " PM";
                txtViewTime.setText(time);
            }
        } else if (hour > 12) {
            if (minute >= 10) {
                time = hour - 12 + ":" + minute + " PM";
                txtViewTime.setText(time);
            } else {
                time = hour - 12 + ":0" + minute + " PM";
                txtViewTime.setText(time);
            }
        }
    }

    @Override
    public void sendInput(String input) {
        notes.add(input);
        notesAdapter.notifyDataSetChanged();
        txtViewNotesEmpty.setText("");
    }

    @Override
    public void sendInput(ArrayList<String> input) {
        repeatedDays = input;
    }
}