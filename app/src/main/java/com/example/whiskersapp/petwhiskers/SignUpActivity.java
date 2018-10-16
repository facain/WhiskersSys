package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText firstname;
    private EditText lastname;
    private EditText contact;
    private EditText email;
    private EditText password;
    private EditText cpassword;
    private FirebaseAuth userAuth;
    private DatabaseReference dbRef;

    ProgressDialog progressDialog;

    int result_auth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userAuth = FirebaseAuth.getInstance();

        if(userAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, MenuActivity.class));

        }

        firstname = findViewById(R.id.signup_fname);
        lastname = findViewById(R.id.signup_lname);
        contact = findViewById(R.id.signup_contact);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        cpassword = findViewById(R.id.signup_confirm_password);
        dbRef = FirebaseDatabase.getInstance().getReference("user_account");
        progressDialog = new ProgressDialog(this);
    }

    public void registerUser(View view){
        final String fname = firstname.getText().toString();
        final String lname = lastname.getText().toString();
        final String contact_num = contact.getText().toString();
        final String email_add = email.getText().toString();
        final String pword = password.getText().toString();
        final String cpword = cpassword.getText().toString();

        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(contact_num)
                && !TextUtils.isEmpty(email_add) && !TextUtils.isEmpty(pword)){
            if(fname.matches("[a-zA-Z][a-zA-Z ]+") && lname.matches("[a-zA-Z][a-zA-Z ]+")){
                if ((contact_num.length() == 11 || contact_num.length() == 12) && contact_num.matches("[0-9]+")) {
                    if(pword.equals(cpword)){
                        userAuth.createUserWithEmailAndPassword(email_add, pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = userAuth.getUid();
                                    User user = new User(id, fname, lname, contact_num, email_add, pword,"0");
                                    dbRef.child(id).setValue(user);

                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "User Added!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "User authentication Failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Password and Confirm Password doesn't match!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid Contact Number: Ex. 09XX or 639XX!", Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Invalid Input!", Toast.LENGTH_LONG).show();
            }


                }else{
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please fill in the missing field/s!", Toast.LENGTH_LONG).show();
        }

    }


    public void loginView(View view){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}

