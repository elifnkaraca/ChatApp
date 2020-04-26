package com.example.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fragments.OtherProfileDetailFragment;
import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ChangeFragment;
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

public class Friend_Req_Adapter extends RecyclerView.Adapter<Friend_Req_Adapter.ViewHolder> {

    List<String> userkeyList;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String userId;

    public Friend_Req_Adapter(List<String> userkeyList, Activity activity, Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.friend_req_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // holder.usernameTextView.setText(userkeyList.get(position).toString());
        reference.child("Users").child(userkeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                //Log.i("istek",users.toString());

                Picasso.get().load(users.getImage()).into(holder.friend_req_image);
                holder.friend_req_text.setText(users.getName());

                holder.friend_req_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accept(userId, userkeyList.get(position));
                    }
                });

                holder.friend_req_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss(userId, userkeyList.get(position));
                    }
                });
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

        TextView friend_req_text;
        CircleImageView friend_req_image;
        Button friend_req_accept, friend_req_delete;

        ViewHolder(View itemView) {
            super(itemView);
            friend_req_text = (TextView) itemView.findViewById(R.id.friend_req_text);
            friend_req_image = (CircleImageView) itemView.findViewById(R.id.friend_req_image);
            friend_req_accept = (Button) itemView.findViewById(R.id.friend_req_accept);
            friend_req_delete = (Button) itemView.findViewById(R.id.friend_req_delete);

        }
    }

    public void accept(final String userId, final String otherId) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyy");
        Date today = Calendar.getInstance().getTime();
        final String reportDate = df.format(today);

        reference.child("Friends").child(userId).child(otherId).child("date").setValue(reportDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference.child("Friends").child(otherId).child(userId).child("date").setValue(reportDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "You accepted friendship request", Toast.LENGTH_SHORT).show();
                            reference.child("Friendship_request").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    reference.child("Friendship_request").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });

                                }
                            });
                        }

                    }
                });
            }
        });

    }

    public void dismiss(final String userId, final String otherId) {

        reference.child("Friendship_request").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child("Friendship_request").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "You deleted friendship request", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

}
