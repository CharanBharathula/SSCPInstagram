package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText name,email,password,fullname;
    Button register;
    TextView text_login;
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Initialize();
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Please Wait...");
                progressDialog.setMessage("We are adding you to our Database");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String values[]={name.getText().toString(),email.getText().toString(),password.getText().toString(),fullname.getText().toString()};
                if(TextUtils.isEmpty(values[0]) || TextUtils.isEmpty(values[1]) || TextUtils.isEmpty(values[2]) || TextUtils.isEmpty(values[3]) )
                {
                    Toast.makeText(RegisterActivity.this,"Enter valid details",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(values[0],values[1],values[2],values[3]);
                }
            }
        });
    }

    private void register(final String name, final String email, final String password, final String fullname)
    {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser cuser=auth.getCurrentUser();
                            String userid=cuser.getUid();
                            ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String,Object> hashmap=new HashMap<>();
                            hashmap.put("id",userid);
                            hashmap.put("username",name);
                            hashmap.put("password",password);
                            hashmap.put("fullname",fullname);
                            hashmap.put("bio","");
                            hashmap.put("email",email);
                            hashmap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/sscpinstagram-ce64d.appspot.com/o/insta.png?alt=media&token=ac223b73-fd92-4851-8110-27a91745fa70");
                            //storing details to the database
                            ref.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"you cant register",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void Initialize()
    {
        name=findViewById(R.id.uname);
        email=findViewById(R.id.uemail);
        password=findViewById(R.id.upwd);
        text_login=findViewById(R.id.txt_login);
        auth=FirebaseAuth.getInstance();
        register=findViewById(R.id.register);
        fullname=findViewById(R.id.fullname);
    }
}
