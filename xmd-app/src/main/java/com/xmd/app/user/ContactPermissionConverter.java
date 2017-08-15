package com.xmd.app.user;

import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by mo on 17-8-15.
 */

public class ContactPermissionConverter implements PropertyConverter<ContactPermission, String> {
    private Gson gson = new Gson();

    @Override
    public ContactPermission convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return gson.fromJson(databaseValue, ContactPermission.class);
    }

    @Override
    public String convertToDatabaseValue(ContactPermission entityProperty) {
        return entityProperty == null ? null : gson.toJson(entityProperty);
    }
}
