package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.PetDetails;
import com.example.whiskersapp.petwhiskers.R;
import com.example.whiskersapp.petwhiskers.ViewProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfilePetViewHolder extends RecyclerView.Adapter<ProfilePetViewHolder.ProfileViewHolder>{
    private Context context;
    private List<Pet> petList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ProfilePetViewHolder(Context context, List<Pet> petList){
        this.context = context;
        this.petList = petList;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.profile_pet_layout,parent,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, final int position) {
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
        holder.profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PetDetails.class);
                intent.putExtra("id",petList.get(position).getId());
                intent.putExtra("owner_id",petList.get(position).getOwner_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();

    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView petName, petBreed, petTrans;
        ImageView imageView;
        CardView profileCardView;
        public ProfileViewHolder(View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_profilename);
            petBreed = itemView.findViewById(R.id.pet_profilebreed);
            petTrans = itemView.findViewById(R.id.pet_profilestatus);
            imageView = itemView.findViewById(R.id.pet_profileimg);
            profileCardView = itemView.findViewById(R.id.cardview_profile);
        }
    }
}
