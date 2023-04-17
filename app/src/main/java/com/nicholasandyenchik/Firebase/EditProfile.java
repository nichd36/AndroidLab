package com.nicholasandyenchik.Firebase;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button save, cancel;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        save = findViewById(R.id.saveBtn);
        cancel = findViewById(R.id.cancel);
        username = findViewById(R.id.editName);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(username)){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.editnamescreen), "Make sure username field is not empty", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else{
                    String usern = username.getText().toString();
                    userName(usern);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void userName(String username) {
        UserProfileChangeRequest setName = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        FirebaseUser user = mAuth.getCurrentUser();

        user.updateProfile(setName)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.reload();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User display name updated.");
                    }
                }
            });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
