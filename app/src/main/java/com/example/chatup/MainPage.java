package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatup.Fragments.ChatsFragment;
import com.example.chatup.Fragments.ProfileFragment;
import com.example.chatup.Fragments.UsersFragment;
import com.example.chatup.Model.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.example.chatup.Fragments.UsersFragment;

public class MainPage extends AppCompatActivity {

    ImageButton settings;

    public static String myNumber;
    public static String myImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        //userSettings button

        /*settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, profileSettings.class);
                startActivity(intent);
            }
        }); */


        // Firebase

        FirebaseUser firebaseUser;
        DatabaseReference mDatabase;


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser
                .getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                //Toast.makeText(MainPage.this, "User Login: "+users.getUsername(), Toast.LENGTH_SHORT).show();
                myNumber = users.getMobile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        // Tab layout Instantiate and viewpager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        //tabLayout.setupWithViewPager(viewPager);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"Contacts");
        viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);





    }

    // Adding Logout Functionality

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logoutUser){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainPage.this, StartPage.class));
            finish();
            return true;
        }
        return false;
    }





    // Class ViewPagerAdapter

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment , String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            //return super.getPageTitle(position);
            return titles.get(position);
        }
    }





}