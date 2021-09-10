package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.app.sscpinstagram.fragmet.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.app.sscpinstagram.fragmet.Fav_Fragment;
import com.app.sscpinstagram.fragmet.Home_Fragment;
import com.app.sscpinstagram.fragmet.Profile_Fragment;
import com.app.sscpinstagram.fragmet.Search_Fragment;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottom_nav;
    Fragment selectedFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottom_nav=findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle i=getIntent().getExtras();
        if(i!=null)
        {
            String publisher=i.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREF",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Profile_Fragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home_Fragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.nav_home:
                    selectedFragment=new Home_Fragment();
                    break;
                case R.id.nav_saerch:
                    selectedFragment=new Search_Fragment();
                    break;
                case R.id.nav_add:
                    selectedFragment=null;
                    startActivity(new Intent(HomeActivity.this,PostActivity.class));
                    break;
                case R.id.nav_fav:
                    selectedFragment=new NotificationFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor=getSharedPreferences("PREF",MODE_PRIVATE).edit();
                    editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment=new Profile_Fragment();
                    break;
            }
            if(selectedFragment!=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
            return true;
        }
    };

}
