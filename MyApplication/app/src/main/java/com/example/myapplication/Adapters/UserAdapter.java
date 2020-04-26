package com.example.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.Fragments.OtherProfileDetailFragment;
import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ChangeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<String> userkeyList;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    public UserAdapter(List<String> userkeyList, Activity activity, Context context) {
        this.userkeyList = userkeyList;
        this.activity = activity;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.userlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // holder.usernameTextView.setText(userkeyList.get(position).toString());
        reference.child("Users").child(userkeyList.get(position).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                Boolean userState = Boolean.parseBoolean(dataSnapshot.child("state").getValue().toString());

                Picasso.get().load(users.getImage()).into(holder.userimage);
                holder.usernameTextView.setText(users.getName());

                if(userState == true){

                    holder.user_state_img.setImageResource(R.drawable.greenicon);
                }else{
                    holder.user_state_img.setImageResource(R.drawable.redicon);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.usermainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment fragment = new ChangeFragment(context);
                fragment.changewithParameter(new OtherProfileDetailFragment(), userkeyList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userkeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        CircleImageView userimage,user_state_img;
        LinearLayout usermainLayout;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            userimage = (CircleImageView) itemView.findViewById(R.id.userimage);
            usermainLayout = (LinearLayout) itemView.findViewById(R.id.usermainLayout);
            user_state_img = (CircleImageView) itemView.findViewById(R.id.user_state_img);
        }
    }

}
