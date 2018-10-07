package com.example.geonhokim.pospirl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>
{
    RequestOptions options;
    private Context mContext;
    private List<CompanyArticle> datalist;



    public RvAdapter(Context mContext, List datalist)
    {
        this.mContext = mContext;
        this.datalist = datalist;
        options = new RequestOptions()
                .centerCrop();
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.loading);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.rv_articles, parent, false);
        // click listener here


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        double score = datalist.get(position).getScores();;

        if (score >= 0) { holder.article_scores.setTextColor(Color.GREEN);
        } else { holder.article_scores.setTextColor(Color.RED);
        }

        holder.article_scores.setText(String.valueOf(score));
        holder.article_title.setText(datalist.get(position).getTitle());
        holder.article_keywords.setText(datalist.get(position).getKeywords());


        holder.article_title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String links = datalist.get(position).getLinks();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(links));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return datalist.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        Button article_title;
        TextView article_keywords, article_scores;


        public MyViewHolder(View itemView)
        {
            super(itemView);
            article_title = itemView.findViewById(R.id.article_title);
            article_keywords = itemView.findViewById(R.id.article_keywords);
            article_scores = itemView.findViewById(R.id.article_scores);
        }
    }
}