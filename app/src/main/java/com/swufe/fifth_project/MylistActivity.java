package com.swufe.fifth_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MylistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    List<String> data = new ArrayList<String>();
    private String TAG = "MylistActivity";
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);

        //获取控件
        ListView listView = findViewById(R.id.mylist);
        //初始化数据
        for(int i = 0;i<10;i++){
            data.add("item"+i);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);  //把当前界面用adapter来管理,此时adapter已经绑定了数据,利用listView来设置adapter对象
        listView.setEmptyView(findViewById(R.id.nodata));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id) {

        Log.i(TAG,"onItemClick:position="+ position);
        Log.i(TAG,"onItemClick:parent="+ listv);
        adapter.remove(listv.getItemAtPosition(position));   //删除数据
        //adapter.notifyDataSetChanged();   //通知数据集被改变,这样的话界面会刷新,被点的列表会移除，最后剩下“No Data”
    }
}
