package com.app.triponeer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {
    ArrayList<Trip> upcomingTrips;
    RecyclerView recyclerViewNotes;
    NotesAdapter notesAdapter;
    Context context;

    public UpcomingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.upcoming_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUpcomingName.setText(upcomingTrips.get(position).getName());
        holder.tvUpcomingDesc.setText(upcomingTrips.get(position).getDescription());
        holder.tvUpcomingDate.setText(upcomingTrips.get(position).getDate());
        holder.tvUpcomingTime.setText(upcomingTrips.get(position).getTime());
        holder.tvUpcomingSource.setText(upcomingTrips.get(position).getSourceName());
        holder.tvUpcomingDestination.setText(upcomingTrips.get(position).getDestinationName());
        holder.tvUpcomingType.setText(upcomingTrips.get(position).getType());
        holder.tvUpcomingDistance.setText(upcomingTrips.get(position).getDistance() + " km");


        holder.btnUpcomingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.upcoming_menu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.btnMenuEdit:
                                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                        .replace(R.id.fragment_container, new AddTrip(upcomingTrips.get(position), "edit")).addToBackStack(null).commit();
                                return true;
                            case R.id.btnMenuCancel:
                                upcomingTrips.get(position).setStatus("cancelled");
                                // TODO -- Update DataBase
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

        holder.btnUpcomingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((v.getRootView().getContext()));
                View dialogView = LayoutInflater.from(v.getRootView().getContext())
                        .inflate(R.layout.view_notes, null);

                recyclerViewNotes = dialogView.findViewById(R.id.recyclerViewNotes);
                notesAdapter = new NotesAdapter(v.getRootView().getContext(), upcomingTrips.get(position).getNotes());
                recyclerViewNotes.setAdapter(notesAdapter);
                recyclerViewNotes.setLayoutManager(new LinearLayoutManager(v.getRootView().getContext()));

                builder.setView(dialogView);
                builder.setCancelable(true);
                builder.show();
            }
        });

        holder.btnStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("google.navigation:q=" +
                        upcomingTrips.get(position).getDestLat() + ", " +
                        upcomingTrips.get(position).getDestLong());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                v.getContext().startActivity(mapIntent);

                if (!Settings.canDrawOverlays(v.getContext())) {
                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + v.getContext().getPackageName()));
                    v.getContext().startActivity(intent);
                } else {
                    v.getContext().startService(new Intent(v.getContext(), Bubble.class)
                            .putStringArrayListExtra("notes", upcomingTrips.get(position).getNotes()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return upcomingTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUpcomingName, tvUpcomingDesc, tvUpcomingDate, tvUpcomingTime,
                tvUpcomingSource, tvUpcomingDestination, tvUpcomingType, tvUpcomingDistance;
        ConstraintLayout clExpandable;
        CardView cvUpcoming;
        ImageButton btnUpcomingMenu;
        Button btnStartNavigation, btnUpcomingNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpcomingName = itemView.findViewById(R.id.tvUpcomingName);
            tvUpcomingDesc = itemView.findViewById(R.id.tvUpcomingDesc);
            tvUpcomingDate = itemView.findViewById(R.id.tvUpcomingDate);
            tvUpcomingTime = itemView.findViewById(R.id.tvUpcomingTime);
            tvUpcomingSource = itemView.findViewById(R.id.tvUpcomingSource);
            tvUpcomingDestination = itemView.findViewById(R.id.tvUpcomingDestination);
            tvUpcomingType = itemView.findViewById(R.id.tvUpcomingType);
            tvUpcomingDistance = itemView.findViewById(R.id.tvUpcomingDistance);
            clExpandable = itemView.findViewById(R.id.expandableHistory);
            cvUpcoming = itemView.findViewById(R.id.cvUpcoming);
            btnUpcomingMenu = itemView.findViewById(R.id.btnUpcomingMenu);
            btnStartNavigation = itemView.findViewById(R.id.btnStartNavigation);
            btnUpcomingNote = itemView.findViewById(R.id.btnUpcomingNote);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clExpandable.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cvUpcoming, new AutoTransition());
                        clExpandable.setVisibility(View.VISIBLE);
                    } else {
                        TransitionManager.beginDelayedTransition(cvUpcoming, new AutoTransition());
                        clExpandable.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void setData(ArrayList<Trip> upcomingTrips) {
        if (this.upcomingTrips != null) {
            this.upcomingTrips.clear();
        } else {
            this.upcomingTrips = upcomingTrips;
            notifyDataSetChanged();
        }
    }
}

