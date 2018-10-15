package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FindPetViewHolder extends RecyclerView.ViewHolder{

        public static TextView petBreed, petStatus, petGender,petName;
        public static ImageView petImage;
        String currency = "₱";
        public static CardView cardView;

        public FindPetViewHolder(View itemView) {
            super(itemView);

            petImage = (ImageView)itemView.findViewById(R.id.pet_image);
            petName = (TextView)itemView.findViewById(R.id.pet_name);
            petBreed = (TextView)itemView.findViewById(R.id.pet_breed);
            petGender = (TextView)itemView.findViewById(R.id.pet_gender);
            petStatus = (TextView)itemView.findViewById(R.id.pet_sale);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
        }
        public void setPetBreed(String petBreedText){
            petBreed.setText(petBreedText);
        }
        public void setPetGender(String petGenderText){
            petGender.setText(petGenderText);
           /* if(petPriceText.equals("Free") || petPriceText.equals("free")){
                currency = "";
            }
           // petPrice.setText(currency+petPriceText);*/

        }
        public void setPetStatus(String petStatusText){
            petStatus.setText(petStatusText);
        }

    public void setPetImage(final Context context, final String thumb_image) {
        final ImageView pet_image = (ImageView) itemView.findViewById(R.id.pet_image);

        if (!thumb_image.equals("default_image")) {
            Picasso.with(context).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_image).into(pet_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_image).into(pet_image);
                }
            });
        }
    }
    public void setPetName(String petNameText){ petName.setText(petNameText); }

}
