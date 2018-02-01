package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.purui.myapplication.R;

public class Adjustsetactivity extends Activity  implements View.OnClickListener{
    private Button btn_adjustsetback;

    /**
     * 校准界面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustsetactivity);
        init();
    }

    private void init() {
        btn_adjustsetback= (Button) findViewById(R.id.btn_adjustsetback);
        btn_adjustsetback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_adjustsetback:
                Intent intent = new Intent(Adjustsetactivity.this,parametersetting.class);
                startActivity(intent);
                break;
        }
    }
}
