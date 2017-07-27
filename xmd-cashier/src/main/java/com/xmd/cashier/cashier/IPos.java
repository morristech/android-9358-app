package com.xmd.cashier.cashier;

import android.content.Context;

import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;

/**
 * Created by heyangya on 16-9-6.
 */

public interface IPos {

    String getAppCode(); //返回POS类型码

    boolean needCheckUpdate(); //需要检查升级信息

    void init(Context context, Callback<?> callback);// 初始化

    //调用支付之前是否需要选择支付方式
    boolean needChoicePayTypeByCaller();

    //当前处于支付界面，但是由于某些原因支付界面的task被切换到后台，然后打开9358收银台时，需要确保交易结束，这时
    //需要重新将支付task切换前台显示，此函数返回的package就是关联支付task的包名
    String getPackageName();

    void pay(Context context, String tradeNo, int money, int payType, PayCallback<Object> callback);

    int getPayType(Object o);

    String getTradeNo(Object o);

    String getPayCertificate(Object o);

    String getExtraInfo(Object o);

    boolean isUserCancel(Object o);

    void printBitmap(byte[] bitmap);

    // 默认居左
    void printText(String text);

    void printText(String text, boolean highLight);

    // 居右
    void printRight(String text);

    void printRight(String text, boolean highLight);

    // 居中
    void printCenter(String text);

    void printCenter(String text, boolean highLight);

    // 效果:left居左 right居右 huipos未实现
    void printText(String left, String right);

    void printText(String left, String right, boolean highLight);

    // 效果:打印分隔线 huipos空行
    void printDivide();

    void printEnd();

    int GRAVITY_LEFT = 0;
    int GRAVITY_CENTER = 1;
    int GRAVITY_RIGHT = 2;

    // 获取Pos标识:weipos返回en;huipos返回sn;
    String getPosIdentifierNo();

    void textToSound(String text);

    String getMagneticReaderInfo();
}
