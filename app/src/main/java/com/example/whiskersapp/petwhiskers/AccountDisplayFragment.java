package com.example.whiskersapp.petwhiskers;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.whiskersapp.petwhiskers.Model.Bookmark;
import com.example.whiskersapp.petwhiskers.Model.LocationAddress;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.Model.User;
import com.example.whiskersapp.petwhiskers.Model.UserChat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountDisplayFragment extends Fragment {
    private static final int REQUEST_CODE = 1000;

    private Button editProfile;
    private TextView header_name;
    private TextView fname;
    private TextView lname;
    private TextView contact;
    private TextView email;
    private Button removeProfile;
    private Button updateLocation;

    private AlertDialog.Builder choice;
    private AlertDialog alert;

    FirebaseDatabase fbData;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DatabaseReference dbLoc;
    DatabaseReference dbPet;
    DatabaseReference dbBookmark;
    DatabaseReference dbChat;

    private FusedLocationProviderClient fusedLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account_display, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Account");
        FloatingActionButton fab = getActivity().findViewById(R.id.cpefab);
        fab.setVisibility(View.INVISIBLE);
        editProfile = view.findViewById(R.id.acctdisplay_edit);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.contentFrame, new EditAccountFragment()).addToBackStack("tag");
                fragmentTransaction.commit();
            }
        });

        fname = view.findViewById(R.id.acctdisplay_fname);
        lname = view.findViewById(R.id.acctdisplay_lname);
        contact = view.findViewById(R.id.acctdisplay_contact);
        email = view.findViewById(R.id.acctdisplay_email);
        header_name = view.findViewById(R.id.acct_header_name);
        updateLocation = view.findViewById(R.id.acctdisplay_location);

        mAuth = FirebaseAuth.getInstance();
        fbData = FirebaseDatabase.getInstance();
        dbRef = fbData.getReference("user_account");
        dbLoc = fbData.getReference("location");
        dbPet = fbData.getReference("pet");
        dbBookmark = fbData.getReference("bookmark");
        dbChat = fbData.getReference("user_chat");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    if (mAuth.getCurrentUser().getEmail().equals(user.getEmail())) {
                        header_name.setText(user.getFname() + " " + user.getLname());
                        fname.setText(user.getFname());
                        lname.setText(user.getLname());
                        contact.setText(user.getContact());
                        email.setText(user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        choice = new AlertDialog.Builder(getView().getContext());
        choice.setTitle("Are you sure to delete your account?");

        choice.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                deleteMessage(mAuth.getCurrentUser().getUid());
                deleteBookmark(mAuth.getCurrentUser().getUid());
                deleteEntry(mAuth.getCurrentUser().getUid());
                dbRef.child(mAuth.getCurrentUser().getUid()).removeValue();
                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getView().getContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            Intent intent = new Intent(getContext(), StartActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                //Toast.makeText(getView().getContext(),"Account Deleted!",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        choice.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert = choice.create();

        removeProfile = view.findViewById(R.id.acctdisplay_remove);
        removeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            buildLocationRequest();
            buildLocationCallback();

            fusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());

            updateLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        return;
                    }
                    fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });
        }
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for(Location location: locationResult.getLocations()){
                    String latitude = String.valueOf(location.getLatitude());
                    String longtitude = String.valueOf(location.getLongitude());
                    storeLocation(latitude, longtitude);
                }
            }
        };
    }

    public void storeLocation(final String latitude, final String longtitude){
        dbLoc.orderByChild("owner_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationAddress locFound = null;
                LocationAddress test = null;
                String currentUser = mAuth.getCurrentUser().getUid();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    test = ds.getValue(LocationAddress.class);

                    if(test.getOwner_id().equals(currentUser)){
                        locFound = test;
                    }
                }

                if(locFound != null){
                    locFound.setLongitude(longtitude);
                    locFound.setLatitude(latitude);

                    dbLoc.child(locFound.getId()).setValue(locFound);
                    Toast.makeText(getContext(), "Location updated!", Toast.LENGTH_SHORT).show();
                    updatePetLocation(locFound.getId());
                }else{
                    LocationAddress loc = new LocationAddress();
                    String id = dbLoc.push().getKey();

                    loc.setId(id);
                    loc.setLatitude(latitude);
                    loc.setLongitude(longtitude);
                    loc.setOwner_id(currentUser);

                    dbLoc.child(id).setValue(loc);
                    updatePetLocation(loc.getId());
                    Toast.makeText(getContext(), "Location added!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updatePetLocation(final String id_location){
        final String currentUser = mAuth.getCurrentUser().getUid();

        dbPet.orderByChild("owner_id").equalTo(currentUser);
        dbPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Pet pet = ds.getValue(Pet.class);

                    if(pet.getOwner_id().equals(currentUser)){
                        pet.setLocation_id(id_location);

                        dbPet.child(pet.getId()).setValue(pet);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(REQUEST_CODE){
            case REQUEST_CODE:
            {
                if(grantResults.length >0 ){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    }else if(grantResults[0] == PackageManager.PERMISSION_DENIED){

                    }
                }
            }
        }
    }

    public void deleteEntry(final String id){
        dbPet.orderByChild("owner_id").equalTo(id);
        dbPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Pet test = ds.getValue(Pet.class);

                    if(test.getOwner_id().equals(id)){
                        dbPet.child(test.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteBookmark(final String id){
        dbBookmark.orderByChild("bookmark_user_id").equalTo(id);
        dbBookmark.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Bookmark test = ds.getValue(Bookmark.class);

                    if(test.getBookmark_user_id().equals(id)){
                        dbBookmark.child(test.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteMessage(final String id){
        dbChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    UserChat test = ds.getValue(UserChat.class);

                    if(test.getUser_one_id().equals(id) || test.getUser_two_id().equals(id)){
                        dbChat.child(test.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
