package com.nicholasandyenchik.Firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuizResult extends AppCompatActivity {
    private String selectedTopicName = "";
    private String selectedTopicId = "";
    public String selectedTopic = "";
    LinearLayout mButton;
    private List<Topic> question;
    Bitmap bmp, scaledbmp;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private File filePDFOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);
        final TextView TopicName = findViewById(R.id.title);
        String selectedTopic = getIntent().getStringExtra("topic");
//        final String replayTopic = "Topic"; //getIntent().getStringExtra("selectTopicId");
        TopicName.setText(selectedTopic.toUpperCase());
        final ImageView backToHomepage = findViewById(R.id.backbtn);
        final TextView correctAnswer = findViewById(R.id.correcttext);
        final TextView wrongAnswer = findViewById(R.id.wrongtext);
        final int getCorrectAnswer = getIntent().getIntExtra("correct", 0);
        final int getIncorrectAnswer = getIntent().getIntExtra("incorrect", 0);
        final int getID = getIntent().getIntExtra("id", 0);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 40, 40, false);

        mButton = findViewById(R.id.downloadholder);

        correctAnswer.setText(String.valueOf(getCorrectAnswer));
        wrongAnswer.setText(String.valueOf(getIncorrectAnswer));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User").child(uid+"/progress/"+ String.valueOf(getID));
        dbRef.child("id").setValue(String.valueOf(getID));
        dbRef.child("score").setValue(String.valueOf(getCorrectAnswer));

        backToHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizResult.this, Homepage.class));
                finish();
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{READ_MEDIA_IMAGES, WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = null; // internal memory/ storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            storageVolume = storageManager.getStorageVolumes().get(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            selectedTopic = selectedTopic.replaceAll("[^a-zA-Z0-9]", "");
            filePDFOutput = new File(storageVolume.getDirectory().getPath() + "/Download/Certificate_"+selectedTopic+"_"+user.getDisplayName()+".pdf");
        }else{
            filePDFOutput = new File(Environment.getExternalStorageDirectory(), selectedTopic+user.getDisplayName()+".pdf");
        }
    }

    public void buttonPrint(View view) throws IOException {
        PdfDocument pdfDocument = new PdfDocument();
        int pageHeight = 600;
        int pagewidth = 800;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();

        canvas.drawBitmap(scaledbmp, 56, 40, paint);

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(35);
        title.setTextAlign(Paint.Align.CENTER);

        title.setColor(ContextCompat.getColor(this, R.color.black));
        canvas.drawText("CONGRATULATIONS", 400, 100, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setColor(ContextCompat.getColor(this, R.color.blue));
        title.setTextSize(40);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(user.getDisplayName(), 400, 210, title);
        selectedTopic = getIntent().getStringExtra("topic");
        canvas.drawText(selectedTopic, 400, 340, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(28);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("to", 400, 155, title);
        canvas.drawText("for completing the quiz", 400, 280, title);
        canvas.drawText("with a score of", 400, 400, title);

        int correct = getIntent().getIntExtra("correct", 0);
        int incorrect = getIntent().getIntExtra("incorrect", 0);
        canvas.drawText(correct+" out of "+(correct+incorrect), 400, 440, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        title.setColor(ContextCompat.getColor(this, R.color.extradark_grey));
        title.setTextSize(17);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("\"You may not be able to change the outcome of dementia, but you can change the journey\"", 400, 560, title);

        pdfDocument.finishPage(myPage);
        pdfDocument.writeTo(new FileOutputStream(filePDFOutput));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.results), "Saved in /Download as Certificate_"+selectedTopic+"_"+user.getDisplayName()+".pdf", Snackbar.LENGTH_LONG);
            snackbar.show();
        }else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.results), "PDF saved in "+Environment.getExternalStorageDirectory(), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        pdfDocument.close();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(QuizResult.this, Homepage.class));
        finish();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}