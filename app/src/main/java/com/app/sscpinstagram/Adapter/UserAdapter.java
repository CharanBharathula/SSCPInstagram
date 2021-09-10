package com.app.sscpinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import com.app.sscpinstagram.Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import com.app.sscpinstagram.fragmet.Profile_Fragment;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.useritem,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        isFollowing(user.getId(),holder.btn_follow);
        if(user.getId().equals(firebaseUser.getUid()))
        {
            holder.btn_follow.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Profile_Fragment()).commit();
            }
        });
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btn_follow.getText().toString().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotifications(user.getId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    private void addNotifications(String userid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("postid","");
        hashMap.put("text","started following");
        hashMap.put("ispost",false);
        ref.push().setValue(hashMap);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public TextView email;
        public CircleImageView image_profile;
        public Button btn_follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            email=itemView.findViewById(R.id.email);
            image_profile=itemView.findViewById(R.id.profile_image);
            btn_follow=itemView.findViewById(R.id.follow);
        }
    }
    private void isFollowing(final String userid,final Button button)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists())
                {
                    button.setText("following");
                }
                else
                {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}