package com.app.sscpinstagram.fragmet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.sscpinstagram.Adapter.NotificationAdapter;
import com.app.sscpinstagram.Model.Notification;
import com.app.sscpinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    RecyclerView recyclerView;
    List<Notification> notificationList;
    NotificationAdapter nAdapter;
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView=view.findViewById(R.id.notifications);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lm);
        notificationList=new ArrayList<>();
        readNotifications();
        nAdapter=new NotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(nAdapter);
        return view;
    }

    private void readNotifications()
    {
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Notifications").child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Notification notification=snap.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Collections.reverse(notificationList);
                nAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
