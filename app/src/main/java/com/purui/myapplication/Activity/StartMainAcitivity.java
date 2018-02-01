package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class StartMainAcitivity extends Activity  implements View.OnClickListener{
   private Button btn_startstart,btn_startcancel;
   private Intent intent;
    private  static  final  String TAG= "HL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);
        Log.e(TAG,"StartMainAcitivity");
        init();
    }

    private void init() {
        btn_startstart= (Button) findViewById(R.id.btn_startstart);
        btn_startcancel= (Button) findViewById(R.id.btn_startcancel);
        btn_startstart.setOnClickListener(this);
        btn_startcancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG,"onclick");
        switch (v.getId()){
            case R.id.btn_startstart:
                Log.e(TAG,"start");
                intent = new Intent(StartMainAcitivity.this,TestActivity.class);
                startActivity(intent);
                StartMainAcitivity.this.finish();
                break;
            case R.id.btn_startcancel:
                 intent = new Intent(StartMainAcitivity.this, Mainactivity.class);
                startActivity(intent);
                break;
            default:
                break;



        }
    }
}
