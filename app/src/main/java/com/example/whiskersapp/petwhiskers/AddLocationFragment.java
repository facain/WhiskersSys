package com.example.whiskersapp.petwhiskers;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.LocationAddress;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_CANCELED;
import static android.os.Looper.getMainLooper;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class AddLocationFragment extends Fragment implements OnMapReadyCallback {
    private Button getLocationButton;
    private Button btnAddLocation;
    private StorageReference mStoreRef;
    private DatabaseReference mDatabasePetRef,mDatabaseLocRef;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private long UPDATE_INTERVAL = 1000;  /* 10 secs = 10 * 1000 */
    private long FASTEST_INTERVAL = 1000;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1000;
    private Task<LocationSettingsResponse> result;
    protected static final int REQUEST_CHECK_SETTINGS = 2000;
    private LocationResult location;
    private Pet pet;
    private LocationAddress locationAddress;
    private ProgressDialog progressDialog;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_addFrag);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();

        pet = new Pet();
        locationAddress = new LocationAddress();

        pet.setPet_name(bundle.getString("name"));
        pet.setBreed(bundle.getString("breed"));
        pet.setFurcolor(bundle.getString("furcolor"));
        pet.setEyecolor(bundle.getString("eyecolor"));
        pet.setGender(bundle.getString("gender"));
        pet.setCategory(bundle.getString("category"));
        pet.setBirthdate(bundle.getString("bday"));
        pet.setDetails(bundle.getString("desc"));

        mDatabasePetRef = FirebaseDatabase.getInstance().getReference("pet");
        mDatabaseLocRef = FirebaseDatabase.getInstance().getReference("location");
        mStoreRef = FirebaseStorage.getInstance().getReference("pet_entry");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        getLocationButton = (Button) view.findViewById(R.id.getCurrentLocation);
        btnAddLocation = (Button) view.findViewById(R.id.pet_addLocation);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLocationParameters();
            }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    progressDialog.setMessage("Creating Pet Entry...");
                    progressDialog.show();
                    uploadInformation();

                } else {
                    Toast.makeText(getContext(), "Can't get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initLocationParameters() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(locationSettingsRequest);
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        getCurrentLocation(); //from create
                    }

                    // requests here.
                } catch (ApiException exception) {
                    Log.e("Exception", "Test2");
                    /**
                     * Go in exception because Settings->Location is disabled.
                     * First it will Enable Location Services (GPS) then check for run time permission to app.
                     */
                    switch (exception.getStatusCode()) {


                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),

                                /**
                                 * Display enable Enable Location Services (GPS) dialog like Google Map and then
                                 * check for run time permission to app.
                                 */
                                Log.e("check error", "Test1");
                                if (getActivity() == null) {
                                    Log.e("Get Activity", "yes null");
                                }

                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


    }

    private boolean checkPermissions() {
        int permissionState = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            startLocationPermissionRequest();


        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    public void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getActivity());
            mMap.setMyLocationEnabled(true);
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mMap.clear();
                    location = locationResult;
                    LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    ///LatLng loc = new LatLng(10.3804, 123.9645);

                    mMap.addMarker(new MarkerOptions().position(latLng));
                    // mMap.addMarker(new MarkerOptions().position(loc));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    locationAddress.setLatitude(String.valueOf(location.getLastLocation().getLatitude()));
                    locationAddress.setLongitude(String.valueOf(location.getLastLocation().getLongitude()));

                }
            }, getMainLooper());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        Log.e("Activity", "Test");
        Log.d("Activity", "Test1");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (!checkPermissions()) {
                            requestPermissions();
                        } else {
                            getCurrentLocation(); //from onActivityResult
                        }
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d("Activity", "Cancel");
                        location = null;
                        mMap.clear();
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(false);

                        }
                        Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void uploadInformation(){
        Uri imageUri = Uri.parse(bundle.getString("imgURI"));

        StorageReference fileRef = mStoreRef.child(System.currentTimeMillis()
                +"."+getFileExtension(imageUri));
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storeLocation();
                        String petId = mDatabasePetRef.push().getKey();

                        pet.setId(petId);
                        pet.setImgUrl(taskSnapshot.getDownloadUrl().toString());
                        pet.setIsAdopt("yes");
                        pet.setOwner_id(mAuth.getCurrentUser().getUid());
                        pet.setStatus("available");

                        locationAddress.setOwner_id(mAuth.getCurrentUser().getUid());
                        mDatabasePetRef.child(petId).setValue(pet);
                        progressDialog.dismiss();

                        Toast.makeText(getContext(), "Pet Added!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new PetFragment();
                        fragment.setArguments(bundle);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.contentFrame, fragment);
                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Error in uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });

        progressDialog.dismiss();

    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void storeLocation(){
        mDatabaseLocRef.orderByChild("owner_id").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    pet.setLocation_id(locFound.getId());
                }else{
                    String id = mDatabaseLocRef.push().getKey();

                    locationAddress.setId(id);
                    locationAddress.setOwner_id(currentUser);

                    mDatabaseLocRef.child(id).setValue(locationAddress);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
