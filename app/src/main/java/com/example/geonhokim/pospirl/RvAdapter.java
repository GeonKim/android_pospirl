package com.example.geonhokim.pospirl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>
{

    public static int cnt = 0;

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
        View view = null;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.rv_articles, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final RvAdapter.MyViewHolder itemController = (RvAdapter.MyViewHolder) holder;
        cnt = 0;
//        double score = datalist.get(position).getScores();;

//        if (score >= 0) { holder.article_scores.setTextColor(Color.GREEN);
//        } else { holder.article_scores.setTextColor(Color.RED);
//        }

//        holder.article_scores.setText(String.valueOf(score));
        itemController.article_btn.setText(datalist.get(position).getTitle());
        itemController.highlighted_texts.setText(R.string.long_text);

        itemController.article_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cnt += 1;

                if ( cnt % 2 == 0 ) //짝수면 차트를 숨기고 아래 화살표
                {
                    itemController.expandable_img.setImageResource(R.drawable.ic_expand_more);
                    itemController.article_lime_analysis.setVisibility(View.GONE);
                }else               //홀수면 차트를 보여주고 위 화살표
                {
                    itemController.expandable_img.setImageResource(R.drawable.ic_expand_less);
                    itemController.article_lime_analysis.setVisibility(View.VISIBLE);
                }
//                String links = datalist.get(position).getLinks();
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(links));
//                mContext.startActivity(i);
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
        Button article_btn;
        ImageView expandable_img;
        LinearLayout article_lime_analysis;
        TextView highlighted_texts;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            article_btn = itemView.findViewById(R.id.article_btn);
            expandable_img = itemView.findViewById(R.id.expandable_img);
            article_lime_analysis = itemView.findViewById(R.id.article_lime_analysis);
            highlighted_texts = itemView.findViewById(R.id.highlight_text);
            highlighted_texts.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}