package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class PetHomeViewHolder extends PagerAdapter {

    private Context context;
    private List<Pet> petList;
    private LayoutInflater layoutInflater;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public PetHomeViewHolder(Context context, List<Pet> petList){
        this.context = context;
        this.petList = petList;
    }


    @Override
    public int getCount() {
        return petList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (CardView)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        TextView petName, petBreed, petTrans;
        final ImageView imageView;
        CardView homeCardView;
        Button msgBtn;
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.home_cardview,container,false);

        petName = view.findViewById(R.id.pet_featurename);
        petBreed = view.findViewById(R.id.pet_featurebreed);
        petTrans = view.findViewById(R.id.pet_featuretrans);
        imageView = view.findViewById(R.id.pet_featureimg);
        msgBtn = view.findViewById(R.id.pet_featuremsg);
        homeCardView = view.findViewById(R.id.cardview_feature);

        petName.setText(petList.get(position).getPet_name());
        petBreed.setText(petList.get(position).getBreed());
        petTrans.setText(petList.get(position).getTransaction());
        final String img = petList.get(position).getImgUrl();
        if (!img.equals("default_image")) {
            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_image).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(img).placeholder(R.drawable.default_image).into(imageView);
                }
            });
        }
        homeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PetDetails.class);
                intent.putExtra("id",petList.get(position).getId());
                intent.putExtra("owner_id",petList.get(position).getOwner_id());
                context.startActivity(intent);
            }
        });
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_one_id", mAuth.getCurrentUser().getUid());
                intent.putExtra("user_two_id", petList.get(position).getOwner_id());
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((CardView)object);
    }
}
