package com.example.travelour.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelour.R;

import java.util.List;

public class allTripAdapter extends RecyclerView.Adapter<allTripAdapter.TripViewHolder> {

    private List<Alltripdata> tripList;

    public allTripAdapter(List<Alltripdata> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_trips_created, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Alltripdata trip = tripList.get(position);
        holder.userNameTextView.setText("User Name: "+trip.getUserId());
        holder.tripStartTextView.setText("Trip Start: "+trip.getTripStart());
        holder.tripNameTextView.setText("Trip Name: "+trip.getTripName());
        holder.tripEndTextView.setText("Trip End: "+trip.getTripEnd());
        holder.tripDateTextView.setText("Trip Date"+trip.getDate());
        holder.membersCountTextView.setText("Members Count: "+trip.getMembersCount());
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        TextView tripStartTextView;
        TextView tripNameTextView;
        TextView tripEndTextView;
        TextView tripDateTextView;
        TextView membersCountTextView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.tripusername);
            tripStartTextView = itemView.findViewById(R.id.start);
            tripNameTextView = itemView.findViewById(R.id.tripTitle);
            tripEndTextView = itemView.findViewById(R.id.end);
            tripDateTextView = itemView.findViewById(R.id.tripDate);
            membersCountTextView = itemView.findViewById(R.id.count);
        }
    }
}
