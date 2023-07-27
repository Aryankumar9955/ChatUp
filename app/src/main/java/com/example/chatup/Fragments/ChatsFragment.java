package com.example.chatup.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatup.Adapter.UserAdapter;
import com.example.chatup.Chat.ChatAdapter;
import com.example.chatup.Chat.ChatObject;
import com.example.chatup.Chat.MessageModel;
import com.example.chatup.Chat.MessageObject;
import com.example.chatup.CountrytoPhonePrefix;
import com.example.chatup.MainPage;
import com.example.chatup.Model.Users;
import com.example.chatup.Model.details;
import com.example.chatup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    private ArrayList<Users> contacts;
    private ArrayList<String> seenChats;
    String myId;

    ArrayList<ChatObject> chatList;
    DatabaseReference mDatabase;

    String username;
    String lastMessage;
    String myImageURL;
    String imageURL;
    TextView Date;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }



    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
        //return inflater.inflate(R.layout.fragment_chats, container, false);

        contacts = new ArrayList<>();
        seenChats = new ArrayList<>();

        username = "defaultUsername";
        imageURL = "default";

        myId = FirebaseAuth.getInstance().getUid();

        myImageURL ="default";



        readContacts();



        chatList = new ArrayList<>();




        View view = inflater.inflate(R.layout.fragment_chats,container,false);



        mChatList = view.findViewById(R.id.chatRecyclerView);
        mChatList.setHasFixedSize(false);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatList.setNestedScrollingEnabled(false);

        mChatListLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mChatList.setLayoutManager(mChatListLayoutManager);

        mChatListAdapter = new ChatAdapter(getContext(),chatList);
        mChatList.setAdapter(mChatListAdapter);

        getChatList();




        return view;


    }




    private void getChatList(){



        FirebaseDatabase.getInstance().getReference("Chats").child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(snapshot.exists()){


                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.hasChildren()){



                        if((dataSnapshot.child("member1").getValue()).toString().equals(myId) ||
                                (dataSnapshot.child("member2").getValue()).toString().equals(myId)) {



                            for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                                if(childSnapshot.hasChildren()) {

                                    try {
                                    //    Log.i("SNAPSHOT", childSnapshot.child("details").getValue().toString());


                                        ChatObject chatObject = new ChatObject();
                                        chatObject.setDate(childSnapshot.child("details").child("date").getValue().toString());
                                        chatObject.setLastMessage(childSnapshot.child("details").child("lastMessage").getValue().toString());
                                        chatObject.setChatID(childSnapshot.child("details").child("chatId").getValue().toString());
                                        chatObject.setReceiverId(childSnapshot.child("details").child("receiverId").getValue().toString());


                                        chatObject.setImageURL(childSnapshot.child("details").child("userImage").getValue().toString());

                                        String mobile = childSnapshot.child("details").child("mobile").getValue().toString();
                                        String username = "Unknown";
                                        chatObject.setMobile(childSnapshot.child("details").child("mobile").getValue().toString());





                                        for (int i = 0; i < contacts.size(); i++) {
                                            if (contacts.get(i).getMobile().equals(mobile)) {
                                                username = contacts.get(i).getUsername();
                                                break;
                                            }
                                        }
                                        chatObject.setUsername(username);

                                        if(seenChats.contains(chatObject.getMobile())){
                                            chatList.remove(seenChats.indexOf(chatObject.getMobile()));
                                            chatList.add(seenChats.indexOf(chatObject.getMobile()),chatObject);
                                        //    seenChats.remove(chatObject.getMobile());
                                        //    seenChats.add(chatObject.getMobile());

                                            mChatListAdapter.notifyDataSetChanged();
                                            Log.i("ALREADYHAVEUSER","ALready have chat");
                                        }else{
                                            chatList.add(chatObject);
                                            seenChats.add(chatObject.getMobile());
                                            mChatListAdapter.notifyDataSetChanged();
                                        }



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                }
                        }



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


        }

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




}