package com.swufe.fifth_project;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends ArrayAdapter{

    private static final String TAG = "MyAdapter";

    public MyAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String,String>> list){
        super(context, resource, list);
    }


    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = convertView;
        if(itemView==null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        Map<String,String> map = (Map<String,String>) getItem(position);
        //从View中找控件
        TextView title = (TextView) itemView.findViewById(R.id.itemTitle);
        TextView detail = (TextView) itemView.findViewById(R.id.itemDetail);

        title.setText("Title:"+map.get("ItemTitle"));
        detail.setText("Detail:"+map.get("ItemDetail"));

        return itemView;
    }
}