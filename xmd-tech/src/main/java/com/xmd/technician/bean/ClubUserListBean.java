package com.xmd.technician.bean;

/**
 * Created by Lhj on 17-6-5.
 */

public class ClubUserListBean {
    /**
     * id : 601634063966539776
     * name : 刘德华
     * telephone :
     * techNo : null
     * avatar : 157254
     * userType : manager
     * roles : club_manager
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/0D/oIYBAFhPo3aAdbW7AApWp8RJy5g827.png?st=sYpAuc_Mo5KR9m2BIm5yCA&e=1498462675
     */

    public String id;
    public String name;
    public String telephone;
    public String techNo;
    public String avatar;
    public String userType;
    public String roles;
    public String avatarUrl;

    public ClubUserListBean(String id, String name, String techNo, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.techNo = techNo;
        this.avatarUrl = avatarUrl;

    }
}
