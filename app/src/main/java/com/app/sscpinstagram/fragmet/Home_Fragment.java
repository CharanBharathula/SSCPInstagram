package com.app.sscpinstagram.fragmet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.sscpinstagram.Adapter.PostAdapter;
import com.app.sscpinstagram.Adapter.StoryAdapter;
import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.Model.Story;
import com.app.sscpinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
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
public class Home_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post>postList;
    private List<String>followingList;

    RecyclerView recyclerView_story;
    List<Story> mStories;
    StoryAdapter storyAdapter;
    ProgressBar pb;
    public Home_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_home_, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        pb=view.findViewById(R.id.progress_bar);
        LinearLayoutManager lm=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lm);
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story=view.findViewById(R.id.recycle_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager l=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(l);
        mStories=new ArrayList<>();
        checkFollowing();
        storyAdapter=new StoryAdapter(getContext(),mStories);
        recyclerView_story.setAdapter(storyAdapter);
        return view;
    }

    private void checkFollowing()
    {
        followingList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren())
                {
                    followingList.add(snap.getKey());
                }
                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapShot:dataSnapshot.getChildren())
                {
                    Post post=snapShot.getValue(Post.class);
                    for(String id:followingList)
                    {
                        if(post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readStory()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Story");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent=System.currentTimeMillis();
                mStories.clear();
                mStories.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id:followingList)
                {
                    int countStory=0;
                    Story story=null;
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        story=snap.getValue(Story.class);
                        if(timecurrent > story.getTimestart() && timecurrent < story.getTimeEnd())
                        {
                            countStory++;
                        }
                    }
                    if(countStory>0)
                    {
                        mStories.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
