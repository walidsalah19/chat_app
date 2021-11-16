package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class reycleradapter extends RecyclerView.Adapter<reycleradapter.recyler> {
    private FirebaseAuth auth;
   private List<massageclass> massagelist;
   public reycleradapter(List<massageclass> massagelist)
   {
       this.massagelist=massagelist;
   }

    @NonNull
    @Override
    public recyler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recivedatachat,parent,false);
       recyler jk=new recyler(v);
       auth=FirebaseAuth.getInstance();
       return jk;
    }

    @Override
    public void onBindViewHolder(@NonNull recyler holder, int position) {
        String iduser = auth.getCurrentUser().getUid();
        massageclass massage = massagelist.get(position);
        String idsend = massage.getId();
       String type=massage.getType();
       String noty=massage.getNoty();
        holder.sendertextmassage.setVisibility(View.INVISIBLE);
        holder.recivertextmassage.setVisibility(View.INVISIBLE);
        holder.imageViewreciver.setVisibility(View.INVISIBLE);
        holder.imagesender.setVisibility(View.INVISIBLE);

        if (type.equals("text")) {
            if (iduser.equals(idsend)) {
                holder.sendertextmassage.setVisibility(View.VISIBLE);
                holder.sendertextmassage.setBackgroundResource(R.drawable.reciver);
                holder.sendertextmassage.setText(massage.getMassage());
                holder.sendertextmassage.setTextColor(Color.RED);

            } else {
                holder.recivertextmassage.setVisibility(View.VISIBLE);
                holder.recivertextmassage.setBackgroundResource(R.drawable.sender);
                holder.recivertextmassage.setText(massage.getMassage());
                holder.recivertextmassage.setTextColor(Color.BLACK);
            }
        }
        else if(type.equals("image"))
        {
            if(iduser.equals(idsend))
            {
                holder.imagesender.setVisibility(View.VISIBLE);
                holder.imagesender.setMaxHeight(50);
                holder.imagesender.setMaxWidth(20);
                Picasso.get().load(massage.getMassage()).into(holder.imagesender);
            }
            else
            {
                holder.imageViewreciver.setVisibility(View.VISIBLE);
                Picasso.get().load(massage.getMassage()).into(holder.imageViewreciver);
            }
        }

    }

    @Override
    public int getItemCount() {
        return massagelist.size();
    }

    public class recyler extends RecyclerView.ViewHolder{
             TextView recivertextmassage,sendertextmassage;
             ImageView imagesender,imageViewreciver;
        public recyler(@NonNull View itemView) {
            super(itemView);
            recivertextmassage=(TextView) itemView.findViewById(R.id.chateresiver);
            sendertextmassage=(TextView)itemView.findViewById(R.id.chatesender);
            imagesender=(ImageView) itemView.findViewById(R.id.imageViewmassagesender);
            imageViewreciver=(ImageView) itemView.findViewById(R.id.imageViewresiver);
        }
    }

}
