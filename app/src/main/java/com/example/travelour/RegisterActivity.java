package com.example.travelour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtname,edtemail,edtidnumber,edtpwd,edtpwdagain,edtcontact;
    private Button registerbtn;

    String name,email,idnumber,password,contact,passwordagain;

    private ProgressDialog pd;

    private static final  String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Sign UP!");

        edtname=findViewById(R.id.edtname);
        edtemail=findViewById(R.id.edtemail);
        edtpwd=findViewById(R.id.edtpassowrd);
        edtidnumber=findViewById(R.id.edtid);
        edtpwdagain=findViewById(R.id.edtpassowrdagain);
        registerbtn=findViewById(R.id.registerbtn);
        edtcontact=findViewById(R.id.edtcontact);
        pd=new ProgressDialog(this);


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=edtname.getText().toString();
                email=edtemail.getText().toString();
                password=edtpwd.getText().toString();
                passwordagain=edtpwdagain.getText().toString();
                contact=edtcontact.getText().toString();
                idnumber=edtidnumber.getText().toString();


                String mobileRegx="[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegx);
                mobileMatcher=mobilePattern.matcher(contact);

                if(TextUtils.isEmpty(name)){
                    edtname.setError("Empty!");
                    edtname.requestFocus();
                }else if(TextUtils.isEmpty(email))
                {
                    edtemail.setError("Empty!");
                    edtemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    edtemail.setError("Enter valid E-mail");
                    edtemail.requestFocus();
                }
                else if(TextUtils.isEmpty(contact))
                {
                    edtcontact.setError("Empty!");
                    edtcontact.requestFocus();
                }else if(contact.length()!=10)
                {
                    edtcontact.setError("Contact number should be of 10 digits");
                    edtcontact.requestFocus();
                }else if(!mobileMatcher.find()){
                    edtcontact.setError("Format is wrong");
                    edtcontact.requestFocus();
                }else if(TextUtils.isEmpty(idnumber))
                {
                    edtidnumber.setError("Empty!");
                    edtidnumber.requestFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    edtpwd.setError("Empty!");
                    edtpwd.requestFocus();
                }else if(password.length()<=6)
                {
                    edtpwd.setError("Password should be grater than 6 digits.");
                    edtpwd.requestFocus();
                }else if(!passwordagain.equals(password))
                {
                    edtpwdagain.setError("Password not matches");
                    edtpwdagain.requestFocus();
                }
                else {
                    registerUser(name,email,contact,idnumber,password);
                }

            }
        });



    }

    private void registerUser(String name, String email, String contact, String idnumber, String password) {
        pd.setMessage("Registration Started!");
        pd.show();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser=auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            // enter user data firebase in the real time
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(name,contact,idnumber);

                            /// extracting reference for databse registered users
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Registration successfully!!", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        pd.dismiss();
                                    }else {

                                        Toast.makeText(RegisterActivity.this,"Registration failed , try again",Toast.LENGTH_LONG).show();

                                    }
                                    pd.dismiss();
                                }
                            });

                        }
                        else {
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException e){
                                edtpwd.setError("Your password is too weak. Kindly use numeric,alphabets and special characters");
                                edtpwd.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                edtemail.setError("Your e-mail is invalid or already in use. Kindly re-enter");
                                edtemail.requestFocus();

                            }catch(FirebaseAuthUserCollisionException e){
                                edtemail.setError("User is already registered with this e-mail, use another");
                                edtemail.requestFocus();
                            }

                            catch (Exception e) {
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            pd.dismiss();
                        }

                    }
                });

    }
}