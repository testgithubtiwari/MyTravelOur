package com.example.travelour.settings;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelour.LoginActivity;
import com.example.travelour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private EditText username, useremail, usercontact, userVehicleId;
    private String currusername, curruseremail, currusercontact, currVehicleId;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        username = findViewById(R.id.updateuserName);
        useremail = findViewById(R.id.updateuserEmail);
        usercontact = findViewById(R.id.updatecontactNumber);
        userVehicleId = findViewById(R.id.updatectVehicleID);
        Button deleteButton = findViewById(R.id.deleteProfile);
        authProfile=FirebaseAuth.getInstance();

        // Get the current user from Firebase Auth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if(currentUser.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Something went wrong! User details not available ",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser(currentUser);
                }
            });
        }

        // Get the user's data from the database and populate the input fields
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        userRef = database.child("Registered Users").child(currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currusername = snapshot.child("username").getValue(String.class);
                curruseremail = currentUser.getEmail();
                currusercontact = snapshot.child("contatcnumber").getValue(String.class);
                currVehicleId = snapshot.child("vehicleIdnumber").getValue(String.class);

                username.setText(currusername);
                useremail.setText(curruseremail);
                usercontact.setText(currusercontact);
                userVehicleId.setText(currVehicleId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(UpdateProfile.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });

        Button updateButton = findViewById(R.id.updateProfile);
        updateButton.setOnClickListener(v -> {
            Updateprofile();
        });
    }

    private void deleteUser(FirebaseUser currentUser) {
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(getApplicationContext(),"User has been deleted!",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void deleteUserData() {
        // delete the profile pic
        /*FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(currentUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/

        // delete data from relatime database;
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess: User Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Updateprofile() {
        String updatedUsername = username.getText().toString();
        String updatedUserEmail = useremail.getText().toString();
        String updatedUserContact = usercontact.getText().toString();
        String updatedVehicleId = userVehicleId.getText().toString();
        if(updatedUsername.isEmpty())
        {
            username.setError("Empty!");
            username.requestFocus();
        }else if(updatedUserEmail.isEmpty())
        {
            useremail.setError("Empty!");
            useremail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(updatedUserEmail).matches())
        {
            useremail.setError("Enter valid email!");
            useremail.requestFocus();
        }else if(updatedUserContact.isEmpty())
        {
            usercontact.setError("Empty!");
            usercontact.requestFocus();
        }else if(updatedUserContact.length()!=10)
        {
            usercontact.setError("Mobile number should be of 10 digits!");
            usercontact.requestFocus();
        }else if(updatedVehicleId.isEmpty())
        {
            userVehicleId.setError("Empty!");
            userVehicleId.requestFocus();
        }else {
            // Update the user's data in the database
            userRef.child("username").setValue(updatedUsername);
            userRef.child("userEmail").setValue(updatedUserEmail);
            userRef.child("contatcnumber").setValue(updatedUserContact);
            userRef.child("vehicleIdnumber").setValue(updatedVehicleId);
            Toast.makeText(this, "Profile updated", Toast.LENGTH_LONG).show();
        }
    }
}
