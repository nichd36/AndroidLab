package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SignIn extends AppCompatActivity {
    EditText email, password;
    TextView goSignUp, forgotPass;
    Button btnSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAGGoogle = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        email = findViewById(R.id.insertEmail);
        password = findViewById(R.id.insertPassword);
        goSignUp = findViewById(R.id.signup);
        forgotPass = findViewById(R.id.forgetpassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        ImageView google = findViewById(R.id.google);
        mAuth = FirebaseAuth.getInstance();

        setUpListeners();

        GoogleSignInOptions gso = new
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAGGoogle, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAGGoogle, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAGGoogle, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Intent goHome = new Intent(SignIn.this, Homepage.class);
                        startActivity(goHome);
                    } else {
                        Log.w(TAGGoogle, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                }
            });
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Intent goHome = new Intent(SignIn.this, Homepage.class);
                        startActivity(goHome);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    private void updateUI(FirebaseUser user) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setUpListeners(){
        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSignUp = new Intent(SignIn.this, SignUp.class);
                startActivity(goSignUp);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goForgotPass = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(goForgotPass);
            }
        });
    }

    void checkCredentials(){
        if(isEmpty(email)){
            email.setError("Email cannot be empty");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.signinscreen), "Make sure email field is not empty", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        if(isEmpty(password)){
            password.setError("Password cannot be empty");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.signinscreen), "Make sure password field is not empty", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        String strEmail = email.getText().toString();
        String strPass = password.getText().toString();
        signIn(strEmail,strPass);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
