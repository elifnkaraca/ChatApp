package com.example.myapplication.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText input_email_login,input_password_login;
    private Button loginButton;
    private FirebaseAuth auth;
    private TextView donthaveAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        define();
    }

    public void define(){
        input_email_login = (EditText) findViewById(R.id.input_email_login);
        input_password_login = (EditText) findViewById(R.id.input_password_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        donthaveAccountText = (TextView) findViewById(R.id.donthaveAccountText);
        auth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = input_email_login.getText().toString();
                String password = input_password_login.getText().toString();
                if(!email.equals("") && !password.equals("")){
                    login(email,password);
                }else{
                    Toast.makeText(LoginActivity.this, "Please fill blank places", Toast.LENGTH_LONG).show();
                }

            }
        });
        donthaveAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    public void login(String email, String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Wrong e-mail or password", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
