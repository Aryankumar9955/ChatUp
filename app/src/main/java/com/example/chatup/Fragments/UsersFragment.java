package com.example.chatup.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.chatup.Adapter.UserAdapter;
import com.example.chatup.CountrytoPhonePrefix;
import com.example.chatup.Model.Users;
import com.example.chatup.R;
import com.example.chatup.ViewPhoto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Permissions;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    ImageButton refreshUsers;

    private DatabaseReference mDatabase;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mContactAdapter;
    private RecyclerView.LayoutManager contactLayoutManager;
    private UserAdapter userAdapter;


    private ArrayList<Users> mUsers,contacts;
    private ArrayList<String> seenUsers;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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


        mUsers = new ArrayList<>();
        contacts = new ArrayList<>();
        seenUsers = new ArrayList<>();


        View view = inflater.inflate(R.layout.fragment_users,container,false);

        refreshUsers = view.findViewById(R.id.refresh);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        contactLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(contactLayoutManager);

        mContactAdapter = new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(mContactAdapter);



        refreshUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seenUsers.clear();
                mUsers.clear();
                readContacts();
            }
        });


        //Read users from contacts
        readContacts();
    //    readUsers();








        return view;



    }

    private void readContacts() {

        String isoPrefix = getCountryCode();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            @SuppressLint("Range") String ContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String ContactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //Replacing spaces with empty space in contactList numbers
            ContactNumber = ContactNumber.replace(" ","");
            ContactNumber = ContactNumber.replace("-","");
            ContactNumber = ContactNumber.replace("(","");
            ContactNumber = ContactNumber.replace(")","");

            if(!String.valueOf(ContactNumber.charAt(0)).equals("+")){
                ContactNumber = isoPrefix + ContactNumber;
            }




            //Creating new User for recycler view
            Users usersContact = new Users();
            usersContact.setUsername(ContactName);
            usersContact.setMobile(ContactNumber);
            usersContact.setImageURL("default");
            usersContact.setId("");
            usersContact.setAbout("");




            contacts.add(usersContact);
        //    mContactAdapter.notifyDataSetChanged();

            // Getting user details from the database matching the contact
            //getUserDetails(usersContact);
            getActualUser(usersContact);

        }

    }

    private void getActualUser(Users usersContact) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MyUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users user = dataSnapshot.getValue(Users.class);

                        String ContactNumber = user.getMobile();
                        //Replacing spaces with empty space in contactList numbers
                        ContactNumber = ContactNumber.replace(" ","");
                        ContactNumber = ContactNumber.replace("-","");
                        ContactNumber = ContactNumber.replace("(","");
                        ContactNumber = ContactNumber.replace(")","");


                            if(seenUsers.contains(usersContact.getMobile())){
                                break;
                            }else {
                                if (ContactNumber.equals(usersContact.getMobile())) {

                                    user.setUsername(usersContact.getUsername());

                                    user.setImageURL(user.getImageURL());
                                    mUsers.add(user);
                                    seenUsers.add(ContactNumber);
                                    mContactAdapter.notifyDataSetChanged();

                                }
                            }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

        private void getUserDetails(Users usersContact) {





        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("MyUsers");

        Query query = mUserDB.orderByChild("mobile").equalTo(usersContact.getMobile());









        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phoneNumber = "";
                    String username = "";
                    String about = "";
                    String imageURL = "";
                    String id = "";

                    for(DataSnapshot childSnapshot : snapshot.getChildren()){


                        Users users = childSnapshot.getValue(Users.class);
                        mUsers.add(users);

                    /*    if(childSnapshot.child("mobile").getValue()!=null ){
                            phoneNumber = (String)childSnapshot.child("mobile").getValue();
                        }
                        if(childSnapshot.child("username").getValue()!=null){
                            username = (String)childSnapshot.child("username").getValue();
                        }
                        if(childSnapshot.child("id").getValue()!=null){
                            id = (String)childSnapshot.child("id").getValue();
                        }
                        if(childSnapshot.child("imageURL").getValue()!=null){
                            imageURL = (String)childSnapshot.child("imageURL").getValue();
                        }
                        if(childSnapshot.child("about").getValue()!=null){
                            about = (String)childSnapshot.child("about").getValue();
                        }

                        Log.i("Database Contact Number",phoneNumber);

                        Users applicationUsers = new Users();
                        applicationUsers.setUsername(username);
                        applicationUsers.setAbout(about);
                        applicationUsers.setId(id);
                        applicationUsers.setMobile(phoneNumber);
                        applicationUsers.setImageURL(imageURL);


                        mUsers.add(applicationUsers);  */
                        mContactAdapter.notifyDataSetChanged();
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String getCountryCode(){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE);

        if(telephonyManager.getNetworkCountryIso() != null )
            if(!telephonyManager.getNetworkCountryIso().toString().equals("")){
                iso = telephonyManager.getNetworkCountryIso().toString();



            }



        return CountrytoPhonePrefix.getPhone(iso);
    }


    //Read users from contacts
/*    private void readUsers(){


            ContentResolver cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,null);
            while(Objects.requireNonNull(cursor).moveToNext()){
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                number = number.replace("\\s","");
                String num = String.valueOf(number.charAt(0));

                if(num.equals("0")){
                    number = number.replace("(?:0)+","");
                }

                // Finds the contact in our database through the Firebase Query to know whether that contact is using our app or not.
                findUsers(number,name);
            }


    }

    private void findUsers(String number,String name) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("MyUsers")
                .orderByChild("mobile")
                .equalTo(number);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
//                   Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    // this will print whole map of contacts who is using our app from clients contacts.
//                    Log.d("ContactSync", snapshot.getValue().toString());
                    // so you can use any value from map to add it in your Recycler View.

                    for(DataSnapshot ds : snapshot.getChildren()){
                        String mobile = ds.child("mobile").getValue().toString();
                        String about = ds.child("about").getValue().toString();
                        String imageURL = ds.child("imageURL").getValue().toString();
                        String UID = ds.child("id").getValue().toString();


                        Users users = new Users();
                        users.setUsername(name);
                        users.setAbout(about);
                        users.setMobile(mobile);
                        users.setImageURL(imageURL);
                        users.setId(UID);

                        mUsers.add(users);
                    }

                }
                userAdapter = new UserAdapter(getContext(),mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ContactSync", error.getMessage());
            }
        });
    }
*/

}