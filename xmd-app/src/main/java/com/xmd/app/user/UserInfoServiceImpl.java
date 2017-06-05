package com.xmd.app.user;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息管理
 */

public class UserInfoServiceImpl implements UserInfoService {
    private static final UserInfoServiceImpl ourInstance = new UserInfoServiceImpl();

    public static UserInfoServiceImpl getInstance() {
        return ourInstance;
    }

    private UserInfoServiceImpl() {

    }

    private List<String> mUserIdList;
    private Map<String, User> mChatIdMap;
    private Map<String, User> mUserIdMap;

    private static final String USER_ID_LIST = "user_id_list";

    @Override
    public void init(Context context) {
        try {
            DiskCacheManager.init(context.getFilesDir(), 10 * 1024 * 1024);
        } catch (IOException e) {
            XLogger.e("初始化用户信息缓存失败！");
        }
        mChatIdMap = new HashMap<>();
        mUserIdMap = new HashMap<>();

        mUserIdList = (List<String>) DiskCacheManager.getInstance().get(USER_ID_LIST);
        if (mUserIdList == null) {
            mUserIdList = new ArrayList<>();
        } else {
            Iterator<String> userIdIterator = mUserIdList.iterator();
            while (userIdIterator.hasNext()) {
                String userId = userIdIterator.next();
                User user = (User) DiskCacheManager.getInstance().get(userId);
                if (user == null) {
                    userIdIterator.remove();
                    continue;
                }
                mUserIdMap.put(userId, user);
                if (!TextUtils.isEmpty(user.getChatId())) {
                    mChatIdMap.put(user.getChatId(), user);
                }
            }
        }
    }

    @Override
    public User getUserByChatId(String chatId) {
        return mChatIdMap.get(chatId);
    }

    @Override
    public User getUserByUserId(String userId) {
        return mUserIdMap.get(userId);
    }

    @Override
    public void saveUser(User user) {
        if (user == null || TextUtils.isEmpty(user.getId())) {
            XLogger.e("无法保存用户：" + user);
            return;
        }
        User old = mUserIdMap.get(user.getId());
        if (old == null) {
            old = mChatIdMap.get(user.getChatId());
        }

        if (old == null || !user.equals(old)) {
            //保存信息
            DiskCacheManager.getInstance().put(user.getId(), user);
            if (!mUserIdList.contains(user.getId())) {
                mUserIdList.add(user.getId());
                DiskCacheManager.getInstance().put(USER_ID_LIST, mUserIdList);
            }
        }

        mUserIdMap.put(user.getId(), user);
        if (!TextUtils.isEmpty(user.getChatId())) {
            mChatIdMap.put(user.getChatId(), user);
        }
    }
}
