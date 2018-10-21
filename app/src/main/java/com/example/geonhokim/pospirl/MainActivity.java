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
import com.github.mikephil.charting.utils.ColorTemplate;

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
    public TextView tv1, tv2, tv3, tv4;
    private RelativeLayout rl1, rl2, rl3;
    private BottomNavigationView bottomNavigationView;
    private List<CompanyArticle> datalist = new ArrayList<>();
    private RecyclerView myrv;
//    private MenuItem prevBottomNavigation;

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
        tv4 = (TextView) findViewById(R.id.tv4);
        rl1 = (RelativeLayout) findViewById(R.id.analysis_contents);
        rl2 = (RelativeLayout) findViewById(R.id.articles_contents);
        rl3 = (RelativeLayout) findViewById(R.id.prediction_contents);
//        prevBottomNavigation = bottomNavigationView.getMenu().getItem(0);


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
//
//    public void onPageSelected(int position)
//    {
//        if (prevBottomNavigation != null)
//        {
//            prevBottomNavigation.setChecked(false);
//        }
//        prevBottomNavigation = bottomNavigationView.getMenu().getItem(position);
//        prevBottomNavigation.setChecked(true);
//    }


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
                tv3.setText(company_name + "의 오늘 종가예측 XXX원 (95%)");
                tv4.setText("누적 적중률 90%");
                tv4.setTextColor(Color.BLUE);
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

    public void displayPieChart()
    {
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        //delete legend and description
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        Description d = pieChart.getDescription();
        d.setEnabled(false);

        List<PieEntry> yvalues = new ArrayList<>();

        yvalues.add(new PieEntry(positive, "긍정"));
        yvalues.add(new PieEntry(negative, "부정"));

        PieDataSet dataSet = new PieDataSet(yvalues, "Sentiment of Articles");


        dataSet.setColors(new int[]{R.color.postechOrange, R.color.postechRed}, MainActivity.this);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        pieChart.setCenterText(company_name);
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(R.color.colorPrimary);
        pieChart.setData(data); //plotting

        positive = 0;
        negative = 0;
    }


    private void displayLineChart()
    {
        LineChart lineChart = (LineChart) findViewById(R.id.linechart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(3, 4f));
        entries.add(new Entry(4, 8f));
        entries.add(new Entry(5, 6f));
        entries.add(new Entry(6, 2f));
        entries.add(new Entry(7, 18f));
        entries.add(new Entry(8, 9f));
        entries.add(new Entry(9, 12f));
        entries.add(new Entry(10, 5f));
        entries.add(new Entry(11, 3f));
        entries.add(new Entry(12, 7f));
        entries.add(new Entry(13, 9f));

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        labels.add("August");
        labels.add("September");
        labels.add("October");
        labels.add("November");
        labels.add("December");

        LineData data = new LineData(dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        /*dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);


        lineChart.animateY(1000);
        lineChart.setData(data);
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

                cumulativeScore(scores);
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

    private void cumulativeScore(double scores)
    {
        float scoref = (float) scores;

        if (scores >= 0)
        {
            positive += scoref;
        } else
        {
            negative += Math.abs(scoref);
        }
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

        }

        @Override
        protected void onPostExecute(JSONArray jsonResult) //requset->response 과정을 거치고 나면
        {
            super.onPostExecute(jsonResult);
//            returnParsedJsonObject(jsonResult);
            int errorCheck = returnParsedJsonObject(jsonResult);

            if (errorCheck == 1)
            {
                Toast.makeText(MainActivity.this, company_name+"에 대한 정보를 찾았습니다.", Toast.LENGTH_SHORT).show();
                displayPieChart();
                displayLineChart();
            } else
            {
                Toast.makeText(MainActivity.this, company_name+"에 대한 정보가 없습니다.\nposco 또는 sk hynix로 검색해주세요.", Toast.LENGTH_SHORT).show();
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
