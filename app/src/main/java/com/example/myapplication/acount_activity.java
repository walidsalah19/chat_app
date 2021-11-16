package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class acount_activity extends AppCompatActivity {
    private EditText textname;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String username;
    private Button btn;
    private CircleImageView image;
    private static final int num=1;
    private StorageReference strorag;
    private String picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_activity);
        initioalize();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storigeacount();

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent im=new Intent();
                im.setAction(Intent.ACTION_GET_CONTENT);
                im.setType("image/*");
                startActivityForResult(im,num);
            }
        });
        getname();
    }

    private void getname() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("image" )&& snapshot.hasChild("name")) {
                        String kl = snapshot.child("name").getValue().toString();
                        String im=snapshot.child("image").getValue().toString();
                        Picasso.get().load(im).into(image);
                        textname.setText(kl);
                    }
                    else if(snapshot.hasChild("name"))
                    {
                        String kl = snapshot.child("name").getValue().toString();
                        textname.setText(kl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storigeacount() {
        String nam=textname.getText().toString();
        if(TextUtils.isEmpty(nam))
        {
            Toast.makeText(this, "inter the name of you'r account", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,String> akk=new HashMap<>();
            akk.put("name",nam);
            akk.put("id",username);
            akk.put("image",picture);
            reference.setValue(akk).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        gotomain();
                    }
                }
            });
        }
    }

    private void gotomain() {
        Intent i=new Intent(acount_activity.this,MainActivity.class);
        startActivity(i);
    }

    private void initioalize()
    {
        textname=findViewById(R.id.name);
        btn=findViewById(R.id.button);
        auth=FirebaseAuth.getInstance();
        username=auth.getCurrentUser().getUid().toString();
        reference=FirebaseDatabase.getInstance().getReference("users").child(username);
        image=findViewById(R.id.circleimage);
        strorag= FirebaseStorage.getInstance().getReference().child("chat");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==num && resultCode==RESULT_OK && data !=null&& data.getData()!=null)
        {  Uri resultUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(acount_activity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                strorag.child(username+"."+"jpg");
                strorag.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                           picture=resultUri.toString();
                            reference.child("image").setValue(picture).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        //storigeacount();
                                        Toast.makeText(acount_activity.this, "update image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

}