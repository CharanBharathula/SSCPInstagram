package com.app.sscpinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.CommentActivity;
import com.app.sscpinstagram.FollowersActivity;
import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.Model.User;
import com.app.sscpinstagram.R;
import com.app.sscpinstagram.fragmet.PostDetailsFragment;
import com.app.sscpinstagram.fragmet.Profile_Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
    public Context mContext;
    public List<Post> mPost;
    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList)
    {
        this.mContext=context;
        this.mPost=postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Post post=mPost.get(position);
        Glide.with(mContext).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.noimage)).into(holder.post_image);
        if(post.getDescription().equals(""))
        {
            holder.description.setVisibility(View.GONE);
        }
        else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        publisherIfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());
        isLikes(post.getPostid(),holder.like);
        nolikes(holder.likes,post.getPostid());
        getComment(post.getPostid(),holder.comments);
        isSaved(post.getPostid(),holder.save);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, CommentActivity.class);
                i.putExtra("postid",post.getPostid());
                i.putExtra("publisher",post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,CommentActivity.class);
                i.putExtra("postid",post.getPostid());
                i.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(),post.getPostid());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid())
                    .child(post.getPostid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }

            }
        });
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Profile_Fragment()).commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Profile_Fragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment()).commit();
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, FollowersActivity.class);
                i.putExtra("id",post.getPostid());
                i.putExtra("title","likes");
                mContext.startActivity(i);
            }
        });
    }
    private void isSaved(final String postid, final ImageView image)
    {
        FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Saves").child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists())
                {
                    image.setImageResource(R.drawable.ic_saved_pics);
                    image.setTag("saved");
                }
                else
                {
                    image.setImageResource(R.drawable.ic_save_pics);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getComment(String postid, final TextView comments)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }
    private void publisherIfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLikes(String postid, final ImageView image)
    {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    image.setImageResource(R.drawable.ic_like_red);
                    image.setTag("Liked");
                }
                else
                {
                    image.setImageResource(R.drawable.ic_like);
                    image.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void nolikes(final TextView likes, String postid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addNotifications(String userid,String postid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("postid",postid);
        hashMap.put("text","i Like your Post");
        hashMap.put("ispost",true);

        ref.push().setValue(hashMap);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView image_profile,post_image,like,comment,save;
        TextView username,likes,publisher,description,comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            description=itemView.findViewById(R.id.description);
            comments=itemView.findViewById(R.id.comments);

        }
    }
}
