package com.example.chatup.Chat;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<MessageObject> messageList;
    String imageURL;
    String myId;


    //Constructor
    public MessageAdapter(Context context,List<MessageObject> messageList){
        this.context = context;
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        myId = FirebaseAuth.getInstance().getUid();

        switch(viewType){
            case 0:
                View view = LayoutInflater.from(context).inflate(R.layout.sender_item_layout,parent,false);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);

                ViewHolder rcv = new ViewHolder(view);

                return rcv;
            case 1:
                View view1 = LayoutInflater.from(context).inflate(R.layout.user_message_item,parent,false);
                RecyclerView.LayoutParams lp1 = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                view1.setLayoutParams(lp1);

                ViewHolder rcv1 = new ViewHolder(view1);

                return rcv1;
            default:
                return null;
        }




    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        holder.message.setText(messageList.get(position).getMessage());
        holder.time.setText(messageList.get(position).getTime());

        if(messageList.get(position).getImageURL().equals("default")){
            holder.userImage.setImageResource(R.drawable.baseline_person_24);
        }else{
            // Adding the Glide Library
        /*    Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView); */
            Picasso.get().load(messageList.get(position).getImageURL()).rotate(0).placeholder(R.drawable.baseline_person_24).into(holder.userImage);

        }

    }

    @Override
    public int getItemViewType(int position){
        if(messageList.get(position).getReceiverId().equals(myId)){
            return 0;
        }else{
            return 1;
        }
    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout mLayout;

        public ImageView userImage;

        public TextView time;
        public TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mLayout = itemView.findViewById(R.id.messageLayout);
            time = itemView.findViewById(R.id.messageTime);
            message = itemView.findViewById(R.id.messageSend);
            userImage = itemView.findViewById(R.id.messageImage);

        }
    }



}
