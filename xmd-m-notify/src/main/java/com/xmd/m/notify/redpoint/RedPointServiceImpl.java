package com.xmd.m.notify.redpoint;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mo on 17-6-29.
 * 小红点管理
 */

public class RedPointServiceImpl implements RedPointService {

    private static RedPointServiceImpl instance = new RedPointServiceImpl();

    private RedPointServiceImpl() {
        dataViewMap = new HashMap<>();
        dataValueMap = new HashMap<>();
    }

    public static RedPointService getInstance() {
        return instance;
    }

    private Map<String, List<TextView>> dataViewMap;
    private Map<String, Integer> dataValueMap;

    @Override
    public void bind(String id, TextView view, int showType) {
        List<TextView> list = dataViewMap.get(id);
        if (list == null) {
            list = new ArrayList<>();
            dataViewMap.put(id, list);
        }
        if (!list.contains(view)) {
            view.setTag(showType);
            list.add(view);
        }
        set(view, getValue(id));
    }

    @Override
    public void unBind(String id, TextView view) {
        List<TextView> list;
        list = dataViewMap.get(id);
        if (list != null) {
            list.remove(view);
        }
    }

    @Override
    public void inc(String id) {
        set(id, getValue(id) + 1);
    }

    @Override
    public void set(String id, int count) {
        dataValueMap.put(id, count);
        List<TextView> list = dataViewMap.get(id);
        if (list != null) {
            for (TextView textView : dataViewMap.get(id)) {
                set(textView, count);
            }
        }
    }

    @Override
    public void clear(String id) {
        set(id, 0);
    }

    private void set(TextView textView, int count) {
        textView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        if (count > 0) {
            Integer style = (Integer) textView.getTag();
            if (style == RedPointService.SHOW_TYPE_DIGITAL) {
                textView.setText(count > 99 ? "99+" : String.valueOf(count));
            }
        }
    }

    private int getValue(String id) {
        Integer value = dataValueMap.get(id);
        return value == null ? 0 : value;
    }
}
