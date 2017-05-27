package com.xmd.manager;

import com.xmd.manager.beans.AuthData;
import com.xmd.manager.beans.ClubInfo;

import java.io.File;
import java.util.List;

/**
 * Created by sdcm on 15-10-27.
 */
public class ClubData {

    private ClubData() {
    }

    private ClubInfo mClubInfo;

    private String mClubImageLocalPath;

    private int mMsgCount;

    private List<AuthData> mAuthList;

    private static class AccountDataHolder {
        private static ClubData sInstance = new ClubData();
    }

    public static ClubData getInstance() {
        return AccountDataHolder.sInstance;
    }

    public void setClubInfo(ClubInfo clubInfo) {
        if (clubInfo != null) {
            mClubInfo = clubInfo;
            mClubImageLocalPath = AppConfig.getAvatarFolder() + File.separator + clubInfo.clubId;
        }
    }

    public ClubInfo getClubInfo() {
        return mClubInfo;
    }

    public String getClubImageLocalPath() {
        return mClubImageLocalPath;
    }

    public int getEmchatMsgCount() {
        return mMsgCount;
    }

    public void addEmchatMsgCount(int count) {
        mMsgCount += count;
    }

    public void clearEmchatMsgCount() {
        mMsgCount = 0;
    }

    public void setAuthData(List<AuthData> authList) {
        mAuthList = authList;
    }

    public List<AuthData> getAuthDataList() {
        return mAuthList;
    }
}