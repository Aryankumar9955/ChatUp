package com.example.chatup.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.example.chatup.Chat.ChatObject;
import com.example.chatup.Chat.MessageAdapter;
import com.example.chatup.Chat.MessageModel;
import com.example.chatup.Chat.MessageObject;
import com.example.chatup.MainPage;
import com.example.chatup.Model.Users;
import com.example.chatup.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    public static boolean isActive = false;

    DatabaseReference databaseReference;

    private ArrayList<Users> contacts;
    private RecyclerView mchat;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;
    ArrayList<MessageObject> messageList;

    private FirebaseRecyclerAdapter<MessageObject, MessageAdapter.ViewHolder> firebaseRecyclerAdapter;

    String receiverID,myId,chatID;
    String imageURL;
    ImageView userImage;
    String senderUsername;
    String mobile;
    String myMobile = "+919955886424";
    TextView usernameMessage;
    ImageButton addMediaButton;
    ImageButton sendButton;
    EditText message;
    String myImageURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        isActive = true;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers").child("Chats");

        receiverID = getIntent().getExtras().getString("senderID");

        mobile = getIntent().getExtras().getString("mobile");
    /*    FirebaseDatabase.getInstance().getReference("MyUsers").child(myId).child("mobile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                myMobile = task.getResult().toString();
            }
        });  */

        //Sender Image
        myImageURL = getIntent().getExtras().getString("senderImage");
        imageURL = getIntent().getExtras().getString("imageURL");

        chatID = getIntent().getExtras().getString("chatId");

        Log.i("CHATIDINACTIVITY",chatID);

        myId = FirebaseAuth.getInstance().getUid();


        addMediaButton = findViewById(R.id.addMediaButton);
        senderUsername = getIntent().getExtras().getString("username");




        //Sender Image
        imageURL = getIntent().getExtras().getString("imageURL");


        // Initialise RecyclerView
    //    initialiseRecyclerView();
        messageList = new ArrayList<>();
        mchat = findViewById(R.id.message);
        mchat.setNestedScrollingEnabled(false);
        mchat.setHasFixedSize(false);
        chatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mchat.setLayoutManager(chatLayoutManager);
        chatAdapter = new MessageAdapter(getApplicationContext(),messageList);
        mchat.setAdapter(chatAdapter);



        userImage = findViewById(R.id.chatHeadingPhoto);
        usernameMessage = findViewById(R.id.usernameMessage);

        usernameMessage.setText(senderUsername);

        if(imageURL.equals("default")){
            userImage.setImageResource(R.drawable.baseline_person_24);
        }else{

            Picasso.get().load(imageURL).rotate(0).placeholder(R.drawable.baseline_person_24).into(userImage);

        }



        if(chatID.equals("null")){
            createChat(receiverID);
        }



        message = findViewById(R.id.messageEditText);

        sendButton = findViewById(R.id.sendMessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageToSend = message.getText().toString().trim();
                if(messageToSend.isEmpty()){

                }else{
                    message.setText(null);
                    sendMessage(messageToSend);

                }
            }
        });



        //Reading chats
        readChats();



    }


    private void readChats(){


        FirebaseDatabase.getInstance().getReference().child("Chats").child(myId).child(chatID).child(myId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    int size = (int)snapshot.getChildrenCount();


                    try{
                        Log.i("DATASNAPSHOT", snapshot.getValue().toString());
                        MessageObject newMessage = new MessageObject();
                        newMessage.setUsername(snapshot.child("username").getValue().toString());
                        newMessage.setMessage(snapshot.child("message").getValue().toString());
                        newMessage.setTime(snapshot.child("time").getValue().toString());
                        newMessage.setMessageId(snapshot.child("messageId").getValue().toString());
                        newMessage.setImageURL(snapshot.child("imageURL").getValue().toString());
                        newMessage.setReceiverId(snapshot.child("receiverId").getValue().toString());

                        Log.i("OBJECT",newMessage.getMessage());
                        Log.i("OBJECT",newMessage.getReceiverId());
                        Log.i("OBJECT",newMessage.getUsername());
                        Log.i("OBJECT",newMessage.getTime());


                        messageList.add(newMessage);
                        chatLayoutManager.scrollToPosition(messageList.size() - 1);
                        chatAdapter.notifyDataSetChanged();
                    }catch(Exception e){
                        Log.i("EXCEPTION",e.getMessage());
                    }




                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }





/*
    private void readMessages(String chatId){

        Query query = FirebaseDatabase
                .getInstance().getReference().child("Chats")
                .child(chatId).child(myId);

        FirebaseRecyclerOptions<MessageObject> options = new FirebaseRecyclerOptions.Builder<MessageObject>()
                .setQuery(query,MessageObject.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessageObject, MessageAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position, @NonNull MessageObject model) {
                holder.message.setText(messageList.get(position).getMessage());
                holder.time.setText(messageList.get(position).getTime());

                if(messageList.get(position).getImageURL().equals("default")){
                    holder.userImage.setImageResource(R.drawable.baseline_person_24);
                }else{

                    Picasso.get().load(messageList.get(position).getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.userImage);

                }
            }

            @NonNull
            @Override
            public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if(viewType==0){
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.user_message_item,parent,false);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    view.setLayoutParams(lp);

                    MessageAdapter.ViewHolder rcv = new MessageAdapter.ViewHolder(view);

                    return rcv;
                }else{

                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.sender_item_layout,parent,false);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    view.setLayoutParams(lp);

                    MessageAdapter.ViewHolder rcv = new MessageAdapter.ViewHolder(view);

                    return rcv;

                }

            }

            @Override
            public int getItemViewType(int position){

                MessageObject messageObject = getItem(position);
                if(myId.equals(messageObject.getReceiverId())){
                    return 0;
                }
                else{
                    return 1;
                }
            }

        };




    }  */


    @Override
    public void onBackPressed(){
        isActive = false;
        Intent intent = new Intent(getApplicationContext(), MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    // Create Chat
    private void createChat(String id) {

        HashMap<String,String> hs = new HashMap<>();
        hs.put("receiverId",id);
        hs.put("lastMessage","");
        hs.put("date",new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        hs.put("mobile",mobile);
        hs.put("userImage",imageURL);


        chatID = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        hs.put("chatId",chatID);

        mDatabase.child(myId).child(chatID).child("member1").setValue(myId);
        mDatabase.child(myId).child(chatID).child("member2").setValue(id);


        mDatabase.child(myId).child(chatID).child(myId).child("details").setValue(hs);

        HashMap<String,String> hm = new HashMap<>();
        hm.put("receiverId",myId);
        hm.put("lastMessage","");
        hm.put("date",new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        hm.put("mobile",MainPage.myNumber);
        hm.put("userImage",myImageURL);
        hm.put("chatId",chatID);

        mDatabase.child(receiverID).child(chatID).child("member1").setValue(id);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(receiverID).child(chatID).child("member2").setValue(myId);

        mDatabase.child(receiverID).child(chatID).child(receiverID).child("details").setValue(hm);
        Log.i("CHATID",chatID);

    }





    public void sendMessage(String msg){




            MessageObject newMessage = new MessageObject(senderUsername,chatID,msg,receiverID,new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),myImageURL);



            FirebaseDatabase.getInstance().getReference("Chats").child(myId).child(chatID).child(myId).push().setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    HashMap<String,String > hm = new HashMap<>();
                    hm.put("lastMessage",msg);
                    hm.put("date",new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                    hm.put("userImage",imageURL);
                    hm.put("mobile",mobile);
                    hm.put("receiverId",receiverID);
                    hm.put("chatId",chatID);


                    FirebaseDatabase.getInstance().getReference("Chats").child(myId).child(chatID).child(myId).child("details").setValue(hm);
                }
            });






            FirebaseDatabase.getInstance().getReference("Chats").child(receiverID).child(chatID).child(receiverID).push().setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    HashMap<String,String > hs = new HashMap<>();
                    hs.put("lastMessage",msg);
                    hs.put("date",new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                    hs.put("userImage",myImageURL);
                    hs.put("mobile",myMobile);
                    hs.put("receiverId",myId);
                    hs.put("chatId",chatID);
                    FirebaseDatabase.getInstance().getReference("Chats").child(receiverID).child(chatID).child(receiverID).child("details").setValue(hs);
                }
            });






        //        databaseReference.push().setValue(hm);

    //        messageList.add(messageObject);
    //        chatAdapter.notifyDataSetChanged();


    }

    //Read contact to get contact name







    private void initialiseRecyclerView(){
        messageList = new ArrayList<>();
        mchat = findViewById(R.id.message);
        mchat.setNestedScrollingEnabled(false);
        mchat.setHasFixedSize(false);
        chatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mchat.setLayoutManager(chatLayoutManager);
        chatAdapter = new MessageAdapter(getApplicationContext(),messageList);
        mchat.setAdapter(chatAdapter);
    }


}