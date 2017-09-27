package com.xmd.app.user;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.CommonNetService;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.SpConstants;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    private User currentUser;
    private String currentToken;

    private DaoSession daoSession;
    private ConcurrentHashMap<String, User> chatIdMap;
    private ConcurrentHashMap<String, User> userIdMap;

    @Override
    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "xmd-db-user");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        chatIdMap = new ConcurrentHashMap<>();
        userIdMap = new ConcurrentHashMap<>();

        EventBusSafeRegister.register(this);
    }

    @Override
    public List<User> getAllUsers() {
        return daoSession.getUserDao().loadAll();
    }

    @Override
    public User getUserByChatId(String chatId) {
        User user = chatIdMap.get(chatId);
        if (user == null) {
            user = daoSession.getUserDao().queryBuilder()
                    .where(UserDao.Properties.ChatId.eq(chatId))
                    .unique();
            if (user != null) {
                chatIdMap.put(chatId, user);
            }
        }
        return user;
    }

    @Override
    public User getUserByUserId(String userId) {
        User user = userIdMap.get(userId);
        if (user == null) {
            user = daoSession.getUserDao().queryBuilder()
                    .where(UserDao.Properties.UserId.eq(userId))
                    .unique();
            if (user != null) {
                userIdMap.put(userId, user);
            }
        }
        return user;
    }

    @Override
    public synchronized void saveUser(User user) {
        if (user == null || TextUtils.isEmpty(user.getId())) {
            XLogger.e("无法保存用户：" + user);
            return;
        }
        User old = getUserByUserId(user.getUserId());

        if (old == null || !user.equals(old)) {
            //保存信息
            if (old != null) {
                user = old.update(user);
                daoSession.getUserDao().update(user);
            } else {
                daoSession.getUserDao().insert(user);
            }
            XLogger.i(TAG, "save user: " + user);
            userIdMap.put(user.getId(), user);
            chatIdMap.put(user.getChatId(), user);
        }
    }

    @Override
    public synchronized User getCurrentUser() {
        if (currentUser == null) {
            String id = XmdApp.getInstance().getSp().getString(SpConstants.KEY_CURRENT_USER_ID, null);
            if (id != null) {
                currentUser = getUserByUserId(id);
            }
        }
        return currentUser;
    }

    @Override
    public synchronized void saveCurrentUser(User user) {
        if (user != null) {
            saveUser(user);
            currentUser = getUserByUserId(user.getId());
            XmdApp.getInstance().getSp().edit().putString(SpConstants.KEY_CURRENT_USER_ID, user.getId()).apply();
        } else {
            if (currentUser == null) {
                return;
            }
            daoSession.getUserDao().delete(currentUser);
            XmdApp.getInstance().getSp().edit().remove(SpConstants.KEY_CURRENT_USER_ID).apply();
            currentUser = null;
        }
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

    @Subscribe(sticky = true, priority = 10)
    public void onLogin(EventLogin eventLogin) {
        XLogger.i(TAG, "===>user login");
        currentUser = eventLogin.getUser();
        currentToken = eventLogin.getToken();
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
