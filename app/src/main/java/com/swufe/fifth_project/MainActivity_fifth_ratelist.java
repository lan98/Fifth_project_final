package com.swufe.fifth_project;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_fifth_ratelist extends ListActivity implements Runnable{

    String data[] = {"wait..."};
    Handler handler;
    public final String TAG = "MainActivity_fifth_ratelist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_fifth_ratelist);
        //定义一个list列表，这个列表是一组数据集合，但是没有长度限制，我们可以在里边不断加数据，此时我们可以用list来获取数据
        List<String> list1 = new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);  //把当前界面用adapter来管理,此时adapter已经绑定了数据

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(MainActivity_fifth_ratelist.this, android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                handler.handleMessage(msg);
            }
        };
    }
    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        //获取网络数据，放入list带回到主线程中
        List<String> retList = new ArrayList<String>();

        Document doc = null;
        try {
            Thread.sleep(3000);
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
                retList.add(td1.text()+"==>"+td2.text());

                /*if("美元".equals(td1.text())){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("欧元".equals(td1.text())){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("韩元".equals(td1.text())){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Message msg = handler.obtainMessage(5);   //获得message
        //msg.what = 5;   //两个参数之间传递数据
        //msg.obj = "Hello from run()";   //写好了对方的标识
        msg.obj = retList;  //存放数据给obj
        handler.sendMessage(msg);//发送消息
    }
}
