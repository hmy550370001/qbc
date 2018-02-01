package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;

import com.purui.myapplication.R;

public class logoactivity extends Activity {

    private static   final String TAG = "heliang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logoactivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"logoactivity");
               init();
            }
        }, 4000);
    }

    private void init() {
        Intent intent = new Intent(logoactivity.this,welcomeactivity.class);
        startActivity(intent);
        logoactivity.this.finish();
    }
}
