package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReadArticle extends AppCompatActivity {

    private CardView quizBtn;
    private TextView article_content;
    private TextView title;
    private TextView duration;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String articleId = getIntent().getStringExtra("id");
        CheckBox bookmark = findViewById(R.id.bookmark);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User").child(uid+"/bookmarked/"+ articleId+"/bookmarked");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checked = String.valueOf(snapshot.getValue());
                if(TextUtils.equals(checked, "1")){
                    bookmark.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        int index = getIntent().getIntExtra("index", 0) +1;

        title = findViewById(R.id.newtitle);
        String judul = getIntent().getStringExtra("title");
        title.setText(judul);
        title.setSelected(true);

        String content = getIntent().getStringExtra("content");

        article_content = findViewById(R.id.content);
        article_content.setText(content);

        int words = content.trim().split("\\s+").length;
        int durasi = words/265;
        duration = findViewById(R.id.duration);

        image = findViewById(R.id.image);
        String uri = getIntent().getStringExtra("uri");
        Picasso.get().load(uri).fit().centerCrop().into(image);

        if(durasi == 0){
            duration.setText("Less than a minute read");
        }else{
            duration.setText(durasi +"-minute read");
        }

        quizBtn = findViewById(R.id.quizWrapper);

        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goQuiz = new Intent(ReadArticle.this, Quiz.class);
                goQuiz.putExtra("index", index);
                goQuiz.putExtra("title", judul);
                startActivity(goQuiz);
            }
        });

        ImageView share = findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I am sharing the article: " + getIntent().getStringExtra("title") + ", to know more kindly download our app from this link: https://drive.google.com/drive/folders/1bbyV8MSIBZAk4Gt_jkEr6ouCcXsNDg-t?usp=sharing");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Send to");
            startActivity(shareIntent);
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                String articleId = getIntent().getStringExtra("id");

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User").child(uid+"/bookmarked/"+ articleId);
                dbRef.child("id").setValue(articleId);
                if(bookmark.isChecked()){
                    dbRef.child("bookmarked").setValue("1");
                }else{
                    dbRef.child("bookmarked").setValue("0");
                }
            }
        });

        ImageView btnBack = findViewById(R.id.backbtn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
