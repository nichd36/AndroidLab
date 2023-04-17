package com.nicholasandyenchik.Firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Privacy extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button save, cancel;
    private EditText password, reenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        password = findViewById(R.id.changepassword);
        reenter = findViewById(R.id.reenterpassword);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newpass = password.getText().toString();
                String newpass2 = reenter.getText().toString();

                if(TextUtils.isEmpty(user.getEmail())){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.privacyscreen), "Password cannot be changed as you signed in with Google Account", Snackbar.LENGTH_LONG);
                    snackbar.show();

//                    Toast.makeText(Privacy.this, "Password cannot be changed as you signed in with your Google Account", Toast.LENGTH_LONG).show();
                }else if(TextUtils.equals(newpass,newpass2)){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(Privacy.this, "new is "+newpass, Toast.LENGTH_SHORT).show();
                    user.updatePassword(newpass);
                    user.reload();
                    finish();
                }else{
                    Toast.makeText(Privacy.this, "Make sure both password are the same", Toast.LENGTH_SHORT).show();
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
}