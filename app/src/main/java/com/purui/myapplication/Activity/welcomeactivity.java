package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.purui.myapplication.R;

/**
 * 开机自检
 */
public class welcomeactivity extends Activity {
  private static   final String TAG = "HL";
    private ImageView imageView;
    private AnimationDrawable anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomeactivity);
        imageView= (ImageView) findViewById(R.id.iv_welcomeui);
        anim = (AnimationDrawable) imageView.getDrawable();
        anim.start();
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 Log.e(TAG,"welcomeactivity");
                 init();
             }
         }, 4000);
    }

    private void init() {

        Intent intent = new Intent(welcomeactivity.this,Mainactivity.class);
        startActivity(intent);
        welcomeactivity.this.finish();
    }
}
