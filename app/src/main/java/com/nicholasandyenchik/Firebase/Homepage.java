package com.nicholasandyenchik.Firebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import android.Manifest;

public class Homepage extends AppCompatActivity {
    private RecyclerView categoryRV;
    private int lastFirstVisiblePosition;
    private ArrayList<MainTopic> categoryArrayList;
    private ArrayList<ScoreList> scoreListArrayList;
    private ArrayList<ArticleItem> articleItemArrayList;
    private ArrayList<Long> countArrayList;
    private ArrayList<Bookmark> bookmarkedDbList;
    private String selectedFilter = "all";
    private RadioButton homeBtn, bookmarkedBtn;
    private ProgressBar loading;
    private LinearLayout empty;
    DatabaseReference dbRef;

    public Homepage() {
    }

    String[] permissions = new String[]{
        Manifest.permission.POST_NOTIFICATIONS
    };

    boolean permission_post_notification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        if(!permission_post_notification)
            {
                requestPermissionNotification();
            }
            else {
            Toast.makeText(Homepage.this, "Notification Permission Granted..", Toast.LENGTH_SHORT).show();
        }

        ImageView menu = findViewById(R.id.menubutton);
        ImageView profile = findViewById(R.id.profile);

        categoryRV = findViewById(R.id.categoryRecycleView);

        categoryRV.setLayoutManager(new LinearLayoutManager(this));
        categoryArrayList = new ArrayList<>();
        bookmarkedDbList = new ArrayList<>();
        articleItemArrayList = new ArrayList<>();
        scoreListArrayList = new ArrayList<>();
        countArrayList = new ArrayList<>();

        loading = findViewById(R.id.loading);
        empty = findViewById(R.id.empty);
        homeBtn = findViewById(R.id.home);
        bookmarkedBtn = findViewById(R.id.bookmarkedpage);

        if(checkCurrentUser()){
            setBookmarkdata();
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
            snapHelper.attachToRecyclerView(categoryRV);
            setBookmarkdata();

            bookmarkedBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setBookmarkdata();
                }
            });

            homeBtn.setChecked(true);
            homeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setData();
                    SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
                    snapHelper.attachToRecyclerView(categoryRV);
                }
            });
            setData();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMenu = new Intent(Homepage.this, Menu.class);
                goMenu.putExtra("activity","fromHome");
                startActivity(goMenu);
                overridePendingTransition(R.anim.slidein_left, 0);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goProfile = new Intent(Homepage.this, AccountSettings.class);
                startActivity(goProfile);
                overridePendingTransition(R.anim.slidein_right, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookmarkdata();
    }

    public void requestPermissionNotification(){
        if(ContextCompat.checkSelfPermission(Homepage.this,permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            permission_post_notification=true;
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
                {
                    Log.d("Permission ", "inside else first time don't allow");
                }
                else
                {
                    Log.d("Permission ", "inside else 2nd time don't allow");
                }
                requestPermissionLauncherNotification.launch(permissions[0]);
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncherNotification = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
        if(isGranted)
        {
            permission_post_notification=true;
        }
        else
        {
            permission_post_notification=false;
            showPermissionDialog("Notification Permission");
        }
    });

    public void showPermissionDialog(String permission_desc){
        new AlertDialog.Builder(
                Homepage.this
        ).setTitle("Alert for Permission")
            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent rintent=new Intent();
                    rintent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri= Uri.fromParts("package", getPackageName(),null);
                    rintent.setData(uri);
                    startActivity(rintent);
                    dialogInterface.dismiss();
                }
            })
            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .show();
    }
    public boolean checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent goSignUp = new Intent(Homepage.this, SignUp.class);
            startActivity(goSignUp);
            finish();
            overridePendingTransition(0, 0);
            return false;
        }else{
            return true;
        }
    }

    private void setData()
    {
        empty.setVisibility(View.GONE);
        dbRef = FirebaseDatabase.getInstance().getReference("Category_Information");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    MainTopic category = snapshot1.getValue(MainTopic.class);
                    categoryArrayList.add(category);
                    MainTopicAdapter adapter = new MainTopicAdapter(Homepage.this, categoryArrayList);
                    categoryRV.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
   }

    private void setBookmarkdata()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dbRef = FirebaseDatabase.getInstance().getReference("User/"+user.getUid()+"/progress");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreListArrayList.clear();
                for(DataSnapshot snapshotScore: snapshot.getChildren()){
                    ScoreList score = snapshotScore.getValue(ScoreList.class);
                    scoreListArrayList.add(score);
                }
                loading.setVisibility(View.GONE);
                BookmarkAdapter adapter = new BookmarkAdapter(Homepage.this, articleItemArrayList, scoreListArrayList, bookmarkedDbList);
                categoryRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("User/" + user.getUid() + "/bookmarked");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkedDbList.clear();
                for(DataSnapshot snapshotB: snapshot.getChildren()) {
                    Bookmark book = snapshotB.getValue(Bookmark.class);
                        bookmarkedDbList.add(book);
                }

                if(bookmarkedDbList.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                }

                BookmarkAdapter adapter = new BookmarkAdapter(Homepage.this, articleItemArrayList, scoreListArrayList, bookmarkedDbList);
                categoryRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("Category_Information");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MainTopic category = snapshot1.getValue(MainTopic.class);
                    categoryArrayList.add(category);
                }

                articleItemArrayList.clear();
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    String PATH = "";
                    PATH = "Category_Information/Chapter " + i + "/article";

                    dbRef = FirebaseDatabase.getInstance().getReference(PATH);
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ArticleItem articleItem = snapshot1.getValue(ArticleItem.class);
                                for (int i = 0; i < bookmarkedDbList.size(); i++) {
                                    if (TextUtils.equals(String.valueOf(articleItem.getId()), bookmarkedDbList.get(i).getId())) {
                                        if (TextUtils.equals(bookmarkedDbList.get(i).getBookmarked(), "1")) {
                                            articleItemArrayList.add(articleItem);

                                            DataSnapshot snapshot2 = snapshot1.child("quiz");
                                            Long count = snapshot2.getChildrenCount();
                                            countArrayList.add(count);
                                        }
                                    }
                                }
                            }
                            ArticleListRVAdapter adapter = new ArticleListRVAdapter(Homepage.this, articleItemArrayList, scoreListArrayList, countArrayList);
                            categoryRV.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (homeBtn.isChecked()) {
                                empty.setVisibility(View.GONE);
                                setData();
                            }
                        }

                        @Override
                        public void onCancelled(@org.checkerframework.checker.nullness.qual.NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
