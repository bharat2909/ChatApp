package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    TextInputEditText EditTextForgot;
    Button btnSend;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        EditTextForgot = findViewById(R.id.EditTextForgot);
        btnSend = findViewById(R.id.buttonSendEmail);
        auth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EditTextForgot.getText().toString();
                if(email!=null){
                    sendEmail(email);
                }else{
                    Toast.makeText(ForgotActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendEmail(String Email){
        auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ForgotActivity.this, "Password Reset e-mail has been sent!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotActivity.this, "Mail has not been sent", Toast.LENGTH_SHORT).show();
            }
        });
    }
}