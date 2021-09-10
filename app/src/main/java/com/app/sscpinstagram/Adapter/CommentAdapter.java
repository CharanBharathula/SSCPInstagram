package com.app.sscpinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.CommentActivity;
import com.app.sscpinstagram.HomeActivity;
import com.app.sscpinstagram.Model.Comment;
import com.app.sscpinstagram.Model.User;
import com.app.sscpinstagram.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>
{
    Context mContext;
    List<Comment>mComments;
    FirebaseUser fUser;

    public CommentAdapter(Context context, List<Comment> mComments) {
        mContext=context;
        this.mComments=mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comments=mComments.get(position);
        holder.comment.setText(comments.getComment());
        getUserInfo(holder.profile_pic,holder.username,comments.getpublisher());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, HomeActivity.class);
                i.putExtra("publisherid", comments.getpublisher());
                mContext.startActivity(i);
            }
        });
        holder.profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, HomeActivity.class);
                i.putExtra("publisherid", comments.getpublisher());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profile_pic;
        TextView username,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView image, final TextView username, String publisherid)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
