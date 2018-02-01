package com.purui.myapplication.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.purui.myapplication.R;

public class Closeactivity extends Activity implements View.OnClickListener {
    private ImageView imageView;
    private AnimationDrawable anim;
    private Button btn_closeback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closeactivity);
        init();
    }

    private void init() {
        btn_closeback= (Button) findViewById(R.id.btn_closeback);
        btn_closeback.setOnClickListener(this);
        imageView= (ImageView) findViewById(R.id.close);
        anim = (AnimationDrawable) imageView.getDrawable();
        anim.start();
    }

    @Override
    public void onClick(View v) {
        Log.e("tag","closeclick");
            switch (v.getId()){
                case R.id.btn_closeback:
                    Intent intent = new Intent(Closeactivity.this,Mainactivity.class);
                    startActivity(intent);
                    Closeactivity.this.finish();
                    break;

            }
    }
}
