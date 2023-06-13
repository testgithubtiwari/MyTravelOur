package com.example.travelour;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelour.profile.TripData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class MakeYourTrip extends AppCompatActivity {

    private EditText tripname,locationStart,locationEnd,dateoftrip,membersct;
    private Button creteTripbtn;
    private DatabaseReference reference,dbref;
    private StorageReference storageReference;
    private ProgressDialog pd;
    private  String name,start,end,dofTrip,members;

    private DatePickerDialog picker;
    private static final  String TAG="MakeYourTrip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_your_trip);


        tripname=findViewById(R.id.edttripname);
        locationStart=findViewById(R.id.edtstartLocation);
        locationEnd=findViewById(R.id.edtdestination);
        dateoftrip=findViewById(R.id.edtdateofTrip);
        dateoftrip.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        dateoftrip.setFocusable(false);
        membersct=findViewById(R.id.edtmemeberct);
        creteTripbtn=findViewById(R.id.createTrip);
        pd=new ProgressDialog(this);
        reference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();


        dateoftrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                // datepicker dialog
                picker=new DatePickerDialog(MakeYourTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateoftrip.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        creteTripbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        name=tripname.getText().toString();
        start=locationStart.getText().toString();
        end=locationEnd.getText().toString();
        dofTrip=dateoftrip.getText().toString();
        members=membersct.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            tripname.setError("Empty!");
            tripname.requestFocus();
        }else if(TextUtils.isEmpty(start))
        {
            locationStart.setError("Empty!");
            locationStart.requestFocus();
        }else if(TextUtils.isEmpty(end))
        {
            locationEnd.setError("Empty!");
            locationEnd.requestFocus();
        }else if(TextUtils.isEmpty(dofTrip))
        {
            dateoftrip.setError("Empty!");
            dateoftrip.requestFocus();
        }else if(TextUtils.isEmpty(members))
        {
            membersct.setError("Empty!");
            membersct.requestFocus();
        }else
        {
            createTrip();
        }
    }

    private void createTrip() {
        pd.setMessage("Creating Trip...");
        pd.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        dbref = reference.child("Registered Users").child(uid).child("trips");
        final String uniqueKey = dbref.push().getKey();
        TripData tripData=new TripData(name,start,end,dofTrip,members,uniqueKey);


        dbref.child(uniqueKey).setValue(tripData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Trip Created!", Toast.LENGTH_SHORT).show();
                tripname.setText("");
                locationStart.setText("");
                locationEnd.setText("");
                dateoftrip.setText("");
                membersct.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Someting went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}