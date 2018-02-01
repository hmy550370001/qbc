package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class Timesetactivity extends Activity implements View.OnClickListener {
    private Button btn_timesetback;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesetactivity);
        init();
    }

    private void init() {
        btn_timesetback = (Button) findViewById(R.id.btn_timesetback);
        btn_timesetback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_timesetback:
                intent = new Intent(Timesetactivity.this, parametersetting.class);
                startActivity(intent);
                Timesetactivity.this.finish();
                break;
        }
    }
}
