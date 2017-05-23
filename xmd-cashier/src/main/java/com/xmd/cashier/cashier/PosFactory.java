package com.xmd.cashier.cashier;

import com.xmd.cashier.pos.PosImpl;

/**
 * Created by heyangya on 16-9-6.
 */

public class PosFactory {
    public static IPos getCurrentCashier() {
        return PosImpl.getInstance();
    }
}
