package com.xmd.app;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by heyangya on 17-6-8.
 * 避免重复注册崩溃的问题
 */

public class EventBusSafeRegister {
    private static Set<Object> mObjectSet = new HashSet<>();

    public static void register(Object o) {
        if (!mObjectSet.contains(o)) {
            mObjectSet.add(o);
            EventBus.getDefault().register(o);
        }
    }

    public static void unregister(Object o) {
        if (mObjectSet.contains(o)) {
            mObjectSet.remove(o);
            EventBus.getDefault().unregister(o);
        }
    }
}
