package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettings extends AppCompatActivity {

    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        ImageView backbtn = findViewById(R.id.backbtn);
        LinearLayout editProfile = findViewById(R.id.editprofilewrapper);

        TextView name = findViewById(R.id.name);
        name.setText(user.getDisplayName());

        LinearLayout privacy = findViewById(R.id.privacywrapper);
        LinearLayout invite = findViewById(R.id.invitefriendwrapper);
        LinearLayout signout = findViewById(R.id.signoutwrapper);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goEdit = new Intent(AccountSettings.this, EditProfile.class);
                startActivity(goEdit);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPrivacy = new Intent(AccountSettings.this, Privacy.class);
                startActivity(goPrivacy);
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goInvite = new Intent(AccountSettings.this, InviteFriend.class);
                startActivity(goInvite);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signOut = new Intent(AccountSettings.this, SignUp.class);
                mAuth.signOut();
                finishAffinity();
                Toast.makeText(getApplicationContext(),"Signed out" ,Toast.LENGTH_SHORT).show();
                startActivity(signOut);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload();

        TextView name = findViewById(R.id.name);
        name.setText(user.getDisplayName());
    }
}
