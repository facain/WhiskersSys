package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditPetEntry extends AppCompatActivity {
    private TextView petName;
    private TextView petBreed;
    private Spinner petGender;
    private TextView petFurcolor;
    private TextView petEyecolor;
    private TextView petBday;
    private TextView petDesc;
    private Spinner petCategory;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;

    List<String> petGenderList;
    List<String> petCategoryList;

    private ProgressDialog progressDialog;

    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_edit);

        petName = findViewById(R.id.petedit_name);
        petBreed = findViewById(R.id.petedit_breed);
        petGender = findViewById(R.id.petedit_gender);
        petEyecolor = findViewById(R.id.petedit_eyecolor);
        petCategory = findViewById(R.id.petedit_category);
        petDesc = findViewById(R.id.petedit_desc);
        petBday = findViewById(R.id.petedit_bday);
        petFurcolor = findViewById(R.id.petedit_furcolor);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        petGenderList = new ArrayList<String>();
        petCategoryList = new ArrayList<String>();

        petGenderList.add("Male");
        petGenderList.add("Female");

        petCategoryList.add("Dog");
        petCategoryList.add("Cat");

        final ArrayAdapter<String> dataGenAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, petGenderList);
        final ArrayAdapter<String> dataCatAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, petCategoryList);

        petGender.setAdapter(dataGenAdapter);
        petCategory.setAdapter(dataCatAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();

        id = getIntent().getStringExtra("pet_id");
        dbRef = firebaseDatabase.getReference("pet");

        dbRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Pet test = dataSnapshot.getValue(Pet.class);

                    petName.setText(test.getPet_name());
                    petBreed.setText(test.getBreed());
                    petEyecolor.setText(test.getEyecolor());
                    petFurcolor.setText(test.getFurcolor());
                    petDesc.setText(test.getDetails());
                    petBday.setText(test.getBirthdate());

                    int posGen = dataGenAdapter.getPosition(test.getGender());
                    petGender.setSelection(posGen);

                    int posCat = dataCatAdapter.getPosition(test.getCategory());
                    petCategory.setSelection(posCat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateEntry(View view){
        progressDialog.setMessage("Updating data...");
        progressDialog.show();
        final String name = petName.getText().toString();
        final String breed = petBreed.getText().toString();
        final String eyecolor = petEyecolor.getText().toString();
        final String furcolor = petFurcolor.getText().toString();
        final String desc = petDesc.getText().toString();
        final String category = petCategory.getSelectedItem().toString();
        final String gender = petGender.getSelectedItem().toString();
        final String bday = petBday.getText().toString();
        final String id = getIntent().getStringExtra("pet_id");

        DatabaseReference tabPet = firebaseDatabase.getReference("pet");
        tabPet.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (!name.isEmpty() && !breed.isEmpty() && !eyecolor.isEmpty() && !furcolor.isEmpty()
                            && !desc.isEmpty() && !category.isEmpty() && !gender.isEmpty() && !bday.isEmpty()) {
                        Pet pet = dataSnapshot.getValue(Pet.class);

                        pet.setPet_name(name);
                        pet.setBirthdate(bday);
                        pet.setBreed(breed);
                        pet.setGender(gender);
                        pet.setEyecolor(eyecolor);
                        pet.setFurcolor(furcolor);
                        pet.setCategory(category);
                        pet.setDetails(desc);

                        dbRef.child(id).setValue(pet);
                        Toast.makeText(getApplicationContext(), "Pet Entry Updated!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditPetEntry.this, PetDetailsEdit.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();

    }
}
