package com.app.sscpinstagram.fragmet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.sscpinstagram.Adapter.PostAdapter;
import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDetailsFragment extends Fragment {

    String postid;
    private RecyclerView posts;
    List<Post> postList;
    private PostAdapter pa;
    TextView close;
    public PostDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post_details, container, false);
        SharedPreferences pref=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        postid=pref.getString("postid","none");

        posts=view.findViewById(R.id.userposts);
        posts.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(getContext());
        posts.setLayoutManager(lm);
        postList=new ArrayList<>();
        readPosts();
        pa=new PostAdapter(getContext(),postList);
        posts.setAdapter(pa);

        return view;

    }

    private void readPosts()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                    Post post=dataSnapshot.getValue(Post.class);
                    postList.add(post);
                    pa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
