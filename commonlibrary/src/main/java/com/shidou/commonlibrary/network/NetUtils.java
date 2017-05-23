package com.shidou.commonlibrary.network;

import java.util.Map;

/**
 * Created by heyangya on 16-3-7.
 */
public class NetUtils {

    //map to string
    // eg:  mapToString(map,"=","&")  ===>  k1=v1&k2=v2......
    public static String mapToString(Map<?, ?> map, String t1, String t2) {
        if (map.size() == 0) {
            return null;
        }
        String result = "";
        Object[] keys = map.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            if (i == 0) {
                result = key.toString();
            } else {
                result += t2 + key.toString();
            }
            Object value = map.get(key);
            if (value != null) {
                result += t1 + value.toString();
            } else {
                result += t1 + "";
            }
        }
        return result;
    }


    /**
     * 将IPV4地址从 long类型转换为 String类型
     */
    public static String ip4LongToString(long ipAddress) {
        return ((ipAddress & 0xff) + "." + ((ipAddress >> 8) & 0xff) + "."
                + ((ipAddress >> 16) & 0xff) + "." + ((ipAddress >> 24) & 0xff));
    }

    public static boolean isIpV4Address(String ip) {
        return ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    }
}
