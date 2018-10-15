package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.User;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountFragment extends Fragment {
    private TextView fname;
    private TextView lname;
    private TextView contact;
    private TextView email;
    private TextView password;
    private Button editProfile;
    private ProgressDialog progressDialog;
    private GoogleSignInAccount gToken;

    FirebaseDatabase fbData;
    DatabaseReference dbRef;
    FirebaseAuth fbAUth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_edit, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.cpefab);
        fab.setVisibility(View.INVISIBLE);
        AccessToken token;
        gToken = GoogleSignIn.getLastSignedInAccount(getContext());
        token = AccessToken.getCurrentAccessToken();
        fname = view.findViewById(R.id.edit_fname);
        lname = view.findViewById(R.id.edit_lname);
        contact = view.findViewById(R.id.edit_contact);
        email = view.findViewById(R.id.edit_email);
        password = view.findViewById(R.id.edit_password);
        editProfile = view.findViewById(R.id.editProfile);
        if(token!=null || gToken !=null){
            password.setVisibility(View.INVISIBLE);
            email.setFocusable(false);
            email.setTextColor(Color.GRAY);
        }
        
        progressDialog = new ProgressDialog(getContext());

        fbAUth = FirebaseAuth.getInstance();
        fbData = FirebaseDatabase.getInstance();
        dbRef = fbData.getReference("user_account");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User test = ds.getValue(User.class);
                    if(fbAUth.getCurrentUser().getEmail().equals(test.getEmail())){
                        fname.setText(test.getFname());
                        lname.setText(test.getLname());
                        contact.setText(test.getContact());
                        email.setText(test.getEmail());
                        password.setText(test.getPassword());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserAccount(getTextValues());
            }
        });

    }

    public User getTextValues(){
        String firstname = fname.getText().toString();
        String lastname = lname.getText().toString();
        String contactnum = contact.getText().toString();
        String emailadd = email.getText().toString();
        String pword = password.getText().toString();
        String id = fbAUth.getUid();

        User user = new User(id,firstname,lastname,contactnum,emailadd,pword,"0");

        return user;
    }

    public void updateUserAccount(final User user){
        progressDialog.setMessage("Updating User...");
        progressDialog.show();
        if(!user.getEmail().equals(fbAUth.getCurrentUser().getEmail())){
            fbAUth.getCurrentUser().updateEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dbRef.child(user.getId()).setValue(user);
                    }else{
                        Toast.makeText(getView().getContext(),"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            fbAUth.getCurrentUser().updatePassword(user.getPassword()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getView().getContext(),"Updated Successfully!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            dbRef.child(user.getId()).setValue(user);
            Toast.makeText(getView().getContext(),"Updated Successfully!",Toast.LENGTH_SHORT).show();
        }

        progressDialog.dismiss();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, new AccountDisplayFragment());
        fragmentTransaction.commit();
    }
}
