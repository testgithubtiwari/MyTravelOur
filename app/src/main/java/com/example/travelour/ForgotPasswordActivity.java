package com.example.travelour;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtemail;
    private Button resetPwdBtn;
    private String email;
    private FirebaseAuth authProfile;
    private static final  String TAG="ForgotPasswordActivity";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Forgot Password!");

        edtemail=findViewById(R.id.edtemail);
        resetPwdBtn=findViewById(R.id.letbtn);
        pd=new ProgressDialog(this);

        resetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=edtemail.getText().toString();
                if(email.isEmpty())
                {
                    edtemail.setError("Empty!");
                    edtemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    edtemail.setError("Enter Valid Email!");
                    edtemail.requestFocus();
                }else
                {
                    resetPassword(email);
                }
            }
        });


    }

    private void resetPassword(String email) {
        pd.setMessage("Getting new password");
        pd.show();
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,"Please check your email inbox to get link for password reset",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);

                    // clear stack to prevent user coming back to this activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else
                {
                    pd.dismiss();
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        edtemail.setError("User doesn't exist or no longer exist. Please register again");
                        edtemail.requestFocus();

                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        edtemail.setError("User doesn't exist or no longer exist. Please register again");
                        edtemail.requestFocus();
                        Toast.makeText(ForgotPasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
}