package com.xmd.appointment.beans;

import java.io.Serializable;

/**
 * Created by heyangya on 17-5-27.
 * 服务类别
 */

public class ServiceCategory implements Serializable {
    private String id;
    private String image;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
