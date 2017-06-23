package com.shidou.commonlibrary;

/**
 * Created by mo on 17-6-21.
 * 数据转换接口
 */

public interface DataTranslator<F,T> {
    T translate(F data);
}
