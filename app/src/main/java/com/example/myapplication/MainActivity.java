package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView list;
    private DatabaseReference reference;
    private ArrayList<resiclclass> arrayList;
    private String username;
    private Toolbar toolbar;
    private static int splash_opene=0;
    private static int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.mainactivity_toolbar);
        splash_opene();
        toolbar.setTitle("chat");
        setSupportActionBar(toolbar);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        list = (RecyclerView) findViewById(R.id.recitemview);
        list.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<resiclclass>();

    }
    private void fulllist()
    {

        if ( null != auth.getCurrentUser().getUid()){
            FirebaseRecyclerOptions<resiclclass> options = new FirebaseRecyclerOptions.Builder<resiclclass>()
                    .setQuery(reference, resiclclass.class).build();
            FirebaseRecyclerAdapter<resiclclass, adapt> recyclerAdapt = new FirebaseRecyclerAdapter<resiclclass, adapt>(options) {
                @Override
                protected void onBindViewHolder(@NonNull adapt holder, int position, @NonNull resiclclass model) {
                    String ggg = getRef(position).getKey();
                    reference.child(ggg).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String ide = snapshot.child("id").getValue().toString();

                                if (snapshot.hasChild("image")) {
                                    String image = snapshot.child("image").getValue().toString();
                                    Picasso.get().load(image).into(holder.imageitem);
                                }
                                String name = snapshot.child("name").getValue().toString();

                                holder.nameitem.setText(name);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(MainActivity.this, chatactinity.class);
                                        i.putExtra("name", name);
                                        i.putExtra("id", ide);
                                        startActivity(i);
                                    }
                                });
                            

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @NonNull
                @Override
                public adapt onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycllayout, parent, false);
                    adapt jk = new adapt(v);
                    return jk;
                }
            };
            list.setAdapter(recyclerAdapt);
            recyclerAdapt.startListening();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        check_user_auth();
    }
     private void splash_opene()
     {
         if(splash_opene==0) {
             startActivity(new Intent(MainActivity.this, splash.class));
             splash_opene++;
         }
     }

    private void check_user_auth()
   {
       if(user == null)
       {
           gotologin();
       }

       else {
            username =FirebaseAuth.getInstance().getCurrentUser().getUid();
           reference.child(username).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.hasChild("name")) {
                   }
                   else
                   {
                       gotoaccount();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
           fulllist();
       }
   }
    @Override
    protected void onStop() {
        super.onStop();
        if(user!=null) {
            String  u = user.getUid();
        }
    }
    private void gotologin() {
        Intent o=new Intent(MainActivity.this,registar.class);
        startActivity(o);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.login_out:
                auth.signOut();
                gotologin();
                break;
            case R.id.acountname:
                gotoaccount();
                break;
            case R.id.searcht:
               startActivity(new Intent(MainActivity.this,search_activity.class));
        }
        return true;
    }

    private void gotoaccount() {
        Intent i=new Intent(MainActivity.this,acount_activity.class);
        startActivity(i);
    }
    public class adapt extends RecyclerView.ViewHolder {
        TextView nameitem;
        CircleImageView imageitem;

        public adapt(@NonNull View itemView) {
            super(itemView);
            nameitem = (TextView) itemView.findViewById(R.id.textViewname);
            imageitem = (CircleImageView) itemView.findViewById(R.id.circilpicture);

        }
    }
}