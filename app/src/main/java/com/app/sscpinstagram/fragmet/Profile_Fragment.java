package com.app.sscpinstagram.fragmet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sscpinstagram.Adapter.PhotoAdapter;
import com.app.sscpinstagram.FollowersActivity;
import com.app.sscpinstagram.HomeActivity;
import com.app.sscpinstagram.MainActivity;
import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.Model.User;
import com.app.sscpinstagram.OptionsActivity;
import com.app.sscpinstagram.ProfileActivity;
import com.app.sscpinstagram.R;
import com.app.sscpinstagram.RegisterActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Fragment extends Fragment {
    ImageView image_profile,options;
    TextView username,fullname,biodata,posts,followers,following;
    Button edit_profile;
    String profileid;
    ImageButton myPhotos,savedPhotos;
    FirebaseUser fUser;

    RecyclerView recyclerView_posts;
    List<Post> mPostList;
    PhotoAdapter pa;

    List<String> mSaves;
    List<Post> mSaveList;
    RecyclerView savedList;
    PhotoAdapter pa_save;
    Fragment selectedFragment=null;

    public Profile_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences pref=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        profileid=pref.getString("profileid","none");

        recyclerView_posts=view.findViewById(R.id.recyclerview_post_item);
        recyclerView_posts.setHasFixedSize(true);
        LinearLayoutManager lm=new GridLayoutManager(getContext(),3);
        recyclerView_posts.setLayoutManager(lm);
        mPostList=new ArrayList<>();
        readPosts();
        pa=new PhotoAdapter(getContext(),mPostList);
        recyclerView_posts.setAdapter(pa);

        savedList=view.findViewById(R.id.recycler_save_pics);
        savedList.setHasFixedSize(true);
        LinearLayoutManager lm_save=new GridLayoutManager(getContext(),3);
        savedList.setLayoutManager(lm_save);
        mSaveList=new ArrayList<>();
        savedpics();
        pa_save=new PhotoAdapter(getContext(),mSaveList);
        savedList.setAdapter(pa_save);

        username=view.findViewById(R.id.username);
        fullname=view.findViewById(R.id.fullname);
        biodata=view.findViewById(R.id.biodat);
        image_profile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        myPhotos=view.findViewById(R.id.my_photos);
        savedPhotos=view.findViewById(R.id.save_photos);
        edit_profile=view.findViewById(R.id.edit_profile);

        readUserInfo();
        getFollowers();
        getNrPosts();
        if(profileid.equals(fUser.getUid()))
        {
            edit_profile.setText("Edit Profile");
        }
        else
        {
            checkFollowing();
            savedPhotos.setVisibility(View.GONE);
        }
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=edit_profile.getText().toString();
                if(btn.equals("Edit Profile"))
                {
                    Intent i=new Intent(getContext(), ProfileActivity.class);
                    startActivity(i);
                }
                else if(btn.equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(fUser.getUid()).setValue(true);
                    addNotifications();
                }
                else if(btn.equals("following"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(fUser.getUid()).removeValue();
                }
            }
        });

        myPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_posts.setVisibility(View.VISIBLE);
                savedList.setVisibility(View.GONE);
            }
        });
        savedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_posts.setVisibility(View.GONE);
                savedList.setVisibility(View.VISIBLE);
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), FollowersActivity.class);
                i.putExtra("id",profileid);
                i.putExtra("title","followers");
                startActivity(i);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), FollowersActivity.class);
                i.putExtra("id",profileid);
                i.putExtra("title","following");
                startActivity(i);
            }
        });
        return view;
    }
    private void addNotifications()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",fUser.getUid());
        hashMap.put("postid","");
        hashMap.put("text","started following you");
        hashMap.put("ispost",false);
        ref.push().setValue(hashMap);
    }

    private void savedpics()
    {
        mSaves=new ArrayList<>();
        DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("Saves").child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    mSaves.add(snap.getKey());
                }
                getPhotos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPhotos()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Post post=snap.getValue(Post.class);
                    for(String id:mSaves)
                    {
                        if(post.getPostid().equals(id))
                        {
                            mSaveList.add(post);
                        }
                    }
                }
                pa_save.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPostList.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Post post=snap.getValue(Post.class);
                    if(post.getPublisher().equals(profileid))
                    {
                        mPostList.add(post);
                    }
                }
                Collections.reverse(mPostList);
                pa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readUserInfo()
    {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null)
                {
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getfullname());
                biodata.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowing()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileid).exists())
                {
                    edit_profile.setText("following");
                }
                else
                {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getNrPosts()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Post post=snap.getValue(Post.class);
                    if(post.getPublisher().equals(profileid))
                    {
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
