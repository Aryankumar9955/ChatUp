package com.example.chatup.Chat;

import static com.example.chatup.MainPage.myImageURL;

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
import com.example.chatup.Fragments.ChatsFragment;
import com.example.chatup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<ChatObject> chatList;
    String myImageURL = "default";


    //Constructor
    public ChatAdapter(Context context,List<ChatObject> chatList){
        this.context = context;
        this.chatList = chatList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        ViewHolder rcv = new ViewHolder(view);


        FirebaseDatabase.getInstance().getReference("MyUsers").child(FirebaseAuth.getInstance().getUid()).child("imageURL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("ImageURL",snapshot.getValue().toString());
                myImageURL = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return rcv;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        ChatObject chatObject = chatList.get(position);
        holder.username.setText(chatObject.getUsername());

        holder.lastMessage.setText(chatObject.getLastMessage());



        if(chatObject.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.drawable.baseline_person_24);
        }else{

            Picasso.get().load(chatObject.getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.imageView);

        }
        holder.date.setText(chatObject.getDate());


        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("meee",myImageURL);
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatId",chatList.get(holder.getAbsoluteAdapterPosition()).getChatID());
                bundle.putString("senderID",chatList.get(holder.getAbsoluteAdapterPosition()).getReceiverId());

                bundle.putString("senderImage",myImageURL);
                bundle.putString("imageURL", chatList.get(holder.getAbsoluteAdapterPosition()).getImageURL());
                bundle.putString("username", chatList.get(holder.getAbsoluteAdapterPosition()).getUsername());
                bundle.putString("mobile",chatList.get(holder.getAbsoluteAdapterPosition()).getMobile());
                intent.putExtras(bundle);

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(chatObject.getImageURL().equals("default")){
                    holder.imageView.setImageResource(R.drawable.baseline_person_24);
                }else{

                    Picasso.get().load(chatObject.getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.imageView);

                }

                return false;
            }
        });

    /*    imageURL = chatObject.getImageURL();

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
    /*        Picasso.get().load(.getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.imageView);

        }

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





            }
        });
        */

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout mLayout;

        public TextView username;
        public ImageView imageView;
        public TextView lastMessage;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mLayout = itemView.findViewById(R.id.chatItemLayout);
            username = itemView.findViewById(R.id.usernameChatRecycler);
            imageView = itemView.findViewById(R.id.userImageChatRecycler);
            lastMessage = itemView.findViewById(R.id.messageFromUser);
            date = itemView.findViewById(R.id.chatDate);


        }
    }



}
