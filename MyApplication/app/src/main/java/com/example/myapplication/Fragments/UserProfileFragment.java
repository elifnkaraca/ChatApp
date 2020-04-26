package com.example.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Models.Users;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ChangeFragment;
import com.example.myapplication.Utils.RandomName;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    View view;
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText username, input_birthdate, input_education, input_aboutme;
    CircleImageView profile_image;
    Button updateInfoButton,infoRequestButton,infoFriendsButon;
    String imageUrl;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        define();
        getInformations();
        return view;
    }

    public void define() {
        username = (EditText) view.findViewById(R.id.username);
        input_birthdate = (EditText) view.findViewById(R.id.input_birthdate);
        input_education = (EditText) view.findViewById(R.id.input_education);
        input_aboutme = (EditText) view.findViewById(R.id.input_aboutme);
        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        updateInfoButton = (Button) view.findViewById(R.id.updateInfoButton);

        infoFriendsButon = (Button) view.findViewById(R.id.infoFriendsButton);
        infoRequestButton = (Button) view.findViewById(R.id.infoRequestButton);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(user.getUid());

        infoFriendsButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment changeFragment = new ChangeFragment(getContext());
                changeFragment.change(new FriendsFragment());

            }
        });
        infoRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment changeFragment = new ChangeFragment(getContext());
                changeFragment.change(new NotificationFragment());
            }
        });
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,5);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == 5) {
            Uri filePath = data.getData();
            final StorageReference ref = storageReference.child("userimages").child(RandomName.getSaltString()+".jpg");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(getContext(), "Image updated succesfully", Toast.LENGTH_SHORT).show();
                            String name = username.getText().toString();
                            String educaton = input_education.getText().toString();
                            String birthdate = input_birthdate.getText().toString();
                            String aboutme = input_aboutme.getText().toString();

                            reference = database.getReference().child("Users").child(auth.getUid());
                            Map map = new HashMap();
                            map.put("name", name);
                            map.put("education", educaton);
                            map.put("birthdate", birthdate);
                            map.put("aboutme", aboutme);
                            map.put("image", uri.toString());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Informations updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void getInformations() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                //Log.i("bilgiler",users.toString());
                username.setText(users.getName());
                input_education.setText(users.getEducation());
                input_birthdate.setText(users.getBirthdate());
                input_aboutme.setText(users.getAboutme());
                imageUrl = users.getImage();
                if(!users.getImage().equals("null")){

                    Picasso.get().load(users.getImage()).into(profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void update(){
        String name = username.getText().toString();
        String educaton = input_education.getText().toString();
        String birthdate = input_birthdate.getText().toString();
        String aboutme = input_aboutme.getText().toString();

        reference = database.getReference().child("Users").child(auth.getUid());
        Map map = new HashMap();
        map.put("name",name);
        map.put("education",educaton);
        map.put("birthdate",birthdate);
        map.put("aboutme",aboutme);
        if(imageUrl.equals("null")){
            map.put("image","null");
        }else{
            map.put("image",imageUrl);
        }
        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    ChangeFragment changeFragment = new ChangeFragment(getContext());
                    changeFragment.change(new UserProfileFragment());
                    Toast.makeText(getContext(), "Informations updated succesfully", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Informations could not update", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
