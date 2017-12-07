package com.xmd.inner.bean;

/**
 * Created by Lhj on 17-12-4.
 */

public class NativeCategoryBean {

    /**
     * id : 621629470339506176
     * name : SPA2
     * image : 146442
     * scope : spa
     * imageUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/31/oIYBAFXlKEeABwtOAABizMznBCE460.jpg?st=bhVkXbnPNpPgwDPblRVjpQ&e=1514454084
     */

    public String id;
    public String name;
    public String image;
    public String scope;
    public String imageUrl;
    public boolean isSelected;

    @Override
    public String toString() {
        return "NativeCategoryBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", scope='" + scope + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}
