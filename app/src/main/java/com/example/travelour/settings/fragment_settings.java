package com.example.travelour.settings;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelour.MainActivity;
import com.example.travelour.R;
import com.example.travelour.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class fragment_settings extends Fragment {

    private ImageView profilepic;
    private Button updateProfile;
    private TextView name,email,contact,id,chooseprofile,uploadprofile;
    public static final int PICK_IMAGE_REQUEST = 1001;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private String fullname,fullemail,fullcontact,fullId;
    private DatabaseReference reference;

    private StorageReference storageReference;
    private ProgressDialog pd1,pd2;

    private Uri uriImage;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_fragment_settings, container, false);

        profilepic=view.findViewById(R.id.profilepic);
        name=view.findViewById(R.id.tvname);
        email=view.findViewById(R.id.tvemail);
        contact=view.findViewById(R.id.tvcontact);
        id=view.findViewById(R.id.tvvehicle);
        chooseprofile=view.findViewById(R.id.choosepic);
        uploadprofile=view.findViewById(R.id.uploadpic);
        updateProfile=view.findViewById(R.id.updateProfile);

        reference= FirebaseDatabase.getInstance().getReference();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        showUserProfile(firebaseUser);

        pd1=new ProgressDialog(getContext());
        pd2=new ProgressDialog(getContext());
        storageReference= FirebaseStorage.getInstance().getReference().child("ProfilePics");

        chooseprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        uploadprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),UpdateProfile.class);
                startActivity(intent);
            }
        });

        return  view;
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId= firebaseUser.getUid();
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null)
                {
                    fullname=readUserDetails.username;
                    name.setText("User name: "+fullname);

                    fullemail=firebaseUser.getEmail();
                    email.setText("Email: "+fullemail);

                    fullcontact=readUserDetails.contatcnumber;
                    contact.setText("Contact No: "+fullcontact);

                    fullId=readUserDetails.vehicleIdnumber;
                    id.setText("Vehicle ID: "+fullId);

                    Uri uri=firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(profilepic);


                }else {
                    Toast.makeText(getContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImage() {
        pd1.setMessage("Uploading..");
        pd1.show();
        if (uriImage!=null)
        {
            // save the image with the currently logged in user
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));


            // upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri=uri;
                            firebaseUser= authProfile.getCurrentUser();

                            // finally set the dispaly image of the uploaded photo
                            UserProfileChangeRequest ProfileUpdate=new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(ProfileUpdate);

                        }
                    });
                    pd1.dismiss();
                    Toast.makeText(getContext(),"Upload Successful!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd1.dismiss();
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            pd1.dismiss();
            Toast.makeText(getContext(),"No file selected!",Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR=getContext().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriImage=data.getData();
            profilepic.setImageURI(uriImage);
        }
    }
}
