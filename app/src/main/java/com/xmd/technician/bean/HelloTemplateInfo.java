package com.xmd.technician.bean;

/**
 * Created by zr on 17-3-21.
 * 打招呼模版或者内容
 */

public class HelloTemplateInfo extends SelectBean {
    public Integer id;              //模版ID
    public Integer parentId;        //系统模版ID
    public String contentImageId;   //图片ID
    public String contentImageLink; //图片跳转链接
    public String contentImageUrl;  //图片URL
    public String contentText;      //模版文本内容
}
