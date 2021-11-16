package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatactinity extends AppCompatActivity {
    private Button snad, sendfiles;
    private DatabaseReference reference, chatref;
    private FirebaseAuth auth;
    private EditText mass;
    private String idsender, userid, filetype = "", myuri = " ";
    private Toolbar bartool;
    private List<massageclass> malist = new ArrayList<>();
    private reycleradapter adpter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private int num = 1;
    private StorageTask upTask;
    private Uri uri;
    private StorageReference storag;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactinity);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel("notify","notofy_channel",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("notify_option");
            NotificationManager notificationManager =(NotificationManager) ContextCompat.getSystemService(this,NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        initialize();
        snad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.equals(idsender) ){
                    Toast.makeText(getApplicationContext(), "can not send to you'r self", Toast.LENGTH_SHORT).show();
                } else {
                    sendmassege();
                    mass.setText("");
                }
            }
        });
        sendfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.equals(idsender)) {
                    Toast.makeText(getApplicationContext(), "can not send to you'r self", Toast.LENGTH_SHORT).show();
                } else {
                    CharSequence sequence[] = new CharSequence[]
                            {
                                    "pictures",
                                    "video",
                                    "word file"
                            };
                    AlertDialog.Builder dialog = new AlertDialog.Builder(chatactinity.this);
                    dialog.setItems(sequence, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                filetype = "image";
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                            }
                            if (which == 1) {
                                filetype = "video";
                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                            }
                            if (which == 2) {
                                filetype = "word";
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
    }
    private void initialize() {

        name=getIntent().getExtras().get("name").toString();
        idsender = getIntent().getExtras().get("id").toString();
        bartool=findViewById(R.id.chattoolbar);
        bartool.setTitle(name);
        setSupportActionBar(bartool);
        snad = (Button) findViewById(R.id.send);
        mass = (EditText) findViewById(R.id.massege);
        storag=FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference("users");
        chatref = FirebaseDatabase.getInstance().getReference("chat");
        auth = FirebaseAuth.getInstance();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.chatrecycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        adpter = new reycleradapter(malist);
        recyclerView.setAdapter(adpter);
        sendfiles = (Button) findViewById(R.id.send_file);
    }
    @Override
    protected void onStart() {
        super.onStart();
        userid = auth.getCurrentUser().getUid().toString();
        chatref.child(userid).child(idsender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                malist.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    massageclass m = snap.getValue(massageclass.class);
                    String noty=m.getNoty();
                    String id=m.getId();
                    if( noty.equalsIgnoreCase("new") && !id.equalsIgnoreCase(userid))
                    {
                        chatref.child(userid).child(idsender).child(snap.getKey()).child("noty").setValue("old");
                        NotificationCompat.Builder notify=new NotificationCompat.Builder(chatactinity.this,"walid")
                                .setSmallIcon(R.drawable.ic_baseline_perm_identity_24)
                                .setContentText(m.getMassage())
                                .setContentTitle(name)
                                .setNumber(1)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(chatactinity.this);
                        managerCompat.notify(0,notify.build());


                    }

                    malist.add(m);
                    adpter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (filetype.equals("image")) {
                uri=data.getData();
               uploadImage();
            }
            else if(filetype.equals("video"))
            {
                uploadvideo();
            }
        }
    }
    private void uploadvideo() {
        if(uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference ref = storag.child("images/"+ UUID.randomUUID().toString());
            upTask=ref.putFile(uri);
            upTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {    progressDialog.dismiss();
                        Uri jk=task.getResult();
                        myuri=jk.toString();
                        Calendar date = Calendar.getInstance();
                        SimpleDateFormat dd = new SimpleDateFormat("ss,mm,hh");
                        String time = dd.format(date.getTime());
                        HashMap<String, Object> fffff = new HashMap<>();
                        fffff.put("time", time);
                        fffff.put("massage", myuri);
                        fffff.put("id", userid);
                        fffff.put("type", filetype);
                        fffff.put("noty","new");
                        fffff.put("name",name);
                        chatref.child(userid).child(idsender).push().setValue(fffff);
                        chatref.child(idsender).child(userid).push().setValue(fffff);
                    }
                }
            } );

        }
    }
    private void sendmassege () {
        String ma = mass.getText().toString();
        if (TextUtils.isEmpty(ma)) {
            Toast.makeText(this, "inter the massage", Toast.LENGTH_SHORT).show();
        } else {

                Calendar date = Calendar.getInstance();
                SimpleDateFormat dd = new SimpleDateFormat("ss,mm,hh");
                String time = dd.format(date.getTime());
                HashMap<String, Object> fffff = new HashMap<>();
                fffff.put("time", time);
                fffff.put("massage", ma);
                fffff.put("id", userid);
                fffff.put("type", "text");
                fffff.put("noty", "new");
                fffff.put("name", name);
                chatref.child(userid).child(idsender).push().setValue(fffff);
                chatref.child(idsender).child(userid).push().setValue(fffff);

            }

    }
    private void uploadImage() {

        if(uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference ref = storag.child("images/"+ UUID.randomUUID().toString());
            upTask=ref.putFile(uri);
            upTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    { progressDialog.dismiss();
                        Uri jk=task.getResult();
                        myuri=jk.toString();
                        Calendar date = Calendar.getInstance();
                        SimpleDateFormat dd = new SimpleDateFormat("ss,mm,hh");
                        String time = dd.format(date.getTime());
                        HashMap<String, Object> fffff = new HashMap<>();
                        fffff.put("time", time);
                        fffff.put("massage", myuri);
                        fffff.put("id", userid);
                        fffff.put("type", filetype);
                        fffff.put("noty","new");
                        fffff.put("name",name);
                        chatref.child(userid).child(idsender).push().setValue(fffff);
                        chatref.child(idsender).child(userid).push().setValue(fffff);
                    }
                }
            } );

        }
    }
}