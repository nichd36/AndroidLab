package com.nicholasandyenchik.Firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        ImageView backMenu = findViewById(R.id.backMenu);
        TextView homepage = findViewById(R.id.homepage);
        TextView search = findViewById(R.id.search);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if(!getIntent().getStringExtra("activity").equals("fromHome")) {
                    Intent goHome = new Intent(Menu.this, Homepage.class);
                    startActivity(goHome);
                    overridePendingTransition(R.anim.slidein_right, R.anim.slideout_left);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if(!getIntent().getStringExtra("activity").equals("fromSearch")) {
                    Intent goSearch = new Intent(Menu.this, SearchHomepage.class);
                    startActivity(goSearch);
                    overridePendingTransition(R.anim.slidein_right, R.anim.slideout_left);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.slideout_left);
    }
}