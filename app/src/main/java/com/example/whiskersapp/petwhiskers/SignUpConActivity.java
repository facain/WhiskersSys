package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpConActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText cpassword;
    private ProgressDialog progressDialog;

    private FirebaseAuth userAuth;
    private DatabaseReference dbRef;

    int result_auth = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_con);

        dbRef = FirebaseDatabase.getInstance().getReference("user_account");
        userAuth = FirebaseAuth.getInstance();
        if(userAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, MenuActivity.class));

        }

        username = findViewById(R.id.signup_username);
        password = findViewById(R.id.signup_password);
        cpassword = findViewById(R.id.signup_confirm_password);
        progressDialog = new ProgressDialog(this);
    }

    public void createAccount(View view){
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String user_name = username.getText().toString();
        String pword = password.getText().toString();
        String cpword = cpassword.getText().toString();

        if(!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(pword)){
            if(pword.equals(cpword)){
                Intent intent = getIntent();

                String fname = intent.getExtras().getString("fname");
                String lname = intent.getExtras().getString("lname");
                String contact = intent.getExtras().getString("contact");
                String email = intent.getExtras().getString("email");

                if(registerAuth(email,pword) == 1){
                    String id = userAuth.getUid();

                    User user = new User(id,fname,lname,contact,user_name,pword,"0");
                    dbRef.child(id).setValue(user);


                    Toast.makeText(this,"User Added!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Password and Confirm Password not match",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Fill up the blanks",Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();

    }

    public int registerAuth(String email, String password){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Toast.makeText(getApplicationContext(),"User Authentication Successful!", Toast.LENGTH_LONG).show();
                        result_auth=1;
                    }else{
                        Toast.makeText(getApplicationContext(),"Error on Adding User Auth!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return result_auth;
    }

    public void loginView(View view){
        Intent intent = new Intent(SignUpConActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
