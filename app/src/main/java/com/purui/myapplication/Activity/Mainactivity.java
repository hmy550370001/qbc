package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.purui.myapplication.R;

public class Mainactivity extends Activity implements View.OnClickListener {
    Button btn_starttest, btn_queryresult, btn_parametersetting, btn_close;
    //开始测试 , 查询结果, 参数设置 ,光强设置
    private static final String TAG = "HL";
    private Handler mHandler;
    private Intent intent;
    /**
     * 主界面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Log.e(TAG, "mainacitivity");
    }

    private void init() {
        mHandler = new Handler();
        btn_starttest = (Button) findViewById(R.id.btn_mainstarttest);
        btn_parametersetting = (Button) findViewById(R.id.btn_mainparametersetting);
        btn_queryresult = (Button) findViewById(R.id.btn_mainqueryresult);
        btn_close = (Button) findViewById(R.id.btn_main_close);
        btn_starttest.setOnClickListener(this);
        btn_parametersetting.setOnClickListener(this);
        btn_queryresult.setOnClickListener(this);
        btn_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mainstarttest:
//                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
//                vibrator.vibrate(new long[]{0,1000}, -1);
                intent = new Intent(Mainactivity.this, StartMainAcitivity.class);
                startActivity(intent);
                break;
            case R.id.btn_mainparametersetting:
                Toast.makeText(this, "参数设置", Toast.LENGTH_SHORT).show();
                intent = new Intent(Mainactivity.this, parametersetting.class);
                startActivity(intent);
                break;
            case R.id.btn_mainqueryresult:
                Toast.makeText(this, "查询结果", Toast.LENGTH_SHORT).show();
                intent = new Intent(Mainactivity.this, queryresultactivity.class);
                startActivity(intent);
                break;
            case R.id.btn_main_close:
                Toast.makeText(this, "关机", Toast.LENGTH_SHORT).show();
                intent = new Intent(Mainactivity.this, Closeactivity.class);
                startActivity(intent);
                break;
            //   case R.id.btn_mainlightsetting:
            //         Toast.makeText(this,"光强设置",Toast.LENGTH_SHORT).show();
            //          break;
        }

    }
}
