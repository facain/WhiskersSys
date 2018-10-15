package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.ChatActivity;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.PetDetails;
import com.example.whiskersapp.petwhiskers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PetHomeViewHolder extends RecyclerView.Adapter<PetHomeViewHolder.HomeViewHolder>{

    private Context context;
    private List<Pet> petList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public PetHomeViewHolder(Context context, List<Pet> petList){
        this.context = context;
        this.petList = petList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.home_cardview,parent,false);

        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        holder.petName.setText(petList.get(position).getPet_name());
        holder.petBreed.setText(petList.get(position).getBreed());
        holder.petTrans.setText(petList.get(position).getTransaction());
        final String img = petList.get(position).getImgUrl();
        if (!img.equals("default_image")) {
            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_image).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(img).placeholder(R.drawable.default_image).into(holder.imageView);
                }
            });
        }
        holder.homeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PetDetails.class);
                intent.putExtra("id",petList.get(position).getId());
                intent.putExtra("owner_id",petList.get(position).getOwner_id());
                context.startActivity(intent);
            }
        });
        holder.msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_one_id", mAuth.getCurrentUser().getUid());
                intent.putExtra("user_two_id", petList.get(position).getOwner_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder{
        TextView petName, petBreed, petTrans;
        ImageView imageView;
        CardView homeCardView;
        Button msgBtn;
        public HomeViewHolder(View itemView){
            super(itemView);
            petName = itemView.findViewById(R.id.pet_featurename);
            petBreed = itemView.findViewById(R.id.pet_featurebreed);
            petTrans = itemView.findViewById(R.id.pet_featuretrans);
            imageView = itemView.findViewById(R.id.pet_featureimg);
            msgBtn = itemView.findViewById(R.id.pet_featuremsg);
            homeCardView = itemView.findViewById(R.id.cardview_feature);


        }
    }



}
