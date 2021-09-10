package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView text_signup,forget;
    FirebaseAuth auth;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();
        text_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("") || password.getText().toString().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertData(email.getText().toString(),password.getText().toString());
                }
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup vg=findViewById(android.R.id.content);
                View view=getLayoutInflater().inflate(R.layout.forget_password,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                builder.setView(view);
                builder.setTitle("Password Reset");
                final AlertDialog alert=builder.create();
                alert.show();
                final EditText email=view.findViewById(R.id.email);
                Button submit=view.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(email.getText().toString().equals(""))
                        {
                            Toast.makeText(LoginActivity.this, "Enter Email please", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseAuth fAuth=FirebaseAuth.getInstance();
                            fAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, "Please Check your email for password reset link", Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void insertData(String email, String password)
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pd.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Wrong username or password",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }

    private void Initialize()
    {
        email=findViewById(R.id.uemail);
        password=findViewById(R.id.upwd);
        login=findViewById(R.id.login);
        text_signup=findViewById(R.id.txt_signup);
        forget=findViewById(R.id.forgt_pwd);
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}