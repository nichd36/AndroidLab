package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SignUp extends AppCompatActivity {
//    private Button seeBtn;
    private EditText usernameEdit, emailEdit, passwordEdit;
    TextView goSignIn;
    Button signUp;
    ImageView google;

    private static final String TAG = "EmailPassword";
    private static final String TAGGoogle = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        usernameEdit = findViewById(R.id.insertUsername);
        emailEdit = findViewById(R.id.insertEmail);
        passwordEdit = findViewById(R.id.insertPassword);
        goSignIn = findViewById(R.id.signin);
        signUp = findViewById(R.id.btnSignUp);
        google = findViewById(R.id.google);
        setUpListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if(currentUser != null){
            reload();
            Intent goHome = new Intent(SignUp.this, Homepage.class);
            startActivity(goHome);
            finish();
            overridePendingTransition(0, 0);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAGGoogle, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
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
                        Intent goHome = new Intent(SignUp.this, Homepage.class);
                        startActivity(goHome);
                    } else {
                        Log.w(TAGGoogle, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                }
            });
    }

    private void createAccount(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        userName(username);
                        updateUI(user);
                        Intent goHome = new Intent(SignUp.this, Homepage.class);
                        startActivity(goHome);
                        usernameEdit.setText("");
                        emailEdit.setText("");
                        passwordEdit.setText("");

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.signupscreen), "User with that email already exist!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        updateUI(null);
                    }
                }
            });
    }

    private void userName(String username) {
        UserProfileChangeRequest setName = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        FirebaseUser user = mAuth.getCurrentUser();
        user.updateProfile(setName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User display name updated.");
                }
            }
        });
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {
    }

    private void setUpListeners() {
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSignIn = new Intent(SignUp.this, SignIn.class);
                startActivity(goSignIn);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (username.isEmpty() || email.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.signupscreen), "Make sure username and email is not empty", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }else{
                    if(password.length()<6){
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.signupscreen), "Password need to have at least 6 characters", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                }
                    if(!isEmail(emailEdit)){
                        Toast.makeText(SignUp.this, "Please make sure your email is correct", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        createAccount(email,password, username);
                    }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
