package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewHolder> {

    public List<String> list;
    public Context context;
    String userName;
    FirebaseDatabase database;
    DatabaseReference reference;

    public userAdapter(List<String> list, Context context, String userName) {
        this.list = list;
        this.context = context;
        this.userName = userName;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        reference.child("Users").child(list.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherUser = snapshot.child("UserName").getValue().toString();
                String imageUrl = snapshot.child("Image").getValue().toString();

                holder.uName.setText(otherUser);
                if(imageUrl.equals("null")){
                    holder.imageUser.setImageResource(R.drawable.profile);
                }else{
                    Picasso.get().load(imageUrl).into(holder.imageUser);
                }

                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context,ChatActivity.class);
                        intent.putExtra("username",userName);
                        intent.putExtra("otheruser",otherUser);
                        Log.e("Name::::",otherUser);

                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageUser;
        TextView uName;
        CardView card;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.imageViewUser);
            uName = itemView.findViewById(R.id.textViewUser);
            card = itemView.findViewById(R.id.cardView);
        }
    }

}
