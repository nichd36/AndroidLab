package com.nicholasandyenchik.Firebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainTopicAdapter extends RecyclerView.Adapter<MainTopicAdapter.ViewHolder> {
    Context context;
    ArrayList<MainTopic> mainTopicArrayList;

    public MainTopicAdapter(Context context, ArrayList<MainTopic> mainTopicArrayList) {
        this.context = context;
        this.mainTopicArrayList = mainTopicArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_topic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainTopic categoryFolder = mainTopicArrayList.get(position);
        holder.category.setText(categoryFolder.getTitle());
        holder.desc.setText(categoryFolder.getDesc());
        Picasso.get().load(categoryFolder.getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.background);
        holder.background.setOnClickListener(v -> {
            Intent goArticle = new Intent(context, ListOfArticles.class);
            goArticle.putExtra("categoryID", categoryFolder.getId());
            context.startActivity(goArticle);
        });
    }

    @Override
    public int getItemCount() {
        return mainTopicArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView background;
        TextView category, desc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.categoryTitle);
            desc = itemView.findViewById(R.id.desc);
            background = itemView.findViewById(R.id.categoryBackground);
        }
    }
}