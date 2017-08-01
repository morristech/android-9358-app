package com.xmd.app.user;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.CommonNetService;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息管理
 */

public class UserInfoServiceImpl implements UserInfoService {
    private static final String TAG = "userService";
    private static final UserInfoServiceImpl ourInstance = new UserInfoServiceImpl();

    public static UserInfoServiceImpl getInstance() {
        return ourInstance;
    }

    private UserInfoServiceImpl() {

    }

    private List<String> mUserIdList;
    private Map<String, User> mChatIdMap;
    private Map<String, User> mUserIdMap;

    private static final String USER_CURRENT_USER = "user_current_user";
    private static final String USER_ID_LIST = "user_id_list";

    private User currentUser;
    private String currentToken;

    @Override
    public void init(Context context) {
        if (!DiskCacheManager.isInit()) {
            throw new RuntimeException("dependency DiskCacheManager, but not init");
        }
        mChatIdMap = new HashMap<>();
        mUserIdMap = new HashMap<>();

        currentUser = (User) DiskCacheManager.getInstance().get(USER_CURRENT_USER);
        XLogger.i("get current user=" + currentUser);

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

                XLogger.d(TAG, "find user: " + user);
            }
        }

        EventBusSafeRegister.register(this);
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

        if (old == null || !user.equals(old)) {
            //保存信息
            if (old != null) {
                user = old.update(user);
            }
            XLogger.i(TAG, "save user: " + user);
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

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void saveCurrentUser(User user) {
        DiskCacheManager.getInstance().put(USER_CURRENT_USER, user);
    }

    @Override
    public String getCurrentToken() {
        return currentToken;
    }

    @Override
    public void loadUserInfoByUserIdFromServer(String userId, Callback<User> callback) {
        loadUserInfoFromServer(userId, "user", callback);
    }

    @Override
    public void loadUserInfoByChatIdFromServer(String chatId, Callback<User> callback) {
        loadUserInfoFromServer(chatId, "emchat", callback);
    }

    @Override
    public void loadUserInfoByChatId(String chatId, Callback<User> callback) {
        User user = getUserByChatId(chatId);
        if (user != null) {
            callback.onResponse(user, null);
        } else {
            loadUserInfoByChatIdFromServer(chatId, callback);
        }
    }

    private void loadUserInfoFromServer(String id, String idType, final Callback<User> callback) {
        Observable<BaseBean<User>> observable = XmdNetwork.getInstance()
                .getService(CommonNetService.class)
                .getUserBaseInfo(id, idType, true);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<User>>() {
            @Override
            public void onCallbackSuccess(BaseBean<User> result) {
                saveUser(result.getRespData());
                callback.onResponse(result.getRespData(), null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onResponse(null, e);
            }
        });
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        XLogger.i(TAG, "===>user login");
        currentUser = eventLogin.getUser();
        currentToken = eventLogin.getToken();
        saveUser(currentUser);
        saveCurrentUser(currentUser);
    }

    @Subscribe(sticky = true, priority = -1)
    public void onLogout(EventLogout eventLogout) {
        XLogger.i(TAG, "<===user logout");
        currentUser = null;
        currentToken = null;
        saveCurrentUser(null);
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        XLogger.i(TAG, "<===user expired");
        currentUser = null;
        currentToken = null;
        saveCurrentUser(null);
    }
}
