package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sscpinstagram.Adapter.CommentAdapter;
import com.app.sscpinstagram.Model.Comment;
import com.app.sscpinstagram.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    EditText addComment;
    ImageView image_profile;
    TextView post;
    String postid,publisherId;
    FirebaseUser firebaseUser;
    Context mContext;
    CommentAdapter commentAdapter;
    List<Comment>mComments;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Initialize();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent i=getIntent();
        postid=i.getStringExtra("postid");
        publisherId=i.getStringExtra("publisherid");
        getImage();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        mComments=new ArrayList<>();
        readComments();
        commentAdapter=new CommentAdapter(this,mComments);
        recyclerView.setAdapter(commentAdapter);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals(""))
                {
                    Toast.makeText(CommentActivity.this, "Enter your comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addComment();
                }
            }
        });
    }

    private void readComments()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Comment comment=snap.getValue(Comment.class);
                    mComments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addComment()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        ref.push().setValue(hashMap);
        //addNotifications();
        addComment.setText("");

    }
    private void addNotifications()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("postid",postid);
        hashMap.put("text","commented "+addComment.getText().toString());
        hashMap.put("ispost",true);
        ref.push().setValue(hashMap);
    }

    private void getImage()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initialize()
    {
        addComment=findViewById(R.id.addComment);
        image_profile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post_cmt);firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=findViewById(R.id.recyclerView);
    }
}
