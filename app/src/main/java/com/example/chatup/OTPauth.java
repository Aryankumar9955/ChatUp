package com.example.chatup;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPauth extends AppCompatActivity {
    EditText getOTP;
    ImageButton verify;
    TextView verifyText;
    String phonenumber;
    String phoneWithoutCountry;
    String OTPid;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpauth);

        phonenumber = getIntent().getStringExtra("mobile");
        phoneWithoutCountry = getIntent().getStringExtra("mobileWithoutCountry");


        mAuth = FirebaseAuth.getInstance();


        getOTP = findViewById(R.id.getOTP);
        verifyText = findViewById(R.id.verifyText);
        verify = findViewById(R.id.verify);
        verify.setActivated(false);
        verifyText.setAlpha(0.5f);


        generateOTP();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verify.isActivated() == false){
                    Toast.makeText(OTPauth.this, "Please wait for OTP.", Toast.LENGTH_SHORT).show();
                }
                else if(getOTP.getText().toString().isEmpty()){
                    Toast.makeText(OTPauth.this, "Please enter OTP sent to your mobile number.", Toast.LENGTH_SHORT).show();
                }else if(getOTP.getText().toString().length()!=6){
                    Toast.makeText(OTPauth.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }else{

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTPid,getOTP.getText().toString());
                    signInWithPhoneAuthCredential(credential);


                }
            }
        });




    }

    private void generateOTP() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Toast.makeText(OTPauth.this, "OTP sent.", Toast.LENGTH_SHORT).show();
                                verify.setActivated(true);
                                verifyText.setAlpha(1f);
                                OTPid=s;


                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPauth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPauth.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");


                            Intent intent = new Intent(OTPauth.this, SetUsername.class);
                            intent.putExtra("mobile",phonenumber);
                            intent.putExtra("mobileWithoutCountry",phoneWithoutCountry);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                            // Update UI
                        } else {
                            Toast.makeText(OTPauth.this, "User verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPauth.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}