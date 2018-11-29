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
    public String company_name;
    public TextView tv1, tv2, tv3, sv1, sv2, sv3, sv4;
    private RelativeLayout rl1, rl2, rl3;
    private BottomNavigationView bottomNavigationView;
    private List<CompanyArticle> datalist = new ArrayList<>();
    private RecyclerView myrv;

    public static Float[] tokenizeStrToFloatArr(String str)
    {
        String new_str = str.replace("[", "").replace("]", "");
        String[] str_arr = new_str.split(", ");
        Float[] new_fa = new Float[str_arr.length];
        for (int i = 0; i < str_arr.length; i++)
        {
            new_fa[i] = Float.valueOf(str_arr[i]);
        }
        return new_fa;
    }

    public static String[] tokenizeStrToStrArr(String str)
    {
        String new_str = str.replace("[", "").replace("]", "");
        String[] str_arr = new_str.split(", ");
        return str_arr;
    }

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
        sv1 = (TextView) findViewById(R.id.stock_yesterday_close_value);
        sv2 = (TextView) findViewById(R.id.stock_today_close_value);
        sv3 = (TextView) findViewById(R.id.stock_today_upper_value);
        sv4 = (TextView) findViewById(R.id.stock_today_lower_value);
        rl1 = (RelativeLayout) findViewById(R.id.analysis_contents);
        rl2 = (RelativeLayout) findViewById(R.id.articles_contents);
        rl3 = (RelativeLayout) findViewById(R.id.prediction_contents);

        String token;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        MyFirebaseInstanceIDService myf = new MyFirebaseInstanceIDService();
        myf.onTokenRefresh();
        token = myf.getRefreshedToken();

        DatabaseReference fcmToken = database.getReference().child("users").child(token).child("fcm_token");
        fcmToken.setValue(token);


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
                Toast.makeText(MainActivity.this, s + "의 정보를 수신합니다.", Toast.LENGTH_SHORT).show();
                company_name = s;
                tv1.setText("오늘의 뉴스는...");
                tv2.setText("뉴스분석");
                tv3.setText(s.toUpperCase() + " 종가 예측");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child(s);

                // 리얼타임 데이터베이스 읽는 방법1. ValueEventListener 이용해서 전체 차일드 수신.
                myRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Log.d("MainActivity", "EventListener - onChildChanged : " + dataSnapshot.getValue());

                        datalist.clear();
                        float positive = 0;
                        float negative = 0;

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {
                            CompanyArticle ca = dataSnapshot1.getValue(CompanyArticle.class);
                            Float[] up_down_prob = tokenizeStrToFloatArr(ca.getUp_down());
                            positive += up_down_prob[0];
                            negative += up_down_prob[1];

                            datalist.add(ca);
                        }

                        if (datalist.size() <= 0)
                        {
                            if (company_name.equals("posco") || company_name.equals("sk hynix") || company_name.equals("kogas"))
                            {
                                Toast.makeText(MainActivity.this, company_name + "의 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                            } else
                            {
                                Toast.makeText(MainActivity.this, company_name + "의 정보가 존재하지 않습니다.\nposco, sk hynix, kogas 중 검색해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        setRvadapter(datalist);
                        displayPieChart(positive, negative);
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

                DatabaseReference myRef2 = database.getReference().child(s + "_stock").child(s + "_data");


                myRef2.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Log.d("MainActivity", "EventListener - onChildChanged : " + dataSnapshot.getValue());


                        try
                        {
                            CompanyStock cs = dataSnapshot.getValue(CompanyStock.class);
                            String[] date_arr = tokenizeStrToStrArr(cs.getDate());
                            Float[] close_arr = tokenizeStrToFloatArr(cs.getClose());
                            Float[] upper_arr = tokenizeStrToFloatArr(cs.getUpper());
                            Float[] lower_arr = tokenizeStrToFloatArr(cs.getLower());
                            displayLineChart(date_arr, close_arr, upper_arr, lower_arr);

                        } catch (NullPointerException e)
                        {
                            System.err.println(e.getStackTrace());
                            displayLineChart();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error)
                    {
                        // Failed to read value
                        Log.d("MainActivity", "Failed to read value.", error.toException());
                        Toast.makeText(MainActivity.this, "정보 수신 실패", Toast.LENGTH_SHORT).show();
                    }
                });

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

//        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        pieChart.setCenterText(company_name.toUpperCase());
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(R.color.colorPrimary);
        pieChart.setData(data); //plotting
    }

    private void displayLineChart()
    {
        LineChart lineCharts = (LineChart) findViewById(R.id.linechart);

        ArrayList<Entry> close_price = new ArrayList<>();

        close_price.add(new Entry(0, 0f));

        LineDataSet dataset_close = new LineDataSet(close_price, "");

        LineData chartData = new LineData();
        chartData.addDataSet(dataset_close);

        lineCharts.setData(chartData);

        sv1.setText("");
        sv2.setText("");
        sv3.setText("");
        sv4.setText("");
    }

    private void displayLineChart(String[] date_arr, Float[] close_arr, Float[] upper_arr, Float[] lower_arr)
    {
        LineChart lineCharts = (LineChart) findViewById(R.id.linechart);

        String[] period = date_arr;

        ArrayList<Entry> close_price = new ArrayList<>();
        ArrayList<Entry> upper_price = new ArrayList<>();
        ArrayList<Entry> lower_price = new ArrayList<>();


        for (int i = 0; i < date_arr.length; i++)
        {
            close_price.add(new Entry(i, close_arr[i]));
            upper_price.add(new Entry(i, upper_arr[i]));
            lower_price.add(new Entry(i, lower_arr[i]));
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

        XAxis xAxis = lineCharts.getXAxis();
        xAxis.setTextSize(3f);

        lineCharts.getXAxis().setValueFormatter(new LabelFormatter(period));
        lineCharts.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 그림 하단에만 X-value 표시
        lineCharts.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        lineCharts.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        lineCharts.getXAxis().setDrawGridLines(false); // x그리드 제거
//        lineCharts.animateY(1000);

        lineCharts.setData(chartData);
//        lineCharts.invalidate();

        sv1.setText(String.valueOf(Math.round(close_arr[date_arr.length - 2])) + "원");
        sv2.setText(String.valueOf(Math.round(close_arr[date_arr.length - 1])) + "원");
        sv3.setText(String.valueOf(Math.round(upper_arr[date_arr.length - 1])) + "원");
        sv4.setText(String.valueOf(Math.round(lower_arr[date_arr.length - 1])) + "원");

    }

    public class LabelFormatter implements IAxisValueFormatter
    {
        private final String[] mLabels;

        public LabelFormatter(String[] labels)
        {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis)
        {
            return mLabels[(int) value];
        }
    }

}
