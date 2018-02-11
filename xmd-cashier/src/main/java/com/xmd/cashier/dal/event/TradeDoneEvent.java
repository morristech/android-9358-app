package com.xmd.cashier.dal.event;

/**
 * Created by zr on 18-2-1.
 */

// 交易支付完成
public class TradeDoneEvent {
    public int type;

    public TradeDoneEvent(int type) {
        this.type = type;
    }
}
