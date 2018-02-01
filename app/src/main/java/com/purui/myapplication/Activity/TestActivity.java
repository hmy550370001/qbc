package com.purui.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.purui.myapplication.R;

/**
 * Created by wangruyi on 2017/6/27.
 */
public class TestActivity extends Activity {
    private TextView tv_testtest;
    private boolean inputtesttube;
    private int ing = 0;
    private  static  final  String TAG= "HL";
    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String a = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    tv_testtest.setText(a + "");
                    Log.e("dd", a + "");
                    break;
                case 1:
                    tv_testtest.setText(a + "");
                    Log.e("dd", a + "");
                    break;
                case 2:
                    tv_testtest.setText(a);
                    Log.e("dd", a + "");
                    break;
                case 3:
                    tv_testtest.setText(a);
                    Log.e("dd", a + "");
                    break;
                case 4:
                    tv_testtest.setText(a);
                    Log.e("dd", a + "");
                    break;
                case 5:
                    Intent intent = new Intent(TestActivity.this, Showresultactivity.class);
                    startActivity(intent);
                    TestActivity.this.finish();
                    break;
                case 6:

                    break;
                case 7:

                    break;
                default:



                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.e(TAG,"TestActivity");
        init();
        testing();
    }

    private void testing() {
//        if (inputtesttube==false){
//           //    return;
//        }
//        new Thread(new jinyangThread()).start();
//        new Thread(new jinyangThread1()).start();
//        new Thread(new jinyangThread2()).start();
//        new Thread(new jinyangThread3()).start();
//        hander.postDelayed(testThread, 2000);
//        hander.postDelayed(testThread, 2000);
        hander.postDelayed(testThread1, 2000);
        hander.postDelayed(testThread2, 2000);
        hander.postDelayed(testThread3, 2000);
        hander.postDelayed(testThread4, 2000);
    }

    private void init() {
        tv_testtest = (TextView) findViewById(R.id.tv_test);

    }


    Runnable testThread = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.obj = "1";
                msg.what = 1;
                hander.sendMessageDelayed(msg, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable testThread1 = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                Message msg1 = new Message();
                msg1.obj = "1";
                msg1.what = 2;
                hander.sendMessageDelayed(msg1, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable testThread2 = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                Message msg1 = new Message();
                msg1.obj = "2";
                msg1.what = 3;
                hander.sendMessageDelayed(msg1, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable testThread3 = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                Message msg1 = new Message();
                msg1.obj = "3";
                msg1.what = 4;
                hander.sendMessageDelayed(msg1, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable testThread4 = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                Message msg1 = new Message();
                msg1.obj = "开始";
                msg1.what = 5;
                hander.sendMessageDelayed(msg1, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable testThread5 = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                Message msg1 = new Message();
                msg1.obj = "开始";
                msg1.what = 1;
                hander.sendMessageDelayed(msg1, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    hander.removeCallbacks(testThread);
//        hander.removeCallbacks(testThread1);
//        hander.removeCallbacks(testThread2);
//        hander.removeCallbacks(testThread3);
//        hander.removeCallbacks(testThread4);

    }

    public class jinyangThread implements Runnable {

        @Override
        public void run() {
            try {
                //        while (true) {
                Thread.sleep(3000);


//

//                    for (int i = 0; i < Global.ecg.getEcg_data().ECG_II.size(); i++) {
//                        ecg_Curve.setCurve(Global.ecg.getEcg_data().ECG_II.get(i));
//                    }

                // 发送这个消息到消息队列中
                Message msg1 = new Message();
                msg1.obj = "结束";
                msg1.what = 0;
                hander.sendMessage(msg1);
                //        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class jinyangThread1 implements Runnable {

        @Override
        public void run() {
            try {
                //        while (true) {
                Thread.sleep(3000);


//

//                    for (int i = 0; i < Global.ecg.getEcg_data().ECG_II.size(); i++) {
//                        ecg_Curve.setCurve(Global.ecg.getEcg_data().ECG_II.get(i));
//                    }

                // 发送这个消息到消息队列中
                Message msg1 = new Message();
                msg1.obj = "1";
                msg1.what = 1;
                hander.sendMessage(msg1);
                //        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class jinyangThread2 implements Runnable {

        @Override
        public void run() {
            try {
                //        while (true) {
                Thread.sleep(3000);


//

//                    for (int i = 0; i < Global.ecg.getEcg_data().ECG_II.size(); i++) {
//                        ecg_Curve.setCurve(Global.ecg.getEcg_data().ECG_II.get(i));
//                    }

                // 发送这个消息到消息队列中
                Message msg1 = new Message();
                msg1.obj = "2";
                msg1.what = 2;
                hander.sendMessage(msg1);
                //        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class jinyangThread3 implements Runnable {

        @Override
        public void run() {
            try {
                //        while (true) {
                Thread.sleep(3000);


//

//                    for (int i = 0; i < Global.ecg.getEcg_data().ECG_II.size(); i++) {
//                        ecg_Curve.setCurve(Global.ecg.getEcg_data().ECG_II.get(i));
//                    }

                // 发送这个消息到消息队列中
                Message msg1 = new Message();
                msg1.obj = "3";
                msg1.what = 3;
                hander.sendMessage(msg1);
                //        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }






    }
}
