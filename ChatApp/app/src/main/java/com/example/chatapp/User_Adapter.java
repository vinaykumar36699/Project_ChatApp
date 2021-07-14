package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Adapter extends RecyclerView.Adapter<User_Adapter.Viewholder> {
    Context homeActivity;
    ArrayList<Users> usersArrayList;
    public User_Adapter(Home_Activity home_activity, ArrayList<Users> usersArrayList) {
        this.homeActivity = home_activity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull User_Adapter.Viewholder holder, int position) {
        Users users = usersArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUid()))
        {
            holder.itemView.setVisibility(View.GONE);
        }


        holder.user_name.setText(users.name);
        holder.user_status.setText(users.status);
        Picasso.get().load(users.getImageUri()).placeholder(R.drawable.profile_logo).into(holder.user_profile);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity,Chat_Activity.class);
                intent.putExtra("name",users.getName());
                intent.putExtra("Reciever Image",users.getImageUri());
                intent.putExtra("uid",users.getUid());
                homeActivity.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{
        CircleImageView user_profile;
        TextView user_name;
        TextView user_status;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            user_profile = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_status = itemView.findViewById(R.id.user_status);
        }
    }
}
