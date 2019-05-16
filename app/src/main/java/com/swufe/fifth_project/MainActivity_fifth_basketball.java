package com.swufe.fifth_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity_fifth_basketball extends AppCompatActivity{

    TextView score,score2;
    public final String TAG = "MainActivity_fifth_basketball";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fifth_basketball);
        score = (TextView) findViewById(R.id.score);
        score2 = (TextView) findViewById(R.id.score2);
    }

    //处理旋转时数据丢失的问题
    @SuppressLint("LongLogTag")
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //获得控件中的内容
        String scorea = ((TextView) findViewById(R.id.score)).getText().toString();
        String scoreb = ((TextView) findViewById(R.id.score2)).getText().toString();
        Log.i(TAG,"onSaveInstanceState:");
        outState.putString("teamA_score",scorea);
        outState.putString("teamB_score",scoreb);
    }

    //返回
    @SuppressLint("LongLogTag")
    @Override   //用bundle从中获取内容
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String scorea = savedInstanceState.getString("teamA_score");
        String scoreb = savedInstanceState.getString("teamB_score");
        Log.i(TAG,"savedInstanceState:");
    }

    public void btnAdd1(View btn) {
        if(btn.getId()==R.id.btn_1){
            showScore(1);
        }
        else{
            showScore2(1);
        }
    }
    public void btnAdd2(View btn) {
        if(btn.getId()==R.id.btn_2){
            showScore(2);
        }
        else{
            showScore2(2);
        }
    }
    public void btnAdd3(View btn) {
        if(btn.getId()==R.id.btn_3){
            showScore(3);
        }
        else{
            showScore2(3);
        }
    }
    public void btnReset(View btn) {
        TextView out = (TextView) findViewById(R.id.score);
        out.setText("0");
        ((TextView) findViewById(R.id.score2)).setText("0");
    }
    private void showScore(int i){
        TextView out = (TextView) findViewById(R.id.score);
        String oldScore = (String) out.getText();
        String newScore = String.valueOf(Integer.parseInt(oldScore)+i);
        out.setText(newScore);
    }
    private void showScore2(int i){
        TextView out = (TextView) findViewById(R.id.score2);
        String oldScore = (String) out.getText();
        String newScore = String.valueOf(Integer.parseInt(oldScore)+i);
        out.setText(newScore);
    }

}