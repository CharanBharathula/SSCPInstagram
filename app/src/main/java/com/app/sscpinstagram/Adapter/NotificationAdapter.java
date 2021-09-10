package com.app.sscpinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.Model.Notification;
import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.Model.User;
import com.app.sscpinstagram.R;
import com.app.sscpinstagram.fragmet.PostDetailsFragment;
import com.app.sscpinstagram.fragmet.Profile_Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{
    private Context mContext;
    private List<Notification> mNotiifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotiifications) {
        this.mContext = mContext;
        this.mNotiifications = mNotiifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification=mNotiifications.get(position);
        holder.comment.setText(notification.getText());
        getUserInfo(holder.profile_pic,holder.username,notification.getUserid());
        if(notification.isPost())
        {
            holder.post_pic.setVisibility(View.VISIBLE);
            getPostImage(holder.post_pic,notification.getPostid());
        }
        else
        {
            holder.post_pic.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isPost())
                {
                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment()).commit();
                }
                else
                {
                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Profile_Fragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotiifications.size();
    }

    private void getUserInfo(final ImageView image, final TextView username, String publisherid)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Glide.with(mContext).load(user.getImageurl()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPostImage(final ImageView pimage, String postid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post=dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(pimage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView post_pic,profile_pic;
        TextView username,comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_pic=itemView.findViewById(R.id.post_image);
            profile_pic=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.text);
        }
    }

}
