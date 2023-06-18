package com.nicholasandyenchik.Firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Quiz extends AppCompatActivity {

    private TextView questions;
    private TextView question;
    private SoundPool soundPool;
    private int wrong,correct;
    private TextView option1,option2,option3,option4;
    private TextView nextBtn;
    private List<QuestionsList> questionsListList = new ArrayList<>();
    private int currentQuestionPosition, articleIndex, index = 0;
    private String selectedOptionByUser = "";
    int correctAnswer;
    int realAnswer;
    CountDownTimer timercountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        View black = findViewById(R.id.blackbackground);
        VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.GONE);
                black.setVisibility(View.GONE);
                TextView timer=findViewById(R.id.timer1);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                final int[] secondsRemaining = {30};
                timercountdown = new CountDownTimer(30000,1000) {
                    @Override
                    public void onTick(long l) {
                        secondsRemaining[0]--;
                        timer.setText(Integer.toString(secondsRemaining[0]) + "sec");
                        progressBar.setProgress(30 - secondsRemaining[0]);
                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(Quiz.this, TimesUp.class);
                        startActivity(intent);
                    }
                };
                timercountdown.start();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    soundPool = new SoundPool.Builder()
                            .setMaxStreams(2)
                            .setAudioAttributes(audioAttributes)
                            .build();
                }else{
                    soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
                }
                wrong = soundPool.load(Quiz.this,R.raw.wrong,1);
                correct = soundPool.load(Quiz.this,R.raw.correct,1);

                questions = findViewById(R.id.questions);
                question = findViewById(R.id.questiontitle);

                option1= findViewById(R.id.option1);
                option2 = findViewById(R.id.option2);
                option3 = findViewById(R.id.option3);
                option4 = findViewById(R.id.option4);

                nextBtn = findViewById(R.id.next);

                index = getIntent().getIntExtra("index", 1001);

                int category = 0;
                int articleIndex = index;

                while(index != 0) {
                    category = index % 10;
                    index = index / 10;
                }

                int articleID = articleIndex - (category*(1000));

                String PATH = "Category_Information/Chapter " + category + "/article/" + articleID +"/quiz";
//                Toast.makeText(Quiz.this, PATH, Toast.LENGTH_SHORT).show();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(PATH);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        questionsListList.clear();
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            QuestionsList questionsList = snapshot1.getValue(QuestionsList.class);
                            questionsListList.add(questionsList);
                        }

                        questions.setText((currentQuestionPosition+1)+ "/"+questionsListList.size());
                        question.setText(questionsListList.get(0).getQuestion());
                        option1.setText(questionsListList.get(0).getOpt1());
                        option2.setText(questionsListList.get(0).getOpt2());
                        option3.setText(questionsListList.get(0).getOpt3());
                        option4.setText(questionsListList.get(0).getOpt4());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                option1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedOptionByUser.isEmpty()) {
                            selectedOptionByUser = option1.getText().toString();

                            option1.setBackgroundResource(R.drawable.round_red);
                            option1.setTextColor(Color.WHITE);
                            correctAnswer = 1;
                            revealAnswer();

                            if (correctAnswer == realAnswer) {
                                soundPool.play(correct, 1, 1, 0, 0, 1);
                            } else {
                                soundPool.play(wrong, 1, 1, 0, 0, 1);
                            }
                            questionsListList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                        }
                    }
                });

                option2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedOptionByUser.isEmpty()){
                            selectedOptionByUser = option2.getText().toString();

                            option2.setBackgroundResource(R.drawable.round_red);
                            option2.setTextColor(Color.WHITE);

                            revealAnswer();
                            correctAnswer =2;
                            if (correctAnswer == realAnswer) {
                                soundPool.play(correct, 1, 1, 0, 0, 1);
                            }
                            else{
                                soundPool.play(wrong, 1, 1, 0, 0, 1);
                            }
                            questionsListList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                        }
                    }
                });

                option3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedOptionByUser.isEmpty()){
                            selectedOptionByUser = option3.getText().toString();

                            option3.setBackgroundResource(R.drawable.round_red);
                            option3.setTextColor(Color.WHITE);

                            revealAnswer();
                            correctAnswer = 3;
                            if (correctAnswer == realAnswer) {
                                soundPool.play(correct, 1, 1, 0, 0, 1);
                            }
                            else{
                                soundPool.play(wrong, 1, 1, 0, 0, 1);
                            }
                            questionsListList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                        }
                    }
                });

                option4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedOptionByUser.isEmpty()){
                            selectedOptionByUser = option4.getText().toString();

                            option4.setBackgroundResource(R.drawable.round_red);
                            option4.setTextColor(Color.WHITE);

                            revealAnswer();
                            correctAnswer =4;
                            if (correctAnswer == realAnswer) {
                                soundPool.play(correct, 1, 1, 0, 0, 1);
                            }
                            else{
                                soundPool.play(wrong, 1, 1, 0, 0, 1);
                            }
                            questionsListList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                        }
                    }
                });

                nextBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(selectedOptionByUser.isEmpty()){
                            Toast.makeText(Quiz.this, "Please select an option", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            changeNextQuestion();
                        }
                    }
                });

            }
        });
    }

    private void changeNextQuestion(){
        currentQuestionPosition++;

        if(currentQuestionPosition == questionsListList.size()){
            timercountdown.cancel();
        }
        if(currentQuestionPosition+1 == questionsListList.size()){
            nextBtn.setText("Submit Quiz");
        }

        if(currentQuestionPosition < questionsListList.size()){
            selectedOptionByUser = "";
            option1.setBackgroundResource(R.drawable.rounded_card);
            option1.setTextColor(getResources().getColor(R.color.primaryText));

            option2.setBackgroundResource(R.drawable.rounded_card);
            option2.setTextColor(getResources().getColor(R.color.primaryText));

            option3.setBackgroundResource(R.drawable.rounded_card);
            option3.setTextColor(getResources().getColor(R.color.primaryText));

            option4.setBackgroundResource(R.drawable.rounded_card);
            option4.setTextColor(getResources().getColor(R.color.primaryText));

            questions.setText((currentQuestionPosition+1)+ "/"+questionsListList.size());
            question.setText(questionsListList.get(currentQuestionPosition).getQuestion());
            option1.setText(questionsListList.get(currentQuestionPosition).getOpt1());
            option2.setText(questionsListList.get(currentQuestionPosition).getOpt2());
            option3.setText(questionsListList.get(currentQuestionPosition).getOpt3());
            option4.setText(questionsListList.get(currentQuestionPosition).getOpt4());
        }
        else
        {
            Toast.makeText(Quiz.this, String.valueOf(getIntent().getIntExtra("index", 1001)), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Quiz.this, QuizResult.class);
            intent.putExtra("topic", getIntent().getStringExtra("title"));
            intent.putExtra("id", getIntent().getIntExtra("index", 1001));
            intent.putExtra("correct", getCorrectAnswers());
            intent.putExtra("incorrect", getIncorrectAnswers());

            startActivity(intent);
            finish();
        }
    }

    private int getCorrectAnswers(){

        int correctAnswers = 0;

        for(int i=0;i<questionsListList.size();i++){
            final String getUserSelectedAnswer = questionsListList.get(i).getUserSelectedAnswer();
            final String getAnswer = questionsListList.get(i).getAnswer();

            if(getUserSelectedAnswer.equals(getAnswer)){
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private int getIncorrectAnswers(){

        int IncorrectAnswers = 0;

        for(int i=0;i<questionsListList.size();i++){
            final String getUserSelectedAnswer = questionsListList.get(i).getUserSelectedAnswer();
            final String getAnswer = questionsListList.get(i).getAnswer();

            if(!getUserSelectedAnswer.equals(getAnswer)){
                IncorrectAnswers++;
            }
        }
        return IncorrectAnswers;
    }

    private int revealAnswer() {
        final String getAnswer = questionsListList.get(currentQuestionPosition).getAnswer();

        if (option1.getText().toString().equals(getAnswer)) {
            option1.setBackgroundResource(R.drawable.round_green);
            option1.setTextColor(Color.WHITE);
            return realAnswer = 1;
        } else if (option2.getText().toString().equals(getAnswer)) {
            option2.setBackgroundResource(R.drawable.round_green);
            option2.setTextColor(Color.WHITE);
            return realAnswer = 2;

        } else if (option3.getText().toString().equals(getAnswer)) {
            option3.setBackgroundResource(R.drawable.round_green);
            option3.setTextColor(Color.WHITE);
            return realAnswer = 3;

        } else if (option4.getText().toString().equals(getAnswer)) {
            option4.setBackgroundResource(R.drawable.round_green);
            option4.setTextColor(Color.WHITE);
            return realAnswer = 4;

        }
        else {
            return realAnswer = 0;
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        timercountdown.cancel();
        finish();
    }

    @Override
    public void onBackPressed() {

        timercountdown.cancel();
        finish();
    }
}