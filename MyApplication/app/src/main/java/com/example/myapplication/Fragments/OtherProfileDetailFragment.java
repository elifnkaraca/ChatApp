package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Activity.ChatActivity;
import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class OtherProfileDetailFragment extends Fragment {

    View view;
    String otherId, userId;
    TextView userprofileNameText, userprofileBirthdateText, userprofileEducationText, userprofileAboutmeText,
            userprofileFriendText, userprofileFollowerText;
    ImageView userprofileFriendImage, userprofileFollowImage, userprofileMessageImage;
    CircleImageView userprofileimage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, referenceFriendship;
    FirebaseAuth auth;
    FirebaseUser user;
    String control = "", followControl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_other_profile_detail, container, false);
        define();
        action();
        getFollowersText();
        getFriendsText();
        return view;
    }

    public void define() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        referenceFriendship = firebaseDatabase.getReference().child("Friendship_request");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();

        otherId = getArguments().getString("userid");
        userprofileNameText = (TextView) view.findViewById(R.id.userprofileNameText);
        userprofileBirthdateText = (TextView) view.findViewById(R.id.userprofileBirthdateText);
        userprofileEducationText = (TextView) view.findViewById(R.id.userprofileEducationText);
        userprofileAboutmeText = (TextView) view.findViewById(R.id.userprofileAboutmeText);

        userprofileFriendImage = (ImageView) view.findViewById(R.id.userprofileFriendImage);
        userprofileFollowImage = (ImageView) view.findViewById(R.id.userprofileFollowImage);
        userprofileMessageImage = (ImageView) view.findViewById(R.id.userprofileMessageImage);
        userprofileimage = (CircleImageView) view.findViewById(R.id.userprofileimage);
        userprofileFriendText = (TextView) view.findViewById(R.id.userprofileFriendText);
        userprofileFollowerText = (TextView) view.findViewById(R.id.userprofileFollowerText);

        referenceFriendship.child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    control = "request";
                    userprofileFriendImage.setImageResource(R.drawable.removefriend);
                } else {
                    userprofileFriendImage.setImageResource(R.drawable.addfriend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Friends").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(otherId)) {

                    control = "friend";
                    userprofileFriendImage.setImageResource(R.drawable.deletinguser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Followers").child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {

                    followControl = "followed";
                    userprofileFollowImage.setImageResource(R.drawable.unfollow);
                }else{
                    userprofileFollowImage.setImageResource(R.drawable.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void action() {

        reference.child("Users").child(otherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                userprofileNameText.setText(users.getName());
                userprofileBirthdateText.setText("Birthdate: " + users.getBirthdate());
                userprofileAboutmeText.setText("About me: " + users.getAboutme());
                userprofileEducationText.setText("Education: " + users.getEducation());

                if (!users.getImage().equals("null")) {

                    Picasso.get().load(users.getImage()).into(userprofileimage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userprofileFriendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (control.equals("request")) {
                    removeFriend(otherId, userId);

                } else if (control.equals("friend")) {
                    removefromFriendshipTable(otherId, userId);

                } else {
                    addFriend(otherId, userId);
                }
            }
        });
        userprofileFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followControl.equals("followed")){
                    unfollow(userId,otherId);
                }else{
                    follow(userId,otherId);
                }
            }
        });
        userprofileMessageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userName",userprofileNameText.getText().toString());
                intent.putExtra("id",otherId);
                startActivity(intent);
            }
        });

    }

    private void unfollow(String userId, String otherId) {

        reference.child("Followers").child(otherId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userprofileFollowImage.setImageResource(R.drawable.follow);
                followControl = "";
                Toast.makeText(getContext(), "You unfollowed this profile", Toast.LENGTH_SHORT).show();
                getFollowersText();

            }
        });
    }

    private void removefromFriendshipTable(final String otherId, final String userId) {

        reference.child("Friends").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child("Friends").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        control = "";
                        userprofileFriendImage.setImageResource(R.drawable.addfriend);
                        Toast.makeText(getContext(), "You deleted your friend succesfully", Toast.LENGTH_SHORT).show();
                        getFriendsText();
                    }
                });

            }
        });
    }

    public void addFriend(final String otherId, final String userId) {

        referenceFriendship.child(userId).child(otherId).child("type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    referenceFriendship.child(otherId).child(userId).child("type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                control = "request";
                                Toast.makeText(getContext(), "Friendship request send succesfully", Toast.LENGTH_SHORT).show();
                                userprofileFriendImage.setImageResource(R.drawable.removefriend);


                            } else {
                                Toast.makeText(getContext(), "There is an error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(getContext(), "There is an error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void removeFriend(final String otherId, final String userId) {
        referenceFriendship.child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                referenceFriendship.child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        control = "";
                        userprofileFriendImage.setImageResource(R.drawable.addfriend);
                        Toast.makeText(getContext(), "Friendship request cancelled succesfully", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }

    public void follow(String userId, String otherId) {

        reference.child("Followers").child(otherId).child(userId).child("type").setValue("followed").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "You followed this profile", Toast.LENGTH_LONG).show();
                    userprofileFollowImage.setImageResource(R.drawable.unfollow);
                    followControl = "followed";
                    getFollowersText();
                }
            }
        });
    }
    public void getFollowersText() {

        reference.child("Followers").child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userprofileFollowerText.setText(dataSnapshot.getChildrenCount()+" followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFriendsText(){

        reference.child("Friends").child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userprofileFriendText.setText(dataSnapshot.getChildrenCount()+" friends");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
