package com.example.geonhokim.pospirl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public TextView tv1, tv2, tv3;
    private BottomNavigationView bottomNavigationView;
    public  RelativeLayout rl1, rl2, rl3;
        @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        rl3 = (RelativeLayout) findViewById(R.id.rl3);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.analysis_content);

                switch (item.getItemId())
                {
                    case R.id.action_favorite:
                        tv1.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                        tv3.setVisibility(View.GONE);
                        break;
                    case R.id.action_search:
                        tv1.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.GONE);
                        break;
                    case R.id.action_trend:
                        tv1.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                        tv3.setVisibility(View.VISIBLE);
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
                tv2.setText(s);

                PieChart pieChart = (PieChart)findViewById(R.id.piechart);
                pieChart.setUsePercentValues(true);
                Legend l = pieChart.getLegend();
                l.setEnabled(false);
                Description d = pieChart.getDescription();
                d.setEnabled(false);

                List<PieEntry> yvalues = new ArrayList<>();

                yvalues.add(new PieEntry(8f, "중립"));
                yvalues.add(new PieEntry(15f, "긍정"));
                yvalues.add(new PieEntry(12f, "부정"));

                PieDataSet dataSet = new PieDataSet(yvalues, "Election Results");
                dataSet.setColors(new int[] { R.color.postechGray,R.color.postechOrange, R.color.postechRed}, MainActivity.this);
                PieData data = new PieData(dataSet);

                //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                //data.setValueFormatter(new PercentFormatter()); //show proportion
                data.setValueTextSize(20f);

                pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
                pieChart.setData(data); //plotting

                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);

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
}
