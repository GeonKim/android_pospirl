package com.example.geonhokim.pospirl;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
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
        plotFeatureChart(itemController.featureChart, position);
        plotProbChart(itemController.probChart, position);

        cnt = 0;

        String str = datalist.get(position).getContents();
        String newStr = highlightText(str, position);

        itemController.article_btn.setText(datalist.get(position).getTitle());
        itemController.highlighted_texts.setText(str);

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

    public String highlightText(String inputText, int position)
    {
        String word1 = datalist.get(position).getWord1();
        String word2 = datalist.get(position).getWord2();
        String word3 = datalist.get(position).getWord3();
        String word4 = datalist.get(position).getWord4();
        String word5 = datalist.get(position).getWord5();
        String word6 = datalist.get(position).getWord6();

        String[] words = {word1, word2, word3, word4, word5, word6};

        float prob1 = (float) datalist.get(position).getProb1();
        float prob2 = (float) datalist.get(position).getProb2();
        float prob3 = (float) datalist.get(position).getProb3();
        float prob4 = (float) datalist.get(position).getProb4();
        float prob5 = (float) datalist.get(position).getProb5();
        float prob6 = (float) datalist.get(position).getProb6();

        float[] f_prob = {prob1, prob2, prob3, prob4, prob5, prob6};


        String[] token = inputText.split(" ");

        for(int i = 0; i < token.length; i++)
        {
            for(int j = 0; j < words.length; j++)
            {
                if (token[i] == words[j])
                {
                    if (f_prob[j] < 0){
                        token[i] = "<font color=\"#FFFFB300\">"+token[i]+"</font>";
                    } else {
                        token[i] = "<font color=\"#303F9F\">"+token[i]+"</font>";
                    }
                }
            }
        }

        String newStr = TextUtils.join(" ", token);

        return newStr;
    }

    public static Spanned fromHtml(String source) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            // noinspection deprecation
            return Html.fromHtml(source);
        }
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    }


    public void plotFeatureChart(HorizontalBarChart featureChart, int position)
    {


        float prob1 = (float) datalist.get(position).getProb1();
        float prob2 = (float) datalist.get(position).getProb2();
        float prob3 = (float) datalist.get(position).getProb3();
        float prob4 = (float) datalist.get(position).getProb4();
        float prob5 = (float) datalist.get(position).getProb5();
        float prob6 = (float) datalist.get(position).getProb6();

        float[] f_prob = {prob1, prob2, prob3, prob4, prob5, prob6};

        String word1 = datalist.get(position).getWord1();
        String word2 = datalist.get(position).getWord2();
        String word3 = datalist.get(position).getWord3();
        String word4 = datalist.get(position).getWord4();
        String word5 = datalist.get(position).getWord5();
        String word6 = datalist.get(position).getWord6();

        String[] labels = {word1, word2, word3, word4, word5, word6};

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(6, prob1));
        entries.add(new BarEntry(5, prob2));
        entries.add(new BarEntry(4, prob3));
        entries.add(new BarEntry(3, prob4));
        entries.add(new BarEntry(2, prob5));
        entries.add(new BarEntry(1, prob6));



        BarDataSet bardataset = new BarDataSet(entries, "Feature Chart");

        int[] colorArray = new int[f_prob.length];
        for(int i = 0; i < f_prob.length; i++)
        {
            if(f_prob[i] >= 0){
                colorArray[i] = Color.BLUE;
            } else {
                colorArray[i] = Color.YELLOW;
            }
        }

        bardataset.setColors(colorArray);
        bardataset.setValueTextSize(10f); //y값 크기


        BarData fdata = new BarData(bardataset);

//        featureChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        featureChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED); // 그림 좌우에 X-value 표시


        featureChart.getDescription().setEnabled(false); // Hide the description
        featureChart.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
        featureChart.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        featureChart.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
        featureChart.getLegend().setEnabled(false);   // 범례 사라짐
        featureChart.setDrawValueAboveBar(true); // 그림에 중첩되어 value가 그려질 수 있음. 그러다가 그래프 밖으로 삐져나오기도 함
//        featureChart.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

//        fdata.setValueTextSize(20f);

        float newLimit = fdata.getYMax() + (float) 1.03;
        featureChart.getAxisLeft().setAxisMinimum((float)-0.1); // 그래프 표현범위 지정
        featureChart.getAxisLeft().setAxisMaximum((float)0.1); // 그래프 표현범위 지정
        featureChart.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        featureChart.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        featureChart.getXAxis().setDrawGridLines(false); // x그리드 제거

        featureChart.animateY(1000);
        featureChart.setData(fdata);
    }

    public class LabelFormatter implements IAxisValueFormatter
    {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }

    public void plotProbChart(HorizontalBarChart probChart, int position)
    {
        float down_prob = (float) datalist.get(position).getDown_prob();
        float up_prob = (float) datalist.get(position).getUp_prob();
        int[] colorArray = {Color.BLUE, Color.YELLOW};




        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, down_prob));
        entries.add(new BarEntry(1, up_prob));

        BarDataSet bardataset = new BarDataSet(entries, "Prob Chart");

        bardataset.setColors(colorArray);

        BarData data = new BarData(bardataset);


        probChart.animateY(1000);

        probChart.getDescription().setEnabled(false); // Hide the description
        probChart.getAxisLeft().setDrawLabels(false);
        probChart.getAxisRight().setDrawLabels(false);
        probChart.getXAxis().setDrawLabels(false);
        probChart.getLegend().setEnabled(false);   // Hide the legend

        //data.setValueTextSize(100f);

        probChart.setData(data);


    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        Button article_btn;
        ImageView expandable_img;
        LinearLayout article_lime_analysis;
        TextView highlighted_texts;
        HorizontalBarChart featureChart;
        HorizontalBarChart probChart;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            article_btn = itemView.findViewById(R.id.article_btn);
            expandable_img = itemView.findViewById(R.id.expandable_img);
            article_lime_analysis = itemView.findViewById(R.id.article_lime_analysis);
            highlighted_texts = itemView.findViewById(R.id.highlight_text);
            highlighted_texts.setMovementMethod(new ScrollingMovementMethod());
            featureChart = (HorizontalBarChart) itemView.findViewById(R.id.featureChart);
            probChart = (HorizontalBarChart) itemView.findViewById(R.id.probChart);

        }
    }


}