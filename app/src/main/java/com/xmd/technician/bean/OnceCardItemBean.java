package com.xmd.technician.bean;

/**
 * Created by Lhj on 2017/2/20.
 */

public class OnceCardItemBean {
    public int id;
    public String name;
    public String imageUrl;
    public boolean isPreferential;
    public String comboDescription;
    public String techRoyalty;
    public String price;
    public String shareUrl;
    public String shareDescription;

    public OnceCardItemBean(int id,String name,String imageUrl, boolean isPreferential, String comboDescription,String shareDescription,String techRoyalty, String price,String shareUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isPreferential = isPreferential;
        this.comboDescription = comboDescription;
        this.techRoyalty = techRoyalty;
        this.price = price;
        this.shareUrl = shareUrl;
        this.shareDescription = shareDescription;
    }


}
