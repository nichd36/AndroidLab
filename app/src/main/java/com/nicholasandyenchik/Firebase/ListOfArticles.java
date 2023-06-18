package com.nicholasandyenchik.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfArticles extends AppCompatActivity {
    DatabaseReference dbRef;
    RecyclerView articleListRV;
    ArrayList<ScoreList> scoreListArrayList;
    ArrayList<ArticleItem> articleItemArrayList;
    ArrayList<Long> countArrayList;
    TextView category, done;
    String PATH;
    ProgressBar progressBar;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list);

        articleListRV = findViewById(R.id.articleList);
        done = findViewById(R.id.done);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
        snapHelper.attachToRecyclerView(articleListRV);

        articleListRV.setLayoutManager(new LinearLayoutManager(this));

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scoreListArrayList = new ArrayList<ScoreList>();
        articleItemArrayList = new ArrayList<ArticleItem>();
        countArrayList = new ArrayList<Long>();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra("categoryID",0);

        PATH = "Category_Information/Chapter "+categoryID+"/article";
//        if(categoryID == 3) {
//            PATH = "Category_Information/Symptoms/article";
//        } else if (categoryID == 2) {
//            PATH = "Category_Information/Chapter 1/article";
//        } else if (categoryID == 1) {
//            PATH = "Category_Information/Dealing/article";
//        } else {
//            Toast.makeText(this, "Error occured, category ID not found", Toast.LENGTH_SHORT).show();
//        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Toast.makeText(this, "Error acquiring score list", Toast.LENGTH_SHORT).show();
        }else{
            dbRef = FirebaseDatabase.getInstance().getReference("User/" + user.getUid() + "/progress");
            scoreListArrayList.clear();

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    scoreListArrayList.clear();
                    for(DataSnapshot snapshotScore: snapshot.getChildren()){
                        ScoreList scoreList = snapshotScore.getValue(ScoreList.class);
                        scoreListArrayList.add(scoreList);
                    }
                    ArticleListRVAdapter adapter = new ArticleListRVAdapter(ListOfArticles.this, articleItemArrayList, scoreListArrayList, countArrayList);
                    articleListRV.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    DatabaseReference articles = FirebaseDatabase.getInstance().getReference(PATH);
                    category = findViewById(R.id.title);
                    category.setText(" " + articles.getParent().getKey());
                    category.setSelected(true);

                    articles.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            articleItemArrayList.clear();
                            countArrayList.clear();
                            for(DataSnapshot snapshot1: snapshot.getChildren()){
                                ArticleItem articleItem = snapshot1.getValue(ArticleItem.class);
                                articleItemArrayList.add(articleItem);

                                DataSnapshot snapshot2 = snapshot1.child("quiz");
                                Long count = snapshot2.getChildrenCount();
                                countArrayList.add(count);
                            }
                            ArticleListRVAdapter adapter = new ArticleListRVAdapter(ListOfArticles.this, articleItemArrayList, scoreListArrayList, countArrayList);
                            articleListRV.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            int totalArticle = articleItemArrayList.size();

                            int workdone = 0;
                            for(int position = 0; position<articleItemArrayList.size(); position++){
                                for(int i = 0; i<scoreListArrayList.size(); i++){
                                    if( TextUtils.equals(Integer.toString(articleItemArrayList.get(position).getId()), scoreListArrayList.get(i).getId())) {
                                        workdone = workdone+1;
                                    }
                                }
                            }
                            progressBar = findViewById(R.id.progress);
                            int currentProgress = 0;
                            currentProgress = ((workdone*100)/totalArticle);
                            progressBar.setProgress(currentProgress);
                            progressBar.setMax(100);

                            done.setText(workdone+" done out of "+totalArticle);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });



        }

    }
}
