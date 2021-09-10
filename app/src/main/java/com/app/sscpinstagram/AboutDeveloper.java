package com.app.sscpinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutDeveloper extends AppCompatActivity {
    TextView name,email,mobile;
    TextView workingon,education,location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        workingon=findViewById(R.id.working);
        education=findViewById(R.id.educatiom);
        location=findViewById(R.id.location);

        name.setText("B.Purna Sai Surya Charan");
        email.setText("bcharan197@gmail.com");
        mobile.setText("7659887184");
        workingon.setText("Android Application Development and Web Development");
        education.setText("IIIrd Btech in the Stream of Computer Science and Engineering at Prakasam Engineering College,Kandukur");
        location.setText("Nandavari street 1st line,kandukur,prakasam Dist AP 523105");

    }
}
