package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;
import android.content.pm.PackageManager;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class AddPetPhotoFragment extends Fragment {
    private Button btnfilechoose;
    private Button btnTakePic;
    private Button btnAddPet;
    private ImageView imagePreview;
    private Bitmap imagePic;
    private Pet pet;
    private Uri file;
    private File imageFile;
    private Uri imageUri;
    private StorageReference mStoreRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private ThumbnailUtils thumbnail;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pet, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        pet = new Pet();

        pet.setPet_name(bundle.getString("name"));
        pet.setBreed(bundle.getString("breed"));
        pet.setFurcolor(bundle.getString("furcolor"));
        pet.setEyecolor(bundle.getString("eyecolor"));
        pet.setGender(bundle.getString("gender"));
        pet.setCategory(bundle.getString("category"));
        pet.setBirthdate(bundle.getString("bday"));
        pet.setDetails(bundle.getString("desc"));
        pet.setTransaction(bundle.getString("trans"));
        pet.setIsAdopt("no");
        pet.setVerStat("0");

        Date tentime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
        final String time = df.format(tentime);

        pet.setDatePost(time);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pet");
        mStoreRef = FirebaseStorage.getInstance().getReference("pet_entry");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        btnfilechoose = view.findViewById(R.id.pet_btnupload);
        btnTakePic = view.findViewById(R.id.pet_btnCamera);
        imagePreview = view.findViewById(R.id.pet_image_preview);
        btnAddPet = view.findViewById(R.id.pet_addEntry);

        btnfilechoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageFile();
            }
        });
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePicture();
            }
        });

        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog  = new ProgressDialog(getActivity());
                progressDialog.setMessage("Creating Pet Entry...");
                progressDialog.show();
                uploadFile();


            }
        });

    }

    public void takePicture(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT,file);

        startActivityForResult(intent,2);

    }
    public void openImageFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            mediaStorageDir.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File image_file = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return image_file;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            //Picasso.with(getContext()).load(imageUri).into(imagePreview);



            imagePreview.setImageURI(imageUri);
        }else if (requestCode == 2 && resultCode == RESULT_OK ){
            System.out.println("ACAINNN"+file.toString());
            System.out.println("HELLOWW");
            try {
                System.out.println("ACAINNN"+file.toString());
                System.out.println("HELLOWW");
                imagePic = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),file);
                imagePreview.setImageBitmap( thumbnail.extractThumbnail(imagePic,imagePic.getWidth(),imagePic.getHeight()));
            }catch (Exception e){
                e.printStackTrace();
            }




        }
    }





    private void uploadFile(){

        if(imageUri != null){

            StorageReference fileRef = mStoreRef.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String id = mDatabaseRef.push().getKey();

                            pet.setId(id);
                            pet.setImgUrl(taskSnapshot.getDownloadUrl().toString());
                            pet.setIsAdopt("no");
                            pet.setOwner_id(mAuth.getCurrentUser().getUid());
                            pet.setStatus("available");
                            progressDialog.cancel();

                            mDatabaseRef.child(id).setValue(pet);
                            Toast.makeText(getContext(), "Pet Added!", Toast.LENGTH_SHORT).show();
                            Fragment fragment = new PetFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.contentFrame, fragment);
                            fragmentTransaction.commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(),"Error in uploading image.", Toast.LENGTH_SHORT).show();
                        }
                    });


            }else if(imagePic!=null){
            StorageReference fileRef = mStoreRef.child(file.toString());
            fileRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String id = mDatabaseRef.push().getKey();

                            pet.setId(id);
                            pet.setImgUrl(taskSnapshot.getDownloadUrl().toString());
                            pet.setIsAdopt("no");
                            pet.setOwner_id(mAuth.getCurrentUser().getUid());
                            pet.setStatus("available");

                            mDatabaseRef.child(id).setValue(pet);

                            Toast.makeText(getContext(), "Pet Added!", Toast.LENGTH_SHORT).show();
                            Fragment fragment = new PetFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.contentFrame, fragment);
                            fragmentTransaction.commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(),"Error in uploading image.", Toast.LENGTH_SHORT).show();
                        }
                    });}else{
            Toast.makeText(getContext(), "No file uploaded.", Toast.LENGTH_SHORT).show();

        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}