package com.app.sscpinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sscpinstagram.Model.Post;
import com.app.sscpinstagram.R;
import com.app.sscpinstagram.fragmet.PostDetailsFragment;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>
{
    public PhotoAdapter(Context context, List<Post> mPostList) {
        this.context = context;
        this.mPostList = mPostList;
    }

    private Context context;
    List<Post>mPostList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.photos_item,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post=mPostList.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.pic_item);

        holder.pic_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView pic_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic_item=itemView.findViewById(R.id.pic_item);
        }
    }
}
