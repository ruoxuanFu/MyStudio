package com.studio.mystudio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView news_listView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        new NewsAsyncTask().execute(URL);
    }

    private void initView() {
        news_listView = (ListView) findViewById(R.id.news_listView);
    }


    //参数1：请求网址，参数2：是否需要记录中间过程，Void是不需要记录，参数3：bean对象的封装
    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        //此方法的参数params是传递进来的String，现在只有一个，就是请求的网址URL
        //实现网络的异步访问方法
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        //访问完成后执行的方法
        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this,newsBeen);
            news_listView.setAdapter(adapter);
        }
    }

    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsInfoList = new ArrayList<>();
        NewsBean newsBean;
        try {
            String jsonString = readStream(new URL(url).openStream());

            NewsInfo info = NewsJson.handleInfoResponse(jsonString);
            for (int i = 0;i<info.getData().size();i++) {
                newsBean = new NewsBean();
                newsBean.icon = info.getData().get(i).getPicSmall();
                newsBean.title = info.getData().get(i).getName();
                newsBean.content = info.getData().get(i).getDescription();
                newsInfoList.add(newsBean);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsInfoList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";

        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
