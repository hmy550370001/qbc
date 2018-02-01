package com.purui.myapplication.Until.QBCsuanfa;

import android.util.Log;

import com.purui.myapplication.Until.QBCsuanfa.defined;

/**
 * Created by wangruyi on 2017/7/25.
 */


public class QbcRun {
    //所有const 转换为
    int blackdistant= 150;
    float bili=2592/10550;
    int TESTRADIO=4;
    public    float mid_black_line = 4.25f;
    public    float den_f = 1.055f;
    public    float den_g = 1.0747f;
    public    float den_lm = 1.0632f;
    public    float den_plt = 1.049f;
    public    float den_p = 1.028f;
    public    float height_first_mid_black_line = 36.4f;
    public    int CCDPixelSum = 2592;
    //*******************************定义配置参数与层厚阈值
    public    int config1_threshold = 5200;
    public    int config2_threshold = 9200;
    public    int x6_9_threshold = 850;
    public    int x6_7_threshold1 = 30;
    public    int x6_7_threshold2 = 600;
    public    int x7_8_threshold = 20;
    public    int x8_9_threshold1 = 50;
    public    int x8_9_threshold2 = 500;
    public    int RED_X6_9_threshold = 30;
    public    int Mean_Three_Green_threshold = 50;
    public    int hole_wid_threshold1 = 20;
    public    int hole_wid_threshold2 = 150;
    public    int hole_depth_threshold = 20;
   // public  int
   // public struct Position		//定义位置结构体，存放目前特征检测后各个位置
   public  class Position
    {
         int x1;		//红光所测浮子底端
         int x2;		//红光所测浮子顶端
         int x3;		//红光所测黑线位置1
         int x4;		//红光所测黑线位置2
         int x5;		//红光所测页面位置
         int x6;		//蓝光所测粒细胞底端
         int x7;		//蓝光所测粒细胞顶端
         int x8;		//蓝光所测血小板底端
         int x9;		//蓝光所测血小板顶端
    }



    public class Result 	//定义结果结构体，存放检测后各个项目数值
    {
        public double hct;
        public double hgb;
        public double wbc;
        public double plt;
        public double grans;
        public double lm;
        public double percent_grans;
        public double percent_lm;
        public double mchc;
    }
    Result  result = new Result();
    public volatile int[] cab = new int[2];
    public int config_b1 = 0, config_b2 = 0, Intime = 0;	//上次校准的黑带位置及其整数化范围
    double height_mid_black_line = 0;

    //region 处理数据使用

    //滤波使用
    public double[] A_R = new double[] { 1, -1.7786, 0.8008 };
    public double[] B_R = new double[] { 0.0055, 0.0111, 0.0055 };
    public double[] A_G = new double[] { 1, -1.5610, 0.6414 };
    public double[] B_G = new double[] { 0.0201, 0.0402, 0.0201 };
    public double[] parameter = new double[9];

    //endregion

    //region 处理结果使用

    public int[][] three_layer = new int[40][4];
    public int sum_count = 0, tp;
    public int x6_final = 0, x7_final = 0, x8_final = 0, x9_final = 0;
    public Position pos_res;                   //九个点
    int[] point_result = new int[9];    //最后的结果
    Result result_get_final;

    //endregion

    //取图像数组中的 红像素点
    public void RedPixel(    int[] ImageBuf,    int[] Red)
    {
        // i横 j是纵
        int i, j = 2591;
        for (i = 0; i < ImageBuf.length; i = i + 3, j--)
        {
            Red[j] =  ImageBuf[i] >> 4;
        }
    }

    //取图像数组中的 绿像素点
    public void GreenPixel( int[] ImageBuf,        int[] Green)
    {
        int i, j = 2591;
        for (i = 1; i < ImageBuf.length; i = i + 3, j--)
        {
            Green[j] = (ImageBuf[i] >> 4);
        }
    }

   //region 测试红光

    //红光照射下数据处理
    public int RedRead(  int[] Reddata,  Position pos)
    {
        double[] f_Redred = new double[CCDPixelSum];    //数组长度2592 元长度CCDPixelSum = 10550
        double mn_1 = 0, mn_2 = 0, df = 0, miny = 1000, maxy = 0;
        int mx1=0,mx2=0,mn1=0,mn2=0;
        int i=0;
        double[] A = new double[]{1,-1.7786,0.8008};
        double[] B = new double[]{0.0055,0.0111,0.0055};
        double miny1=1000,miny2=1000,sumx=0,sumy=0;
        //计算x2中用到的变量
        double sum_mx1_mx2=0,sum_y=0,min_x2 =0,left_max_x2=0,right_max_x2=0,left_middle_standar=0,right_middle_standar=0;
        int pos_min_x2=0,pos_left_x2=0,pos_right_x2=0;
        //计算x5中用到的变量
        //double min_x5,left_max_x5,right_max_x5;
        //int pos_min_x5,pos_left_x5,pos_right_x5;
        double min_x5=0;
        int pos_min_x5=0;

        //重置九个点为0
        pos.x1 = 0; pos.x2 = 0; pos.x3 = 0; pos.x4 = 0; pos.x5 = 0; pos.x6 = 0; pos.x7 = 0; pos.x8 = 0; pos.x9 = 0;
        //将数组Reddata ‘滤波’后存入f_Redred数组中
        filter(B, A, Reddata,    f_Redred, 12);	//滤波红光

        for (i = 0; i <= 99; i++)
        {
            f_Redred[i] = f_Redred[i + 100];	//防止前100个出现突变，强行赋值
        }

        //开始计算X3，X4
        mn1 = config_b1; mn2 = config_b2;
        //MessageBox.Show(config_b1.ToString() + " " + config_b2.ToString());
        miny1 = 1000/TESTRADIO; miny2 = 1000/TESTRADIO; maxy = 0;
        mx1 = 0; mx2 = 0;
        for (i = mn1; i < mn2; i = i + 2)
        {
            if (miny1 > f_Redred[i])
            {
                miny1 = f_Redred[i];
                mx1 = i;
            }
            if (maxy < f_Redred[i])
                maxy = f_Redred[i];
        }

        for (i = mn1; i < mn2; i = i + 2)
        {
            if (Math.abs(i - mx1) < blackdistant)    //绝对值<300
                continue;
            if (miny2 > f_Redred[i])
            {
                miny2 = f_Redred[i];
                mx2 = i;
            }
        }
        //MessageBox.Show(mx1.ToString());

        mn1 = mx1 - (blackdistant/2); mn2 = mx1 + (blackdistant/2);
        //**************/
        if (mn1 < 0)
        {
            return 6;
        }
        //*************/
        for (i = mn1; i < mn2; i++)
        {
            if (f_Redred[i] > (2.0 / 3.0 * maxy + 1.0 / 3.0 * miny1))
                continue;
            sumx += (maxy - f_Redred[i]);
            sumy += i * (maxy - f_Redred[i]);
        }
        mx1 = (int)(sumy / sumx + 0.5);
        sumx = 0; sumy = 0;
        mn1 = mx2 - blackdistant/2; mn2 = mx2 + blackdistant/2;
        for (i = mn1; i < mn2; i++)
        {
            if (f_Redred[i] > (2.0 / 3.0 * maxy + 1.0 / 3.0 * miny2))
                continue;
            sumx += (maxy - f_Redred[i]);
            sumy += i * (maxy - f_Redred[i]);
        }
        //MessageBox.Show(mx1.ToString()+" "+mx2.ToString());
        mx2 = (int)(sumy / sumx + 0.5);
        //// TODO: 2017/7/26  740 没换 看情况定
        if (Math.abs(Math.abs(mx1 - mx2) - 740/TESTRADIO) > blackdistant/2)
        {
            pos.x1 = 0; pos.x2 = 0; pos.x3 = 0; pos.x4 = 0; pos.x5 = 0;
            return 7 ;
        }

        pos.x3 = (mx1 < mx2) ? (mx1 - 1) : (mx2 - 1);
        pos.x4 = (mx1 < mx2) ? (mx2 - 1) : (mx1 - 1);	//X3,X4计算完毕

        //开始计算X2
        mn1 = config_b1 - 2000/TESTRADIO;
        //  mn1 = config_b1 - 2000;
        mn2 = config_b1;
        mx1 = 0; mx2 = 0;

        mint_d_l(f_Redred, mn1, mn2,   min_x2,   pos_min_x2);
        maxt_d_l(f_Redred, mn1, pos_min_x2,   left_max_x2,   pos_left_x2);
        maxt_d_l(f_Redred, pos_min_x2, mn2,   right_max_x2,   pos_right_x2);
        left_middle_standar = 0.5 * (min_x2 + left_max_x2);
        right_middle_standar = 0.5 * (min_x2 + right_max_x2);
        for (i = pos_min_x2; i >= mn1; i--)
        {
            if (f_Redred[i] >= left_middle_standar)
            {
                mx1 = i;
                break;
            }
        }
        for (i = pos_min_x2; i <= mn2; i++)
        {
            if (f_Redred[i] >= right_middle_standar)
            {
                mx2 = i;
                break;
            }
        }
        for (i = mx1; i <= mx2; i++)
        {
            sum_mx1_mx2 = sum_mx1_mx2 + i * f_Redred[i];
            sum_y = sum_y + f_Redred[i];
        }

        pos.x2 = (int)(sum_mx1_mx2 / sum_y + 0.5) - 1;		//X2寻找完毕

        //开始计算X1
        miny = 250/TESTRADIO; maxy = 0; mx1 = 0; mx2 = 0;
        //for (i = 500; i <= pos.x2 - 2800; i++)
        for (i = 500/TESTRADIO; i <= pos.x2 - 2800/TESTRADIO; i++)
        {
            int ii = 0;
            mn_1 = 0; mn_2 = 0;
            for (ii = 50/TESTRADIO*2; ii <= 100/TESTRADIO*2; ii = ii + 1)
            {
                mn_1 += f_Redred[i - ii];
                mn_2 += f_Redred[i + ii];
            }
            if (Math.abs(mn_2 - mn_1) > df)
            {
                df = Math.abs(mn_2 - mn_1);
                mx1 = i;
            }
            if (miny > f_Redred[i])
                miny = f_Redred[i];
            if (maxy < f_Redred[i])
                maxy = f_Redred[i];
        }

        pos.x1 = mx1 - 1;		//X1位置求完

        //计算x5位置
        mn1 = config_b1 + 2000/TESTRADIO;
        mn2 = config_b1 + 3500/TESTRADIO;
        mint_d_l(f_Redred, mn1, mn2,   min_x5,   pos_min_x5);
        pos.x5 = pos_min_x5;



        //红光采集正确，则继续进行，采集蓝光，现省略，直接调 数据
     //   if (pos_res.x2 > config_b1 - 2000 && pos_res.x2 < config_b1 && Math.abs(pos_res.x2 - pos_res.x1 - 3500) < 500 && pos_res.x3 >= config_b1 && pos_res.x3 <= config_b2 && pos_res.x4 >= config_b1 && pos_res.x4 <= config_b2 && pos_res.x4 - pos_res.x3 - 740 < 150 && pos_res.x4 - pos_res.x3 > 0 && pos_res.x5 >= (config_b1 + 2000) && pos_res.x5 <= (config_b1 + 3500))
        if (pos_res.x2 > config_b1 - 2000/TESTRADIO && pos_res.x2 < config_b1 && Math.abs(pos_res.x2 - pos_res.x1 - 3500) < 500 && pos_res.x3 >= config_b1 && pos_res.x3 <= config_b2 && pos_res.x4 >= config_b1 && pos_res.x4 <= config_b2 && pos_res.x4 - pos_res.x3 - 740 < 150 && pos_res.x4 - pos_res.x3 > 0 && pos_res.x5 >= (config_b1 + 2000) && pos_res.x5 <= (config_b1 + 3500))

                return 0;    //("红光正确！\r\n");
        else
        {
            return 8;   //("红光错误！\r\n");
        }
    }

    //滤波
    public void filter(double[] B, double[] A,     int[] data,    double[] f_data, int shift)
    {
        double[] temp_Redred = new double[CCDPixelSum];
        int k = 0, j = 0, i = 0;
        for (k = 0; k < CCDPixelSum; k++)
        {
            temp_Redred[k] = 0;
            f_data[k] = 0;
        }
        for (i = 0; i < CCDPixelSum; i++)
        {
            for (j = 0; i >= j && j < 3; j++)
            {
                temp_Redred[i] += (B[j] * data[i - j] - A[j] * temp_Redred[i - j]);
            }
        }
        for (i = 0; i < CCDPixelSum - shift; i++)
        {
            f_data[i] = temp_Redred[i + shift];
        }
        for (i = CCDPixelSum - shift; i < CCDPixelSum; i++)
        {
            f_data[i] = temp_Redred[CCDPixelSum - 1];
        }
    }

    //endregion

    //数组中 a~b 中的最大值
    private void maxt_d_l(double[] array, int a, int b,   double d,   int c)		//重载函数2
    {
        double p = array[a];
        int q = a, i;
        for (i = a; i <= b; i++)
        {
            if (p < array[i])
            {
                p = array[i];
                q = i;
            }
        }
        d = p;
        c = q;
    }
    //数组中 a~b 中的最小值
    private void mint_d_l(double[] array, int a, int b,   double d,   int c)	   //函数重载3
    {
        double p = array[a];
        int q = a, i;
        for (i = a + 1; i <= b; i++)
        {
            if (p > array[i])
            {
                p = array[i];
                q = i;
            }
        }
        d = p;
        c = q;
    }
    //数组中 a~b 中的最大值
    private void maxt_i_l(int[] array, int a, int b,   int d,   int c)
    {
        int p = array[a];
        int q = a, i;
        for (i = a; i <= b; i++)
        {
            if (p < array[i])
            {
                p = array[i];
                q = i;
            }
        }
        d = p;
        c = q;
    }


    //蓝光照射下数据处理
    public void BlueRead(double[] flo_B_Reddata, double[] flo_B_Greendata, int x1, int x2,   double x6_9_diff,    Position b_read)
    {
        double c_mean_three_green=0, mini_peak_flag=0, upper_line=0, x6_mid_mean, x9_mid_mean, cmp_g, r_cmp;
        int hole_left_boundary=0, hole_right_boundary=0, hole_mid_s = 0, p1=0, p2=0, hf=0, g_l_standar, g_r_standar, x7_t=0, x7_s=0, x7_u=0, x8_t=0, x8_s=0, x8_u, glo_pos_max=0, mid_peak_width=0, length;

        length = x2 - x1 + 1;
        //处理红光（蓝光）
        blueread_red(flo_B_Reddata, flo_B_Greendata,   hole_left_boundary,   hole_right_boundary,   p1,   p2,    hf,    hole_mid_s,    b_read, length);

        if (hole_left_boundary > 20/TESTRADIO)
            g_l_standar = hole_left_boundary - 20/TESTRADIO;
        else
            g_l_standar = hole_left_boundary;

        if (hole_right_boundary < b_read.x9 - b_read.x6 - 20/TESTRADIO)
            g_r_standar = hole_right_boundary + 20/TESTRADIO;
        else
            g_r_standar = hole_right_boundary;
        c_mean_three_green = mean_d_l(flo_B_Greendata, b_read.x6, b_read.x9);

        x6_9_diff = Math.abs(flo_B_Reddata[b_read.x6] - flo_B_Reddata[b_read.x9]);

        if (x6_9_diff > RED_X6_9_threshold || (c_mean_three_green < Mean_Three_Green_threshold && hf == 0) || b_read.x9 - b_read.x6 > x6_9_threshold)
        {
            b_read.x6 = b_read.x6 + (x1 - 1);
            b_read.x9 = b_read.x9 + (x1 - 1);
            b_read.x7 = b_read.x6 + 1;
            b_read.x8 = b_read.x6 + 1;
            return;
        }
        else
        {
            blue_green_processing(flo_B_Greendata, b_read.x6, b_read.x9, hf, hole_left_boundary, hole_right_boundary,   x7_t,   x8_t,   x7_s,   x8_s,   mini_peak_flag,   mid_peak_width,   glo_pos_max,   upper_line);
            if ((x7_t == 1 || x8_t == 1 || x7_s == 1 || x8_s == 1 || mini_peak_flag == 0) && hf == 0)
            {
                b_read.x6 = b_read.x6 + (x1 - 1);
                b_read.x9 = b_read.x9 + (x1 - 1);
                b_read.x7 = b_read.x6 + 1;
                b_read.x8 = b_read.x6 + 1;
                return;
            }

            x7_u = x7_s + b_read.x6;
            x8_u = x8_s + b_read.x6;
            x6_mid_mean = mean_d_l(flo_B_Greendata, (int)(1.0 / 2.0 * (b_read.x6 + x7_u) + 0.5), x7_u);
            x9_mid_mean = mean_d_l(flo_B_Greendata, x8_u, (int)(1.0 / 2.0 * (x8_u + b_read.x9) + 0.5));

            if (x6_mid_mean >= x9_mid_mean)
            {
                cmp_g = upper_line - x6_mid_mean;
                r_cmp = (upper_line - mint_d_s(flo_B_Greendata, b_read.x6, glo_pos_max)) / cmp_g;
            }
            else
            {
                cmp_g = upper_line - x9_mid_mean;
                r_cmp = (upper_line - mint_d_s(flo_B_Greendata, glo_pos_max, b_read.x9)) / cmp_g;
            }

            if (r_cmp > 1 && r_cmp < 5 && cmp_g >= 30 && mid_peak_width >= 10)
            {
                if (hf == 1)
                {
                    if (x7_t >= g_l_standar && x7_t <= hole_mid_s && x8_t <= g_r_standar && x8_t >= hole_mid_s)
                    {
                        b_read.x7 = x7_t;
                        b_read.x8 = x8_t;
                    }
                    else
                    {
                        b_read.x7 = p1;
                        b_read.x8 = p2;
                    }
                }
                else
                {
                    if (x7_s >= (int)(1.0 / 4.0 * glo_pos_max + 0.5) && x7_s <= glo_pos_max && x8_s >= glo_pos_max && x8_s <= (int)(1.0 / 2.0 * (glo_pos_max + b_read.x9 - b_read.x6 + 1) + 0.5))
                    {
                        b_read.x7 = x7_s;
                        b_read.x8 = x8_s;
                    }
                    else
                    {
                        b_read.x7 = 1;
                        b_read.x8 = 1;
                    }
                }
            }
            else
            {
                if (hf == 0)
                {
                    if (mini_peak_flag >= 15 && x7_s >= (int)(1.0 / 4.0 * glo_pos_max + 0.5) && x7_s <= glo_pos_max && x8_s >= glo_pos_max && x8_s <= (int)(1.0 / 2.0 * (glo_pos_max + b_read.x9 - b_read.x6 + 1) + 0.5))
                    {
                        b_read.x7 = x7_s;
                        b_read.x8 = x8_s;
                    }
                    else
                    {
                        b_read.x7 = 1;
                        b_read.x8 = 1;
                    }
                }
                else
                {
                    b_read.x7 = p1;
                    b_read.x8 = p2;
                }
            }
        }
        b_read.x6 = b_read.x6 + x1 - 1;
        b_read.x7 = b_read.x7 + b_read.x6 - 1;
        b_read.x8 = b_read.x8 + b_read.x6 - 1;
        b_read.x9 = b_read.x9 + x1 - 1;

        if (((b_read.x9 - b_read.x6) <= x6_9_threshold) && ((b_read.x7 - b_read.x6) < x6_7_threshold2) && ((b_read.x7 - b_read.x6) > x6_7_threshold1) && ((b_read.x8 - b_read.x7) > x7_8_threshold) && ((b_read.x9 - b_read.x8) < x8_9_threshold2) && ((b_read.x9 - b_read.x8) > x8_9_threshold1) && (x6_9_diff <= RED_X6_9_threshold))
        {
            three_layer[sum_count][ 0] = b_read.x6;
            three_layer[sum_count][ 1] = b_read.x7;
            three_layer[sum_count][ 2] = b_read.x8;
            three_layer[sum_count][ 3] = b_read.x9;
            sum_count++;
        }
    }
    //处理红光（蓝光）
    private void blueread_red(double[] red, double[] green,   int h_l_b,   int h_r_b,   int p1,   int p2,    int hf,    int hole_mid_s,    Position b_r_red, int length)
    {
        int p, t1, t2, i_index = 0, i = 0, j = 1, k = 0, x1_find = 0, x2_find = 0;
        int pos_red, pos_in=0, x6_min_fid=0, x9_min_fid=0, fid, in_fid, len_three_red, intersection, left_hole, right_hole, index, kk, jj;
        int hole_index = 0, hole_time = 0, tt = 0, qq = 0, core;
        double deep_x6_x9, max_red, max_in=0, min_for_x6=0, min_for_x9=0, max_peak, min_peak, peak_standar, green_max_layer=0;
        double x6_ave = 0, x9_ave = 0, x6_standar, x9_standar, x6_9_min=0, deep = 0;		//max_in,pos_in分别为所选定区域的最大值和坐标
        double middle_left_max = 0, middle_left_min = 0, middle_right_max = 0, middle_right_min = 0, middle_left_value = 0, middle_right_value = 0;
        double green_hole_left=0, green_hole_right=0, left_sta, right_sta, compare, max_hole_min=0;
        int left_max_p = 0, left_min_p = 0, right_max_p = 0, right_min_p = 0, left_index = 0, right_index = 0, find_tim = 0;
        int green_pos_max=0, find_left, find_right, pos_hole_left=0, pos_hole_right=0, t, m, n, left_index_temp, right_index_temp, p1_g, p2_g;
        int min_peak_pos, max_hole_min_pos=0, l_s, r_s, left_b = 0, right_b = 0, x6_9_pos=0, property = 0, x1_temp=0, x2_temp=0, ind_for = 0;
        int left_bound=0, right_bound=0, red_p1, red_p2, hole_right_find = 0, hole_left_find = 0, hole_right_temp = 0, hole_left_temp = 0, finds = 0;

        int[][] hole_x6_x9 = new int [50][2];
        int[][] hole = new int[200][2];
        int[] hole_d = new int[50];

        //double[] dif_red = new double[3328];
        double[] dif_red = new double[4096];
        double[] three_red = new double[2048];
        double[] d_three_red = new double[4096];
        double[] dd_three_red = new double[6144];
        double[] three_green = new double[6144];

        int[] hole_ij = new int[48];
        int[] max_hole = new int[48];

        dif(red,dif_red, length);

        t1 = 600/TESTRADIO; t2 = length - 1500/TESTRADIO; max_red = 0; pos_red = 0;
        while (t1 < length - 1500/TESTRADIO && t2 > 600/TESTRADIO)
        {
            maxt_d_l(red, t1, t2,   max_red,   pos_red);
            maxt_d_l(red, pos_red - 10/TESTRADIO, pos_red + 10/TESTRADIO,   max_in,   pos_in);
            if (max_red == max_in)
                break;
            else
            {
                t1 += 10;
                t2 -= 10;
                continue;
            }
            //t1 += 1;
            //t2 -= 1;
        }

        mint_d_l(red, pos_red - 600/TESTRADIO, pos_red,   min_for_x6,   x6_min_fid);
        mint_d_l(red, pos_red, pos_red + 600/TESTRADIO,   min_for_x9,   x9_min_fid);
        fid = x6_min_fid;
        in_fid = 0;

        if (x6_min_fid - 50/TESTRADIO <= 0)
            x6_ave = mean_d(red, x6_min_fid);
        else
            x6_ave = mean_d_l(red, x6_min_fid - 50/TESTRADIO, x6_min_fid);


        if (x9_min_fid + 50/TESTRADIO >= length)
            x9_ave = mean_d_l(red, x9_min_fid, length - 1);
        else
            x9_ave = mean_d_l(red, x9_min_fid, x9_min_fid + 50);

        while (fid <= x9_min_fid)
        {
            deep_x6_x9 = 0;
            if (red[fid] > red[fid + 1])
            {
                for (in_fid = fid + 1; in_fid <= x9_min_fid; in_fid++)
                {
                    if (red[in_fid] >= red[fid])
                    {
                        deep_x6_x9 = red[fid] - mint_d_s(red, fid, in_fid);
                        break;
                    }
                }
                if (in_fid >= x9_min_fid - 2)
                {
                    fid = fid + 1;
                    continue;
                }
                if (in_fid >= fid + 19 && in_fid - fid <= 150 && deep_x6_x9 >= 15)
                {
                    hole_x6_x9[i_index][0] = fid;			//i_index 为所寻找的红色坑的个数
                    hole_x6_x9[i_index][1] = in_fid;
                    fid = in_fid;
                    i_index++;
                    continue;
                }
            }
            fid++;
        }

        if (i_index == 0)
        {
            x6_standar = min_for_x6 + 1.0 / 3.0 * (max_red - min_for_x6);
            x9_standar = min_for_x9 + 1.0 / 3.0 * (max_red - min_for_x9);
        }
        else
        {
            double max_for_x6, max_for_x9;
            int max_i=0, hole_max69=0;
            for (i = 0; i < i_index; i++)
            {
                hole_d[i] = hole_x6_x9[i][1] - hole_x6_x9[i][0];
            }
            maxt_i_l(hole_d, 0, i_index - 1,   hole_max69,   max_i);
            mint_d_l(red, hole_x6_x9[max_i][0], hole_x6_x9[max_i][1],   x6_9_min,   x6_9_pos);
            max_for_x6 = maxt_d_s(red, x6_min_fid, x6_9_pos);
            max_for_x9 = maxt_d_s(red, x6_9_pos, x9_min_fid);
            x6_standar = min_for_x6 + 0.33 * (max_for_x6 - min_for_x6);
            x9_standar = min_for_x9 + 0.33 * (max_for_x9 - min_for_x9);
        }

        j = 1;
        while (j <= 600/TESTRADIO)
        {
            if (red[pos_red - 600/TESTRADIO + j] >= x6_standar && red[pos_red - 600/TESTRADIO + j - 1] <= x6_standar)
            {
                property = 0;
                x1_temp = pos_red - 600/TESTRADIO + j;
                for (p = -5; p <= 5; p++)
                {
                    if (red[x1_temp + p + 1] - red[x1_temp + p] < 0)
                        property += 1;
                }
                if (property >= 6/TESTRADIO)
                    j = j + 30/TESTRADIO;
                else
                    break;
            }
            j = j + 1;
        }

        for (j = 1; j <= x1_temp - 1; j++)
        {
            if (dif_red[x1_temp - j] * dif_red[x1_temp - j - 1] <= 0 && red[x1_temp - j] <= red[x1_temp - j - 1] || red[x1_temp - j] - red[x1_temp - j - 1] <= 0.3)
            {
                ind_for = x1_temp - j;
                break;
            }
        }

        if (ind_for <= 32/TESTRADIO)
            x1_find = ind_for;
        else
        {
            x1_find = Point_find(red, dif_red, length, 1, 0, 30, round(1.0 / 2.0 * length), 0.5, 0.3, 32, ind_for, 30, 10, 20, length, 0, x6_ave + 30, x9_ave + 30);
        }
        b_r_red.x6 = x1_find;		//x6寻找完毕


        //开始寻找x9
        j = 1;
        while (j <= 600/TESTRADIO)
        {
            if (red[pos_red + 600 /TESTRADIO- j] >= x9_standar && red[pos_red + 600/TESTRADIO - j + 1] <= x9_standar)
            {
                property = 0;
                x2_temp = pos_red + 600 /TESTRADIO- j;
                for (p = -5; p <= 5; p++)
                {
                    if (red[x2_temp + p] - red[x2_temp + p + 1] < 0)
                        property = property + 1;
                }
                if (property >= 6)
                    j = j + 30/TESTRADIO;
                else
                    break;
            }
            j += 1;
        }

        for (j = 1; j <= length - x2_temp - 2; j++)
        {
            if (dif_red[x2_temp + j] * dif_red[x2_temp + j + 1] <= 0 && red[x2_temp + j] <= red[x2_temp + j + 1] || red[x2_temp + j] - red[x2_temp + j + 1] <= 0.3)
            {
                ind_for = x2_temp + j;
                break;
            }
        }

        if (ind_for >= length - 37)
            x2_find = ind_for;
        else
        {
            x2_find = Point_find(red, dif_red, length, 1, 1, 30, round(1.0 / 2.0 * length), 0.3, 0.3, length - 37, ind_for, 35, 10, 10, length, length, x6_ave + 30, x9_ave + 30);
        }

        b_r_red.x9 = x2_find;			//x9寻找完毕

        if (b_r_red.x9 - b_r_red.x6 > 850/TESTRADIO)				//开始寻找中间层边界
        {
            h_l_b = round(0.5 * (b_r_red.x9 - b_r_red.x6 + 1));
            h_r_b = round(0.5 * (b_r_red.x9 - b_r_red.x6 + 1));
            p1 = 1;
            p2 = 1;
            hf = 0;
            hole_mid_s = 1;
            return;
        }
        else
        {

            for (i = 0; i <= b_r_red.x9 - b_r_red.x6; i++)			//数组范围0-b_red.x9-b_red.x6
            {
                three_red[i] = red[b_r_red.x6 + i];
            }
            len_three_red = b_r_red.x9 - b_r_red.x6 + 1;
            dif(three_red,    d_three_red, len_three_red);
            dif(d_three_red,    dd_three_red, len_three_red);

            i = 0; min_peak_pos = 0;
            while (i <= len_three_red - 2)
            {
                deep = 0;
                if (three_red[i] > three_red[i + 1])
                {
                    for (j = i + 1; j <= len_three_red - 1; j++)
                    {
                        if (three_red[i] <= three_red[j])
                        {
                            deep = three_red[i] - mint_d_s(three_red, i, j);
                            break;
                        }
                    }
                    if (j > len_three_red - 3)
                    {
                        i = i + 1;
                        continue;
                    }
                    if (j >= i + hole_wid_threshold1 && j - i <= hole_wid_threshold2 && deep >= hole_depth_threshold)
                    {
                        int peak_index = 0;	//此处设定此变量值，从下方for移动过来
                        intersection = 0; left_hole = 0; right_hole = 0; index = 0;
                        max_peak = maxt_d_s(three_red, i, j);
                        min_peak = mint_d_s(three_red, i, j);
                        peak_standar = max_peak - 0.33 * (max_peak - min_peak);

                        mint_d_l(three_red, i, j,   min_peak,   min_peak_pos);

                        for (peak_index = i + 1; peak_index <= min_peak_pos; peak_index++)
                        {
                            if (three_red[peak_index] - three_red[peak_index - 1] >= 0 && three_red[peak_index] - three_red[peak_index + 1] >= 0 && three_red[i] - three_red[peak_index] < 1.0 / 6 * deep && peak_index - i > 1.0 / 3.0 * (j - i))
                                i = peak_index;
                        }

                        for (peak_index = (j - 1); peak_index >= min_peak_pos; peak_index--)
                        {
                            if (three_red[peak_index] - three_red[peak_index - 1] >= 0 && three_red[peak_index] - three_red[peak_index + 1] >= 0 && three_red[j] - three_red[peak_index] < 1.0 / 6.0 * deep && j - peak_index > 1.0 / 3.0 * (j - i))
                                j = peak_index;
                        }

                        for (index = i; index <= j; index++)
                        {
                            if (three_red[index] <= peak_standar)
                            {
                                left_hole = index;
                                break;
                            }
                        }
                        for (index = j; index >= i; index--)
                        {
                            if (three_red[index] <= peak_standar)
                            {
                                right_hole = index;
                                break;
                            }
                        }
                        for (index = left_hole + 2; index <= right_hole - 2; index++)
                        {
                            if (three_red[index] >= peak_standar)
                                intersection += 1;
                        }
                        if (intersection > 0)
                        {
                            i = i + 1;
                            continue;
                        }
                        else
                        {
                            for (kk = left_hole; kk >= i; kk--)
                            {
                                if (three_red[kk] - three_red[kk + 1] <= 0)
                                    break;
                            }
                            for (jj = right_hole; jj <= j; jj++)
                            {
                                if (three_red[jj] >= three_red[kk])
                                    break;
                            }
                            hole[hole_index][0] = kk;
                            hole[hole_index][1] = jj;
                            hole_index++;
                            i = j;
                            continue;
                        }
                    }
                }
                i = i + 1;
            }

            if (hole_index == 0)
            {
                h_l_b = 1;
                h_r_b = 1;
                hole_mid_s = 1;
                p1 = 1;
                p2 = 1;
                hf = 0;
                return;
            }


            for (i = 0; i < hole_index; i++)
            {
                hole_ij[i] = hole[i][1] - hole[i][0];
            }
            sort_i(hole_ij, hole_index, "descend",    max_hole);

            while (hole_time < hole_index)
            {
                if (three_red[hole[max_hole[hole_time]][0]] >= 1.0 / 4.0 * maxt_d_s(three_red, 0, b_r_red.x9 - b_r_red.x6))
                {
                    left_bound = hole[max_hole[hole_time]][0];
                    right_bound = hole[max_hole[hole_time]][1];
                    hf = 1;
                    break;
                }
                else
                hole_time++;
            }
            if (hole_time >= hole_index)
            {
                h_l_b = 1;
                h_r_b = 1;
                hole_mid_s = 1;
                p1 = 1;
                p2 = 1;
                hf = 0;
                return;
            }

            //至此，谷判断完毕，下面进行下一步寻找
            red_p1 = 0; red_p2 = 0; h_l_b = 0; h_r_b = 0; hole_right_find = 0; hole_left_find = 0; hole_right_temp = 0; hole_left_temp = 0; finds = 0;		            //开始寻找hole_right_boundary 右边界
            k = right_bound;
            while (k <= b_r_red.x9 - b_r_red.x6)
            {
                if (three_red[k] - three_red[k - 1] <= 0)
                    break;
                k++;
            }
            hole_right_temp = k;
            if (k >= b_r_red.x9 - b_r_red.x6 - 5)
                hole_right_find = right_bound;
            else
            {
                while (finds <= 10 && hole_right_temp <= b_r_red.x9 - b_r_red.x6)
                {
                    finds = finds + 1;
                    for (tt = hole_right_temp + 1; tt <= b_r_red.x9 - b_r_red.x6 - 1; tt++)
                    {
                        if (three_red[tt] - three_red[tt - 1] >= 0)
                            break;
                    }
                    if (tt >= b_r_red.x9 - b_r_red.x6 - 5)
                    {
                        hole_right_find = hole_right_temp;
                        break;
                    }
                    else
                    {
                        for (qq = tt + 1; qq <= b_r_red.x9 - b_r_red.x6; qq++)
                        {
                            if (three_red[qq] - three_red[qq - 1] <= 0)
                                break;
                        }
                        if (qq >= b_r_red.x9 - b_r_red.x6 - 5)
                        {
                            hole_right_find = hole_right_temp;
                            break;
                        }
                        else
                        {
                            if (three_red[qq] - three_red[hole_right_temp] >= 20 && qq - hole_right_temp < 1.0 / 2.0 * (hole_right_temp - left_bound))
                                hole_right_find = qq;
                            else
                            {
                                hole_right_find = hole_right_temp;
                                break;
                            }
                        }
                    }
                    hole_right_temp = hole_right_find;
                }
            }
            h_r_b = hole_right_find;	//坑右侧最高点寻找完毕
            t = left_bound; finds = 0;
            while (t >= 4)
            {
                if (three_red[t] - three_red[t - 1] >= 0)
                    break;
                t = t - 1;
            }
            hole_left_temp = t;
            if (t <= 5)
                hole_left_find = left_bound;
            else
            {
                while (finds <= 10/TESTRADIO && hole_left_temp >= 2/TESTRADIO*2)
                {
                    finds = finds + 1;
                    for (tt = hole_left_temp - 1; tt >= 2; tt--)
                    {
                        if (three_red[tt] - three_red[tt - 1] <= 0)
                            break;
                    }
                    if (tt <= 5)
                    {
                        hole_left_find = hole_left_temp;
                        break;
                    }
                    else
                    {
                        for (qq = tt - 1; qq >= 2; qq--)
                        {
                            if (three_red[qq] - three_red[qq - 1] >= 0)
                                break;
                        }
                        if (qq <= 5)
                        {
                            hole_left_find = hole_left_temp;
                            break;
                        }
                        else
                        {
                            if (three_red[qq] - three_red[hole_left_temp] >= 20 && hole_left_temp - qq < 1.0 / 2.0 * (right_bound - hole_left_temp))
                                hole_left_find = qq;
                            else
                            {
                                hole_left_find = hole_left_temp;
                                break;
                            }

                        }
                    }
                    hole_left_temp = hole_left_find;
                }
            }
            h_l_b = hole_left_find;
            core = round(1.0 / 2.0 * (left_bound + right_bound));
            mint_d_l(three_red, left_bound, right_bound,   max_hole_min,   max_hole_min_pos);
            if (Math.abs(core - max_hole_min_pos) >= 10)
                hole_mid_s = core;
            else
                hole_mid_s = max_hole_min_pos;

            maxt_d_l(three_red, h_l_b, core,   middle_left_max,   left_max_p);
            mint_d_l(three_red, h_l_b, core,   middle_left_min,   left_min_p);
            maxt_d_l(three_red, core, h_r_b,   middle_right_max,   right_max_p);
            mint_d_l(three_red, core, h_r_b,   middle_right_min,   right_min_p);
            middle_left_value = 1.0 / 3.0 * middle_left_max + 2.0 / 3.0 * middle_left_min;
            middle_right_value = 1.0 / 2.0 * middle_right_max + 1.0 / 2.0 * middle_right_min;

            left_index_temp = 0; right_index_temp = 0; index = left_bound; find_tim = 0;

            while (index >= h_l_b)
            {
                if (index == left_bound + 1)
                    find_tim += 1;
                if (three_red[index] <= middle_left_value)
                {
                    left_index_temp = index;
                    if (find_tim > 5)
                        break;
                    if (left_index_temp >= left_max_p && left_index_temp <= left_min_p)
                        break;
                    else
                    {
                        index = left_bound;
                        middle_left_value = 1.0 / 4.0 * middle_left_max + 3.0 / 4.0 * middle_left_min;
                    }
                }
                index = index + 1;
            }
            left_index = left_index_temp;

            index = right_min_p; find_tim = 0;
            while (index <= h_r_b)
            {
                if (index == right_min_p + 1)
                    find_tim = find_tim + 1;
                if (three_red[index] >= middle_right_value)
                {
                    right_index_temp = index;
                    if (find_tim > 5)
                        break;
                    if (right_index_temp >= right_min_p && right_index_temp <= right_max_p)
                        break;
                    else
                    {
                        index = right_min_p;
                        middle_right_value = 1.0 / 4.0 * middle_right_max + 3.0 / 4.0 * middle_right_min;
                    }
                }
                index = index + 1;
            }
            right_index = right_index_temp;

            if (dd_three_red[left_index] >= 0)
            {
                for (j = left_index; j >= h_l_b; j--)
                {
                    if (dd_three_red[j] * dd_three_red[j - 1] <= 0)
                        break;
                }
            }
            else
            {
                for (j = left_index; j <= right_bound; j++)
                {
                    if (dd_three_red[j] * dd_three_red[j + 1] <= 0)
                        break;
                }
            }

            red_p1 = j;

            if (dd_three_red[right_index] >= 0)
            {
                for (j = right_index; j <= h_r_b; j++)
                {
                    if (dd_three_red[j] * dd_three_red[j + 1] <= 0)
                        break;
                }
            }
            else
            {
                for (j = right_index; j >= left_bound; j--)
                {
                    if (dd_three_red[j] * dd_three_red[j - 1] <= 0)
                        break;
                }
            }

            red_p2 = j;

            for (i = red_p1; i <= red_p2; i++)
            {
                if (d_three_red[i] * d_three_red[i + 1] <= 0 || three_red[i] - three_red[i + 1] <= 0)
                {
                    left_b = i;
                    break;
                }
            }
            if (left_b == 0)
                left_b = round(1.0 / 2.0 * (left_bound + right_bound));

            for (i = red_p2; i >= red_p1; i--)
            {
                if (d_three_red[i] * d_three_red[i - 1] <= 0 || three_red[i] - three_red[i - 1] <= 0)
                {
                    right_b = i;
                    break;
                }
            }
            if (right_b == 0)
                right_b = (int)(1.0 / 2.0 * (left_bound + right_bound) + 0.5);	//至此，P1，P2寻找完毕

            for (i = 0; i <= b_r_red.x9 - b_r_red.x6; i++)
            {
                three_green[i] = green[i + b_r_red.x6];
            }

            //此处删去d_three_green，因为存在无意义，有待全面验证
            maxt_d_l(three_green, left_bound, right_bound,   green_max_layer,   green_pos_max);
            if (green_pos_max - 50/TESTRADIO > 0)
                find_left = green_pos_max - 50/TESTRADIO;
            else
                find_left = (int)(1.0 / 2.0 * (left_bound + green_pos_max) + 0.5);

            if (green_pos_max + 50/TESTRADIO < b_r_red.x9 - b_r_red.x6)
                find_right = green_pos_max + 50/TESTRADIO;
            else
                find_right = round(1.0 / 2.0 * (right_bound + green_pos_max));

            mint_d_l(three_green, find_left, green_pos_max,   green_hole_left,   pos_hole_left);
            mint_d_l(three_green, green_pos_max, find_right,   green_hole_right,   pos_hole_right);
            left_sta = 1.0 / 2.0 * (green_max_layer + green_hole_left);
            right_sta = 1.0 / 2.0 * (green_max_layer + green_hole_right);

            m = find_left + 5;
            while (m <= green_pos_max)
            {
                if (three_green[m] >= left_sta && three_green[m] - three_green[m - 1] >= 0 && three_green[m + 1] - three_green[m] >= 0 && three_green[m + 2] - three_green[m + 1] >= 0 && m >= pos_hole_left && m <= green_pos_max)
                    break;
                m = m + 1;
            }

            if (m <= find_left + 5)
                m = find_left + 5;
            if (m >= green_pos_max - 2)
                m = green_pos_max - 2;

            n = m;

            p1_g = green_lookup_hole_once(three_green, b_r_red.x9 - b_r_red.x6 + 1, n, "left", 1.0 / 4.0 * (green_max_layer - green_hole_left), h_l_b);

            m = find_right - 5;
            while (m >= green_pos_max)
            {
                if (three_green[m] >= right_sta && three_green[m] - three_green[m - 1] <= 0 && three_green[m + 1] - three_green[m] <= 0 && three_green[m + 2] - three_green[m + 1] <= 0 && m >= green_pos_max && m <= pos_hole_right)
                    break;
                m = m - 1;
            }
            if (m >= find_right - 5)
                m = find_right - 5;
            if (m <= green_pos_max - 2)
                m = green_pos_max - 2;

            n = m;
            p2_g = green_lookup_hole_once(three_green, b_r_red.x9 - b_r_red.x6 + 1, n, "right", 1.0 / 4.0 * (green_max_layer - green_hole_right), h_r_b);

            if (green_hole_left > green_hole_right)
                compare = green_hole_left;
            else
                compare = green_hole_right;

            if (h_l_b > 10)
                l_s = h_l_b - 10;
            else
                l_s = h_l_b;

            if (h_r_b < b_r_red.x9 - b_r_red.x6 - 10)
                r_s = h_r_b + 10;
            else
                r_s = h_r_b;

            if (green_max_layer - compare >= 15 && p1_g >= l_s && p1_g <= left_b - 3 && p2_g >= right_b + 3 && p2_g <= r_s)
            {
                p1 = p1_g;
                p2 = p2_g;
            }
            else
            {
                p1 = red_p1;
                p2 = red_p2;
            }
        }
    }

    //处理绿光（蓝光）
    private void blue_green_processing(double[] green, int x6, int x9, int hf, int h_l_b, int h_r_b,   int x7_t,   int x8_t,   int x7_s,   int x8_s,   double mini_peak_flag,   int mid_peak_width,   int glo_pos_max,   double upper_line)
    {
        double glo_max = 0.0, height = 0.0, min_leftpeak = 0.0, min_rightpeak = 0.0;
        int i = 0, left_up_pt = 0, left_mid_pt = 0, right_up_pt = 0, right_mid_pt = 0;
        int g_left_standar = 0, g_right_standar = 0, peak_left = 0, peak_right = 0, standar_left_point = 0;
        int standar_left_hole = 0, standar_right_point = 0, standar_right_hole = 0, x7_find = 0, x8_find = 0, p_leftpeak = 0, p_rightpeak = 0;
        //	double* three_layer=(double* )0x496800,* d_three_layer=(double* )0x497000,* three_green=(double* )0x497800;
        double[] three_layer = new double[2048];
        double[] d_three_layer = new double[2048];
        double[] three_green = new double[2048];

        upper_line = 0.0; glo_pos_max = 0; mid_peak_width = 0;
        for (i = 0; i <= x9 - x6; i++)
        {
            three_layer[i] = green[i + x6];
            three_green[i] = green[i + x6];
        }

        dif(three_layer,    d_three_layer, x9 - x6 + 1);
        //查找绿色特征点
        green_characteristics_find(three_green, x9 - x6 + 1, hf, h_l_b, h_r_b,    glo_max,    glo_pos_max,    upper_line,    mid_peak_width,    left_up_pt,    left_mid_pt,    right_up_pt,    right_mid_pt,    height);

        if (left_up_pt <= 1 || left_up_pt >= glo_pos_max - 1 || left_mid_pt <= 1 || left_mid_pt >= glo_pos_max - 1 || right_up_pt <= 1 || right_up_pt >= x9 - x6 - 1 || right_mid_pt <= 1 || right_mid_pt >= x9 - x6 - 1)
        {
            x7_t = 1; x8_t = 1; x7_s = 1; x8_s = 1; mini_peak_flag = 0;
            return;
        }
        if (hf == 1)
        {
            if (h_l_b > 20/TESTRADIO)
                g_left_standar = h_l_b - 20/TESTRADIO;
            else
                g_left_standar = h_l_b;

            if (h_r_b < x9 - x6 - 20/TESTRADIO)
                g_right_standar = h_r_b + 20/TESTRADIO;
            else
                g_right_standar = h_r_b;
        }
        else
        {
            g_left_standar = round(1.0 / 4.0 * (glo_pos_max));
            g_right_standar = round(1.0 / 2.0 * (glo_pos_max + x9 - x6));
        }

        if (glo_pos_max - 50/TESTRADIO <= 1)
            peak_left = round(1.0 / 2.0 * (glo_pos_max));
        else
            peak_left = glo_pos_max - 50/TESTRADIO;

        if (glo_pos_max + 50/TESTRADIO >= x9 - x6)
            peak_right = round(1.0 / 2.0 * (glo_pos_max + x9 - x6));
        else
            peak_right = glo_pos_max + 50/TESTRADIO;

        mint_d_l(three_green, peak_left, glo_pos_max,   min_leftpeak,   p_leftpeak);
        mint_d_l(three_green, glo_pos_max, peak_right,   min_rightpeak,   p_rightpeak);

        if (min_leftpeak >= min_rightpeak)
            mini_peak_flag = glo_max - min_leftpeak;
        else
            mini_peak_flag = glo_max - min_rightpeak;

        standar_left_point = round(1.0 / 2.0 * (glo_pos_max));
        standar_left_hole = round(2.0 / 3.0 * (glo_pos_max));
        standar_right_point = round(1.0 / 3.0 * (x9 - x6 - glo_pos_max));
        standar_right_hole = round(1.0 / 2.0 * (x9 - x6 - glo_pos_max));

        if (glo_pos_max - left_mid_pt <= standar_left_point)
            x7_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, left_mid_pt, "left", standar_left_hole, height, g_left_standar);
        if (glo_pos_max - left_mid_pt >= standar_left_point && glo_pos_max - left_up_pt <= standar_left_point)
            x7_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, left_up_pt, "left", standar_left_hole, height, g_left_standar);
        if (glo_pos_max - left_up_pt > standar_left_point)
            x7_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, glo_pos_max, "left", standar_left_hole, height, g_left_standar);
        if (right_mid_pt - glo_pos_max <= standar_right_point)
            x8_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, right_mid_pt, "right", standar_right_hole, height, g_right_standar);
        if (right_mid_pt - glo_pos_max > standar_right_point && right_up_pt - glo_pos_max <= standar_right_point)
            x8_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, right_up_pt, "right", standar_right_hole, height, g_right_standar);
        if (right_up_pt - glo_pos_max > standar_right_point)
            x8_find = green_lookup_hole(three_layer, x9 - x6 + 1, glo_pos_max, glo_pos_max, "right", standar_right_hole, height, g_right_standar);

        if (x7_find <= 2)
            x7_t = 2;
        else
            x7_t = x7_find;

        if (x8_find >= x9 - x6 - 2)
            x8_t = x9 - x6 - 2;
        else
            x8_t = x8_find;


        x7_find = 0; x8_find = 0;
        if (glo_pos_max - left_mid_pt <= standar_left_point)
            x7_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, left_mid_pt, "left", height, g_left_standar);
        if (glo_pos_max - left_mid_pt > standar_left_point && glo_pos_max - left_up_pt <= standar_left_point)
            x7_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, left_up_pt, "left", height, g_left_standar);
        if (glo_pos_max - left_up_pt > standar_left_point)
            x7_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, glo_pos_max, "left", height, g_left_standar);

        if (right_mid_pt - glo_pos_max <= standar_right_point)
            x8_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, right_mid_pt, "right", height, g_right_standar);
        if (right_mid_pt - glo_pos_max > standar_right_point && right_up_pt - glo_pos_max <= standar_right_point)
            x8_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, right_up_pt, "right", height, g_right_standar);
        if (right_up_pt - glo_pos_max > standar_right_point)
            x8_find = green_lookup_hole_once(three_layer, x9 - x6 + 1, glo_pos_max, "right", height, g_right_standar);

        if (x7_find <= 2)
            x7_s = 2;
        else
            x7_s = x7_find;

        if (x8_find >= x9 - x6 - 2)
            x8_s = x9 - x6 - 2;
        else
            x8_s = x8_find;
    }

    //region 有返回值的方法

    //四舍五入
    int round(double input)
    {
        int value = (int)(input + 0.5);
        return value;
    }
    //返回数组 从a~b 之间的数组元素的最小值
    private double mint_d_s(double[] array, int a, int b)
    {
        double p = array[a];
        int q = a, i;
        for (i = a + 1; i <= b; i++)
        {
            if (p > array[i])
            {
                p = array[i];
                q = i;
            }
        }
        return p;
    }
    //返回数组 从a~b 之间的数组元素和的最大值
    private double maxt_d_s(double[] array, int a, int b)
    {
        double p = array[a];
        int q = a, i;
        for (i = a; i <= b; i++)
        {
            if (p < array[i])
            {
                p = array[i];
                q = i;
            }
        }
        return p;
    }
    //返回数组 从0开始长度为length 的数组元素和的平均值
    private double mean_d_s(double[] data, int length)	//重载函数1
    {
        double sum_data = 0;
        double mean_data;
        int i = 0;
        for (i = 0; i < length; i++)
        {
            sum_data += data[i];
        }
        mean_data = sum_data / length;
        return (mean_data);
    }
    //返回数组 从a~b 之间的数组元素和的平均值
    private double mean_d_l(double[] data, int a, int b)
    {
        double sum_data = 0;
        double mean_data;
        int i = 0;
        for (i = a; i <= b; i++)
        {
            sum_data += data[i];
        }
        mean_data = sum_data / (b - a + 1);
        return (mean_data);
    }
    //返回数组 从0开始长度为length 的数组元素和的平均值
    private double mean_d(double[] data, int length)
    {
        double sum_data = 0, mean_data = 0;
        int i = 0;
        for (i = 0; i < length; i++)
        {
            sum_data += data[i];
        }
        mean_data += sum_data / length;
        return (mean_data);
    }

    //endregion

    //为数组dif_data 0~length 的元素重新赋值
    private void dif(double[] data,    double[] dif_data, int length)
    {
        int i = 0;
        for (i = 0; i < length - 1; i++)
        {
            dif_data[i] = data[i + 1] - data[i];
        }

        dif_data[length - 1] = 0;
    }

    //排序
    private void sort_i(int[] array, int length, String order,    int[] out1)
    {
        int temp, temp_a, p = 0;
        //	int temp,temp_a,* tarray=(int* )0x45d100,p=0;
        int[] tarray = new int[48];

        for (p = 0; p < length; p++)
        {
            out1[p] = p;
            tarray[p] = array[p];
        }

        if (length == 1)
        {
            out1[0] = 0;
        }
        else
        {
            if (order.equals( "ascend") )
            {
                int i = 0, j = 0;
                for (i = 1; i < length; i++)
                {
                    for (j = 0; j < length - i; j++)
                    {
                        if (tarray[j] > tarray[j + 1])
                        {
                            temp_a = tarray[j];
                            tarray[j] = tarray[j + 1];
                            tarray[j + 1] = temp_a;
                            temp =  out1[j];
                            out1[j] =  out1[j + 1];
                            out1[j + 1] = temp;
                        }
                    }
                }
            }
            else if (order.equals("descend"))
            {
                int i = 0, j = 0;
                for (i = 1; i < length; i++)
                {
                    for (j = 0; j < length - i; j++)
                    {
                        if (tarray[j] < tarray[j + 1])
                        {
                            temp_a = tarray[j + 1];
                            tarray[j + 1] = tarray[j];
                            tarray[j] = temp_a;
                            temp =  out1[j + 1];
                             out1[j + 1] =  out1[j];
                             out1[j] = temp;
                        }
                    }
                }
            }
            else
            {
                return;
            }
        }
    }
    //查找绿色特征点
    private void green_characteristics_find(double[] three_green, int len, int hf, int h_l_b, int h_r_b,    double glo_max,    int glo_pos_max,    double upper_line,    int mid_peak_width,    int left_up_pt,    int left_mid_pt,    int right_up_pt,    int right_mid_pt,    double height)
    {
        int i = 0, peak_t = 0, max_left_range = 0, max_right_range = 0, pos_left_min = 0, pos_right_min = 0;
        int left_std = 0, right_std = 0, left_set_p = 0, left_set_h = 0, left_set_up_p = 0, left_set_up_h = 0;
        int right_set_p = 0, right_set_h = 0, right_set_up_p = 0, right_set_up_h = 0, left_ind_standar = 0, right_ind_standar = 0, left_weep_b = 0, right_weep_b = 0;
        double middle_line = 0.0, down_line = 0.0, midlayer_left_min = 0.0, midlayer_right_min = 0.0, mid_layer_threshold = 0.0, left_am_standar = 0.0, right_am_standar = 0.0;

        left_up_pt = 0; left_mid_pt = 0; right_up_pt = 0; right_mid_pt = 0; upper_line = 0.0;

        down_line = mean_d_s(three_green, len);
        while (peak_t <= 10)
        {
            peak_t++;
            if (hf == 1)
            {
                max_left_range = h_l_b;
                max_right_range = h_r_b;
            }
            else
            {
                if (round(1.0 / 2.0 * len) - 150 <= 0)
                    max_left_range = 1;
                else
                    max_left_range = round(1.0 / 2.0 * len - 150);

                if (round(1.0 / 2.0 * len) < 100)
                    max_right_range = len;
                else
                    max_right_range = round(1.0 / 2.0 * (len) + 100);
            }

            maxt_d_l(three_green, max_left_range, max_right_range,  glo_max,  glo_pos_max);
            mint_d_l(three_green, round(1.0 / 2.0 * (glo_pos_max)), glo_pos_max,   midlayer_left_min,   pos_left_min);
            mint_d_l(three_green, glo_pos_max, round(1.0 / 2.0 * ((glo_pos_max) + len - 1)),   midlayer_right_min,   pos_right_min);

            if (midlayer_left_min > midlayer_right_min)
            {
                mid_layer_threshold = 0.5 * (glo_max + midlayer_left_min);
                if (round(1.0 / 5.0 * (glo_max - midlayer_left_min)) > 10)
                    height = 1.0 / 5.0 * (glo_max - midlayer_left_min);
                else
                    height = 10;
            }
            else
            {
                mid_layer_threshold = 0.5 * (glo_max + midlayer_right_min);
                if (round(1.0 / 5.0 * (glo_max - midlayer_right_min)) > 10)
                    height = 1.0 / 5.0 * (glo_max - midlayer_right_min);
                else
                    height = 10;
            }

            for (left_std = pos_left_min; left_std <= glo_pos_max; left_std++)
            {
                if (three_green[left_std] >= mid_layer_threshold)
                    break;
            }

            for (right_std = pos_right_min; right_std >= glo_pos_max; right_std--)
            {
                if (three_green[right_std] >= mid_layer_threshold)
                    break;
            }

            mid_peak_width = right_std - left_std;
            upper_line = mean_d_l(three_green, left_std, right_std);
            middle_line = 0.5 * (upper_line + down_line);

            for (i = 1; i <= glo_pos_max; i++)
            {
                if (three_green[glo_pos_max - i] <= middle_line)
                {
                    left_set_p = glo_pos_max - i;
                    break;
                }
            }

            for (i = 0; i <= glo_pos_max; i++)
            {
                if (three_green[i] >= middle_line)
                {
                    left_set_h = i;
                    break;
                }
            }

            if (left_set_p == 0 || left_set_h == 0)
            {
                hf = 0;
                continue;
            }

            if (left_set_p - left_set_h > 5 && left_set_h >= round(1.0 / 2.0 * (glo_pos_max)) && glo_max - maxt_d_s(three_green, left_set_h, left_set_p) <= 1.0 / 3.0 * ((glo_max) - midlayer_left_min))
                left_mid_pt = left_set_h;
            else
                left_mid_pt = left_set_p;

            for (i = 1; i <= glo_pos_max; i++)
            {
                if (three_green[glo_pos_max - i] <= upper_line)
                {
                    left_set_up_p = glo_pos_max - i;
                    break;
                }
            }

            for (i = 1; i <= glo_pos_max; i++)
            {
                if (three_green[i] >= upper_line)
                {
                    left_set_up_h = i;
                    break;
                }
            }

            if (left_set_up_p == 0 || left_set_up_h == 0)
            {
                hf = 0;
                continue;
            }

            if (left_set_up_p - left_set_up_h > 5 && left_set_up_h >= round(1.0 / 2.0 * (glo_pos_max)) && glo_max - maxt_d_s(three_green, left_set_up_h, left_set_up_p) <= 1.0 / 3.0 * (glo_max - midlayer_left_min))
                left_up_pt = left_set_up_h;
            else
                left_up_pt = left_set_up_p;


            for (i = 1; i <= len - glo_pos_max - 1; i++)
            {
                if (three_green[glo_pos_max + i] <= middle_line)
                {
                    right_set_p = glo_pos_max + i;
                    break;
                }
            }

            for (i = 1; i <= len - glo_pos_max - 1; i++)
            {
                if (three_green[len - i] >= middle_line)
                {
                    right_set_h = len - i;
                    break;
                }
            }

            if (right_set_p == 0 || right_set_h == 0)
            {
                hf = 0;
                continue;
            }

            if (right_set_h - right_set_p > 5 && right_set_h - glo_pos_max <= round(1.0 / 3.0 * (len - glo_pos_max)) && glo_max - maxt_d_s(three_green, right_set_p, right_set_h) <= 1.0 / 3.0 * (glo_max - midlayer_right_min))
                right_mid_pt = right_set_h;
            else
                right_mid_pt = right_set_p;

            for (i = 1; i <= len - glo_pos_max - 1; i++)
            {
                if (three_green[glo_pos_max + i] <= upper_line)
                {
                    right_set_up_p = glo_pos_max + i;
                    break;
                }
            }

            for (i = 1; i <= len - glo_pos_max - 1; i++)
            {
                if (three_green[len - i] >= upper_line)
                {
                    right_set_up_h = len - i;
                    break;
                }
            }

            if (right_set_up_p == 0 || right_set_up_h == 0)
            {
                hf = 0;
                continue;
            }

            if (right_set_up_h - right_set_up_p > 5 && right_set_up_h - glo_pos_max <= round(1.0 / 3.0 * (len - glo_pos_max)) && glo_max - maxt_d_s(three_green, right_set_up_p, right_set_up_h) <= 1.0 / 3.0 * (glo_max - midlayer_right_min))
                right_up_pt = right_set_up_h;
            else
                right_up_pt = right_set_up_p;

            if (right_mid_pt - left_mid_pt <= 10)
            {
                if (round(1.0 / 5.0 * (glo_max - mint_d_s(three_green, 0, glo_pos_max))) > 10)
                    left_am_standar = 1.0 / 5.0 * (glo_max - mint_d_s(three_green, 0, glo_pos_max));
                else
                    left_am_standar = 10;

                if (glo_pos_max - 50 > 0)
                    left_ind_standar = glo_pos_max - 50;
                else
                    left_ind_standar = 1;
                if (round(1.0 / 5.0 * (glo_max - mint_d_s(three_green, glo_pos_max, len - 1))) > 10)
                    right_am_standar = 1.0 / 5.0 * (glo_max - mint_d_s(three_green, glo_pos_max, len - 1));
                else
                    right_am_standar = 10;

                if (glo_pos_max + 50 < len - 1)
                    right_ind_standar = glo_pos_max + 50;
                else
                    right_ind_standar = len - 1;
                left_weep_b = green_lookup_hole_once(three_green, len, glo_pos_max, "left", left_am_standar, left_ind_standar);
                right_weep_b = green_lookup_hole_once(three_green, len, glo_pos_max, "right", right_am_standar, right_ind_standar);
                for (i = left_weep_b; i <= right_weep_b; i++)
                {
                    three_green[i] = down_line;
                }
                continue;
            }
            else
                break;
        }
    }


    private int green_lookup_hole(double[] three_green, int len_t, int glo_pos_max, int intersection, String direction, int hole_standar, double am_standar, int index_bound)
    {
        int i = 0, q1 = 0, index = 0;
        String left = "left", right = "right";
        //	double* d_three_green=(double* )0x498000;
        double[] d_three_green = new double[2048];

        dif(three_green,    d_three_green, len_t);
        if (direction.equals(left) )
        {
            for (i = 1; i <= intersection - 22; i++)
            {
                if (d_three_green[intersection - i] * d_three_green[intersection - i - 1] <= 0 && three_green[intersection - i] <= three_green[intersection - i - 1] || three_green[intersection - i] - three_green[intersection - i - 1] <= 0.3)
                {
                    q1 = intersection - i;
                    break;
                }
            }

            if (q1 <= 2)
                index = 1;
            else
            {
                index = Point_find(three_green, d_three_green, len_t, 0, 0, 10, glo_pos_max, 0.5, 0.3, 25, q1, 20, 5, am_standar, hole_standar, index_bound, 225, 225);
            }
        }
        else if (direction.equals(right))
        {
            for (i = 1; i <= len_t - intersection - 22; i++)
            {
                if (d_three_green[intersection + i] * d_three_green[intersection + i + 1] <= 0 && three_green[intersection + i] <= three_green[intersection + i + 1] || three_green[intersection + i] - three_green[intersection + i + 1] <= 0.3)
                {
                    q1 = intersection + i;
                    break;
                }
            }
            if (q1 <= 2)
                index = len_t - 1;
            else
            {
                index = Point_find(three_green, d_three_green, len_t, 0, 1, 10, glo_pos_max, 0.5, 0.3, len_t - 25, q1, 20, 5, am_standar, hole_standar, index_bound, 225, 225);
            }
        }
        else
        {
            index = 0;
        }
        return index;
    }

    private int green_lookup_hole_once(double[] three_green, int len_g, int intersection, String direction, double am_standar, int ind_standar)
    {
        int i = 0, index = 0, target_p = 0, p_find = 0;
        String left = "left", right = "right";
        //	double* d_three_green=(double* )0x496000;
        double[] d_three_green = new double[2048];

        dif(three_green,    d_three_green, len_g);

        if (direction.equals( left))
        {
            for (i = 1; i <= intersection - 1; i++)
            {
                if (d_three_green[intersection - i] * d_three_green[intersection - i - 1] <= 0 && three_green[intersection - i] <= three_green[intersection - i - 1] || three_green[intersection - i] - three_green[intersection - i - 1] <= 0.2)
                    break;
            }
            if (intersection - i >= 19)
            {
                p_find = Point_find(three_green, d_three_green, len_g, 0, 0, 1, round(1.0 / 2.0 * len_g), 0.5, 0.2, 19, intersection - i, 15, 3, am_standar, len_g, ind_standar, 225, 225);
            }
        }
        else if (direction.equals( right))
        {
            for (i = intersection; i <= len_g - 2; i++)
            {
                if (d_three_green[i] * d_three_green[i + 1] <= 0 && three_green[i] <= three_green[i + 1] || three_green[i] - three_green[i + 1] <= 0.2)
                    break;
            }
            if (target_p <= len_g - 19)
            {
                p_find = Point_find(three_green, d_three_green, len_g, 0, 1, 1, round(1.0 / 2.0 * len_g), 0.5, 0.2, len_g - 19, i, 15, 3, am_standar, len_g, ind_standar, 225, 225);
            }
        }
        else
            p_find = 0;

        index = p_find;
        return index;
    }

    //本函数用于整合所有边界点查找的部分，缩短代码
    private int Point_find(double[] data, double[] dif_data, int length, int color, int direction, int search_time, int glo_position_max, double first_scop, double second_scop, int boud_index, int inti_point, int first_point, int first_standar, double am_standar, int hole_standar, int ind_standar, double x6_standar, double x9_standar)
    {
        int find_time = 0, x_find = 0, x_back = 0, mm = 0, nn = 0;
        int ind_for_x = inti_point;

        if (direction == 0)	//0表示左侧寻找
        {
            while (find_time <= search_time && ind_for_x >= boud_index)
            {
                find_time = find_time + 1;
                for (mm = ind_for_x - first_point; mm <= ind_for_x; mm++)
                {
                    if (data[mm] - data[mm - 1] >= first_scop)
                        break;
                }
                if (mm > ind_for_x - first_standar)
                {
                    if (color == 1)		//必须进行最低幅值判?
                    {
                        if (data[ind_for_x] <= x6_standar + 30)
                        {
                            x_find = ind_for_x;
                            break;
                        }
                        else
                        {
                            ind_for_x = ind_for_x - 10;
                            continue;
                        }
                    }
                    else			    //无须进行最低幅值判断
                    {
                        x_find = ind_for_x;
                        break;
                    }
                }
                else
                {
                    for (nn = 0; nn <= mm - 1; nn++)
                    {
                        if (dif_data[mm - nn] * dif_data[mm - nn - 1] <= 0 && data[mm - nn] <= data[mm - nn - 1] || data[mm - nn] - data[mm - nn - 1] <= second_scop)
                        {
                            x_back = mm - nn;
                            break;
                        }
                    }
                    if (x_back <= 2)
                    {
                        if (color == 1)
                        {
                            if (data[ind_for_x] <= x6_standar + 30)
                            {
                                x_find = ind_for_x;
                                break;
                            }
                            else
                            {
                                ind_for_x = ind_for_x - 10;
                                continue;
                            }
                        }
                        else
                        {
                            x_find = ind_for_x;
                            break;
                        }

                    }
                    else
                    {
                        if (data[ind_for_x] - data[x_back] >= am_standar && glo_position_max - x_back <= hole_standar && x_back >= ind_standar && data[x_back] <= x6_standar)
                            x_find = x_back;
                        else
                        {
                            if (color == 1)
                            {
                                if (data[ind_for_x] < x6_standar + 30)
                                {
                                    x_find = ind_for_x;
                                    break;
                                }
                                else
                                {
                                    ind_for_x = ind_for_x - 10;
                                    continue;
                                }
                            }
                            else
                            {
                                x_find = ind_for_x;
                                break;
                            }
                        }
                    }
                }
                ind_for_x = x_find;
            }
        }
        else				//其他表示右侧寻找
        {
            while (find_time <= search_time && ind_for_x <= boud_index)
            {
                find_time = find_time + 1;
                for (mm = ind_for_x + first_point; mm >= ind_for_x; mm--)
                {
                    if (data[mm] - data[mm + 1] >= first_scop)
                        break;
                }
                if (mm < ind_for_x + first_standar)
                {
                    if (color == 1)
                    {
                        if (data[ind_for_x] <= x9_standar + 30)
                        {
                            x_find = ind_for_x;
                            break;
                        }
                        else
                        {
                            ind_for_x = ind_for_x + 10;
                            continue;
                        }
                    }
                    else
                    {
                        x_find = ind_for_x;
                        break;
                    }

                }
                else
                {
                    for (nn = 0; nn <= length - mm - 2; mm++)
                    {
                        if (dif_data[mm + nn] * dif_data[mm + nn + 1] <= 0 && data[mm + nn] <= data[mm + nn + 1] || data[mm + nn] - data[mm + nn + 1] <= second_scop)
                        {
                            x_back = mm + nn;
                            break;
                        }
                    }
                    if (x_back <= 2 || x_back >= length - 2)
                    {
                        if (color == 1)
                        {
                            if (data[ind_for_x] <= x9_standar + 30)
                            {
                                x_find = ind_for_x;
                                break;
                            }
                            else
                            {
                                ind_for_x = ind_for_x + 10;
                                continue;
                            }
                        }
                        else
                        {
                            x_find = ind_for_x;
                            break;
                        }

                    }
                    else
                    {
                        if (data[ind_for_x] - data[x_back] >= am_standar && x_back - glo_position_max <= hole_standar && x_back <= ind_standar && data[x_back] <= x9_standar)
                            x_find = x_back;
                        else
                        {
                            if (color == 1)
                            {
                                if (data[ind_for_x] <= x9_standar + 30)
                                {
                                    x_find = ind_for_x;
                                    break;
                                }
                                else
                                {
                                    ind_for_x = ind_for_x + 10;
                                    continue;
                                }
                            }
                            else
                            {
                                x_find = ind_for_x;
                                break;
                            }

                        }
                    }
                }
                ind_for_x = x_find;
            }
        }
        return x_find;
    }

    //处理完数据后，计算得出结果
    public void result_out (int sum_count)
    {
        int begin,over,search=0,start,ender,mean4=0,mean_lf=0;
        int[] af_result_mid = new int[48];
        int[] af_result_left = new int[48];
        int[] ind_result = new int[48];
        int[] ind_lf = new int[48];
        int m_l_ind=0,m_r_ind=0;
        int i=0;

      //  Console.WriteLine(sum_count);   //输出成功次数
        Log.e("TAG","sum_count"+sum_count);
        //蓝光采集成功次数大于20次
        if(sum_count>=20)
        {
            for(i=0;i<sum_count;i++)
            {
                af_result_mid[i]=three_layer[i][2]-three_layer[i][1];
                af_result_left[i]=three_layer[i][1]-three_layer[i][0];
            }
            sort_i(af_result_mid, sum_count, "ascend",    ind_result);
            sort_i(af_result_left, sum_count, "ascend",    ind_lf);

            m_l_ind = round(sum_count / 2.0) - 3;
            m_r_ind = round(sum_count / 2.0) + 3;

            for (i = m_l_ind; i <= m_r_ind; i++)
            {
                mean4 += af_result_mid[ind_result[i]];
                mean_lf += af_result_left[ind_lf[i]];
            }
            mean4 = round(mean4 / 7.0);
            mean_lf = round(mean_lf / 7.0);
            begin = 0; over = sum_count - 1;
            while (begin <= 20 && over >= 20 && over - begin >= 20 && search <= sum_count)
            {
                search++;
                if (Math.abs(af_result_mid[ind_result[begin]] - mean4) >= 15)
                    begin = begin + 1;
                if (Math.abs(af_result_mid[ind_result[over]] - mean4) >= 15)
                    over = over - 1;
            }

            if (over - begin >= 20)		//此处添加此if条件完全为可以在函数体中间添加适当大小的数组，进行进一步处理
            {
                int[][] result_af_all = new int[40][ 4];//  * ind_left=(int* )0x498900,* result_af_1=(int* )0x498930,t=0;
                int[] ind_left = new int[48];
                int[] result_af_1 = new int[48];
                int t = 0;
                for (i = 0; i <= over - begin; i++)
                {
                    result_af_all[i][ 0] = three_layer[ind_result[i + begin]][ 0];
                    result_af_all[i][ 1] = three_layer[ind_result[i + begin]][ 1];
                    result_af_all[i][ 2] = three_layer[ind_result[i + begin]][ 2];
                    result_af_all[i][3] = three_layer[ind_result[i + begin]][ 3];

                    result_af_1[i] = three_layer[ind_result[i + begin]][1] - three_layer[ind_result[i + begin]][ 0];
                }
                sort_i(result_af_1, over - begin + 1, "ascend",    ind_left);
                start = 0; ender = over - begin; search = 0;
                while (start <= 20 && ender >= 20 && ender - start >= 20 && search <= over - begin + 1)
                {
                    search++;
                    if (Math.abs(result_af_1[ind_left[start]] - mean_lf) >= 20)
                        start += 1;
                    if (Math.abs(result_af_1[ind_left[ender]] - mean_lf) >= 20)
                        ender -= 1;
                }
                for (t = start; t <= ender; t++)
                {
                    x6_final = x6_final + result_af_all[ind_left[t]][ 0];
                    x7_final = x7_final + result_af_all[ind_left[t]][ 1];
                    x8_final = x8_final + result_af_all[ind_left[t]][ 2];
                    x9_final = x9_final + result_af_all[ind_left[t]][ 3];
                }
                x6_final = round(x6_final / (ender - start + 1));
                x7_final = round(x7_final / (ender - start + 1));
                x8_final = round(x8_final / (ender - start + 1));
                x9_final = round(x9_final / (ender - start + 1));

            }
            else
            {
                x6_final = 0;
                x7_final = 0;
                x8_final = 0;
                x9_final = 0;
            }

        }
        else
        {
            x6_final = 0;
            x7_final = 0;
            x8_final = 0;
            x9_final = 0;
        }

        sum_count = 0;
        //最后计算出的后四个点
        pos_res.x6 = x6_final;
        pos_res.x7 = x7_final;
        pos_res.x8 = x8_final;
        pos_res.x9 = x9_final;

        //对所有筛选后的点进行赋值 9个点
        point_result[0] = pos_res.x1;
        point_result[1] = pos_res.x2;
        point_result[2] = pos_res.x3;
        point_result[3] = pos_res.x4;
        point_result[4] = pos_res.x5;
        point_result[5] = pos_res.x6;
        point_result[6] = pos_res.x7;
        point_result[7] = pos_res.x8;
        point_result[8] = pos_res.x9;

        //如果后四个点为0 则测试失败 将结果全赋值为0
        if (pos_res.x6 == 0 || pos_res.x7 == 0 || pos_res.x8 == 0 || pos_res.x9 == 0)
        {
            for (i = 0; i < 9; i++)
            {
                parameter[i] = 0;
            }
            result_get_final.hct = parameter[0];
            result_get_final.hgb = parameter[1];
            result_get_final.wbc = parameter[2];
            result_get_final.plt = parameter[3];
            result_get_final.grans = parameter[4];
            result_get_final.lm = parameter[5];
            result_get_final.mchc = parameter[6];
            result_get_final.percent_grans = parameter[7];
            result_get_final.percent_lm = parameter[8];
        }
        else   //正确 计算测试结果
        {
            ResultGet(pos_res,   result_get_final);
            parameter[0] = result_get_final.hct;
            parameter[1] = result_get_final.hgb;
            parameter[2] = result_get_final.wbc;
            parameter[3] = result_get_final.plt;
            parameter[4] = result_get_final.grans;
            parameter[5] = result_get_final.lm;
            parameter[6] = result_get_final.mchc;
            parameter[7] = result_get_final.percent_grans;
            parameter[8] = result_get_final.percent_lm;
        }

    }

    //计算最后的项目测试结果
    private void ResultGet(Position finall,   Result finall_result)
    {
        double ratio, float_up = finall.x2, red_first_mid_black_line = finall.x3, liquid_level = finall.x5, blue_one = finall.x6, blue_two = finall.x7, blue_three = finall.x8, blue_four = finall.x9;
        double whole_height, height_unexpended, height_float_bottom, height_expended, height_grans, height_lm, height_plt, height_p, volume, ratio_volume, den_r, hct_b = 0.0, hgb_b = 0.0, mchc_b = 0.0;

        height_mid_black_line = finall.x4 - finall.x3;
        ratio = mid_black_line / height_mid_black_line;

        whole_height = height_first_mid_black_line + (liquid_level - red_first_mid_black_line) * ratio;
        height_unexpended = height_first_mid_black_line - (red_first_mid_black_line - float_up) * ratio - 19.5;
        height_float_bottom = (red_first_mid_black_line - float_up) * ratio + 19.5;

        height_expended = height_float_bottom - (red_first_mid_black_line - blue_one) * ratio;
        height_grans = (blue_two - blue_one) * ratio;
        height_lm = (blue_three - blue_two) * ratio;
        height_plt = (blue_four - blue_three) * ratio;
        height_p = 19 - height_expended - height_grans - height_lm - height_plt;

        volume = 3.142 * 0.8385 * 0.8385 * (whole_height) - 3.142 * 0.8385 * 0.8385 * 19 * 0.9066 - 0.667782;
        ratio_volume = 111.0 / volume;

        hct_b = 2.64 * ratio_volume * (height_unexpended + 0.066 * height_expended);

        finall_result.hct = defined.configfile.HCTA * hct_b + defined.configfile.HCTB;///////******参数HCTA 参数HCTB
        finall_result.grans = defined.configfile.GRANSA * (4.25 * ratio_volume * height_grans) + defined.configfile.GRANSB; //////*****参数GRANA 参数GRANB

        if ((height_lm * ratio_volume) > 1.21)
            finall_result.lm = defined.configfile.LMA * (4.96 * ratio_volume * 1.21 + 8.97 * ratio_volume * (height_lm - 1.21)) - defined.configfile.LMB;
        else
            finall_result.lm = defined.configfile.LMA * (4.96 * ratio_volume * (height_lm)) - defined.configfile.LMB;//////******参数LMA 参数LMB

        finall_result.percent_grans = finall_result.grans * 100 / (finall_result.grans + finall_result.lm);
        finall_result.percent_lm = finall_result.lm * 100 / (finall_result.grans + finall_result.lm);
        finall_result.wbc = finall_result.grans + finall_result.lm;
        finall_result.plt = defined.configfile.PLTA * 175.8 * height_plt * ratio_volume - defined.configfile.PLTB;///////******参数PLTA 参数PLTB
        den_r = den_f - ((den_g - den_f) * height_grans + (den_lm - den_f) * height_lm + (den_plt - den_f) * height_plt + (den_p - den_f) * height_p) / height_expended;

        mchc_b = (3.9248 * den_r - 3.95824) * 100;							////////////////////暂时添加，为了拟合，将来可以酌情删除
        hgb_b = hct_b * mchc_b / 100.0;									////////////////////暂时添加，为了拟合，将来可以酌情删除

        finall_result.mchc = 0.2875 * mchc_b + 24.2495;
        finall_result.hgb = defined.configfile.HGBA * hgb_b - defined.configfile.HGBB;//////******参数HGBA 参数HGBB
    }

    //////新加自检使用
    /////////////////////////////////////////////////////////////////////

    //ccd自检
    public int CCDCheck(    int[] Red)
    {
        double[] red_data = new double[10550];
        filter(B_R, A_R, Red,    red_data, 12);
        double sum = 0;
        for (int i = 0; i < CCDPixelSum; i++)//判断图像是否正确
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / CCDPixelSum).ToString());
        //if ((sum / CCDPixelSum) < 1300)//1300
        if ((sum / CCDPixelSum) > 0 && (sum / CCDPixelSum) < 65535)//1300
        {
            return 0;
        }
        return 18;
    }

    //判断红光是否开启 和光强是否足够
    public int CheckRedLight(    int[] Red)
    {
        double[] red_data = new double[10550];
        filter(B_R, A_R, Red,    red_data, 12);
        double sum = 0;
        int i = 0, mx1 = 0, mx2 = 0;

        for (i = 0; i < CCDPixelSum; i++)//判断红光是否开启
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / CCDPixelSum).ToString());
        if ((sum / CCDPixelSum) < 1200)
        {
            //MessageBox.Show("红光未打开（错误）");
            return 10;
        }

        mx1 = 2000; mx2 = 3500; sum = 0;
        for (i = mx1; i < mx2; i++)//判断是否放入试管
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / (mx2 - mx1)).ToString());
        if (sum / (mx2 - mx1) < 2000)
        {
            //MessageBox.Show("开机有试管（错误）");
            return 14;
        }
        mx1 = 1000; mx2 = 2000; sum = 0;
        for (i = mx1; i < mx2; i++)//判断红光亮度是够足够
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / (mx2 - mx1)).ToString());
        if (sum / (mx2 - mx1) < 15000)
        {
            //MessageBox.Show("红光亮度不够（错误） ");
            return 11;
        }

        return 0;
    }

    //判断蓝光是否开启 和光强是否足够
    public int CheckBlueLight(    int[] Red,     int[] Green)
    {
        double[] red_data = new double[10550];
        double[] green_data = new double[10550];

        filter(B_R, A_R, Red,    red_data, 12);
        filter(B_G, A_G, Green,    green_data, 12);

        double sum = 0;
        int i = 0, mx1 = 0, mx2 = 0;

        for (i = 0; i < CCDPixelSum; i++)//判断蓝光是否开启
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / CCDPixelSum).ToString());
        if ((sum / CCDPixelSum) < 60)
        {
            //MessageBox.Show("蓝光未打开（错误）");
            return 12;
        }

        mx1 = 3100; mx2 = 4500; sum = 0;
        for (i = mx1; i < mx2; i++)//判断蓝光亮度是够足够
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / (mx2 - mx1)).ToString());
        if (sum / (mx2 - mx1) < 100)//4800
        {
            //MessageBox.Show("蓝光亮度不够（错误）");
            return 13;
        }
        return 0;
    }

    //测试时调用 判断是否放入试管
    public int Check(    int[] Red)
    {
        double[] red_data = new double[10550];
        filter(B_R, A_R, Red,    red_data, 12);
        double sum = 0;
        for (int i = 0; i < CCDPixelSum; i++)//判断红光是否开启
        {
            sum += red_data[i];
        }
        //MessageBox.Show((sum / CCDPixelSum).ToString());
        if ((sum / CCDPixelSum) < 1200)
        {
            //MessageBox.Show("红光未打开（错误）");
            return 10;
        }

        if (red_data[2500] > 10000 && red_data[4000] > 10000 && red_data[6000] > 10000)
        {
            //MessageBox.Show("未放入试管（错误）");
            return 15;
        }

        if (red_data[1000] > 5000 && red_data[2000] > 5000 && red_data[3250] > 5000)
        {
            //MessageBox.Show("试管放反");
            return 16;
        }
        return 0;
    }

    //判断试管是否放反
    public int ReverseVitro(    int[] Red)
    {
        double[] red_data = new double[10550];
        filter(B_R, A_R, Red,    red_data, 12);
        if (red_data[3500]>2000)
        {
            //MessageBox.Show("试管放反");
            return 16;
        }
        return 0;
    }

    //取图像数组中的 红像素点
    //+3的含义是  取全红
    public void RedPixel2(int [] ImageBuf,        int[] Red)
    {
        int i, j = 10549;
        for (i = 0; i < ImageBuf.length; i = i + 3, j--)
        {
            Red[j] = ImageBuf[i];
        }
    }

    //取图像数组中的 绿像素点
    public void GreenPixel2(    int[] ImageBuf,        int[] Green)
    {
        int i, j = 2591;
        for (i = 1; i < ImageBuf.length; i = i + 3, j--)
        {
            Green[j] = ImageBuf[i];
        }
    }



}


/*
*     static void Main(string[] args)
        {
            string temp = "a";
            Change(temp);
            Console.WriteLine(temp);

            ChangeBy  (   temp);
            Console.WriteLine(temp);
            Console.ReadKey();
        }
*
*         private static void Change(string temp)
        {
            temp = temp + "--changed";
        }

        private static void ChangeBy  (   string temp)
        {
            temp = temp + "--  changed";
        }

复制代码

输出结果：
a
a--  changed
● 在Change()方法中，虽然改变了temp的值，但方法没有返回值，打印temp依然是初始值a；
 在ChnageBy  ()方法中，由于加上了关键字，虽然也没有返回值，当改变temp的值后，temp的引用地址发生了改变，再次打印,temp的值为新的引用地址对应的值。
*
* */