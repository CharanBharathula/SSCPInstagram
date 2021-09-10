package com.app.sscpinstagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.AddStoryActivity;
import com.app.sscpinstagram.Model.Story;
import com.app.sscpinstagram.Model.User;
import com.app.sscpinstagram.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>
{
    private Context mContext;
    private List<Story> mStory;
    int c=0;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(mContext, "viewType="+viewType, Toast.LENGTH_SHORT).show();
        if(viewType==0)
        {
            View view= LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Story story=mStory.get(position);
        userInfo(holder,story.getUserid(),position);
        //Toast.makeText(mContext, "onbind View Holder", Toast.LENGTH_SHORT).show();
        if(holder.getAdapterPosition()!=0)
        {
            seeenStory(holder, story.getUserid());
        }
        if(holder.getAdapterPosition()==0)
        {
            myStory(holder.story_text,holder.story_plus,false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()==0)
                {
                    Toast.makeText(mContext, "Status Feature Will come soon :)", Toast.LENGTH_SHORT).show();
                    //myStory(holder.story_text,holder.story_plus,true);
                }
                else
                {
                    //TODO:goto story
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }
    @Override
    public int getItemViewType(int position) {
        c++;
        if(position==0)
        {
            return 0;
        }
        return 1;
    }
    private void userInfo(final ViewHolder viewHolder, String userid, final int pos)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo);
                if(pos!=0)
                {
                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void myStory(final TextView textView, final ImageView imageView, final boolean click)
    {
        //Toast.makeText(mContext, "my story()", Toast.LENGTH_SHORT).show();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Story story = snap.getValue(Story.class);
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeEnd()) {
                        count++;
                    }
                }
                if (click)
                {
                    //Toast.makeText(mContext, "Yes Clicked", Toast.LENGTH_SHORT).show();
                    if(count>0)
                    {
                        AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO:goto story
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i=new Intent(mContext, AddStoryActivity.class);
                                mContext.startActivity(i);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        Intent i=new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(i);
                    }
                }
                else
                {
                    if(count>0)
                    {
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }
                    else
                    {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seeenStory(final ViewHolder viewHolder, String userid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Story").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    if(!snap.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists() && System.currentTimeMillis()<snap.getValue(Story.class).getTimeEnd())
                    {
                        i++;
                    }
                }
                if(i>0)
                {
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView story_photo,story_plus,story_photo_seen;
        TextView story_username,story_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo=itemView.findViewById(R.id.story_photo);
            story_plus=itemView.findViewById(R.id.story_plus);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_username=itemView.findViewById(R.id.username);
            story_text=itemView.findViewById(R.id.addstory_text);

        }
    }
}
