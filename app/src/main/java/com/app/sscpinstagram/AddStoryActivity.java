package com.app.sscpinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {

    Uri imageUri;
    String myurl="";
    StorageReference storageRef;
    StorageTask uploadTask;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageRef= FirebaseStorage.getInstance().getReference("Story");
        CropImage.activity().setAspectRatio(9,16).start(AddStoryActivity.this);
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void publishStory()
    {
        pd=new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.setCancelable(false);
        pd.show();

        if(imageUri!=null)
        {
            final StorageReference imageReference=storageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=imageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete())
                    {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myurl=downloadUri.toString();
                        String myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story").child(myid);
                        String storyid=reference.push().getKey();
                        long timend=System.currentTimeMillis()+86400000;
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageurl",myurl);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timend",timend);
                        hashMap.put("storyid",storyid);
                        hashMap.put("userid",myid);
                        reference.child(storyid).setValue(hashMap);
                        pd.dismiss();
                        Intent i=new Intent(AddStoryActivity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(AddStoryActivity.this, "failed to Post", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult rsult=CropImage.getActivityResult(data);
            imageUri=rsult.getUri();
            publishStory();
        }
        else
        {
            Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(AddStoryActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}
