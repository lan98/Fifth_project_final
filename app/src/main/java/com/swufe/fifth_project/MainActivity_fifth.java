package com.swufe.fifth_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;

public class MainActivity_fifth extends AppCompatActivity implements View.OnClickListener,Runnable{
    public final String TAG = "MainActivity_fifth";
    EditText rmb;
    TextView show;
    Handler handler;
    private float dollarRate = 1 / 6.7f;
    private float euroRate = 1 / 8.9f;
    private float wonRate = 500;
    private String updateDate = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fifth);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);   //使用默认值改变当前汇率
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //将日期转换成字符串  //MM表示月，mm表示分钟
        final String todayStr = sdf.format(today);


        Log.i(TAG, "onCreate:sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate:sp euroRate=" + euroRate);
        Log.i(TAG, "onCreate:sp wonRate=" + wonRate);
        Log.i(TAG, "onCreate:sp updateDate=" + updateDate);
        Log.i(TAG, "onCreate:sp todayStr=" + todayStr);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate:需要更新");
            //开启子线程
            Thread t = new Thread(this);  //把当前对象加到这里，实现后面的Run方法
            t.start();
        }
        else{
            Log.i(TAG, "onCreate:不需要更新");
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    Bundle bd1 = (Bundle) msg.obj;  //获取bundle返回的数据
                    dollarRate = bd1.getFloat("dollar-rate");
                    euroRate = bd1.getFloat("euro-rate");
                    wonRate = bd1.getFloat("won-rate");
                    //输出
                    Log.i(TAG,"handleMessage:dollarRate:"+dollarRate);
                    Log.i(TAG,"handleMessage:euroRate:"+euroRate);
                    Log.i(TAG,"handleMessage:wonRate:"+wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.apply();

                    //提示窗
                    Toast.makeText(MainActivity_fifth.this,"汇率已更新",Toast.LENGTH_SHORT);
                }
                super.handleMessage(msg);
            }
        };
    }

    public void onClick(View btn) {
        //获取用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);//转化弄成浮点型
        } else {
            //提示用户输入内容
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "onClick:r=" + r);
        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f", r * dollarRate));
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f", r * euroRate));
        } else if (btn.getId() == R.id.btn_won) {
            show.setText(String.format("%.2f", r * wonRate));
        }

    }

    public void openOne(View view) {
        openConfig();//把下面openConfig里的一段代码写成方法openConfig，会执行到打开菜单项
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(TAG, "openOne:dollarRate=" + dollarRate);
        Log.i(TAG, "openOne:euroRate=" + euroRate);
        Log.i(TAG, "openOne:wonRate=" + wonRate);
        //startActivity(config);(这个方法只能往下，而不能带回）
        startActivityForResult(config, 1);//可以将activity带回
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);//把资源加载到menu里边,配置参数加载过来
        return true;//返回菜单
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {

            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this, Mylist2Activity.class);
            startActivity(list);  //(这个方法只能往下，而不能带回）
        }
        //配置对话框
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            /*bdl.putFloat("key_dollar",newdollar);
        bdl.putFloat("key_euro",neweuro);
        bdl.putFloat("key_won",newwon);*/
            Bundle bundle = data.getExtras();
            //获取新的值
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);
            Log.i(TAG, "onActivityResult:dollarRate=" + dollarRate);
            Log.i(TAG, "onActivityResult:euroRate=" + euroRate);
            Log.i(TAG, "onActivityResult:wonRate=" + wonRate);

            //将新设置的汇率写道SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();   //保存
            Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run:run()......");
        for(int i=1;i<=6;i++){
            Log.i(TAG,"run:i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  //当前停止2秒钟
        }
        //用于保存获取的汇率
        Bundle bundle;

        //获取网络数据（就是说我们之前的汇率都是自己填的，但是现在要用的是网络上的，具有变更性）
        /*URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();  //获得一个输入流

            String html = inputStream25String(in);  //通过输入流的方法转成字符串
            Log.i(TAG,"run: html="+ html);
            Document doc = Jsoup.parse(html);  //在html中提取对象
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        bundle = getFromBOC();
        //bundle中保存所获取的汇率
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;   //两个参数之间传递数据
        //msg.obj = "Hello from run()";   //写好了对方的标识
        msg.obj = bundle;  //带回bundle对象，在左线程中获得元素
        handler.sendMessage(msg);//由Handler把Message放到队列的过程
    }

    /**
    * 从banlofchina中获取数据
    */
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run:"+doc.title());
            Elements tables = doc.getElementsByTag("table");  //获取标签
            /*for(Element table:tables){
                Log.i(TAG,"run:table["+1+"]="+table);
                i++;
            }*/
            Element table1 = tables.get(0);
            //Log.i(TAG,"run:table1=" + table1);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());

                if("美元".equals(td1.text())){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("欧元".equals(td1.text())){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("韩元".equals(td1.text())){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run:"+doc.title());
            Elements tables = doc.getElementsByTag("table");  //获取标签
            /*for(Element table:tables){
                Log.i(TAG,"run:table["+1+"]="+table);
                i++;
            }*/
            Element table1 = tables.get(0);
            //Log.i(TAG,"run:table1=" + table1);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());

                if("美元".equals(td1.text())){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("欧元".equals(td1.text())){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("韩元".equals(td1.text())){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream25String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for( ; ; ){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }

    public void openOne2(View btn) {
        //打开一个界面Activity
        Log.i("open","openOne2:");
        Intent hello = new Intent(this,MainActivity_fifth_basketball.class);
        startActivity(hello);
    }
}

