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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextView register,forgot;
    private EditText edtemail,edtpwd;
    private Button letbtn;
    String email,password;

    private FirebaseAuth authprofile;
    private static final  String TAG="LoginActivity";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login Page!");

        edtemail=findViewById(R.id.edtemail);
        edtpwd=findViewById(R.id.edtpassowrd);
        letbtn=findViewById(R.id.letbtn);
        pd=new ProgressDialog(this);
        authprofile= FirebaseAuth.getInstance();
        register=findViewById(R.id.tvregister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        letbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email=edtemail.getText().toString();
                password=edtpwd.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    edtemail.setError("Empty!");
                    edtemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    edtemail.setError("Valid e-mail is required");
                    edtemail.requestFocus();
                }else if(TextUtils.isEmpty(password))
                {
                    edtpwd.setError("Empty!");
                    edtpwd.requestFocus();
                }else
                {
                    loginUser(email,password);

                }

            }
        });

        forgot=findViewById(R.id.forgottv);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        pd.setMessage("Loading...");
        pd.show();
        authprofile.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"You have successfully Loged in",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                    pd.dismiss();

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        edtemail.setError("User doesn't exist or no longer valid. Please register again");
                        edtemail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edtpwd.setError("Invalid credentials. kindly check and re-enter");
                        edtpwd.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(authprofile.getCurrentUser()!=null)
        {
            Toast.makeText(LoginActivity.this,"You are already Logged In!",Toast.LENGTH_LONG).show();
            // userprofile start screen
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}