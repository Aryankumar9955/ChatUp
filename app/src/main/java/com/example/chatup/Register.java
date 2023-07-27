package com.example.chatup;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;


public class Register extends AppCompatActivity {
    //Widgets
    EditText getPhoneNumber;
    CountryCodePicker getCountryCode;
    ImageButton getOTP;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Initialising Widgets

        getPhoneNumber = findViewById(R.id.getOTP);
        getCountryCode = (CountryCodePicker) findViewById(R.id.getCountryCode);
        getCountryCode.registerCarrierNumberEditText(getPhoneNumber);

        getOTP = findViewById(R.id.verify);

        //Firebase Authentication
        auth = FirebaseAuth.getInstance();

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getPhoneNumber.getText().toString().isEmpty()){
                    getPhoneNumber.setError("Invalid phone number.");

                }
                else{
                    Intent intent = new Intent(Register.this, OTPauth.class);
                    intent.putExtra("mobile",getCountryCode.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("mobileWithoutCountry",getPhoneNumber.getText().toString());
                    Log.i("Phone Number",getCountryCode.getFullNumberWithPlus().replace(" ",""));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


            }
        });

    }




}