package com.example.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    List<String> userkeyList;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String userId;

    public FriendAdapter(List<String> userkeyList, Activity activity, Context context) {
        this.userkeyList = userkeyList;
        this.activity = activity;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // holder.usernameTextView.setText(userkeyList.get(position).toString());
        reference.child("Users").child(userkeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child("name").getValue().toString();
                String userImage = dataSnapshot.child("image").getValue().toString();
                Boolean stateUser = Boolean.parseBoolean(dataSnapshot.child("state").getValue().toString());
                if (stateUser == true){
                    holder.friend_state_img.setImageResource(R.drawable.greenicon);
                }else{
                    holder.friend_state_img.setImageResource(R.drawable.redicon);
                }


                Picasso.get().load(userImage).into(holder.friend_image);
                holder.friend_text.setText(userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return userkeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView friend_text;
        CircleImageView friend_state_img, friend_image;

        ViewHolder(View itemView) {
            super(itemView);
            friend_text = (TextView) itemView.findViewById(R.id.friend_text);
            friend_state_img = (CircleImageView) itemView.findViewById(R.id.friend_state_img);
            friend_image = (CircleImageView) itemView.findViewById(R.id.friend_image);

        }
    }


}
