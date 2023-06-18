package com.nicholasandyenchik.Firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class SearchHomepage extends AppCompatActivity {
    RecyclerView articleListRV;
    ArrayList<ScoreList> scoreListArrayList;
    ArrayList<ArticleItem> articleItemArrayList;
    ArrayList<Long> countArrayList;
    private SearchView searchView;
    private ProgressBar loading;
    private LinearLayout empty;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_homepage);
        
        scoreListArrayList = new ArrayList<ScoreList>();
        articleItemArrayList = new ArrayList<ArticleItem>();
        countArrayList = new ArrayList<Long>();

        empty = findViewById(R.id.empty);
        loading = findViewById(R.id.loading);
        articleListRV = findViewById(R.id.topicListView);
        articleListRV.setLayoutManager(new LinearLayoutManager(this));
        setData();
        SearchTopic();
        goProfile();
        goMenu();
    }

    private void goProfile(){
        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goProfile = new Intent(SearchHomepage.this, AccountSettings.class);
                startActivity(goProfile);
                overridePendingTransition(R.anim.slidein_right, 0);
            }
        });
    }

    private void goMenu() {
        ImageView btnmenu = findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMenu = new Intent(SearchHomepage.this, Menu.class);
                goMenu.putExtra("activity", "fromSearch");
                startActivity(goMenu);
                overridePendingTransition(R.anim.slidein_left, 0);
            }
        });
    }

    private void SearchTopic(){
        searchView = (SearchView) findViewById(R.id.searchTopic);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ArticleItem>filterTopic = new ArrayList<ArticleItem>();
                for (int i=0; i<articleItemArrayList.size();i++)
                {
                    ArticleItem topic = articleItemArrayList.get(i);
                    if (topic.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filterTopic.add(topic);
                    }
                    if(filterTopic.isEmpty()){
                        empty.setVisibility(View.VISIBLE);
                    }else{
                        empty.setVisibility(View.GONE);
                    }
                }
                ArticleListRVAdapter adapter = new ArticleListRVAdapter(SearchHomepage.this, filterTopic );
                articleListRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }
    private void setData()
    {
        dbRef = FirebaseDatabase.getInstance().getReference("Category_Information");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MainTopic> categoryArrayList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MainTopic category = snapshot1.getValue(MainTopic.class);
                    categoryArrayList.add(category);
                }

                articleItemArrayList.clear();
                for(int i = 0; i<categoryArrayList.size(); i++){
                    String PATH = "";
                    PATH = "Category_Information/Chapter " + i + "/article";
                    dbRef = FirebaseDatabase.getInstance().getReference(PATH);

                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1: snapshot.getChildren()){
                                ArticleItem articleItem = snapshot1.getValue(ArticleItem.class);
                                articleItemArrayList.add(articleItem);

                                DataSnapshot snapshot2 = snapshot1.child("quiz");
                                Long count = snapshot2.getChildrenCount();
                                countArrayList.add(count);
                            }
                            ArticleListRVAdapter adapter = new ArticleListRVAdapter(SearchHomepage.this, articleItemArrayList, scoreListArrayList, countArrayList);
                            articleListRV.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchHomepage.this, "Error occured, please report this to the developer", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference score = FirebaseDatabase.getInstance().getReference("User/" + user.getUid() + "/progress");

        score.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                scoreListArrayList.clear();
                for(DataSnapshot snapshotScore: snapshot.getChildren()){
                    ScoreList scoreList = snapshotScore.getValue(ScoreList.class);
                    scoreListArrayList.add(scoreList);
                }
                ArticleListRVAdapter adapter = new ArticleListRVAdapter(SearchHomepage.this, articleItemArrayList, scoreListArrayList, countArrayList);
                articleListRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                Toast.makeText(SearchHomepage.this, "Error occurred while retrieving score", Toast.LENGTH_SHORT).show();
            }
        });
    }
}