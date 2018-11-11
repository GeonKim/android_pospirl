package com.example.geonhokim.pospirl;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static String company_name;
    public TextView tv1, tv2, tv3;
    private RelativeLayout rl1, rl2, rl3;
    private BottomNavigationView bottomNavigationView;
    private List<CompanyArticle> datalist = new ArrayList<>();
    private RecyclerView myrv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(MainActivity.this, LoadingActivity.class);
        startActivity(i);

        myrv = (RecyclerView) findViewById(R.id.rv_article);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        rl1 = (RelativeLayout) findViewById(R.id.analysis_contents);
        rl2 = (RelativeLayout) findViewById(R.id.articles_contents);
        rl3 = (RelativeLayout) findViewById(R.id.prediction_contents);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.action_analysis:
                        rl1.setVisibility(View.VISIBLE);
                        rl2.setVisibility(View.GONE);
                        rl3.setVisibility(View.GONE);

                        break;
                    case R.id.action_articles:
                        rl1.setVisibility(View.GONE);
                        rl2.setVisibility(View.VISIBLE);
                        rl3.setVisibility(View.GONE);
                        break;
                    case R.id.action_prediction:
                        rl1.setVisibility(View.GONE);
                        rl2.setVisibility(View.GONE);
                        rl3.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s) //검색어 완료시 작동 리스너
            {
                company_name = s;
                tv1.setText("오늘의 뉴스는...");
                tv2.setText("뉴스분석");
                tv3.setText(company_name + " 종가 비율");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child(company_name);

                // 리얼타임 데이터베이스 읽는 방법1. ValueEventListener 이용해서 전체 차일드 수신.
                myRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Log.d("MainActivity", "ChildEventListener - onChildChanged : " + dataSnapshot.getValue());

                        datalist.clear();
                        float positive = 0;
                        float negative = 0;

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {
                            CompanyArticle ca = dataSnapshot1.getValue(CompanyArticle.class);
                            Float[] up_down_prob = tokenizeUpDown(ca.getUp_down());
                            positive += up_down_prob[0];
                            negative += up_down_prob[1];

                            datalist.add(ca);
                        }

                        if (datalist.size() > 0)
                        {
                            Toast.makeText(MainActivity.this, company_name + "의 정보를 수신합니다.", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(MainActivity.this, company_name + "의 정보가 존재하지 않습니다. posco 또는 sk hynix로 검색해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        setRvadapter(datalist);
                        displayPieChart(positive, negative);
                        displayLineChart();
                    }

                    @Override
                    public void onCancelled(DatabaseError error)
                    {
                        // Failed to read value
                        Log.d("MainActivity", "Failed to read value.", error.toException());
                        Toast.makeText(MainActivity.this, "정보 수신 실패", Toast.LENGTH_SHORT).show();
                    }
                });

//                // 리얼타임 데이터베이스 읽는 방법2. ChildEventListener 이용해서 개별 차일드 수신.
//                ChildEventListener listener = myRef.addChildEventListener(new ChildEventListener()
//                {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
//                    {
//                        Log.d("MainActivity", "ChildEventListener - onChildAdded : " + dataSnapshot.getValue());
//                        //Toast.makeText(MainActivity.this, company_name + "의 정보를 수신했습니다.", Toast.LENGTH_SHORT).show();
//                        // A new comment has been added, add it to the displayed list
//                        CompanyArticle ca = dataSnapshot.getValue(CompanyArticle.class);
//                        datalist.add(ca);
//                        setRvadapter(datalist);
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
//                    {
//                        Log.d("MainActivity", "ChildEventListener - onChildChanged : " + s);
//                        Toast.makeText(MainActivity.this, s + " is changed!", Toast.LENGTH_SHORT).show();
//                        CompanyArticle new_ca = dataSnapshot.getValue(CompanyArticle.class);
//                        datalist.add(new_ca);
//                        setRvadapter(datalist);
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot)
//                    {
//                        Log.d("MainActivity", "ChildEventListener - onChildRemoved : " + dataSnapshot.getKey());
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s)
//                    {
//                        Log.d("MainActivity", "ChildEventListener - onChildMoved" + s);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError)
//                    {
//                        Log.d("MainActivity", "ChildEventListener - onCancelled" + databaseError.getMessage());
//                        Toast.makeText(MainActivity.this, "Failed to load " + company_name, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                //myRef.removeEventListener(listener);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) //검색어 입력시 작동 리스너
            {
                return true;
            }
        });
        return true;
    }

    public void setRvadapter(List<CompanyArticle> datalist)
    {
        RvAdapter myAdapter = new RvAdapter(this, datalist);
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
    }

    public void displayPieChart(float pos, float nega)
    {
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        //delete legend and description
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        Description d = pieChart.getDescription();
        d.setEnabled(true);

        List<PieEntry> yvalues = new ArrayList<>();

        yvalues.add(new PieEntry(pos, "상승"));
        yvalues.add(new PieEntry(nega, "하락"));

        PieDataSet dataSet = new PieDataSet(yvalues, "Sentiment of Articles");


        dataSet.setColors(new int[]{R.color.lime_yellow, R.color.lime_blue}, MainActivity.this);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        pieChart.setCenterText(company_name);
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(R.color.colorPrimary);
        pieChart.setData(data); //plotting
    }


    private void displayLineChart()
    {
        LineChart lineCharts = (LineChart) findViewById(R.id.linechart);

        String[] period = {"2018-09-16","2018-09-17", "2018-09-18", "2018-09-19", "2018-09-20", "2018-09-21", "2018-09-26", "2018-09-27", "2018-09-28", "2018-09-30", "2018-10-01", "2018-10-02", "2018-10-04", "2018-10-05", "2018-10-07", "2018-10-08", "2018-10-09", "2018-10-10", "2018-10-11", "2018-10-12", "2018-10-14", "2018-10-15", "2018-10-16", "2018-10-17", "2018-10-18", "2018-10-19", "2018-10-21", "2018-10-22", "2018-10-23", "2018-10-24"};

        double[][] posco_data =
                {{294000, 295180.35, 290619.65},
                {293000, 295149.39, 291050.61},
                {296500, 296963.56, 291236.44},
                {298500, 299705.55, 290694.45},
                {298500, 301169.52, 291030.48},
                {304500, 306554.64, 289845.36},
                {304500, 307983.31, 293016.69},
                {305500, 309285.7, 295314.3},
                {294500, 311091.66, 291908.34},
                {294500, 312049.01, 289350.99},
                {295500, 310089.28, 287710.72},
                {294000, 306587.75, 287012.25},
                {274500, 308633.3, 272566.7},
                {279000, 307412.31, 267587.69},
                {279000, 303679.52, 265120.48},
                {271500, 296911.85, 262288.15},
                {271500, 282629.94, 267570.06},
                {272000, 282643.63, 266556.37},
                {257000, 286271.71, 254128.29},
                {265000, 280388.46, 254411.54},
                {265000, 278314.75, 253885.25},
                {265500, 275539.55, 254260.45},
                {266000, 271236.58, 256163.42},
                {271500, 272140.76, 261059.24},
                {264000, 272290.67, 260509.33},
                {270000, 273779.66, 261020.34},
                {268500, 274041.52, 261958.48},
                {266000, 274041.52, 261958.48},
                {260500, 273303.33, 258296.67},
                {260500, 275173.77, 258965.13}};

        double[][] hynix_data =
                {{77200, 79491.56, 73388.44},
                {78000, 79895.41, 74144.59},
                {78800, 79058.13, 76701.87},
                {79100, 79725.89, 76594.11},
                {76700, 80002.55, 75917.45},
                {76700, 80125.39, 75594.61},
                {75000, 80649.99, 73870.01},
                {73100, 80584.08, 71655.92},
                {73100, 78521.11, 71318.89},
                {73700, 77400.26, 71239.74},
                {71700, 75704.95, 70935.05},
                {71700, 74479.89, 70840.11},
                {70000, 74916.11, 69163.89},
                {70300, 74413.94, 68546.06},
                {70300, 72461.32, 69138.68},
                {71200, 72135.27, 69264.73},
                {71200, 71722.5, 69477.5},
                {70300, 71645.9, 69674.1},
                {69000, 72205.55, 68594.45},
                {72400, 73343.49, 68296.51},
                {72400, 73963.79, 68156.21},
                {70300, 73851.2, 67908.8},
                {69700, 73892.41, 67627.59},
                {70400, 73580.08, 68499.92},
                {68700, 73009.24, 67590.76},
                {70800, 71613.4, 68346.6},
                {69700, 71463.75, 68256.25},
                {70000, 71516.25, 68323.75},
                {69200, 71276.25, 68083.75},
                {69200, 73127.82, 67795.56}};

        ArrayList<Entry> close_price = new ArrayList<>();
        ArrayList<Entry> upper_price = new ArrayList<>();
        ArrayList<Entry> lower_price = new ArrayList<>();

        //Close
        if ( company_name.equals("posco"))
        {
            for(int i = 0; i < posco_data.length; i++){
                close_price.add(new Entry(i, (float) posco_data[i][0]));
                upper_price.add(new Entry(i, (float) posco_data[i][1]));
                lower_price.add(new Entry(i, (float) posco_data[i][2]));
                }
        } else if (company_name.equals("sk hynix")){
            for(int i = 0; i < hynix_data.length; i++){
                close_price.add(new Entry(i, (float) hynix_data[i][0]));
                upper_price.add(new Entry(i, (float) hynix_data[i][1]));
                lower_price.add(new Entry(i, (float) hynix_data[i][2]));
            }
        } else {
            close_price.add(new Entry(0, 0f));
            upper_price.add(new Entry(0, 0f));
            lower_price.add(new Entry(0, 0f));
        }

        LineDataSet dataset_close = new LineDataSet(close_price, "Close");
        LineDataSet dataset_upper = new LineDataSet(upper_price, "Upper Bound");
        LineDataSet dataset_lower = new LineDataSet(lower_price, "Lower Bound");

        dataset_close.setColor(Color.BLUE);
        dataset_close.setCircleColor(Color.BLUE);
        dataset_upper.setColor(Color.RED);
        dataset_upper.setCircleColor(Color.RED);
        dataset_lower.setColor(Color.RED);
        dataset_lower.setCircleColor(Color.RED);

        LineData chartData = new LineData();
        chartData.addDataSet(dataset_close);
        chartData.addDataSet(dataset_upper);
        chartData.addDataSet(dataset_lower);

        YAxis yAxisRight = lineCharts.getAxisRight();
        yAxisRight.setEnabled(false);

        lineCharts.getXAxis().setValueFormatter(new LabelFormatter(period));
        lineCharts.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 그림 하단에만 X-value 표시
        lineCharts.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        lineCharts.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        lineCharts.getXAxis().setDrawGridLines(false); // x그리드 제거
        lineCharts.animateY(1000);

        lineCharts.setData(chartData);
//        lineCharts.invalidate();

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

    public static Float[] tokenizeUpDown(String str){
        String new_str = str.replace("[", "").replace("]", "");
        String[] str_arr = new_str.split(", ");
        Float[] new_ud = new Float[str_arr.length];
        for(int i = 0; i < str_arr.length; i++){
            new_ud[i] = Float.valueOf(str_arr[i]);
        }
        return new_ud;
    }

}
