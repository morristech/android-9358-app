package com.xmd.cashier.cashier;

import android.content.Context;

import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;

/**
 * Created by heyangya on 16-9-6.
 */

public interface IPos {
    int GRAVITY_LEFT = 0;
    int GRAVITY_CENTER = 1;
    int GRAVITY_RIGHT = 2;

    String getAppCode();    //返回POS类型码

    String getPosIdentifierNo();    //返回POS标识码

    boolean needCheckUpdate();  //需要检查升级信息

    boolean needChoicePayTypeByCaller();    //调用支付之前是否需要选择支付方式

    //当前处于支付界面，但是由于某些原因支付界面的task被切换到后台，然后打开9358收银台时，需要确保交易结束，这时需要重新将支付task切换前台显示，此函数返回的package就是关联支付task的包名
    String getPackageName();

    void init(Context context, Callback<?> callback);// 初始化

    void pay(Context context, String tradeNo, int money, int payType, PayCallback<Object> callback);//发起支付

    int getPayType(Object o);

    String getTradeNo(Object o);

    String getPayCertificate(Object o);

    String getExtraInfo(Object o);

    boolean isUserCancel(Object o);

    void printBitmap(byte[] bitmap);    //打印bitmap

    void printText(String text);    //文字居左

    void printText(String text, boolean highLight); //文字居左，可大字体

    void printRight(String text);   //文字居右

    void printRight(String text, boolean highLight);    //文字居右，可大字体

    void printCenter(String text);  //文字居中

    void printCenter(String text, boolean highLight);   //文字居中，可大字体

    void printText(String left, String right);  //文字分别居左居右

    void printBoldText(String left, String right);  //文字分别居左居右，粗体

    void printText(String left, String right, boolean highLight);   //文字分别居左居右，可大字体

    void printDivide();     //打印分隔线

    void printEnd();    //打印终止

    void speech(String text);   //播放语音

    String getMagneticReaderInfo(); //读取磁条信息
}
