package com.example.travelour.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelour.MakeYourTrip;
import com.example.travelour.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profile_fragment extends Fragment {
    private FloatingActionButton fab;
    private RecyclerView tripRecycler;
    private ProgressDialog pd;
    private ArrayList<TripData> list;
    private TripAdapter adapter;
    private TextView notrip;
    private DatabaseReference userRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_profile_fragment, container, false);

        fab=view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MakeYourTrip.class);
                startActivity(intent);
            }
        });
        pd=new ProgressDialog(getContext());
        tripRecycler=view.findViewById(R.id.tripRecycler);
        notrip=view.findViewById(R.id.notriptext);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        userRef = mDatabase.getReference("Registered Users").child(uid);
        getTripdata();
        return  view;
    }
    private void getTripdata() {
        pd.setMessage("Getting your Trips...");
        pd.show();
        userRef.child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TripData> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TripData tripData = snapshot.getValue(TripData.class);
                    list.add(tripData);
                }
                pd.dismiss();
                if(list.isEmpty())
                {
                    notrip.setVisibility(View.VISIBLE);
                }else {
                    notrip.setVisibility(View.GONE);
                    tripRecycler.setHasFixedSize(true);
                    tripRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new TripAdapter(getContext(), list);
                    tripRecycler.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


}
