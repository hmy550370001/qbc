package com.purui.myapplication.Until.QBCsuanfa;

import com.purui.myapplication.Activity.Mainactivity;
import com.purui.myapplication.Until.QBCsuanfa.CommPort;

/**
 * Created by wangruyi on 2017/7/25.
 */

public  class defined
{
    /////////////////////////////////////////////////////////////////////////////
    public static CommPort sp_comm = null;
    public static String msg0 = "";     //FrmMsgboxs 窗口label1中的文本
    public static String msg1 = "";     //FrmMsgboxs 窗口label2中的文本
    //当msgreturn 为0时显示 是否按钮 当msgreturn 为1时显示 确认取消按钮  当msgreturn 为2时显示 确认按钮
    public static int msgreturn = 0;    //当msgreturn 为6时为确认操作 当msgreturn 为7时为取消操作
    public static boolean password = true;    //当passw为true时 界面为输入密码界面 为fals时界面为ID查询界面
    public static String error = "";    //错误ID
    public static String deviceName = "QBC";//设备代码
    //FrmReport窗口使用
    // 0.样本ID 1.测试时间 2.HCT 3.HGB 4.WBC 5.PTL 6.GRANS 7.LM 8.%GRANS 9.%LM 10,MCHC
    public static String[] data = new String[11];
    public static config configfile = new config(1, Mainactivity.class);

    /////注册表使用////////////////////////////////////////////////////////////////////////
    public static String AutoTestDate = "20140101";     //日期
    //public static String PrintCom = "com2:";    //com2口 连接设备口
    //public static String SPCom = "com1:";  //

    public static String SampleID;//样本ID
    public static int SampleIDAutoNum = 1;  //样本后4位
    public static String TestDate = "";//测试日期
    /////算法使用////////////////////////////////////////////////////////////////////////
    public  int CCDPixelSum = 10550;
    public static int[] buffer = new int[10550 * 3];
    public static int[] red_dat = new int[10550];
    public static int[] green_dat = new int[10550];

    public static double[] F_data = new double[10550];
    public static double[] flo_B_Reddata = new double[10550];
    public static double[] flo_B_Greendata = new double[10550];
    public static double x6_9_diff = 0;

    public static class config
    {
        public int[] point;     //初始坐标
        public float HCTA;      //红细胞压积A
        public float HGBA;      //血红蛋白A
        public float GRANSA;    //单颗粒细胞A
        public float LMA;       //淋巴细胞A
        public float PLTA;      //血小板A

        public float HCTB;      //红细胞压积B
        public float HGBB;      //血红蛋白B
        public float GRANSB;    //单颗粒细胞B
        public float LMB;       //淋巴细胞B
        public float PLTB;      //血小板B

        public int RedLight;    //红光强度
        public int BlueLight;   //蓝光强度
        public String equipment_number; //设备编号
        public String Manufacture_date; //出厂日期
        public int Time_ccd_test; //ccd 积分时间
        public int Time_R_measure;// 检测积分时间
        public int Time_B_measure;// 检测积分时间

        public config(int a, Class<Mainactivity> context)
        {
            point = new int[2];
            point[0] = 6000; point[1] = 8000;
            HCTA = 1.0053f;
            HGBA = 1.1058f;
            GRANSA = 1.2592f;
            LMA = 1.3655f;
            PLTA = 0.7792f;

            HCTB = 2.5232f;
            HGBB = 0.072f;
            GRANSB = 0.2032f;
            LMB = 0.4703f;
            PLTB = 21.8161f;
            //光强
            RedLight = 15;
            BlueLight = 13;
            //积分时间
            Time_ccd_test = 200;
            Time_R_measure = 200;
            Time_B_measure = 400;
            equipment_number = "0001";
            Manufacture_date = "20140919";

        }
    }



    ////判断是否有音效
    //public static void SoundMode()
    //{
    //    if (defined.SoundMode_KeyDown == 1)
    //    {
    //        //DllImport.DevBeep((uint)10000, (uint)5);
    //    }
    //}
}
