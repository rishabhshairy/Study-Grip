package com.example.rishabh.studygrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthGoogle extends AppCompatActivity {

     private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mSignInClient;
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();


        mSignInClient= GoogleSignIn.getClient(this,gso);
        mFirebaseAuth=FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is already signed in
        FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAccount=signInAccountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(signInAccount);
            } catch (ApiException e) {
                Toast.makeText(this, "Unable to signIn", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        /*
        After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
         exchange it for a Firebase credential,
         and authenticate with Firebase using the Firebase credential:
         */

        AuthCredential credential= GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("GoogleSignIn","Successfull Login");
                            FirebaseUser user=mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.d("GoogleSignIn","Unsuccessfull sign in");
                            Snackbar.make(findViewById(R.id.auth_layout),"Unsuccessfull Login",1500);
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser account) {
        if (account!=null){
         Intent   acc=new Intent(this,MainActivity.class);
            startActivity(acc);
        }
        else {
          return;
        }

    }



}
