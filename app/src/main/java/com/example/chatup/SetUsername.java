package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;

public class SetUsername extends AppCompatActivity {
    ImageButton btn;
    TextView userID;
    EditText username;
    EditText about;
    String mobileWithoutCountry;
    String imageName;
    ImageView userImage;
    ProgressBar progressBar;
    String imageURL;

    Uri imageUri;
    String storagePath;


    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference fileRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_username);

        btn = findViewById(R.id.gotoMain);
        userImage = findViewById(R.id.userImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageURL = "default";

        btn.setActivated(true);

        mobileWithoutCountry = getIntent().getStringExtra("mobileWithoutCountry");

        username = findViewById(R.id.username);
        about = findViewById(R.id.about);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userid = firebaseUser.getUid();

        mDatabase = FirebaseDatabase
                .getInstance()
                .getReference("MyUsers")
                        .child(userid);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(FirebaseAuth.getInstance().getUid());







        // Opening the Main Activity after setting values in the database


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btn.isActivated()){
                    Toast.makeText(SetUsername.this, "Setting Display photo. Please Wait.", Toast.LENGTH_SHORT).show();
                }

                else if(username.getText().toString().isEmpty()){
                    Toast.makeText(SetUsername.this, "Enter a valid username.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("DownloadURL",imageURL);
                    //HashMap
                    HashMap<String,String> hs = new HashMap<>();
                    hs.put("id",userid);
                    hs.put("username",username.getText().toString());
                    hs.put("imageURL",imageURL);
                    hs.put("mobile", getIntent().getStringExtra("mobile"));
                    hs.put("about",about.getText().toString());


                    mDatabase.setValue(hs).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(SetUsername.this, MainPage.class);
                                intent.putExtra("myImageURL",imageURL);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(SetUsername.this, "User setup failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



            }
        });

        // Upload user image in Firebase Storage
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(SetUsername.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SetUsername.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE /*, Manifest.permission.WRITE_EXTERNAL_STORAGE */},1);

                    if(ContextCompat.checkSelfPermission(SetUsername.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        onClick(view);
                    }
                }else{
                    chosePicture();
                }

            }
        });

    }

    private String getFileExtention(Uri imageUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));

    }

    private void chosePicture() {

        btn.setActivated(false);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent,2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            progressBar.setVisibility(View.VISIBLE);
            btn.setActivated(false);


            imageUri = data.getData();
            userImage.setImageURI(imageUri);
            imageName = "DisplayPicture";
            fileRef = storageReference.child(imageName + "." + getFileExtention(imageUri));
        //    fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef/*.child(imageName + "." + getFileExtention(imageUri))*/.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageURL = task.getResult().toString();
                            progressBar.setVisibility(View.INVISIBLE);
                            btn.setActivated(true);
                            Log.i("URL DOWNLOAD",imageURL);
                        }
                    });

                }
            });


        }

    }
}