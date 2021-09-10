package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myurl="";
    StorageReference storageRef;
    StorageTask uploadTask;
    ImageView image_added,close;
    TextView post;
    EditText des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Initialize();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PostActivity.this,HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });
        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult rsult=CropImage.getActivityResult(data);
            imageUri=rsult.getUri();
            image_added.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(PostActivity.this,HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadimage()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.setCancelable(false);
        pd.show();
        if(imageUri!=null)
        {
            final StorageReference fileref=storageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete())
                    {
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myurl=downloadUri.toString();
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("posts");
                        String postid=ref.push().getKey();
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("postimage",myurl);
                        hashMap.put("description",des.getText().toString());
                        hashMap.put("publisher",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ref.child(postid).setValue(hashMap);
                        reference.child(postid).setValue(hashMap);
                        pd.dismiss();
                        Intent i=new Intent(PostActivity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(PostActivity.this, "failed to Post", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void Initialize()
    {
        image_added=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        des=findViewById(R.id.description);
        close=findViewById(R.id.close);
        storageRef= FirebaseStorage.getInstance().getReference("Post");
    }
}
