package com.swufe.fifth_project;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mylist2Activity extends ListActivity implements Runnable,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private static final String TAG = "Mylist2Activity";
    Handler handler;
    private List<HashMap<String, String>> listItems;   //存放文字、图片信息
    private SimpleAdapter listItemAdapter;  //适配器（适配器：数据和控件的关系需要适配器来完成）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();   //调用该方法
        //this.setListAdapter(listItemAdapter);   //初始化，然后设置listItemAdapter

        //自定义一个adapter
        //MyAdapter myadapter = new MyAdapter(this, R.layout.list_item, listItems);
        //this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    listItems = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(Mylist2Activity.this, listItems, //listItem数据源
                            R.layout.list_item,   //listItem的XML布局实现
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        //获取listView对象,该方法返回的数据就是列表控件
        getListView().setOnItemClickListener(this);   //当列表项被按下的时候有一个监听
        getListView().setOnItemLongClickListener(this);
    }

        private void initListView () {
            //定义对象
            listItems = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < 10; i++) {
                //先定义、创建对象，把map对象放到list里边
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", "Rate:" + i);   //标题文字
                map.put("ItemDetail", "detail:" + i);   //详细描述
                listItems.add(map);
            }
            //生成适配器的Item和动态数组对应的元素
            listItemAdapter = new SimpleAdapter(this, listItems, //listItem数据源
                    R.layout.list_item,   //listItem的XML布局实现
                    new String[]{"ItemTitle", "ItemDetail"},
                    new int[]{R.id.itemTitle, R.id.itemDetail}
            );
        }

        @Override
        public void run(){
            //获取网络数据，放入list带入主线程中
            List<HashMap<String, String>> retList = new ArrayList<>();
            Document doc = null;
            try {
                Thread.sleep(3000);
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                Log.i(TAG, "run:" + doc.title());
                Elements tables = doc.getElementsByTag("table");  //获取标签
            /*for(Element table:tables){
                Log.i(TAG,"run:table["+1+"]="+table);
                i++;
            }*/
                Element table1 = tables.get(0);
                //Log.i(TAG,"run:table1=" + table1);
                //获取td中的数据
                Elements tds = table1.getElementsByTag("td");
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);

                    String str = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "run:" + str + "==>" + val);
                    //retList.add(td1.text() + "==>" + td2.text());
                    HashMap<String, String> map = new HashMap<String, String>();   //构造一个HashMap对象
                    //往map里边放内容
                    map.put("ItemTitle", td1.text());
                    map.put("ItemDetail", td2.text());
                    retList.add(map);

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

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Message msg = handler.obtainMessage(5);   //获得message
            //msg.what = 5;   //两个参数之间传递数据
            //msg.obj = "Hello from run()";   //写好了对方的标识
            msg.obj = retList;  //存放数据给obj
            handler.sendMessage(msg);//发送消息
        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"onItemClick: parent=" + parent);
        Log.i(TAG,"onItemClick: view=" + view);
        Log.i(TAG,"onItemClick: position=" + position);
        Log.i(TAG,"onItemClick: id=" + id);
       /* long map = getListView().getItemIdAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG,"onItemClick:titelStr=" + titleStr);
        Log.i(TAG,"onItemClick:detailStr=" + detailStr);*/

        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String titleStr = String.valueOf(title.getText());
        String detailStr = String.valueOf(detail.getText());
        Log.i(TAG,"onItemClick:titelStr=" + titleStr);
        Log.i(TAG,"onItemClick:detailStr=" + detailStr);


        //打开新的页面，传入参数
        Intent rates = new Intent(this,RateActivity.class);
        rates.putExtra("title",titleStr);
        rates.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rates);

    }

    @Override
    //执行的时间取决于我们长按列表项的执行
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG,"OnItemLongClick:长安列表项position="+ position);
        //删除操作
        //listItems.remove(position);
        //listItemAdapter.notifyDataSetChanged();
        //构造对话框进行确认操作
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            //通过一个匿名的类实现接口,也可以单独定义类来实现这个方法，which表示当前激活事件的按钮是“是”还是“否”，但当前是匿名的，所以它只能响应“是”这个按钮
            public void onClick(DialogInterface dialog, int which) {

                Log.i(TAG,"OnClick:对话框事件处理");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("否",null);   //构造builder对象
        builder.create().show();
        Log.i(TAG,"onItemLongClick:size="+ listItems.size());  //历史的集合里面放了多少个元素在里面
        return false;
    }
}

