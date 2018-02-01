package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class parametersetting extends Activity  implements View.OnClickListener{
private Button btn_back,btn_timeset,btn_lightset,btn_adjustset,btn_restoreset;
    private Intent intent;
    private static   final String TAG = "HL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametersetting);
        init();
        Log.e(TAG,"parametersetting");
    }

    private void init() {
        btn_back= (Button) findViewById(R.id.btn_parameterback);
        btn_back.setOnClickListener(this);
        btn_timeset= (Button) findViewById(R.id.btn_timeset);
        btn_timeset.setOnClickListener(this);
        btn_lightset= (Button) findViewById(R.id.btn_lightset);
        btn_lightset.setOnClickListener(this);
        btn_adjustset= (Button) findViewById(R.id.btn_adjustset);
        btn_adjustset.setOnClickListener(this);
        btn_restoreset= (Button) findViewById(R.id.btn_restoreset);
        btn_restoreset.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_parameterback: //timeback
                Log.e(TAG,"返回1");
                intent =  new Intent(parametersetting.this,Mainactivity.class);
                Log.e(TAG,"返回1:"+intent);
                startActivity(intent);
                parametersetting.this.finish();
                break;
            case R.id.btn_adjustset://校准设置
                Log.e(TAG,"校准设置");
                intent =  new Intent(parametersetting.this,Adjustsetactivity.class);
                startActivity(intent);
                parametersetting.this.finish();
                break;
            case R.id.btn_timeset: //时间设置
                Log.e(TAG,"时间设置");
                intent = new Intent(parametersetting.this,Timesetactivity.class);
                startActivity(intent);
                parametersetting.this.finish();
                break;
            case R.id.btn_restoreset: // 恢复出厂设置
                Log.e(TAG,"恢复出厂设置");
            intent = new Intent(parametersetting.this,Restoresetactivity.class);
                startActivity(intent);
                parametersetting.this.finish();
                break;
            case R.id.btn_lightset://光强设置
                Log.e(TAG,"光强设置");
                intent = new Intent(parametersetting.this,Lightsetactivity.class);
                startActivity(intent);
                parametersetting.this.finish();
                break;
        }
    }
}
