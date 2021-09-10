package com.app.sscpinstagram.fragmet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.app.sscpinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.app.sscpinstagram.Adapter.UserAdapter;
import com.app.sscpinstagram.Model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_Fragment extends Fragment {
    private RecyclerView recyclerview;
    private UserAdapter userAdapter;
    private List<User> musers;
    EditText search_box;
    public Search_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerview=view.findViewById(R.id.recycle_view);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        search_box=view.findViewById(R.id.search_bar);
        musers=new ArrayList<>();

        userAdapter=new UserAdapter(getContext(),musers);
        recyclerview.setAdapter(userAdapter);
        readUsers();

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
    private void searchUsers(String s)
    {
        final FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        Query query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musers.clear();
                for(DataSnapshot snapShot:dataSnapshot.getChildren())
                {
                    User user=snapShot.getValue(User.class);
                    if(user.getId()!= fUser.getUid())
                    {
                        musers.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readUsers()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_box.getText().toString().equals(""))
                {
                    musers.clear();
                    for(DataSnapshot datasnapshot:dataSnapshot.getChildren())
                    {
                        User user=datasnapshot.getValue(User.class);
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        if(!(user.getId().equals(firebaseUser.getUid())))
                        {
                            musers.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
