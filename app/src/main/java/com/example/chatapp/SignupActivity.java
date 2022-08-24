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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText Email,Password,UserName;
    ImageView ProfileImage;
    Button Register;
    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    Boolean ImageControl=false;
    Uri ImageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Email = findViewById(R.id.EditTextEmails);
        Password = findViewById(R.id.EditTextPasswords);
        UserName = findViewById(R.id.EditTextUserName);
        ProfileImage = findViewById(R.id.ImageViewProfile);
        Register = findViewById(R.id.buttonRegister);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString();
                String pass = Password.getText().toString();
                String userName = UserName.getText().toString();
                signup(email,pass,userName);
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
            Picasso.get().load(ImageUri).into(ProfileImage);
            ImageControl=true;
        }
        else{
            ImageControl=false;
        }
    }

    public void signup(String Email, String Password,String userName){
        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    reference.child("Users").child(auth.getUid()).child("UserName").setValue(userName);
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
                                                Toast.makeText(SignupActivity.this, "Write to Database is Successful!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }) .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, "FAILURE:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Error",e.getMessage());
                            }
                        });
                    }else{

                            reference.child("Users").child(auth.getUid()).child("UserName").setValue("null");

                    }
                    Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                    intent.putExtra("username",userName);
                    startActivity(intent);
                    finish();

                }else{
                    String s = "Sign up Failed" + task.getException();

                    Toast.makeText(SignupActivity.this, s,
                            Toast.LENGTH_LONG).show();
                    Log.e("Fail:::",s);

                }

            }
        });
    }
}