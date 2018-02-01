package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.purui.myapplication.R;

public class Restoresetactivity extends Activity implements View.OnClickListener {
    private Button btn_factorysetback;
    private Intent intent;
    private Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_0, btn_submit, btn_return;
    private TextView tv_word;
    private String word;
    private static final    String TAG= "HL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restoresetactivity);
        init();
    }

    private void init() {
        tv_word = (TextView) findViewById(R.id.tv_word);
        btn_factorysetback = (Button) findViewById(R.id.btn_factorysetback);
        btn_factorysetback.setOnClickListener(this);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_factorysetsubmit);
        btn_submit.setOnClickListener(this);
        btn_return = (Button) findViewById(R.id.btn_factorysetreturn);
        btn_return.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_factorysetback:
                intent = new Intent(Restoresetactivity.this, parametersetting.class);
                startActivity(intent);
                break;
            case R.id.btn_0:
                word = (String) tv_word.getText();
                tv_word.setText(word + "0");
                break;
            case R.id.btn_1:
                word = (String) tv_word.getText();
                tv_word.setText(word + "1");
                break;
            case R.id.btn_2:
                word = (String) tv_word.getText();
                tv_word.setText(word + "2");
                break;
            case R.id.btn_3:
                word = (String) tv_word.getText();
                tv_word.setText(word + "3");
                break;
            case R.id.btn_4:
                word = (String) tv_word.getText();
                tv_word.setText(word + "4");
                break;
            case R.id.btn_5:
                word = (String) tv_word.getText();
                tv_word.setText(word + "5");
                break;
            case R.id.btn_6:
                word = (String) tv_word.getText();
                tv_word.setText(word + "6");
                break;
            case R.id.btn_7:
                word = (String) tv_word.getText();
                tv_word.setText(word + "7");
                break;
            case R.id.btn_8:
                word = (String) tv_word.getText();
                tv_word.setText(word + "8");
                break;
            case R.id.btn_9:
                word = (String) tv_word.getText();
                tv_word.setText(word + "9");
                break;
            case R.id.btn_factorysetreturn:
                word = (String) tv_word.getText();
                Log.e(TAG,"word:"+word);
                if (word.length()==0)
                {
                    Toast.makeText(Restoresetactivity.this,"请输入正确的密码,并在之后按下 确定",Toast.LENGTH_LONG).show();


                    return;}
                word=word.substring(0,word.length()-1);
                Log.e(TAG,"word.lenth:"+word.length());
                tv_word.setText(word);
                break;
        }
    }
}
