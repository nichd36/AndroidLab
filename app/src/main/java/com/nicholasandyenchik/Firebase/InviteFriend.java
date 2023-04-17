package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class InviteFriend extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        ImageView back = findViewById(R.id.back);
        Button invite = findViewById(R.id.sharebutton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I am sharing an app which provides information regarding dementia, kindly download our app from this link: https://drive.google.com/drive/folders/1bbyV8MSIBZAk4Gt_jkEr6ouCcXsNDg-t?usp=sharing");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share app link to:");
                startActivity(shareIntent);
            }
        });
    }
}
