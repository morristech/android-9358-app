package com.xmd.manager.journal.model;

/**
 * Created by heyangya on 16-11-18.
 * 编辑内容使用适配器模式，这是适配器接口
 */

public abstract class JournalItemBase implements Cloneable {
    //将字符串转换为对像
    public JournalItemBase(String data) {

    }

    //返回字符串格式的内容
    public abstract String contentToString();

    //数据是否准备好，准备好返回"true",否则返回原因
    public String isDataReady() {
        return "true";
    }

    @Override
    public JournalItemBase clone() throws CloneNotSupportedException {
        return (JournalItemBase) super.clone();
    }
}
