package com.nicholasandyenchik.Firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleListRVAdapter extends RecyclerView.Adapter<ArticleListRVAdapter.ArticleListRVHolder> {
    Context context;
    ArrayList<ArticleItem> articleItemArrayList = new ArrayList<>();
    ArrayList<Long> countArrayList = new ArrayList<>();
    ArrayList<ScoreList> scoreListArrayList = new ArrayList<ScoreList>();
    CheckBox bookmark;

    public ArticleListRVAdapter(Context context, ArrayList<ArticleItem> articleItemArrayList) {
        this.context = context;
        this.articleItemArrayList = articleItemArrayList;
    }

    public ArticleListRVAdapter(Context context, ArrayList<ArticleItem> articleItemArrayList, ArrayList<ScoreList> scoreListArrayList, ArrayList<Long> countArrayList) {
        this.context = context;
        this.articleItemArrayList = articleItemArrayList;
        this.scoreListArrayList = scoreListArrayList;
        this.countArrayList = countArrayList;
    }

    @NonNull
    @Override
    public ArticleListRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleListRVHolder(LayoutInflater.from(context).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListRVHolder holder, int position) {
        holder.title.setText(articleItemArrayList.get(position).getTitle());
        Picasso.get().load(articleItemArrayList.get(position).getImage()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);

        for(int i = 0; i<scoreListArrayList.size(); i++){
            if(TextUtils.equals(Integer.toString(articleItemArrayList.get(position).getId()), scoreListArrayList.get(i).getId())) {
                holder.progress.setText("Score: " + scoreListArrayList.get(i).getScore() + "/" + countArrayList.get(position));
                holder.progress.setTextColor(ContextCompat.getColor(context, R.color.green_neon));
            }
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle titleAndContent = new Bundle();
                titleAndContent.putInt("index", articleItemArrayList.get(position).getId());
                titleAndContent.putString("content", articleItemArrayList.get(position).getContent());
                titleAndContent.putString("title", articleItemArrayList.get(position).getTitle());
                titleAndContent.putString("uri", articleItemArrayList.get(position).getImage());
                titleAndContent.putString("id", String.valueOf(articleItemArrayList.get(position).getId()));
                Intent goArticle = new Intent(context, ReadArticle.class);
                goArticle.putExtras(titleAndContent);
                context.startActivity(goArticle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleItemArrayList.size();
    }

    class ArticleListRVHolder extends RecyclerView.ViewHolder{
        ImageView image, darken;
        TextView title, content, progress;

        public ArticleListRVHolder(View itemView){
            super(itemView);
            bookmark = itemView.findViewById(R.id.bookmark);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            darken = itemView.findViewById(R.id.darkenBackground);
            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);
        }
    };
}
