package com.example.travelour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MakeGroups extends AppCompatActivity {

    private Spinner chooseLeader;
    private Spinner choosetrip;
    private TextView contactNumberEditText;
    private ArrayList<String> tripNamesList = new ArrayList<>();
    private DatabaseReference registeredUsersRef;
    private Button createBtn;
    private String leaderName,Tripname;
    private EditText edtgroupname;
    private String groupname;
    private GroupsMade groupsMade;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_groups);

        chooseLeader = findViewById(R.id.chooseLeadername);
        edtgroupname=findViewById(R.id.edtgroupname);
        contactNumberEditText = findViewById(R.id.edtcontactleader);
        choosetrip=findViewById(R.id.userTrips);
        createBtn=findViewById(R.id.createbtn);
        pd=new ProgressDialog(this);

        registeredUsersRef = FirebaseDatabase.getInstance().getReference("Registered Users");

        ArrayList<String> userNames = new ArrayList<>();


        registeredUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot tripsSnapshot = userSnapshot.child("trips");
                    if (tripsSnapshot.exists()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (username != null) {
                            userNames.add(username);
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MakeGroups.this, android.R.layout.simple_spinner_item, userNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                chooseLeader.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        chooseLeader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserName = parent.getItemAtPosition(position).toString();
                registeredUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String username = userSnapshot.child("username").getValue(String.class);
                            if (username.trim().equalsIgnoreCase(selectedUserName.trim())) {
                                String contactNumber = userSnapshot.child("contatcnumber").getValue(String.class);
                                Log.d("MakeGroups", "Contact Number: " + contactNumber);
                                contactNumberEditText.setText(contactNumber);

                                tripNamesList = new ArrayList<>();
                                for (DataSnapshot tripSnapshot : userSnapshot.child("trips").getChildren()) {
                                    String tripName = tripSnapshot.child("tripName").getValue(String.class);
                                    tripNamesList.add(tripName);
                                }

                                ArrayAdapter<String> tripNamesAdapter = new ArrayAdapter<>(MakeGroups.this,
                                        android.R.layout.simple_spinner_item, tripNamesList);
                                tripNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                choosetrip.setAdapter(tripNamesAdapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MakeGroups.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupname=edtgroupname.getText().toString();

                if(groupname.isEmpty())
                {
                    edtgroupname.setError("Empty!");
                    edtgroupname.requestFocus();
                }else {
                    makeGroup();
                }
            }
        });
    }

    private void makeGroup() {
        // Get the selected trip name from the spinner
        // Get the current user's ID from Firebase Auth
        leaderName=chooseLeader.getSelectedItem().toString();
        Tripname=choosetrip.getSelectedItem().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the user has created a trip with the selected trip name
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isUserCreatedTrip = false;
                for (DataSnapshot tripSnapshot : snapshot.child("trips").getChildren()) {
                    String tripName = tripSnapshot.child("tripName").getValue(String.class);
                    if (tripName != null && tripName.equals(Tripname)) {
                        isUserCreatedTrip = true;
                        break;
                    }
                }

                if (isUserCreatedTrip) {
                    // User cannot join a group for a trip they have created
                    Toast.makeText(MakeGroups.this, "You cannot join a group for a trip you have created", Toast.LENGTH_SHORT).show();
                } else {
                    // User can join the group for the selected trip
                    DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("GroupsMade");
                    String groupId = groupsRef.push().getKey();

                    // Create a new group object and store it in the database
                    groupsMade= new GroupsMade(groupname,leaderName,Tripname,groupId);
                    groupsRef.child(groupId).setValue(groupsMade);

                    // Show a success message to the user
                    Toast.makeText(MakeGroups.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MakeGroups.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
