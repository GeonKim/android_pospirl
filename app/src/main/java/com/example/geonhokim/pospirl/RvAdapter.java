package com.example.geonhokim.pospirl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>
{

    public static int cnt = 0;

    private Context mContext;
    private List<CompanyArticle> datalist;


    public RvAdapter(Context mContext, List datalist)
    {
        this.mContext = mContext;
        this.datalist = datalist;
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

        Map<String, Float> key_prob_map = tokenizeKeyProb(datalist.get(position).getKey_prob());

		Iterator<String> keySetIterator = key_prob_map.keySet().iterator();

		Float[] targetScores = new Float[key_prob_map.size()];
		String[] targetStrings = new String[key_prob_map.size()];

		for(int i = 0; i < key_prob_map.size(); i++){
		    String key = keySetIterator.next();
		    targetStrings[i] = key;
		    targetScores[i] = key_prob_map.get(key);
        }

        plotFeatureChart(itemController.featureChart, targetScores, targetStrings);

        Float[] new_ud = tokenizeUpDown(datalist.get(position).getUp_down());
        plotProbChart(itemController.probChart, new_ud);

        String str = datalist.get(position).getContents();
        itemController.article_btn.setText(datalist.get(position).getTitle());
        itemController.highlighted_title.setText(datalist.get(position).getTitle());
        itemController.highlighted_texts.setText(getUnderLineColorText(str, targetStrings, targetScores));

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

                if (cnt % 2 == 0) //짝수면 차트를 숨기고 아래 화살표
                {
                    itemController.expandable_btn.setImageResource(R.drawable.ic_expand_more);
                    itemController.article_lime_analysis.setVisibility(View.GONE);
                } else               //홀수면 차트를 보여주고 위 화살표
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

    public static SpannableString getUnderLineColorText(String string, String[] targetStrings, Float[] targetScores) {

        SpannableString spannableString = new SpannableString(string);

        for(int i = 0; i < targetStrings.length; i++){
            int targetStartIndex = string.indexOf(targetStrings[i]);
            int targetEndIndex = targetStartIndex + targetStrings[i].length();
            if (targetScores[i] >= 0){ //하락 파랑
                spannableString.setSpan(new BackgroundColorSpan(Color.argb(100, 31, 119, 180)), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {                //상승 노랑
                spannableString.setSpan(new BackgroundColorSpan(Color.argb(100, 255, 127, 14)), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }


    public void plotProbChart(HorizontalBarChart probChart, Float[] targetProb)
    {
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(1, targetProb[0]));
        entries2.add(new BarEntry(0, targetProb[1]));

        BarDataSet bardataset2 = new BarDataSet(entries2, "Prob Chart");
        bardataset2.setColors(Color.argb(255,255, 127, 14), Color.argb(255 ,31, 119, 180)); //orange, blue
        bardataset2.setValueTextSize(17f); //y값 크기

        BarData data2 = new BarData(bardataset2);

        String[] labels2 = {"하락", "상승"};
        probChart.getXAxis().setValueFormatter(new LabelFormatter(labels2));
        probChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 그림 왼쪽에 X-value 표시
        XAxis xAxis = probChart.getXAxis();
        xAxis.setLabelCount(2); //라벨은 두개만
        xAxis.setTextSize(17f); // x값 크기

        probChart.getDescription().setEnabled(false); // Hide the description
        probChart.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
        probChart.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        probChart.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
        probChart.getLegend().setEnabled(false);   // 범례 사라짐
        probChart.setDrawValueAboveBar(true); // 그림에 중첩되어 value가 그려질 수 있음.
        probChart.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

        //final float newLimit = data2.getYMax() + (float) 0.03;
        probChart.getAxisLeft().setAxisMinimum(-0.05f); // 그래프 표현범위 지정
        probChart.getAxisLeft().setAxisMaximum(1.1f); // 그래프 표현범위 지정
        probChart.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        probChart.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        probChart.getXAxis().setDrawGridLines(false); // x그리드 제거
        probChart.animateY(1000);
        probChart.setData(data2);
    }

    public void plotFeatureChart(HorizontalBarChart featureChart, Float[] targetScores, String[] targetStrings)
    {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(5, targetScores[0]));
        entries.add(new BarEntry(4, targetScores[1]));
        entries.add(new BarEntry(3, targetScores[2]));
        entries.add(new BarEntry(2, targetScores[3]));
        entries.add(new BarEntry(1, targetScores[4]));
        entries.add(new BarEntry(0, targetScores[5]));

        BarDataSet bardataset = new BarDataSet(entries, "Feature Chart");

        int[] colorArray = new int[targetScores.length];
        for(int i = 0; i < targetScores.length; i++)
        {
            if(targetScores[i] >= 0){
                colorArray[i] = Color.argb(255,31, 119, 180);
            } else {
                colorArray[i] = Color.argb(255,255, 127, 14);
            }
        }

        bardataset.setColors(colorArray);
        bardataset.setValueTextSize(14f); //y값 크기

        BarData data = new BarData(bardataset);
        String[] labels = {targetStrings[5], targetStrings[4], targetStrings[3], targetStrings[2], targetStrings[1], targetStrings[0]};

        featureChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        featureChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED); // 그림 좌우에 X-value 표시
        XAxis xAxis = featureChart.getXAxis();
        xAxis.setLabelCount(6);
        xAxis.setTextSize(14f); // x값 크기

        featureChart.getDescription().setEnabled(false); // Hide the description
        featureChart.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
        featureChart.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        featureChart.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
        featureChart.getLegend().setEnabled(false);   // 범례 사라짐
        featureChart.setDrawValueAboveBar(true); // 그림에 중첩되어 value가 그려질 수 있음. 그러다가 그래프 밖으로 삐져나오기도 함
        featureChart.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

        float newLimit2 = Math.abs(targetScores[0]) * 2;
        featureChart.getAxisLeft().setAxisMinimum(-newLimit2); // 그래프 표현범위 지정
        featureChart.getAxisLeft().setAxisMaximum(newLimit2); // 그래프 표현범위 지정
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
        }
    }

    public static Float[] tokenizeUpDown(String str){
        String new_str = str.replace("[", "").replace("]", "");
        String[] str_arr = new_str.split(", ");
        Float[] new_ud = new Float[str_arr.length];
        for(int i = 0; i < str_arr.length; i++){
            new_ud[i] = Float.valueOf(str_arr[i]);
        }
        return new_ud;
    }

    public static Map<String, Float> tokenizeKeyProb(String str){

        String new_str = str.replace("[(", "").replace(")]", "").replaceAll("'", "");

        String[] st_ar = new_str.split("\\), \\(");

        Map<String, Float> _map = new LinkedHashMap<String, Float>(); //데이터가 순차적으로 삽입되기 위해 LinkedHashMap 사용한다.

        for(int i = 0; i < st_ar.length; i++){
            String[] arg = st_ar[i].split(", ");
            _map.put(arg[0], Float.valueOf(arg[1]));
        }
        return _map;
    }

}