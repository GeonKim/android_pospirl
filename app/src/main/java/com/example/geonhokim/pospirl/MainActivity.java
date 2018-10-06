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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public TextView tv1, tv2, tv3;
    private BottomNavigationView bottomNavigationView;
    public  RelativeLayout rl1, rl2, rl3;

//    private final String serverUrl = "http://localhost/pos/include/testjson.php";
private final String baseServerUrl = "http://localhost/pos/include/testjson.php?company_name=";
    public static String company_name;
    private List<CompanyArticle> datalist = new ArrayList<>();
    private RecyclerView myrv;
    private JsonArrayRequest ArrayRequest;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myrv = (RecyclerView) findViewById(R.id.rv_article);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.analysis_content);

                switch (item.getItemId())
                {
                    case R.id.action_favorite:
                        tv1.setVisibility(View.VISIBLE);
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
                company_name = s;
                tv2.setText("오늘의 뉴스는...");
                displayPieChart(10f, 2f, 0.5f);
                jsoncall();
//                AsyncDataClass asyncRequestObject = new AsyncDataClass(); //http 통신을 위한 객체를 생성하고 post request 수행한다.
//                asyncRequestObject.execute(serverUrl, company_name); //아이디와 비밀번호로 해당 서버에 로그인 실행


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

    public void displayPieChart(float positive, float negative, float nuetral)
    {
        PieChart pieChart = (PieChart)findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        //delete legend and description
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        Description d = pieChart.getDescription();
        d.setEnabled(false);

        List<PieEntry> yvalues = new ArrayList<>();

        yvalues.add(new PieEntry(positive, "긍정"));
        yvalues.add(new PieEntry(negative, "부정"));
        yvalues.add(new PieEntry(nuetral, "중립"));

        PieDataSet dataSet = new PieDataSet(yvalues, "Sentiment of Articles");


        dataSet.setColors(new int[] { R.color.postechOrange, R.color.postechRed, R.color.postechGray }, MainActivity.this);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        pieChart.setCenterText(company_name);
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(R.color.colorPrimary);
        pieChart.setData(data); //plotting
    }

    public void setRvadapter(List<CompanyArticle> datalist)
    {
        RvAdapter myAdapter = new RvAdapter(this, datalist);
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
    }

    public void jsoncall()
    {
        String serverUrl = "http://localhost/pos/include/testjson.php?company_name=posco";

        ArrayRequest = new JsonArrayRequest(serverUrl, new Response.Listener<JSONArray>()
        {

            @Override
            public void onResponse(JSONArray response)
            {

                JSONObject jsonObject = null;

                String datetime, title, links, keywords;
                double scores;


                for (int i = 0; i < response.length(); i++)
                {

                    try
                    {
                        jsonObject = response.getJSONObject(i);

                        datetime = jsonObject.getString("datetime");
                        title = jsonObject.getString("title");
                        links = jsonObject.getString("links");
                        scores = jsonObject.getDouble("scores");
                        keywords = jsonObject.getString("keywords");

                        CompanyArticle ca = new CompanyArticle(datetime, title, links, scores, keywords);
                        datalist.add(ca);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                setRvadapter(datalist);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(ArrayRequest);
    }


//    private void returnParsedJsonObject(JSONArray jsonResult)
//    {
//        JSONObject jsonObject = null;
//
//        int returnedResult = 0;
//
//
//        String datetime, title, links, keywords;
//        double scores;
//
//        for (int i = 0; i < jsonResult.length(); i++)
//        {
//            try
//            {
//                jsonObject = jsonResult.getJSONObject(i);
//
//                datetime = jsonObject.getString("datetime");
//                title = jsonObject.getString("title");
//                links = jsonObject.getString("links");
//                scores = jsonObject.getDouble("scores");
//                keywords = jsonObject.getString("keywords");
//
//                CompanyArticle ca = new CompanyArticle(datetime, title, links, scores, keywords);
//                datalist.add(ca);
//
//            }catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        setRvadapter(datalist);
//        //return returnedResult;
//
//    }
//
//    private class AsyncDataClass extends AsyncTask<String, Void, JSONArray>
//    {
//        @Override
//        protected JSONArray doInBackground(String... params) //파라미터를 스트링 배열 형태로 받는다. 여기서는 params[0] = url, params[1] = Company Name
//        {
//
//            HttpParams httpParameters = new BasicHttpParams();
//
//            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
//
//            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
//
//            HttpClient httpClient = new DefaultHttpClient(httpParameters);
//
//            HttpPost httpPost = new HttpPost(params[0]);  //serverUrl을 http post의 인풋으로 받음.
//
//            String result = "";
//
//            JSONArray jsonResult = null; //json 파싱 결과를 처음엔 null 상태로 초기화
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
//
//            try
//            {
//
//                nameValuePairs.add(new BasicNameValuePair("company_name", params[1]));
//
//                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                HttpResponse response = httpClient.execute(httpPost);
//
//                result = inputStreamToString(response.getEntity().getContent()).toString(); //인풋 결과를 문자열로 변환
//
//                jsonResult = new JSONArray(result);
//
//            } catch (ClientProtocolException e)
//            {
//
//                e.printStackTrace();
//
//            } catch (IOException e)
//            {
//
//                e.printStackTrace();
//
//            } catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//
//            return jsonResult;
//
//        }
//
//        @Override
//        protected void onPreExecute()
//        {
//
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected void onPostExecute(JSONArray jsonResult) //requset->response 과정을 거치고 나면
//        {
//
//            super.onPostExecute(jsonResult);
//            returnParsedJsonObject(jsonResult);
//
////            if (result.equals("") || result == null) //null 인 경우 아예 서버로의 접속이 불가하여 어떤 값도 받지 못한 경우임
////            {
////
////                Toast.makeText(MainActivity.this, "Server connection failed", Toast.LENGTH_SHORT).show();
////
////                return;
////
////            }
////
////
////            int jsonResult = returnParsedJsonObject(result);
////
////            if (jsonResult == 0) //0이라면 query fail
////            {
////
////                Toast.makeText(MainActivity.this, "No Data about "+company_name, Toast.LENGTH_SHORT).show();
////
////                return;
////
////            }
////
////            if (jsonResult == 1) //1이라면 query success
////            {
////                return
////            }
//
//        }
//
//        private StringBuilder inputStreamToString(InputStream is) //jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
//        {
//
//            String rLine = "";
//
//            StringBuilder answer = new StringBuilder();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//            try
//            {
//
//                while ((rLine = br.readLine()) != null)
//                {
//                    answer.append(rLine);
//                }
//
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//            return answer; //json 파싱결과가 저장이 된다.
//        }
//
//    }

}
