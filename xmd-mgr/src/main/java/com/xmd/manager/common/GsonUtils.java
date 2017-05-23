package com.xmd.manager.common;

/**
 * Created by sdcm on 15-10-23.
 */

import com.google.gson.Gson;

public class GsonUtils {

    private static Gson gson = new Gson();

    /**
     * 将对象转为Json格式字符串，如果 obj=null 返回"null"
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 将json 格式数据转为指定类型的对象
     */
    public static <T extends Object> T toBean(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
