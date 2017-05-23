package com.xmd.manager.journal.model;

import android.text.TextUtils;

import com.xmd.manager.journal.widget.IContentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalContent implements Cloneable {
    private String mTitle;
    private int mSubContentCount;
    private JournalContentType mType;
    private List<JournalItemBase> mDataList;

    private IContentView mView;

    public JournalContent(JournalContentType type, int subContentCount) {
        mType = type;
        mSubContentCount = subContentCount;
        mDataList = new ArrayList<>();
    }

    public JournalContent(JournalContent journalContent) {
        this(journalContent.getType(), journalContent.getSubContentCount());
    }

    //获取内容中子项的数据个数
    public int getDataSize() {
        return mDataList.size();
    }

    //根据索引获取数据
    public JournalItemBase getData(int index) {
        return mDataList.get(index);
    }

    //根据索引删除数据
    public void removeData(int index) {
        mDataList.remove(index);
    }

    public void removeData(JournalItemBase data) {
        mDataList.remove(data);
    }

    //清除所有数据
    public void clearData() {
        mDataList.clear();
        if (getViewHolder() != null) {
            getViewHolder().clearData();
        }
    }

    public void addData(JournalItemBase object) {
        if (object != null) {
            mDataList.add(object);
        }
    }

    public void addData(int index, JournalItemBase object) {
        mDataList.add(index, object);
    }

    public JournalContentType getType() {
        return mType;
    }

    public int getSubContentCount() {
        return mSubContentCount;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public IContentView getViewHolder() {
        return mView;
    }

    public void setView(IContentView view) {
        this.mView = view;
    }


    //数据有没有准备好，如果准备好，返回"true"，否则返回没有准备好的原因
    public String dataIsReady() {
        if (mDataList.size() > 0) {
            //除了相册，其他内容一定要填满或者全部不填才行
            if (!mType.getKey().equals(JournalContentType.CONTENT_KEY_PHOTO_ALBUM) && getDataSize() < getSubContentCount()) {
                return mType.getName() + "的内容还没有填写完整!";
            }
            //查检每一项的内容是否准备好
            String ready;
            for (int i = 0; i < mDataList.size(); i++) {
                ready = getData(i).isDataReady();
                if (!TextUtils.equals(ready, "true")) {
                    return mType.getName() + ":" + ready;
                }
            }
        }

        return "true";
    }

    public String getStringData() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mDataList.size(); i++) {
            String data = getData(i).contentToString();
            if (!TextUtils.isEmpty(data)) {
                builder.append(data).append(",");
            }
        }
        String result = builder.toString();
        if (!TextUtils.isEmpty(result)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public void setStringData(String data) {
        if (getType().getKey().equals(JournalContentType.CONTENT_KEY_IMAGE_ARTICLE)) {
            addData(JournalItemFactory.create(getType(), data));
        } else {
            if (data.contains(",")) {
                String items[] = data.split(",");
                for (int i = 0; i < items.length; i++) {
                    addData(i, JournalItemFactory.create(getType(), items[i]));
                }
            } else {
                addData(JournalItemFactory.create(getType(), data));
            }
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (mTitle != null) {
            hashCode += mTitle.hashCode();
        }
        hashCode += mSubContentCount;
        if (mType != null) {
            hashCode += mType.hashCode();
        }
        hashCode += mDataList.hashCode();
        return hashCode;
    }

    @Override
    public JournalContent clone() throws CloneNotSupportedException {
        JournalContent copy = (JournalContent) super.clone();
        copy.mDataList = new ArrayList<>();
        for (JournalItemBase contentBase : mDataList) {
            copy.mDataList.add(contentBase.clone());
        }
        return copy;
    }
}
