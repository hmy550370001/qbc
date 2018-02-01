package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class queryresultshow extends Activity implements View.OnClickListener {
    private Button btn_queryresultgoon, btn_queryresultshowback;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryresultshow);
        init();
    }

    private void init() {
        btn_queryresultgoon = (Button) findViewById(R.id.btn_queryresultgoon);
        btn_queryresultshowback = (Button) findViewById(R.id.btn_queryresultshowback);
        btn_queryresultgoon.setOnClickListener(this);
        btn_queryresultshowback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_queryresultshowback:
                intent = new Intent(queryresultshow.this, Mainactivity.class);
                startActivity(intent);
                break;
            case R.id.btn_queryresultgoon:
                intent = new Intent(queryresultshow.this, queryresultactivity.class);
                startActivity(intent);
                break;
        }
    }
}
