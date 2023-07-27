package com.example.chatup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

public class ViewPhoto extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        ImageView imageView = findViewById(R.id.userImageShow);

        String URL = getIntent().getStringExtra("URL");

        progressBar = findViewById(R.id.progressBarViewPhoto);

        if(URL.equals("default")){
            imageView.setImageResource(R.drawable.baseline_person_24);
        }else{
            // Adding the Glide Library
        /*    Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView); */
            Picasso.get().load(URL).rotate(0).placeholder(R.drawable.baseline_person_24).into(imageView);

        }

        progressBar.setVisibility(View.INVISIBLE);

    }
}