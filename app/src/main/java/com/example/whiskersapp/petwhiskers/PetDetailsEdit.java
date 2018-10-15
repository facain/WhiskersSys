package com.example.whiskersapp.petwhiskers;

import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class PetDetailsEdit extends AppCompatActivity {
    TextView petBreed, petDesc, status;
    TextView furcolor, eyecolor, birthdate, category, gender;
    ImageView petImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fab;
    String imgurl;
    Toolbar toolbar;

    String id = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference table_pet_entry;
    private FirebaseStorage dbImage;
    private StorageReference storeImg;

    private AlertDialog.Builder choice;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailsedit);
        toolbar = (Toolbar) findViewById(R.id.toolbar_pet);
        toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        table_pet_entry = firebaseDatabase.getReference("pet");
        dbImage = FirebaseStorage.getInstance();

        fab = (FloatingActionButton)findViewById(R.id.fav_fab);
        petImage =(ImageView)findViewById(R.id.img_petedit);
        petBreed = (TextView)findViewById(R.id.petedits_breed);
        petDesc = (TextView)findViewById(R.id.petedits_description_details);
        furcolor = findViewById(R.id.petedits_furcolor);
        eyecolor = findViewById(R.id.petedits_eyecolor);
        category = findViewById(R.id.petedits_category);
        birthdate = findViewById(R.id.petedits_birthdate);
        gender = findViewById(R.id.petedits_gender);
        status = findViewById(R.id.petedits_status);

        collapsingToolbarLayout = findViewById(R.id.collapsing_petedit);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            if(!id.isEmpty()){
                getPetDetails(id);
            }
        }
    }

    private void getPetDetails(String id) {
        table_pet_entry.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pet pet = dataSnapshot.getValue(Pet.class);
                final String imageText = pet.getImgUrl();


                collapsingToolbarLayout.setTitle(pet.getPet_name());
                petBreed.setText(pet.getBreed());
                petDesc.setText(pet.getDetails());
                status.setText(pet.getStatus());
                eyecolor.setText(pet.getEyecolor());
                furcolor.setText(pet.getFurcolor());
                category.setText(pet.getCategory());
                gender.setText(pet.getGender());
                birthdate.setText(pet.getBirthdate());

                imgurl = pet.getImgUrl();

                if (!imageText.equals("default_image")) {
                    Picasso.with(getBaseContext()).load(pet.getImgUrl()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_image).into(petImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getBaseContext()).load(imageText).placeholder(R.drawable.default_image).into(petImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editProfile(View view){
        Intent intent = new Intent(PetDetailsEdit.this, EditPetEntry.class);
        String petId = getIntent().getStringExtra("id");
        intent.putExtra("pet_id", petId);
        startActivity(intent);
    }

    public void removeProfile(View view){
        final String id = getIntent().getStringExtra("id");
        final DatabaseReference tabPet = firebaseDatabase.getReference("pet");

        choice = new AlertDialog.Builder(this);
        choice.setTitle("Are you sure to delete your entry?");

        choice.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if(!imgurl.isEmpty()){
                    storeImg = dbImage.getReferenceFromUrl(imgurl);
                    storeImg.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            tabPet.child(id).removeValue();
                            Intent intent = new Intent(PetDetailsEdit.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "No data URL received!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        choice.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert = choice.create();
        alert.show();
    }

    public void markAdopt(View view){
        final String petId = getIntent().getStringExtra("id");

        if(!TextUtils.isEmpty(petId)){
            table_pet_entry.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Pet pet = null;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Pet test = ds.getValue(Pet.class);

                        if(test.getId().equals(petId)){
                            pet = test;
                        }
                    }

                    if(pet != null){
                        pet.setIsAdopt("yes");
                        table_pet_entry.child(pet.getId()).setValue(pet);
                        Toast.makeText(getApplicationContext(), "Pet Marked as Adopted", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
