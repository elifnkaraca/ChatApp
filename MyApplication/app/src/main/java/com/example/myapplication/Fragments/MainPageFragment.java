package com.example.myapplication.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapters.UserAdapter;
import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
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


public class MainPageFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    List<String> userkeyList;
    RecyclerView userlistRecyclerView;
    View view;
    UserAdapter userAdapter;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_main_page, container, false);
        define();
        getUsers();
        return view;
    }
    public void define(){
        userkeyList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        userlistRecyclerView = view.findViewById(R.id.userlistRecyclerView);
        RecyclerView.LayoutManager mng = new GridLayoutManager(getContext(),2);
        userlistRecyclerView.setLayoutManager(mng);
        userAdapter = new UserAdapter(userkeyList,getActivity(),getContext());
        userlistRecyclerView.setAdapter(userAdapter);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
    public void getUsers(){

        reference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                reference.child("Users").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Users users = dataSnapshot.getValue(Users.class);
                        if(!users.getName().equals("null") && !dataSnapshot.getKey().equals(user.getUid())){

                            if(userkeyList.indexOf(dataSnapshot.getKey()) == -1){
                                userkeyList.add(dataSnapshot.getKey());
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
