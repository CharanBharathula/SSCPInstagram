package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.sscpinstagram.Adapter.UserAdapter;
import com.app.sscpinstagram.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    RecyclerView followers;
    String id, title;
    List<String> idList;
    UserAdapter adapter;
    List<User> userslist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);


        Intent i=getIntent();
        id=i.getStringExtra("id");
        title=i.getStringExtra("title");
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        followers=findViewById(R.id.recyclerView_followers);
        followers.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        followers.setLayoutManager(lm);
        idList=new ArrayList<>();
        userslist=new ArrayList<>();
        switch(title)
        {
            case "likes":
                getLikes();
                break;
            case "followers":
                getFollowers();
                break;
            case "following":
                getFollowing();
                break;
        }
        adapter=new UserAdapter(this,userslist);
        followers.setAdapter(adapter);
    }

    private void getFollowing()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    idList.add(snap.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    idList.add(snap.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikes()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Likes").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    idList.add(snap.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showUsers()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userslist.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    User user=snap.getValue(User.class);
                    for(String id:idList)
                    {
                        if(user.getId().equals(id))
                        {
                            userslist.add(user);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
