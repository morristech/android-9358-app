package com.xmd.appointment.beans;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.appointment.R;

import java.io.Serializable;

/**
 * Created by heyangya on 17-5-27.
 * 服务项目
 */

public class ServiceItem implements Serializable {
    private String categoryId;
    private String categoryName;
    private String clubId;
    private String description;
    private String duration; //服务时长
    private String durationPlus; //加时时长
    private String durationUnit; //服务时长单位
    private String durationUnitPlus; //加时时长单位

    private String id;
    private String imageUrl;
    private String itemCode;
    private String name;
    private String price; //项目价格 单位为元
    private String pricePlus; //项目加时价格 单位为元


    public ObservableBoolean viewSelected = new ObservableBoolean();

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext()).load(url).placeholder(R.drawable.img_default_service).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.img_default_service);
        }
    }



    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationPlus() {
        return durationPlus;
    }

    public void setDurationPlus(String durationPlus) {
        this.durationPlus = durationPlus;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getDurationUnitPlus() {
        return durationUnitPlus;
    }

    public void setDurationUnitPlus(String durationUnitPlus) {
        this.durationUnitPlus = durationUnitPlus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPricePlus() {
        return pricePlus;
    }

    public void setPricePlus(String pricePlus) {
        this.pricePlus = pricePlus;
    }

    @Override
    public String toString() {
        return "ServiceItem{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", clubId='" + clubId + '\'' +
                ", description='" + description + '\'' +
                ", duration='" + duration + '\'' +
                ", durationPlus='" + durationPlus + '\'' +
                ", durationUnit='" + durationUnit + '\'' +
                ", durationUnitPlus='" + durationUnitPlus + '\'' +
                ", id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", pricePlus='" + pricePlus + '\'' +
                ", viewSelected=" + viewSelected +
                '}';
    }
}
