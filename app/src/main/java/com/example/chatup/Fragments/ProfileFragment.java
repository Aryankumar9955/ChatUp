package com.example.chatup.Fragments;

import static android.app.appsearch.AppSearchResult.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatup.Model.Users;
import com.example.chatup.R;
import com.example.chatup.SetUsername;
import com.example.chatup.StartPage;
import com.example.chatup.ViewPhoto;
import com.example.chatup.profileSettings;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    ProgressBar profileProgressbar;

    String URL;
    Users user;
    ImageButton signOut;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    ImageView profileImage;
    TextView profileName;
    TextView profileStatus;
    TextView profilePhone;
    TextView profileDelete;
    ImageButton pickImage;

    Uri imageUri;
    String imageURL;
    String imageName;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference fileRef;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        //return inflater.inflate(R.layout.fragment_profile, container, false);
        profileProgressbar = view.findViewById(R.id.progressBar2);
        pickImage = view.findViewById(R.id.pickProfileImage);

        signOut = view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), StartPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        //Initialising Views
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileStatus = view.findViewById(R.id.profileStatus);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileDelete = view.findViewById(R.id.profileDelete);



        //Getting reference to Firebase Database
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userid = firebaseUser.getUid();

        mDatabase = FirebaseDatabase
                .getInstance()
                .getReference("MyUsers")
                .child(userid);

        URL = "default";
        user = new Users();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(FirebaseAuth.getInstance().getUid());



        //Setting values from the database

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                URL = user.getImageURL();
                Log.i("URL SNAPSHOT",user.getImageURL());

                if(URL.equals("default")){
                    profileImage.setImageResource(R.drawable.baseline_person_24);
                }else{
                    // Adding the Glide Library
        /*    Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView); */
                    Picasso.get().load(URL).rotate(0).placeholder(R.drawable.baseline_person_24).into(profileImage);
                }
                profileName.setText(user.getUsername());
                profileStatus.setText(user.getAbout());
                profilePhone.setText(user.getMobile());

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE /*, Manifest.permission.WRITE_EXTERNAL_STORAGE */},1);

                    if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        onClick(view);
                    }
                }else{
                    chosePicture();
                }
            }
        });


        //Changing User about section
        profileStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDatabase.child("about").setValue(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });






        Log.i("PROFILE URL",URL);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewPhoto.class);
                intent.putExtra("URL",URL);
                startActivity(intent);
            }
        });




        return view;
    }

    private String getFileExtention(Uri imageUri) {

        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));

    }

    private void chosePicture() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //ProfileFragment.this.startActivityForResult(intent,2);
        //getActivity().startActivityForResult(intent,2);
        //startActivityForResult(intent,3);
        startActivityForResult(intent,111);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ACTIVITY RESULT","111");

        if(data == null){
            Log.i("DATA NULL","yo");
        }
        if(resultCode != RESULT_OK){
            Log.i("RESULT ERROR","yo");
        }

        if(requestCode == 111 && /*resultCode == RESULT_OK && */data != null){
            profileProgressbar.setVisibility(View.VISIBLE);

            Log.i("INSIDE CODE","0101");

            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            imageName = "DisplayPicture";
            fileRef = storageReference.child(imageName + "." + getFileExtention(imageUri));
            //    fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.i("MORE INSIDE CODE","0101");
                    fileRef/*.child(imageName + "." + getFileExtention(imageUri))*/.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageURL = task.getResult().toString();
                            URL = task.getResult().toString();
                            Log.i("EVEN MORE INSIDE CODE","0101");
                            profileProgressbar.setVisibility(View.INVISIBLE);
                            Log.i("URL DOWNLOAD",imageURL);
                            mDatabase.child("imageURL").setValue(imageURL);
                        }
                    });

                }
            });


        }

    }


}