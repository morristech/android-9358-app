package com.xmd.appointment.beans;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.widget.ImageView;

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

    public ObservableBoolean viewSelected = new ObservableBoolean();

    public static class TechTag {
        private String technicianId;
        private String tagName;
        private String tagId;
        private String tagType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public List<TechTag> getTechTags() {
        return techTags;
    }

    public void setTechTags(List<TechTag> techTags) {
        this.techTags = techTags;
    }

    public String[] getImpressions() {
        return impressions;
    }

    public void setImpressions(String[] impressions) {
        this.impressions = impressions;
    }

    public int getViewAlbumType() {
        return viewAlbumType;
    }

    public void setViewAlbumType(int viewAlbumType) {
        this.viewAlbumType = viewAlbumType;
    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, String url) {

    }
}
