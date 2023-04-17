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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkAdapterHolder>{
    Context context;
    ArrayList<ArticleItem> articleItemArrayList;
    ArrayList<ScoreList> scoreListArrayList;
    ArrayList<Bookmark> bookmarkArrayList;

    public BookmarkAdapter(Context context, ArrayList<ArticleItem> articleItemArrayList) {
        this.context = context;
        this.articleItemArrayList = articleItemArrayList;
    }

    public BookmarkAdapter(Context context, ArrayList<ArticleItem> articleItemArrayList, ArrayList<ScoreList> scoreListArrayList, ArrayList<Bookmark> bookmarkArrayList) {
        this.context = context;
        this.articleItemArrayList = articleItemArrayList;
        this.scoreListArrayList = scoreListArrayList;
        this.bookmarkArrayList = bookmarkArrayList;
    }

    @NonNull
    @Override
    public BookmarkAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookmarkAdapterHolder(LayoutInflater.from(context).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapterHolder holder, final int position) {
        holder.title.setText(articleItemArrayList.get(position).getTitle());
        holder.image.setImageResource(R.drawable.bookmark_icon);
        Picasso.get().load(articleItemArrayList.get(position).getImage()).resize(1265,569).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);

        for(int i = 0; i<scoreListArrayList.size(); i++){
            if( TextUtils.equals(Integer.toString(articleItemArrayList.get(position).getId()), scoreListArrayList.get(i).getId())) {
                holder.progress.setText("Score: " + scoreListArrayList.get(i).getScore() + "/5");
                holder.progress.setTextColor(ContextCompat.getColor(context, R.color.green_neon));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle titleAndContent = new Bundle();
                titleAndContent.putInt("index", articleItemArrayList.get(position).getId());
                titleAndContent.putString("content", articleItemArrayList.get(position).getContent());
                titleAndContent.putString("title", articleItemArrayList.get(position).getTitle());
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

    class BookmarkAdapterHolder extends RecyclerView.ViewHolder{
        CheckBox bookmark;
        ImageView image, darken;
        TextView title, content, progress;

        public BookmarkAdapterHolder(@NonNull View itemView) {
            super(itemView);
            bookmark = itemView.findViewById(R.id.bookmark);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);
        }
    }
}
