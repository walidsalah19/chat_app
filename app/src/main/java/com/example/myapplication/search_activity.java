package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class search_activity extends AppCompatActivity {
    private ListView listView;
    private DatabaseReference reference;
    private ArrayList<resiclclass> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);
        SearchView searchView = findViewById(R.id.search_tool);
        reference = FirebaseDatabase.getInstance().getReference("users");
        arrayList=new ArrayList<>();
        listView=findViewById(R.id.list_search);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=arrayList.get(position).getName();
                String ides=arrayList.get(position).getId();
                Intent i=new Intent(search_activity.this,chatactinity.class);
                      i.putExtra("name",name);
                      i.putExtra("id",ides);
                      startActivity(i);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                String id = snap.getKey();
                                reference.child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String name = snapshot.child("name").getValue().toString();
                                            if (name.equals(newText)) {
                                                arrayList.clear();
                                                String image = snapshot.child("image").getValue().toString();
                                                String ide = snapshot.child("id").getValue().toString();
                                                arrayList.add(new resiclclass(image, name, ide));
                                            }
                                        }
                                        listadapt ld = new listadapt();
                                        listView.setAdapter(ld);

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }

        });
    }

    public class listadapt extends BaseAdapter
    {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.recycllayout, parent, false);
            TextView name=(TextView) v.findViewById(R.id.textViewname);
            CircleImageView circleImageView=(CircleImageView) v.findViewById(R.id.circilpicture);
            name.setText(arrayList.get(position).getName());
            Picasso.get().load(arrayList.get(position).getImage()).into(circleImageView);
            return v;
        }
    }
}