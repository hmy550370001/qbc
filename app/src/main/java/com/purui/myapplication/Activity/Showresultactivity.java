package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.purui.myapplication.R;

public class Showresultactivity extends Activity  implements View.OnClickListener{
    private  static  final  String TAG= "HL";
    private Intent intent;
    private DataSaveThread dataSaveThread;
    private Button btn_showresultback,btn_showresultcontinuetest,btn_showresultprintresult,btn_showresultuploaddata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showresult);
        Log.e(TAG,"TestActivity");
    init();
    }

    private void init() {
        btn_showresultback= (Button) findViewById(R.id.btn_showresultback);
        btn_showresultcontinuetest= (Button) findViewById(R.id.btn_showresultcontinuetest);
        btn_showresultprintresult= (Button) findViewById(R.id.btn_showresultprintresult);
        btn_showresultuploaddata= (Button) findViewById(R.id.btn_showresultuploaddata);
        btn_showresultback.setOnClickListener(this);
        btn_showresultcontinuetest.setOnClickListener(this);
        btn_showresultprintresult.setOnClickListener(this);
        btn_showresultuploaddata.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.e(TAG,"ShowresultAcitivityonclick");
        switch (v.getId()){
            case R.id.btn_showresultback:
                intent = new Intent(Showresultactivity.this,Mainactivity.class);
                startActivity(intent);
                Showresultactivity.this.finish();
            break;
            case R.id.btn_showresultcontinuetest:
                intent = new Intent(Showresultactivity.this,StartMainAcitivity.class);
                startActivity(intent);
                Showresultactivity.this.finish();
                break;
            case R.id.btn_showresultprintresult:
                Log.e(TAG,"打印成功");
                Toast.makeText(Showresultactivity.this,"打印成功",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_showresultuploaddata:
                Log.e(TAG,"上传数据");
                Toast.makeText(Showresultactivity.this,"上传数据",Toast.LENGTH_LONG).show();
                break;

        }

    }
     class DataSaveThread implements Runnable{
         @Override
         public void run() {
             try {
                 Toast.makeText(Showresultactivity.this,"上传中",Toast.LENGTH_LONG).show();
                 Thread.sleep(3000);
                 Toast.makeText(Showresultactivity.this,"上传完毕",Toast.LENGTH_LONG).show();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     }
}
