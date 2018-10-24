package com.example.geonhokim.pospirl;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static String company_name;
    private static float positive, negative;
    private final String hostaddress = "182.215.14.185";
    private final String serverUrl = "http://" + hostaddress + "/pos/include/testjsonpost.php";
    public TextView tv1, tv2, tv3;
    private RelativeLayout rl1, rl2, rl3;
    private BottomNavigationView bottomNavigationView;
    private List<CompanyArticle> datalist = new ArrayList<>();
    private RecyclerView myrv;
//    public ProgressBar progBar = (ProgressBar) findViewById(R.id.progressBar);


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

        myrv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        {
            @Override
            public boolean canScrollVertically()
            {
                return false;
            }

            @Override
            public boolean canScrollHorizontally()
            {
                return false;
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
                //tv4.setText("누적 적중률 90%");
                ///tv4.setTextColor(Color.BLUE);
                AsyncDataClass asyncRequestObject = new AsyncDataClass(); //http 통신을 위한 객체를 생성하고 post request 수행한다.
                asyncRequestObject.execute(serverUrl, company_name); //아이디와 비밀번호로 해당 서버에 로그인 실행

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

        yvalues.add(new PieEntry(pos, "긍정"));
        yvalues.add(new PieEntry(nega, "부정"));

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


        /*dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/


        YAxis yAxisRight = lineCharts.getAxisRight();
        yAxisRight.setEnabled(false);

        lineCharts.getXAxis().setValueFormatter(new LabelFormatter(period));
        lineCharts.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 그림 좌우에 X-value 표시
//        XAxis xAxis = lineCharts.getXAxis();
//        xAxis.setLabelCount(6);

//        lineCharts.getDescription().setEnabled(false); // Hide the description
//        lineCharts.getAxisLeft().setDrawLabels(false); // 그림의 윗부분 수치(Y-value) 사라짐
//        lineCharts.getAxisRight().setDrawLabels(false); // 그림 아랫부분 수치(Y-value) 사라짐
//        lineCharts.getXAxis().setDrawLabels(false); // 그림 (X-value) 라벨 사라짐
//        lineCharts.getLegend().setEnabled(false);   // 범례 사라짐
//        lineCharts.setClipValuesToContent(true); // 그래프 밖에 value 삐져나오지 않음.

//        float newLimit2 = Math.abs(targetScores[0]) * 2;
//        lineCharts.getAxisLeft().setAxisMinimum(-newLimit2); // 그래프 표현범위 지정
//        lineCharts.getAxisLeft().setAxisMaximum(newLimit2); // 그래프 표현범위 지정
        lineCharts.getAxisLeft().setDrawGridLines(false); // y 왼쪽 그리드 제거
        lineCharts.getAxisRight().setDrawGridLines(false); // y 오른쪽 그리드 제거
        lineCharts.getXAxis().setDrawGridLines(false); // x그리드 제거

        lineCharts.animateY(1000);

        lineCharts.setData(chartData);
//        lineCharts.invalidate();


//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0, 294000f));
//        entries.add(new Entry(1, 293000f));
//        entries.add(new Entry(2, 296500f));
//        entries.add(new Entry(3, 298500f));
//        entries.add(new Entry(4, 298500f));
//        entries.add(new Entry(5, 304500f));
//        entries.add(new Entry(6, 304500f));
//        entries.add(new Entry(7, 305500f));
//        entries.add(new Entry(8, 294500f));
//        entries.add(new Entry(9, 294500f));
//        entries.add(new Entry(10, 295500f));
//        entries.add(new Entry(11, 294000f));
//        entries.add(new Entry(12, 274500f));
//        entries.add(new Entry(13, 279000f));
//        entries.add(new Entry(14, 279000f));
//        entries.add(new Entry(15, 271500f));
//        entries.add(new Entry(16, 271500f));
//        entries.add(new Entry(17, 272000f));
//        entries.add(new Entry(18, 257000f));
//        entries.add(new Entry(19, 265000f));
//        entries.add(new Entry(20, 265000f));
//        entries.add(new Entry(21, 265500f));
//        entries.add(new Entry(22, 266000f));
//        entries.add(new Entry(23, 271500f));
//        entries.add(new Entry(24, 264000f));
//        entries.add(new Entry(25, 270000f));
//        entries.add(new Entry(26, 268500f));
//        entries.add(new Entry(27, 266000f));
//        entries.add(new Entry(28, 260500f));
//        entries.add(new Entry(29, 270347f));
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//        labels.add("May");
//        labels.add("June");
//        labels.add("July");
//        labels.add("August");
//        labels.add("September");
//        labels.add("October");
//        labels.add("November");
//        labels.add("December");
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


    public void setRvadapter(List<CompanyArticle> datalist)
    {
        RvAdapter myAdapter = new RvAdapter(this, datalist);
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
    }


    private int returnParsedJsonObject(JSONArray jsonResult)
    {
        JSONObject jsonObject = null;
        datalist.clear(); //파싱할때마다 데이터리스트 초기화. 안해주면 데이터 누적됨.
        positive = 0;
        negative = 0;


        String title, links, contents;
        double scores, up_prob, down_prob;
        String word1, word2, word3, word4, word5, word6;
        double prob1, prob2, prob3, prob4, prob5, prob6;

        int errorCheck = 0;


        for (int i = 0; i < jsonResult.length(); i++)
        {
            try
            {
                jsonObject = jsonResult.getJSONObject(i);
                title = jsonObject.getString("title");
                links = jsonObject.getString("links");
                contents = jsonObject.getString("contents");
                scores = jsonObject.getDouble("scores");
                up_prob = jsonObject.getDouble("up_prob");
                down_prob =jsonObject.getDouble("down_prob");
                word1 = jsonObject.getString("word1");
                word2 = jsonObject.getString("word2");
                word3 = jsonObject.getString("word3");
                word4 = jsonObject.getString("word4");
                word5 = jsonObject.getString("word5");
                word6 = jsonObject.getString("word6");
                prob1 = jsonObject.getDouble("prob1");
                prob2 = jsonObject.getDouble("prob2");
                prob3 = jsonObject.getDouble("prob3");
                prob4 = jsonObject.getDouble("prob4");
                prob5 = jsonObject.getDouble("prob5");
                prob6 = jsonObject.getDouble("prob6");
                errorCheck = jsonObject.getInt("errorCheck");

                cumulativeScore(up_prob, down_prob);



                CompanyArticle ca = new CompanyArticle(title, links, contents, scores, up_prob, down_prob, word1, word2, word3, word4, word5, word6, prob1, prob2, prob3, prob4, prob5, prob6);
                datalist.add(ca);



            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            setRvadapter(datalist);
        }

        return errorCheck;
    }

    private void cumulativeScore(double up_prob, double down_prob)
    {
        float up_probf = (float) up_prob;
        float down_probf = (float) down_prob;

        positive += up_probf;
        negative += down_probf;

    }

    private class AsyncDataClass extends AsyncTask<String, Void, JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) //파라미터를 스트링 배열 형태로 받는다. 여기서는 params[0] = url, params[1] = Company Name
        {

            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);

            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);

            HttpPost httpPost = new HttpPost(params[0]);  //serverUrl을 http post의 인풋으로 받음.

            String result = "";

            JSONArray jsonResult = null; //json 파싱 결과를 처음엔 null 상태로 초기화

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

            try
            {

                nameValuePairs.add(new BasicNameValuePair("company_name", params[1]));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                result = inputStreamToString(response.getEntity().getContent()).toString(); //인풋 결과를 문자열로 변환

                jsonResult = new JSONArray(result);

            } catch (ClientProtocolException e)
            {
                e.printStackTrace();

            } catch (IOException e)
            {

                e.printStackTrace();

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            return jsonResult;

        }

        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();

//            progBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(JSONArray jsonResult) //requset->response 과정을 거치고 나면
        {
            super.onPostExecute(jsonResult);
//            progBar.setVisibility(View.GONE);

            int errorCheck = returnParsedJsonObject(jsonResult);


            if (errorCheck == 1)
            {
                Toast.makeText(MainActivity.this, company_name+"에 대한 정보를 찾았습니다.", Toast.LENGTH_SHORT).show();
                displayPieChart(positive, negative);
                displayLineChart();
            } else
            {
                Toast.makeText(MainActivity.this, company_name+"에 대한 정보가 없습니다.\nposco 또는 sk hynix로 검색해주세요.", Toast.LENGTH_SHORT).show();
                displayPieChart(0, 0);
                displayLineChart();
            }
        }

        private StringBuilder inputStreamToString(InputStream is) //jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
        {

            String rLine = "";

            StringBuilder answer = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try
            {

                while ((rLine = br.readLine()) != null)
                {
                    answer.append(rLine);
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return answer; //json 파싱결과가 저장이 된다.
        }


    }

}
