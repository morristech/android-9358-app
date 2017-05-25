package com.xmd.app.appointment.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyangya on 17-5-24.
 * 技师信息
 */

public class Technician implements Serializable {
    private String id;
    private String name;
    private String status;//free-空闲 ， buzy-忙
    private String phoneNum;
    private String gender;
    private String avatarUrl;
    private String description;
    private int commentCount;//技师评论数

    private String inviteCode;//邀请码
    private String serialNo;//技师编号
    private int star;
    private List<TechTag> techTags;//技师标签
    private String[] impressions;//印象标签
    private int viewAlbumType = 1;//是否开放相册 1 所有人可见；2 vip用户

    public static class TechTag {
        private String technicianId;
        private String tagName;
        private String tagId;
        private String tagType;
    }
}
