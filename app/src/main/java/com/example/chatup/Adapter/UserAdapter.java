package com.example.chatup.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatup.Fragments.ChatActivity;
import com.example.chatup.Model.Users;
import com.example.chatup.R;
import com.example.chatup.ViewPhoto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> mUsers;
    String imageURL;

    String myUsername;
    String myImageURL;
    String chatId;
    String myId;


    //Constructor
    public UserAdapter(Context context,List<Users> mUsers){
        this.context = context;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        ViewHolder rcv = new ViewHolder(view);



        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseDatabase.getInstance().getReference().child("MyUsers").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUsername = snapshot.child("username").getValue().toString();
                myImageURL = snapshot.child("imageURL").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myId = FirebaseAuth.getInstance().getUid();

        Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

        holder.about.setText(users.getAbout());

        imageURL = users.getImageURL();

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPhoto.class);
                intent.putExtra("URL",users.getImageURL());
                startActivity(context,intent,null);
            }
        });

        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.drawable.baseline_person_24);
        }else{
            // Adding the Glide Library
        /*    Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView); */
            Picasso.get().load(users.getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.imageView);

        }

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chatId = "null";


                findChatId(users.getId(),users.getUsername(),users);



                String key = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();







                // For our UserID
            //    FirebaseDatabase.getInstance().getReference().child("MyUsers").child(FirebaseAuth.getInstance().getUid()).child("Chat").child(key).setValue(forUS);

                // For Opposite UserID
            //    FirebaseDatabase.getInstance().getReference().child("MyUsers").child(users.getId()).child("Chat").child(key).child("username").setValue(myUsername);
            //    FirebaseDatabase.getInstance().getReference().child("MyUsers").child(users.getId()).child("Chat").child(key).child("imageURL").setValue(myImageURL);

            /*    Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatId",chatId);
                bundle.putString("senderID",users.getId());
                bundle.putString("imageURL", users.getImageURL());
                bundle.putString("username", users.getUsername());
                intent.putExtras(bundle);
            //    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            */

            }
        });




    }

    private void createChat(String id, String username, String imageURL) {

        HashMap<String,String> hs = new HashMap<>();
        hs.put("ReceiverId",id);
        hs.put("Name",username);
        chatId = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(hs);
        Log.i("CHATID",chatId);

    }

    public void findChatId(String receiverID,String receiverName,Users users){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");



        reference.child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(ChatActivity.isActive){
                    return;
                }else{
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if((dataSnapshot.child("member1").getValue().toString().equals(myId) && dataSnapshot.child("member2").getValue().toString().equals(receiverID))
                                    || (dataSnapshot.child("member2").getValue().toString().equals(myId)) && dataSnapshot.child("member1").getValue().toString().equals(receiverID) ){
                                chatId = dataSnapshot.getKey();
                                Log.i("CHATIDDDDDD",chatId);
                                Log.i("USERNAME",receiverName);


                                Intent intent = new Intent(context, ChatActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("chatId",chatId);
                                bundle.putString("senderID",users.getId());
                                bundle.putString("senderImage",myImageURL);
                                bundle.putString("imageURL", users.getImageURL());
                                bundle.putString("username", users.getUsername());
                                bundle.putString("mobile",users.getMobile());
                                intent.putExtras(bundle);
                                //    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                                return;
                            }

                        }




                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(chatId=="null"){
            chatId = "null";
            Intent intent = new Intent(context, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("chatId",chatId);
            bundle.putString("senderID",users.getId());
            bundle.putString("senderImage",myImageURL);
            bundle.putString("imageURL", users.getImageURL());
            bundle.putString("username", users.getUsername());
            bundle.putString("mobile",users.getMobile());
            intent.putExtras(bundle);
            //    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }




    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout mLayout;

        public TextView username;
        public ImageView imageView;
        public TextView about;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mLayout = itemView.findViewById(R.id.messageLayout);
            username = itemView.findViewById(R.id.usernameMessage);
            imageView = itemView.findViewById(R.id.userImageRecycler);
            about = itemView.findViewById(R.id.messageFromUser);


        }
    }



}
