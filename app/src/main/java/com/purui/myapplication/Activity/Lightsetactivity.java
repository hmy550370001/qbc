package com.purui.myapplication.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class Lightsetactivity extends Activity implements View.OnClickListener {

    private Button btn;
    private Intent intent;
    private static final String TAG = "HL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightsetactivity);
        Log.e(TAG, "Lightsetactivity: oncreate");
        init();
    }

    private void init() {
        btn = (Button) findViewById(R.id.btn_Lightback);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "Lightsetactivity: onclick");
        switch (v.getId()) {
            case R.id.btn_Lightback:
                intent = new Intent(Lightsetactivity.this, parametersetting.class);
                startActivity(intent);
                Lightsetactivity.this.finish();
                break;
        }
    }
}
