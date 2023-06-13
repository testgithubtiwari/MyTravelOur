package com.example.travelour.groups;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.travelour.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragment_groups extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private allTripAdapter adapter;
    private List<Alltripdata> tripList;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        tripList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_groups, container, false);

        recyclerView = view.findViewById(R.id.tripRecycler);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new allTripAdapter(tripList);
        recyclerView.setAdapter(adapter);
        pd=new ProgressDialog(getContext());
        getTripsFromFirebase();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tripList.clear();
                getTripsFromFirebase();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void getTripsFromFirebase() {
        pd.setMessage("Getting all trips....");
        pd.show();
        Query query = FirebaseDatabase.getInstance().getReference("Registered Users").orderByChild("trips");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list before adding new items to it.
                tripList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    for (DataSnapshot tripSnapshot : userSnapshot.child("trips").getChildren()) {
                        Alltripdata trip = tripSnapshot.getValue(Alltripdata.class);
                        trip.setUserId(username);
                        trip.setMembersCount(userSnapshot.child("trips").child(tripSnapshot.getKey()).child("memberscount").getValue(String.class));
                        tripList.add(trip);
                    }
                }
                adapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
