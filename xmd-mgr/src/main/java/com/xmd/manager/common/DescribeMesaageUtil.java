package com.xmd.manager.common;

import java.util.List;

/**
 * Created by sdcm on 17-3-8.
 */

public class DescribeMesaageUtil {

    public static String getTimePeriodDes(String useTimePeriod) {
        if (useTimePeriod.equals("周一，周二，周三，周四，周五，周六，周日")) {
            return "不限";
        } else if (useTimePeriod.contains("周一，周二，周三，周四，周五，周六，周日") && useTimePeriod.contains("00")) {
            return useTimePeriod.replace("周一，周二，周三，周四，周五，周六，周日", "每天 ");
        } else {
            return useTimePeriod;

        }
    }

    public static String getLimitedItems(List<String> limitItem) {
        if (limitItem == null) {
            return "";
        }
        String limitDes = "";
        limitDes = Utils.listToString(limitItem);
        return limitDes;
    }


}
