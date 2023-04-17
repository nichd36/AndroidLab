package com.nicholasandyenchik.Firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private EditText enterEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        Button btnConfirm = findViewById(R.id.btnConfirm);
        enterEmail = findViewById(R.id.enter_email);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(enterEmail)){
                    enterEmail.setError("Email cannot be empty");
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.forgotten), "Make sure email field is not empty", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else{
                    sendlink();
                }
            }
        });
    }

    void sendlink(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = enterEmail.getText().toString();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.forgotten), "If "+email+ " is linked to an account, a link will be sent", Snackbar.LENGTH_LONG);
        snackbar.show();

        auth.sendPasswordResetEmail(email);
        Toast.makeText(getApplicationContext(),"Please check your email",Toast.LENGTH_SHORT).show();
        finish();
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
