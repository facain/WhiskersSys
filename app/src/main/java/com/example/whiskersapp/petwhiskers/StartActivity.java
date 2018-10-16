package com.example.whiskersapp.petwhiskers;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;



public class StartActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";
    private static final String TAG = "Start" ;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    private LoginButton loginButton;
    private PrefUtil prefUtil;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private String lastName;
    private String firstName;
    private String email;
    private String id;
    private String contact;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        prefUtil = new PrefUtil(this);

        setContentView(R.layout.activity_start);
        firebaseAuth = FirebaseAuth.getInstance();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.whiskersapp.petwhiskers",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MenuActivity.class));
        }

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference("user_account");
        Log.e("TAG", "handleFacebookAccessToken:" );

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.e("TAG","LOL"); // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });




    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            updateUI(currentUser);


        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            Toast.makeText(getApplicationContext(), "You are login!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StartActivity.this, MenuActivity.class);
            finish();
            startActivity(intent);
        }
    }

    public void signupView(View view){
        Intent intent = new Intent(StartActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void loginView(View view){
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e("firebaseAuthWithGoogle:" ,"" +account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "Start Activity";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("signInWithCredential","success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            getGoogleProfileInfo(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG ,"signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"User Authentication Failed",Toast.LENGTH_SHORT).show();
                        }
                    }


                });

    }
    private void getGoogleProfileInfo(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(user!=null){
            firstName = acct.getGivenName();
            lastName = acct.getFamilyName();
            email = user.getEmail();
            id = user.getUid();
            contact = "";
            password = "";
            storeProfileInfo(firstName, lastName, id, contact, email, password);
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user!=null){
                                String name = user.getDisplayName();
                                if(name.split("\\w+").length>1){

                                    lastName = name.substring(name.lastIndexOf(" ")+1);
                                    firstName = name.substring(0, name.lastIndexOf(' '));
                                }else{
                                    firstName = name;
                                    lastName = "";
                                }
                                email = user.getEmail();
                                id = user.getUid();
                                contact = "";
                                password = "";
                                storeProfileInfo(firstName, lastName, id, contact, email, password);
                            }
                            updateUI(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void storeProfileInfo(String firstName, String lastName, String id, String contact, String email, String password){
        final User logIn = new User(id,firstName,lastName,contact,email,password,"0");
        dbRef.child(id).setValue(logIn);
        dbRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}