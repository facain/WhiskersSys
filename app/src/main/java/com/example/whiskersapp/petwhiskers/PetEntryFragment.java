package com.example.whiskersapp.petwhiskers;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.Pet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PetEntryFragment extends Fragment{
    private EditText name;
    private EditText breed;
    private EditText eyecolor;
    private EditText furcolor;
    private EditText desc;
    private EditText bday;

    private Button proceed;

    private DatePickerDialog.OnDateSetListener mDatePickerDialog;
    private Spinner petGen, petCat, petTrans;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet_entry, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        petGen = view.findViewById(R.id.pet_gender);
        petCat = view.findViewById(R.id.pet_category);
        bday = view.findViewById(R.id.pet_bday);
        name = view.findViewById(R.id.pet_name);
        breed = view.findViewById(R.id.pet_breed);
        eyecolor = view.findViewById(R.id.pet_eyecolor);
        furcolor = view.findViewById(R.id.pet_furcolor);
        desc = view.findViewById(R.id.pet_desc);
        proceed = view.findViewById(R.id.pet_proceedAdd);
        petTrans = view.findViewById(R.id.pet_trans);


        List<String> petGenItem = new ArrayList<String>();
        List<String> petCatItem = new ArrayList<String>();
        List<String> petTransItem = new ArrayList<>();

        petCatItem.add("Dog");
        petCatItem.add("Cat");

        petGenItem.add("Male");
        petGenItem.add("Female");

        petTransItem.add("Free");
        petTransItem.add("Sale");

        ArrayAdapter<String> dataAdapterGen = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, petGenItem
        );

        ArrayAdapter<String> dataAdapterCat = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, petCatItem
        );

        ArrayAdapter<String> dataAdapterTrans = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, petTransItem
        );

        petGen.setAdapter(dataAdapterGen);
        petCat.setAdapter(dataAdapterCat);
        petTrans.setAdapter(dataAdapterTrans);

        bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDatePickerDialog, year, month, day
                );

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                bday.setText(year+"-"+month+"-"+day);
            }
        };

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedRec();
            }
        });
    }

    public void proceedRec() {
        String petName = name.getText().toString();
        String petBreed = breed.getText().toString();
        String petEyecolor = eyecolor.getText().toString();
        String petFurcolor = furcolor.getText().toString();
        String petDesc = desc.getText().toString();
        String category = petCat.getSelectedItem().toString();
        String gender = petGen.getSelectedItem().toString();
        String petBday = bday.getText().toString();
        String trans = petTrans.getSelectedItem().toString();

        if (!petName.isEmpty() && !petBreed.isEmpty() && !petEyecolor.isEmpty() && !petFurcolor.isEmpty()
                && !petDesc.isEmpty() && !category.isEmpty() && !gender.isEmpty() && !petBday.isEmpty()) {

            if(petName.matches("^([a-zA-Z])+(\\s)+[a-zA-Z]+$\n") && petBreed.matches("^([a-zA-Z])+(\\s)+[a-zA-Z]+$\n") && petEyecolor.matches("^([a-zA-Z])+(\\s)+[a-zA-Z]+$\n") && petFurcolor.matches("^([a-zA-Z])+(\\s)+[a-zA-Z]+$\n")){
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = df.format(Calendar.getInstance().getTime());
                Log.e("date",currentDate);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    Date date1 = format.parse(petBday);
                    Date date2 = format.parse(currentDate);

                    if(date1.compareTo(date2) <= 0){
                        Fragment fragment = new AddPetPhotoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("name",petName);
                        bundle.putString("breed", petBreed);
                        bundle.putString("eyecolor", petEyecolor);
                        bundle.putString("furcolor", petFurcolor);
                        bundle.putString("desc", petDesc);
                        bundle.putString("category", category);
                        bundle.putString("gender", gender);
                        bundle.putString("bday", petBday);
                        bundle.putString("trans", trans);

                        fragment.setArguments(bundle);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.contentFrame, fragment);
                        fragmentTransaction.commit();
                    }else{
                        Toast.makeText(getContext(), "Invalid date.", Toast.LENGTH_SHORT).show();

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"Invalid input on something",Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Please fill up the fields.", Toast.LENGTH_SHORT).show();
        }
    }


}