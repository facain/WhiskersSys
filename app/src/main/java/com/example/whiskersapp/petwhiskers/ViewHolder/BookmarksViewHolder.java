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

import com.example.whiskersapp.petwhiskers.Model.Bookmark;
import com.example.whiskersapp.petwhiskers.PetDetails;
import com.example.whiskersapp.petwhiskers.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarksViewHolder extends RecyclerView.Adapter<BookmarksViewHolder.PetBookmarkViewHolder>{
    private Context context;
    private List<Bookmark> petList;

    public BookmarksViewHolder(Context ctx, List<Bookmark> pets){
        this.context = ctx;
        this.petList = pets;
    }

    @Override
    public PetBookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout
                ,parent, false);
        return new PetBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PetBookmarkViewHolder holder, int position) {
        final Bookmark book = petList.get(position);

        holder.setPetName(book.getPet_name());
        holder.setPetBreed(book.getBreed());
        holder.setPetGender(book.getGender());
        holder.setPetStatus(book.getStatus());
        holder.setPetImage(context, book.getImgUrl());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PetDetails.class);
                intent.putExtra("owner_id", book.getOwner_id());
                intent.putExtra("id", book.getPet_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public class PetBookmarkViewHolder extends RecyclerView.ViewHolder {
        public TextView petBreed, petStatus, petGender,petName;
        public ImageView petImage;
        public CardView cardView;

        public PetBookmarkViewHolder(View itemView) {
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
        public void setPetGender(String petGenderText){ petGender.setText(petGenderText); }
        public void setPetStatus(String petStatusText){
            petStatus.setText(petStatusText);
        }
        public void setPetName(String petNameText){ petName.setText(petNameText); }
        public void setPetImage(final Context context, final String thumb_image) {

            final ImageView pet_image = itemView.findViewById(R.id.pet_image);

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
    }
}
