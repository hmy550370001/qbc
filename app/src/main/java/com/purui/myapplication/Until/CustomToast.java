package com.purui.myapplication.Until;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.purui.myapplication.R;

/**
 * Created by wangruyi on 2017/7/17.
 */

public class CustomToast {


    private static TextView mTextView;
    private static ImageView mImageView;
   ;

    public  static  void showToast(Context context,String str,int id){
        //加载Toast布局
        View view = LayoutInflater.from(context).inflate(R.layout.toast,null);
                        //初始化布局控件
                mTextView = (TextView) view.findViewById(R.id.message);
                mImageView = (ImageView) view.findViewById(R.id.imageView);
                         //为控件设置属性
                mTextView.setText(str);
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mImageView.setImageResource(id);
                    //Toast的初始化
        Toast toast = new Toast(context);
         //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
         int hi = wm.getDefaultDisplay().getHeight();
          int wi = wm.getDefaultDisplay().getHeight();

        toast.setGravity(Gravity.CENTER  ,wi/3,hi/3);
        toast.setView(view);
        toast.setDuration(toast.LENGTH_SHORT);
        toast.show();
    }



       public  static  void showToast(Context context,String str){
     //加载Toast布局
     View view = LayoutInflater.from(context).inflate(R.layout.toast,null);
                     //初始化布局控件
             mTextView = (TextView) view.findViewById(R.id.message);
             mImageView = (ImageView) view.findViewById(R.id.imageView);
                      //为控件设置属性
                mTextView.setText(str);
        //     mImageView.setImageResource(id);
                 //Toast的初始化
     Toast toast = new Toast(context);
      //获取屏幕高度
     WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
      int hi = wm.getDefaultDisplay().getHeight();
     int wi = wm.getDefaultDisplay().getHeight();
     toast.setGravity(Gravity.CENTER  ,0, (int) (hi*0.3));
     toast.setView(view);
     toast.setDuration(toast.LENGTH_LONG);
     toast.show();
    }
}