package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPetPhoto extends AppCompatActivity {
    private static final int REQUEST_CODE = 1000;

    private Button btnfilechoose;
    private Button btnTakePic;
    private Button btnEditPet;
    private ImageView imagePreview;
    private Toolbar toolbar;
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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference table_pet_entry;

    private String id = "";
    private String imageText = "";

    private StorageReference storeImg;
    private FirebaseStorage dbImage;

    private AlertDialog.Builder choice;
    private AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet_photo);

        btnfilechoose = findViewById(R.id.petedit_btnupload);
        btnTakePic = findViewById(R.id.petedit_btnCamera);
        imagePreview = findViewById(R.id.petedit_image_preview);
        btnEditPet = findViewById(R.id.petedit_addEntry);
        toolbar = findViewById(R.id.toolbar_edit_photo);

        toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
        setSupportActionBar(toolbar);
        pet = new Pet();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog  = new ProgressDialog(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        table_pet_entry = firebaseDatabase.getReference("pet");
        mStoreRef = FirebaseStorage.getInstance().getReference("pet_entry");
        mAuth = FirebaseAuth.getInstance();
        dbImage = FirebaseStorage.getInstance();
        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            if(!id.isEmpty()){
                getPetPhoto(id);
                btnTakePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture();
                    }
                });
                btnfilechoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openImageFile();
                    }
                });
                btnEditPet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("Creating Pet Entry...");
                        progressDialog.show();
                        uploadFile(id);
                    }
                });
            }
        }
    }

    private void uploadFile(final String id) {
        choice = new AlertDialog.Builder(this);
        choice.setTitle("Are you sure to update your pet image?");

        choice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                progressDialog.setMessage("Updating data...");
                progressDialog.show();
                if(!imageText.isEmpty()){
                    storeImg = dbImage.getReferenceFromUrl(imageText);
                    storeImg.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                        }
                    });
                    if(imageUri != null){

                        storeImgFromGallery();

                    }else if(imagePic!=null) {

                        storeImgTakePic();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "No file uploaded.", Toast.LENGTH_SHORT).show();

                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No data URL received!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        choice.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert = choice.create();
        alert.show();
    }

    private void storeImgTakePic() {
        StorageReference fileRef = mStoreRef.child(file.toString());
        fileRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pet.setImgUrl(taskSnapshot.getDownloadUrl().toString());

                        table_pet_entry.child(id).setValue(pet);
                        Toast.makeText(getApplicationContext(), "Pet Updated!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPetPhoto.this, MenuActivity.class);
                        startActivity(intent);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error in uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeImgFromGallery() {
        StorageReference fileRef = mStoreRef.child(System.currentTimeMillis()
                +"."+getFileExtension(imageUri));
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.cancel();
                        String imgUrl = taskSnapshot.getDownloadUrl().toString();
                        if(imgUrl != null){
                            pet.setImgUrl(taskSnapshot.getDownloadUrl().toString());
                        }

                        table_pet_entry.child(id).setValue(pet);
                        Toast.makeText(getApplicationContext(), "Pet Updated!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditPetPhoto.this, MenuActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Error in uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImageFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT,file);

        startActivityForResult(intent,2);
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
                System.out.println("ACAINNN"+file.toString()); //WEW hahaha
                System.out.println("HELLOWW");
                imagePic = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),file);
                imagePreview.setImageBitmap( thumbnail.extractThumbnail(imagePic,imagePic.getWidth(),imagePic.getHeight()));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void getPetPhoto(String id) {
        table_pet_entry.child(id).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                pet = dataSnapshot.getValue(Pet.class);
                imageText = pet.getImgUrl();


                if (!imageText.equals("default_image")) {
                    Picasso.with(getBaseContext()).load(pet.getImgUrl()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_image).into(imagePreview, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getBaseContext()).load(imageText).placeholder(R.drawable.default_image).into(imagePreview);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
