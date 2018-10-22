package com.example.geonhokim.pospirl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
//        String newStr = highlightText(str, position);

        SpannableString newStr = new SpannableString(str);
        newStr.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, 11, 0);


        itemController.article_btn.setText(datalist.get(position).getTitle());
        itemController.highlighted_title.setText(datalist.get(position).getTitle());
        itemController.highlighted_texts.setText(newStr);

        itemController.article_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String links = datalist.get(position).getLinks();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(links));
                mContext.startActivity(i);
            }
        });

        itemController.expandable_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cnt += 1;

                if ( cnt % 2 == 0 ) //짝수면 차트를 숨기고 아래 화살표
                {
                    itemController.expandable_btn.setImageResource(R.drawable.ic_expand_more);
                    itemController.article_lime_analysis.setVisibility(View.GONE);
                }else               //홀수면 차트를 보여주고 위 화살표
                {
                    itemController.expandable_btn.setImageResource(R.drawable.ic_expand_less);
                    itemController.article_lime_analysis.setVisibility(View.VISIBLE);
                }
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

//    public static Spanned fromHtml(String source) {
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
//            // noinspection deprecation
//            return Html.fromHtml(source);
//        }
//        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
//    }

    public void plotProbChart(HorizontalBarChart probChart, int position)
    {
        float up_prob = (float) datalist.get(position).getUp_prob();
        float down_prob = (float) datalist.get(position).getDown_prob();

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(1, up_prob));
        entries2.add(new BarEntry(0, down_prob));



        BarDataSet bardataset2 = new BarDataSet(entries2, "Prob Chart");
        int[] colorArray = {Color.YELLOW, Color.GRAY};
        bardataset2.setColors(colorArray);
        bardataset2.setValueTextSize(12f); //y값 크기

        BarData data2 = new BarData(bardataset2);

        String[] labels2 = {"하락", "상승"};
        probChart.getXAxis().setValueFormatter(new LabelFormatter(labels2));
        probChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 그림 왼쪽에 X-value 표시
        XAxis xAxis = probChart.getXAxis();
        xAxis.setLabelCount(2);


        probChart.getDescription().setEnabled(false); // Hide the description
        probChart.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
        probChart.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        probChart.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
        probChart.getLegend().setEnabled(false);   // 범례 사라짐
        probChart.setDrawValueAboveBar(true); // 그림에 중첩되어 value가 그려질 수 있음.
//        featureChart.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

        //final float newLimit = data2.getYMax() + (float) 0.03;
        probChart.getAxisLeft().setAxisMinimum(-0.05f); // 그래프 표현범위 지정
        probChart.getAxisLeft().setAxisMaximum(1.1f); // 그래프 표현범위 지정
        probChart.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        probChart.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        probChart.getXAxis().setDrawGridLines(false); // x그리드 제거
        probChart.animateY(1000);


        probChart.setData(data2);

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


        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(5, prob1));
        entries.add(new BarEntry(4, prob2));
        entries.add(new BarEntry(3, prob3));
        entries.add(new BarEntry(2, prob4));
        entries.add(new BarEntry(1, prob5));
        entries.add(new BarEntry(0, prob6));



        BarDataSet bardataset = new BarDataSet(entries, "Feature Chart");

        int[] colorArray = new int[f_prob.length];
        for(int i = 0; i < f_prob.length; i++)
        {
            if(f_prob[i] >= 0){
                colorArray[i] = Color.GRAY;
            } else {
                colorArray[i] = Color.YELLOW;
            }
        }

        bardataset.setColors(colorArray);
        bardataset.setValueTextSize(12f); //y값 크기



        BarData data = new BarData(bardataset);


        String[] labels = {word6, word5, word4, word3, word2, word1};
        featureChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        featureChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED); // 그림 좌우에 X-value 표시
        XAxis xAxis = featureChart.getXAxis();
        xAxis.setLabelCount(6);

        featureChart.getDescription().setEnabled(false); // Hide the description
        featureChart.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
        featureChart.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        featureChart.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
        featureChart.getLegend().setEnabled(false);   // 범례 사라짐
        featureChart.setDrawValueAboveBar(true); // 그림에 중첩되어 value가 그려질 수 있음. 그러다가 그래프 밖으로 삐져나오기도 함
//        featureChart.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

//        data.setValueTextSize(20f);

        float newLimit = data.getYMax() + (float) 0.03;
        featureChart.getAxisLeft().setAxisMinimum(-newLimit); // 그래프 표현범위 지정
        featureChart.getAxisLeft().setAxisMaximum(newLimit); // 그래프 표현범위 지정
        featureChart.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        featureChart.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        featureChart.getXAxis().setDrawGridLines(false); // x그리드 제거

        featureChart.animateY(1000);
        featureChart.setData(data);
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




    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        Button article_btn;
        ImageButton expandable_btn;
        LinearLayout article_lime_analysis;
        TextView highlighted_title, highlighted_texts;
        HorizontalBarChart featureChart;
        HorizontalBarChart probChart;
        ScrollView scrollView;




        public MyViewHolder(View itemView)
        {
            super(itemView);
            article_btn = itemView.findViewById(R.id.article_btn);
            expandable_btn = itemView.findViewById(R.id.expandable_btn);
            article_lime_analysis = itemView.findViewById(R.id.article_lime_analysis);
            highlighted_title = itemView.findViewById(R.id.highlight_title);
            highlighted_texts = itemView.findViewById(R.id.highlight_text);
            featureChart = (HorizontalBarChart) itemView.findViewById(R.id.featureChart);
            probChart = (HorizontalBarChart) itemView.findViewById(R.id.probChart);
//            scrollView = (ScrollView) itemView.findViewById(R.id.childScroll);

//            scrollView.setOnTouchListener(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    highlighted_texts.getParent().requestDisallowInterceptTouchEvent(false);
//
//                    return false;
//                }
//            });
//
//
//
//            highlighted_texts.setOnTouchListener(new View.OnTouchListener()
//            {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent)
//                {
//                    highlighted_texts.getParent().requestDisallowInterceptTouchEvent(true);
//                    return false;
//                }
//            });
//
//            highlighted_texts.setMovementMethod(new ScrollingMovementMethod());
        }
    }


}