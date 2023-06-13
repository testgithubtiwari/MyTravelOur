package com.example.travelour.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelour.MainActivity;
import com.example.travelour.MakeYourTrip;
import com.example.travelour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class update_your_trip extends AppCompatActivity {


    private EditText tripname,tripstart,tripend,tripmember,tripdate;
    private Button update,delete;
    private DatePickerDialog picker;

    private String name,start,end,member,date,uniqueKey;
    private DatabaseReference userRef,dbref;
    private ProgressDialog pd;
    private String newTripname,newTripstart,newTripend,newTripdate,newTripmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_your_trip);


        name=getIntent().getStringExtra("tripName");
        start=getIntent().getStringExtra("tripStart");
        end=getIntent().getStringExtra("tripEnd");
        member=getIntent().getStringExtra("memberscount");
        date=getIntent().getStringExtra("date");


        tripdate=findViewById(R.id.updateDoT);
        tripdate.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        tripstart=findViewById(R.id.updateStartLocation);
        tripname=findViewById(R.id.updateTripName);
        tripend=findViewById(R.id.updateEndLocation);
        tripmember=findViewById(R.id.updatectMembers);
        tripmember.setInputType(InputType.TYPE_CLASS_NUMBER);
        update=findViewById(R.id.update);
        delete=findViewById(R.id.delete);

        pd=new ProgressDialog(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        userRef = mDatabase.getReference("Registered Users").child(uid).child("trips");
        dbref=mDatabase.getReference("Registered Users").child(uid).child("trips");

        tripname.setText(name);
        tripstart.setText(start);
        tripend.setText(end);
        tripmember.setText(member);
        tripdate.setText(date);
        uniqueKey=getIntent().getStringExtra("key");

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTripname=tripname.getText().toString();
                newTripstart=tripstart.getText().toString();
                newTripend=tripend.getText().toString();
                newTripdate=tripdate.getText().toString();
                newTripmembers=tripmember.getText().toString();

                checkValidation();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrip();
            }
        });
    }

    private void deleteTrip() {
        pd.setMessage("Deleting..");
        pd.show();
        userRef.child(uniqueKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Trip deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(update_your_trip.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkValidation() {
        if(newTripname.isEmpty())
        {
            tripname.setError("Empty!");
            tripname.requestFocus();
        }else if(newTripstart.isEmpty())
        {
            tripstart.setError("Empty!");
            tripstart.requestFocus();
        }else if(newTripend.isEmpty())
        {
            tripend.setError("Empty!");
            tripend.requestFocus();
        }else if(newTripdate.isEmpty())
        {
            tripdate.setError("Empty!");
            tripdate.requestFocus();
        }else if(newTripmembers.isEmpty())
        {
            tripmember.setError("Empty!");
            tripmember.requestFocus();
        }
        else
        {
            updateTrip();
        }
    }

    private void updateTrip() {
        HashMap hp=new HashMap();
        hp.put("tripName",newTripname);
        hp.put("date",newTripdate);
        hp.put("tripEnd",newTripend);
        hp.put("tripStart",newTripstart);
        hp.put("memberscount",newTripmembers);
        uniqueKey=getIntent().getStringExtra("key");
        dbref.child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getApplicationContext(), "Trip updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(update_your_trip.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}