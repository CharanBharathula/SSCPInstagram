package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sscpinstagram.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    ImageView profile_pic,close;
    TextView change_pic,pasword,save,email;
    TextInputEditText username,fullname,bio;

    Context mContext;
    FirebaseUser fUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_pic=findViewById(R.id.profile_pic);
        close=findViewById(R.id.close);
        change_pic=findViewById(R.id.changeprofile_pic);
        pasword=findViewById(R.id.password);
        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        save=findViewById(R.id.save_profile);
        bio=findViewById(R.id.bio);
        email=findViewById(R.id.email);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                fullname.setText(user.getfullname());
                pasword.setText(user.getPassword());
                email.setText(user.getEmail());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_pic);
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                .start(ProfileActivity.this);
            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(username.getText().toString(),fullname.getText().toString(),bio.getText().toString());
            }
        });
    }

    private void updateProfile(String uname,String fullname,String bio)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("username",uname);
        hashMap.put("fullname",fullname);
        hashMap.put("bio",bio);
        ref.updateChildren(hashMap);
        Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mt=MimeTypeMap.getSingleton();
        return mt.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.setCancelable(true);
        pd.show();
        if(mImageUri!=null)
        {
            final StorageReference fRef=storageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            uploadTask=fRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloaduri=task.getResult();
                        String imagerl=downloaduri.toString();
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageurl",""+imagerl);
                        ref.updateChildren(hashMap);
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(mContext, "Failed to update image", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(mContext, "No image is Selected !!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            uploadImage();
        }
        else
        {
            Toast.makeText(mContext, "Something wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
