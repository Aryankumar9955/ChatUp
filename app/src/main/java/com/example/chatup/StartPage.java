package com.example.chatup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking Contact Permission

        if(ContextCompat.checkSelfPermission(StartPage.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(StartPage.this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }



        FirebaseUser firebaseUser;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //If user is logged in then go straight to MainPage
        if(firebaseUser != null){
            Intent intent = new Intent(this, MainPage.class);
            startActivity(intent);
            finish();
        }


    }
    public void pass(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }
}