package com.xmd.contact;

import android.text.TextUtils;

import com.xmd.contact.bean.TagBean;
import com.xmd.contact.bean.TagListBean;
import com.xmd.contact.bean.TreatedTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-8-9.
 */

public class TagManagerHelp {

    private List<TagListBean> mTagList;
    private List<TagBean> mTagNameType; //用户标签
    private List<TagBean> mUserGroupType;//自定义分组
    private List<TagBean> mCustomerLevelType;//客户等级
    private List<TagBean> mCustomerType;//用户类型
    private List<TagBean> mSerialNoType;//技师编号
    private List<TreatedTagList> mTreatedTagList; //整理后的标签列表

    private static final TagManagerHelp tagHelp = new TagManagerHelp();

    public static TagManagerHelp getInstance() {
        return tagHelp;
    }

    private TagManagerHelp() {
        mTagList = new ArrayList<>();
        mTagNameType = new ArrayList<>();
        mUserGroupType = new ArrayList<>();
        mCustomerLevelType = new ArrayList<>();
        mCustomerType = new ArrayList<>();
        mSerialNoType = new ArrayList<>();
        mTreatedTagList = new ArrayList<>();
    }

    public void setData(List<TagListBean> tagList) {
        this.mTagList = tagList;
        analysisData();
    }

    private void analysisData() {
        if (mTagList == null || mTagList.size() == 0) {
            return;
        }
        mTagNameType.clear();
        mUserGroupType.clear();
        mCustomerLevelType.clear();
        mCustomerType.clear();
        mSerialNoType.clear();
        for (int i = 0; i < mTagList.size(); i++) {
            if (mTagList.get(i).type.equals("tagName") && mTagList.get(i).list.size() > 0) {
                for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                    mTagNameType.add(new TagBean(mTagList.get(i).title, mTagList.get(i).list.get(j), mTagList.get(i).type, false));
                }
                mTagNameType.add(0, new TagBean(mTagList.get(i).title, "不限", mTagList.get(i).type, true));

            }
            if (mTagList.get(i).type.equals("userGroup") && mTagList.get(i).list.size() > 0) {
                mUserGroupType.add(new TagBean(mTagList.get(i).title, "不限", mTagList.get(i).type, true));
                mUserGroupType.add(new TagBean(mTagList.get(i).title, "无分组", mTagList.get(i).type, false));
                for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                    mUserGroupType.add(new TagBean(mTagList.get(i).title, mTagList.get(i).list.get(j), mTagList.get(i).type, false));
                }

            }
            if (mTagList.get(i).type.equals("customerLevel") && mTagList.get(i).list.size() > 0) {
                for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                    mCustomerLevelType.add(new TagBean(mTagList.get(i).title, mTagList.get(i).list.get(j), mTagList.get(i).type, false));
                }
                mCustomerLevelType.add(0, new TagBean(mTagList.get(i).title, "不限", mTagList.get(i).type, true));

            }
            if (mTagList.get(i).type.equals("customerType") && mTagList.get(i).list.size() > 0) {
                mCustomerType.add(new TagBean(mTagList.get(i).title, "不限", mTagList.get(i).type, true));
                for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                    if (mTagList.get(i).list.get(j).equals("weixin")) {
                        mCustomerType.add(new TagBean(mTagList.get(i).title, "微信", mTagList.get(i).type, false));
                    }
                    if (mTagList.get(i).list.get(j).equals("alipay")) {
                        mCustomerType.add(new TagBean(mTagList.get(i).title, "支付宝", mTagList.get(i).type, false));
                    }
                    if (mTagList.get(i).list.get(j).equals("user")) {
                        mCustomerType.add(new TagBean(mTagList.get(i).title, "手机", mTagList.get(i).type, false));
                    }
                    if (mTagList.get(i).list.get(j).equals("tech_add")) {
                        mCustomerType.add(new TagBean(mTagList.get(i).title, "通讯录", mTagList.get(i).type, false));
                    }
                }


            }
            if (mTagList.get(i).type.equals("serialNo") && mTagList.get(i).list.size() > 0) {
                mSerialNoType.add(new TagBean(mTagList.get(i).title, "不限", mTagList.get(i).type, true));
                mSerialNoType.add(new TagBean(mTagList.get(i).title, "未归属", mTagList.get(i).type, false));
                for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                    if (!TextUtils.isEmpty(mTagList.get(i).list.get(j))) {
                        mSerialNoType.add(new TagBean(mTagList.get(i).title, mTagList.get(i).list.get(j), mTagList.get(i).type, false));
                    }
                }
            }
        }
    }

    private List<TagBean> getTagNameList() {
        return mTagNameType;
    }

    private List<TagBean> getUserGroupList() {
        return mUserGroupType;
    }

    private List<TagBean> getCustomerLevelList() {
        return mCustomerLevelType;
    }

    private List<TagBean> getCustomerList() {
        return mCustomerType;
    }

    private List<TagBean> getSerialNoList() {
        return mSerialNoType;
    }

    public List<TreatedTagList> getTreatedTagList() {
        mTreatedTagList.clear();
        if (mTagNameType.size() > 0) {
            mTreatedTagList.add(new TreatedTagList(mTagNameType.get(0).tagTitle, mTagNameType.get(0).tagType, mTagNameType, "1"));
        }
        if (mUserGroupType.size() > 0) {
            mTreatedTagList.add(new TreatedTagList(mUserGroupType.get(0).tagTitle, mUserGroupType.get(0).tagType, mUserGroupType, "1"));
        }
        if (mCustomerLevelType.size() > 0) {
            mTreatedTagList.add(new TreatedTagList(mCustomerLevelType.get(0).tagTitle, mCustomerLevelType.get(0).tagType, mCustomerLevelType, "1"));
        }
        if (mCustomerType.size() > 0) {
            mTreatedTagList.add(new TreatedTagList(mCustomerType.get(0).tagTitle, mCustomerType.get(0).tagType, mCustomerType, "1"));
        }
        if (mSerialNoType.size() > 0) {
            mTreatedTagList.add(new TreatedTagList(mSerialNoType.get(0).tagTitle, mSerialNoType.get(0).tagType, mSerialNoType, "1"));
        }
        return mTreatedTagList;
    }


}
