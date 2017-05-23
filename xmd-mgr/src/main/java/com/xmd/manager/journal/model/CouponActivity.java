package com.xmd.manager.journal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-11-18.
 */

public class CouponActivity {
    protected String mCategory;
    protected String mName;
    protected List<Item> mData;

    public CouponActivity() {
        mData = new ArrayList<>();
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<Item> getData() {
        return mData;
    }

    public static class Item {
        protected String mTitle;
        protected String mValue;

        public Item(String title, String value) {
            mTitle = title;
            mValue = value;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            this.mTitle = title;
        }

        public String getValue() {
            return mValue;
        }

        public void setValue(String value) {
            this.mValue = value;
        }
    }
}
