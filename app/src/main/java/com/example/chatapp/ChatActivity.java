package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ImageView back;
    TextView name;
    EditText msg;
    FloatingActionButton send;
    RecyclerView rv;
    String oName,uName;
    FirebaseDatabase database;
    DatabaseReference reference;
    List<ModelClass> list;
    messageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back = findViewById(R.id.imageViewBack);
        name = findViewById(R.id.textViewName);
        msg = findViewById(R.id.editText);
        send = findViewById(R.id.fab);
        rv = findViewById(R.id.rvChat);
        oName = getIntent().getStringExtra("otheruser");
        uName = getIntent().getStringExtra("username");
        name.setText(oName);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));


        getMessage();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString();
                if(!message.equals("")){
                    sendMessage(message);
                    msg.setText("");
                }
            }
        });
    }

    public void sendMessage(String message){
        String key = reference.child("Messages").child(uName).child(oName).push().getKey();
        Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("Message",message);
        messageMap.put("From",uName);
        reference.child("Messages").child(uName).child(oName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(oName).child(uName).child(key).setValue(messageMap);
                }
            }
        });
    }

    public void getMessage(){
        reference.child("Messages").child(uName).child(oName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelClass modelClass = snapshot.getValue(ModelClass.class);
                Log.e("ModelClass::::",modelClass.getFrom());
                list.add(modelClass);
                adapter.notifyDataSetChanged();
                rv.scrollToPosition(list.size()-1);
                rv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                        if ( i3 < i7) {
                            rv.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rv.smoothScrollToPosition(adapter.getItemCount()-1);
                                }
                            }, 100);
                        }
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new messageAdapter(list,uName);
        rv.setAdapter(adapter);
    }
}