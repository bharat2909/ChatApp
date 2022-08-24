package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextInputEditText userNameUpdate;
    CircleImageView updateImage;
    Button update;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    Uri ImageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private boolean ImageControl;
    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameUpdate = findViewById(R.id.EditTextUpdate);
        update = findViewById(R.id.buttonUpdate);
        updateImage=findViewById(R.id.ImageViewUpdate);
        auth= FirebaseAuth.getInstance();
        firebaseDatabase =FirebaseDatabase.getInstance();
        reference= firebaseDatabase.getReference();
        firebaseUser=auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserInfo();

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });


    }



    public void ImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!=null && resultCode==RESULT_OK){
            ImageUri = data.getData();
            Picasso.get().load(ImageUri).into(updateImage);
            ImageControl=true;
        }
        else{
            ImageControl=false;
        }
    }

    public void getUserInfo(){
        reference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Uid:",firebaseUser.getUid());

                String name = snapshot.child("UserName").getValue().toString();
                image = snapshot.child("Image").getValue().toString();
                Log.e("im:::",image);
                userNameUpdate.setText(name);
                if(image.equals("null")){

                }else{
                    Picasso.get().load(image).into(updateImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateInfo(){
        String uname = userNameUpdate.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("UserName").setValue(uname);

        if(ImageControl){
            UUID randomId = UUID.randomUUID();
            String fileName = "images/"+randomId+".jpg";
            storageReference.child(fileName).putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference mystorageReference = firebaseStorage.getReference(fileName);
                    mystorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath = uri.toString();
                            reference.child("Users").child(auth.getUid()).child("Image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this, "Write to Database is Successful!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }) .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "FAILURE:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Error",e.getMessage());
                }
            });
        }else{

            reference.child("Users").child(auth.getUid()).child("Image").setValue(image);

        }
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        intent.putExtra("username",uname);
        startActivity(intent);
        finish();
    }
}