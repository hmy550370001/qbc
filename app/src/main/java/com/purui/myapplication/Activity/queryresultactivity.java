package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.purui.myapplication.R;

public class queryresultactivity extends Activity implements View.OnClickListener{
    private ImageView iv_queryresultback;
    private Intent intent;
    private Button btn_queryresultsubmit;
    private static   final String TAG = "HL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryresult);
        Log.e(TAG,"mainacitivity");
        init();
    }

    private void init() {
        iv_queryresultback= (ImageView) findViewById(R.id.iv_queryresultback);
        iv_queryresultback.setOnClickListener(this);
        btn_queryresultsubmit= (Button) findViewById(R.id.btn_queryresultsubmit);
        btn_queryresultsubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_queryresultback:
                intent= new Intent(queryresultactivity.this,Mainactivity.class);
                startActivity(intent);
                queryresultactivity.this.finish();
                break;
            case R.id.btn_queryresultsubmit:
                intent= new Intent(queryresultactivity.this,queryresultshow.class);
                startActivity(intent);
                queryresultactivity.this.finish();
        }
    }
}
