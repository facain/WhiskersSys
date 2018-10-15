package com.example.whiskersapp.petwhiskers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private EditText email_add;
    private EditText pword;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            finish();
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("email",mAuth.getCurrentUser().getEmail());

            progressDialog.dismiss();
            startActivity(intent);
        }

        email_add = findViewById(R.id.login_email);
        pword = findViewById(R.id.login_password);

    }


    public void loginUser(View view) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        String email = email_add.getText().toString();
        String password = pword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please fill in the missing field/s",Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(mAuth.getCurrentUser() != null){
                            progressDialog.cancel();
                            //Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Username or Password does not exist!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void signUpView(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
