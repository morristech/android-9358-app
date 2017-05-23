package com.xmd.technician.chat;

import com.xmd.technician.bean.CategoryBean;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-5-12.
 */

/**
 * 根据用户类型去展现具体的聊天页面
 */
public class ChatCategoryManager {
    private List<CategoryBean> mCategoryListResults;
    private List<CategoryBean> mCommentMenu;
    private List<CategoryBean> mMoreMenu;
    private static ChatCategoryManager chatManagerInstance = new ChatCategoryManager();

    public static ChatCategoryManager getInstance() {
        return chatManagerInstance;
    }

    public void getChatManagerData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CHAT_CATEGORY_LIST);
    }

    public void setCategoryList(List<CategoryBean> categories) {
        this.mCategoryListResults = categories;
    }

    public void initial() {
        mCategoryListResults = new ArrayList<>();
        mCommentMenu = new ArrayList<>();
        mMoreMenu = new ArrayList<>();
    }

    public List<CategoryBean> getCommentMenu(int toChatUserType) {
        if (null == mCategoryListResults || mCategoryListResults.size() == 0) {
            return new ArrayList<>();
        }
        mCommentMenu.clear();
        switch (toChatUserType) {
            case ChatConstant.CHAT_USER_TYPE_TECH: //技师,楼面
                for (int i = 0; i < mCategoryListResults.size(); i++) {
                    if (mCategoryListResults.get(i).constKey.equals("01")) {
                        mCommentMenu.add(mCategoryListResults.get(i));
                    }
                    if (mCategoryListResults.get(i).constKey.equals("02")) {
                        mCommentMenu.add(mCategoryListResults.get(i));
                    }
                }
                break;
            case ChatConstant.CHAT_USER_TYPE_MANAGER: //管理者
                for (int i = 0; i < mCategoryListResults.size(); i++) {
                    if (mCategoryListResults.get(i).constKey.equals("01")) {
                        mCommentMenu.add(mCategoryListResults.get(i));
                    }
                    if (mCategoryListResults.get(i).constKey.equals("02")) {
                        mCommentMenu.add(mCategoryListResults.get(i));
                    }
                }
                break;
            case ChatConstant.CHAT_USER_TYPE_CUSTOMER: //普通用户
                for (int j = 0; j < mCategoryListResults.size(); j++) {

                        if (mCategoryListResults.get(j).constKey.equals("01")) {
                            mCommentMenu.add(mCategoryListResults.get(j));
                            continue;
                        }
                        if (mCategoryListResults.get(j).constKey.equals("02")) {
                            mCommentMenu.add(mCategoryListResults.get(j));
                            continue;
                        }
                        if (mCategoryListResults.get(j).constKey.equals("03")) {
                            mCommentMenu.add(mCategoryListResults.get(j));
                            continue;
                        }
                        if (mCategoryListResults.get(j).constKey .equals("04")) {
                            mCommentMenu.add(mCategoryListResults.get(j));
                            continue;
                        }

                }

                break;
        }

        return mCommentMenu;
    }

    public List<CategoryBean> getMoreMenu(int userType) {
        if (null == mCategoryListResults || mCategoryListResults.size() == 0) {
            return new ArrayList<>();
        }
        mMoreMenu.clear();
        switch (userType) {
            case 1: //技师,楼面
                for (int i = 0; i < mCategoryListResults.size(); i++) {
                    if (mCategoryListResults.get(i).constKey== "09") {
                        mMoreMenu.add(mCategoryListResults.get(i));
                    }
                }
                break;
            case 2: //管理者

                break;
            case 3: //普通用户
                for (int j = 0; j < mCategoryListResults.size(); j++) {

                    if (mCategoryListResults.get(j).constKey.equals("05")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }
                    if (mCategoryListResults.get(j).constKey.equals("06")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }
                    if (mCategoryListResults.get(j).constKey.equals("07")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }
                    if (mCategoryListResults.get(j).constKey.equals("08")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }
                    if (mCategoryListResults.get(j).constKey.equals("09")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }
                    if (mCategoryListResults.get(j).constKey.equals("10")) {
                        mMoreMenu.add(mCategoryListResults.get(j));
                        continue;
                    }

                }

        }
        return mMoreMenu;
    }
}
