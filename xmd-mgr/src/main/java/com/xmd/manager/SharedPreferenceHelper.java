package com.xmd.manager;

import android.app.Activity;
import android.content.SharedPreferences;

import com.xmd.m.network.OkHttpUtil;
import com.xmd.m.network.XmdNetwork;
import com.xmd.manager.service.response.LoginResult;

/**
 * Created by sdcm on 15-10-23.
 */
public class SharedPreferenceHelper {

    private static final String SETTING_PREFERENCE = "setting_preference";

    private static SharedPreferences mSettingPreference;

    private static final String KEY_DEV_MODE = "e9fbb7c44116f423bbf68ce03f8d1096"; //开发者模式(开发+测试）

    private static final String KEY_USER_ACCOUNT = "4E6EF539AAF119D82AC4C2BC84FBA21F";//userAccount
    private static final String KEY_USER_TOKEN = "CE4A0B029C785BFAA2B398C06E1D94C0"; //userToken
    private static final String KEY_USER_NAME = "435E0648D634175C46BD40AC366545A8";//userName
    private static final String KEY_USER_ROLE = "646305446E7C2B761FB99334F52870DC";//userRole
    private static final String KEY_MULTI_CLUB_TOKEN = "E88C21638D564FB55DE67C22E30E9CFF"; //multiClubToken
    private static final String KEY_SERVER_HOST = "1867ABFF59547D665AA22BDC2AB31BBD";//serverHost
    private static final String KEY_USER_ID = "8E44F0089B076E18A718EB9CA3D94674"; //userId
    private static final String KEY_EMCHAT_ID = "435E0648D634175C46BD40AC366545A1";//emchatId
    private static final String KEY_EMCHAT_PSW = "7660E9BC2D136134F0FBC2856ABFDDAD";//emchatPassword
    private static final String KEY_USER_AVATAR = "435E0648D634175C46BD40AC366545A2";//avatar
    private static final String KEY_USER_INVITE_CODE = "60A4A46A42B285EE17DB88FDE33D23DE";//userInviteCode
    private static final String KEY_UPDATE_SERVER = "EB8329FF8AAC026D206E3C0A811D0D96";//updateServer
    private static final String KEY_CURRENT_CLUB_NAME = "EB8329FF8AAC026A306E3C0A811D0D96";//updateServer
    private static final String KEY_CURRENT_CLUB_CreateTime = "EB8329FF8AAC03A306E3C0A811D0D96";//createTime
    private static final String KEY_CLUB_ID = "EB832AF8AAC026D20cE3C0A811D0D96";

    private static final String KEY_CLUB_NATIVE_SWITCH = "5155259d37897c45f345d91f0b94c9a6";
    /**
     * Last time to check the upgrade automatically
     */
    private static final String KEY_LAST_AUTO_CHECK_UPGRADE = "85A570AE7CC0F2E82A156E9C61C9E493";//lastAutoCheckUpgrade
    private static final String KEY_LAST_UPLOAD_STAT = "07208C27204A99B2112E6868030C55DC";//lastUploadStat

    private static final String KEY_CLIENT_ID = "F3FFE8E1F804F2074AA5CC55233673B8";//clientId

    private static final String KEY_IS_WARNNING_VIDEO_RECORD = "key_is_warnning_video_record";

    public static final String KEY_LAST_VIEW_CUSTOMER_COUNT = "3cb666ab6be8ddc7d5c7da071e6aa2f8";
    public static final String KEY_LAST_VIEW_FAST_PAY_VALUE = "b39e3160bff4be0862a18d062d7abbfe";

    public static void initialize() {
        if (mSettingPreference == null) {
            mSettingPreference = ManagerApplication.getAppContext().getSharedPreferences(SETTING_PREFERENCE, Activity.MODE_PRIVATE);
            // In 2.3.0, doesn't encrypt the keys
            if (AppConfig.getAppVersionName().compareTo("2.3.0") > 0) {
                mSettingPreference.edit().remove("userAccount")
                        .remove("userToken")
                        .remove("userName")
                        .remove("serverHost")
                        .remove("lastAutoCheckUpgrade").apply();
            }
        }
    }

    public static void setUserAccount(String userAccount) {
        mSettingPreference.edit().putString(KEY_USER_ACCOUNT, userAccount).apply();
    }

    /**
     * Get the last login user account
     *
     * @return
     */
    public static String getUserAccount() {
        return mSettingPreference.getString(KEY_USER_ACCOUNT, "");
    }

    public static void setUserToken(String userToken) {
        OkHttpUtil.getInstance().setCommonHeader("token", userToken);
        mSettingPreference.edit().putString(KEY_USER_TOKEN, userToken).apply();
    }

    public static void saveUser(LoginResult loginResult) {
        setUserToken(loginResult.token);
        setUserName(loginResult.name);
        setUserId(loginResult.userId);
        setEmchatId(loginResult.emchatId);
        setEmchatPassword(loginResult.emchatPassword);
        setUserAvatar(loginResult.avatarUrl);
        setUserInviteCode(loginResult.inviteCode);
        setUserRole(loginResult.roles);
    }

    public static void clearUserInfo() {
        setUserToken("");
        setUserName("");
        setUserId("");
        setUserAvatar("");
        setEmchatId("");
        setUserInviteCode("");
        setUserRole("");
        setMultiClubToken("");
        SharedPreferenceHelper.setCurrentClubName("");
        SharedPreferenceHelper.setClubNativeSwitch(false);  //帐号退出后内网开关默认设置为false
    }

    public static void setDevelopMode(boolean debugMode) {
        mSettingPreference.edit().putBoolean(KEY_DEV_MODE, debugMode).apply();
    }

    public static boolean isDevelopMode() {
        return mSettingPreference.getBoolean(KEY_DEV_MODE, false);
    }

    /**
     * get the userToken, it will be empty when user logs out
     *
     * @return
     */
    public static String getUserToken() {
        String token = mSettingPreference.getString(KEY_USER_TOKEN, "");
        OkHttpUtil.getInstance().setCommonHeader("token", token);
        return token;
    }

    public static void setUserName(String username) {
        mSettingPreference.edit().putString(KEY_USER_NAME, username).apply();
    }

    public static String getUserName() {
        return mSettingPreference.getString(KEY_USER_NAME, "");
    }

    public static void setCurrentClubName(String clubName) {
        mSettingPreference.edit().putString(KEY_CURRENT_CLUB_NAME, clubName).apply();
    }

    public static String getCurrentClubName() {
        return mSettingPreference.getString(KEY_CURRENT_CLUB_NAME, "");
    }

    public static void setCurrentClubCreateTime(String clubCreateTime) {
        mSettingPreference.edit().putString(KEY_CURRENT_CLUB_CreateTime, clubCreateTime).apply();
    }

    public static String getCurrentClubCreateTime() {
        return mSettingPreference.getString(KEY_CURRENT_CLUB_CreateTime, "");
    }

    public static void setUserRole(String userRole) {
        mSettingPreference.edit().putString(KEY_USER_ROLE, userRole).apply();
    }

    public static String getUserRole() {
        return mSettingPreference.getString(KEY_USER_ROLE, "");
    }

    public static void setMultiClubToken(String token) {
        mSettingPreference.edit().putString(KEY_MULTI_CLUB_TOKEN, token).apply();
    }

    public static String getMultiClubToken() {
        String token = mSettingPreference.getString(KEY_MULTI_CLUB_TOKEN, "");
        OkHttpUtil.getInstance().setCommonHeader("token", token);
        return token;
    }

    public static void setUserId(String userId) {
        mSettingPreference.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static String getUserId() {
        return mSettingPreference.getString(KEY_USER_ID, "");
    }

    public static void setServerHost(String serverHost) {
        XmdNetwork.getInstance().changeServer(serverHost);
        mSettingPreference.edit().putString(KEY_SERVER_HOST, serverHost).apply();
    }

    /**
     * get the server host
     *
     * @return
     */
    public static String getServerHost() {
//        return "http://192.168.2.73:8080";
//                return "http://192.168.1.100:9880";
        return mSettingPreference.getString(KEY_SERVER_HOST, Constant.DEFAULT_SERVER_HOST);
    }

    public static void setLastAutoCheckUpgrade(long mill) {
        mSettingPreference.edit().putLong(KEY_LAST_AUTO_CHECK_UPGRADE, mill).apply();
    }

    /**
     * Get the last time when auto upgrade checking was invoked
     *
     * @return
     */
    public static long getLastAutoCheckUprage() {
        return mSettingPreference.getLong(KEY_LAST_AUTO_CHECK_UPGRADE, 0);
    }

    public static void setLastUploadStatDate(long lastDate) {
        mSettingPreference.edit().putLong(KEY_LAST_UPLOAD_STAT, lastDate).apply();
    }

    public static long getLastUploadStatDate() {
        return mSettingPreference.getLong(KEY_LAST_UPLOAD_STAT, 0);
    }

    public static void setClientId(String clientId) {
        mSettingPreference.edit().putString(KEY_CLIENT_ID, clientId).apply();
    }

    public static String getClientId() {
        return mSettingPreference.getString(KEY_CLIENT_ID, AppConfig.sClientId);
    }

    public static String getEmchatId() {
        return mSettingPreference.getString(KEY_EMCHAT_ID, "");
    }

    public static void setEmchatId(String emchatId) {
        mSettingPreference.edit().putString(KEY_EMCHAT_ID, emchatId).commit();
    }

    public static String getEmchatPassword() {
        return mSettingPreference.getString(KEY_EMCHAT_PSW, "");
    }

    public static void setEmchatPassword(String emchatPassword) {
        mSettingPreference.edit().putString(KEY_EMCHAT_PSW, emchatPassword).commit();
    }

    public static String getUserInviteCode() {
        return mSettingPreference.getString(KEY_USER_INVITE_CODE, "");
    }

    public static void setUserInviteCode(String userInviteCode) {
        mSettingPreference.edit().putString(KEY_USER_INVITE_CODE, userInviteCode).apply();
    }

    public static void setUserAvatar(String avatar) {
        mSettingPreference.edit().putString(KEY_USER_AVATAR, avatar).apply();
    }

    public static String getUserAvatar() {
        return mSettingPreference.getString(KEY_USER_AVATAR, "");
    }

    public static void setChatUserType(String chatId, String type) {
        mSettingPreference.edit().putString(chatId, type).apply();
    }

    public static String getChatUserType(String chatId) {
        return mSettingPreference.getString(chatId, "");
    }

    public static void setUpdateServer(String server) {
        mSettingPreference.edit().putString(KEY_UPDATE_SERVER, server).apply();
    }

    public static String getUpdateServer() {
        return mSettingPreference.getString(KEY_UPDATE_SERVER, BuildConfig.DEFAULT_UPDATE_SERVER);
    }

    public static boolean getIsWarnningVideoRecord() {
        return mSettingPreference.getBoolean(KEY_IS_WARNNING_VIDEO_RECORD, false);
    }

    public static void setIsWarnningVideoRecord(boolean value) {
        mSettingPreference.edit().putBoolean(KEY_IS_WARNNING_VIDEO_RECORD, value).apply();
    }

    public static int getLastViewCustomerCount() {
        return mSettingPreference.getInt(KEY_LAST_VIEW_CUSTOMER_COUNT, -1);
    }

    public static void setLastViewCustomerCount(int count) {
        mSettingPreference.edit().putInt(KEY_LAST_VIEW_CUSTOMER_COUNT, count).apply();
    }

    public static int getListViewFastPayValue() {
        return mSettingPreference.getInt(KEY_LAST_VIEW_FAST_PAY_VALUE, -1);
    }

    public static void setLastViewFastPayValue(int count) {
        mSettingPreference.edit().putInt(KEY_LAST_VIEW_FAST_PAY_VALUE, count).apply();
    }

    public static void setClubId(String clubId) {
        mSettingPreference.edit().putString(KEY_CLUB_ID, clubId).apply();
    }

    public static String getClubId() {
        return mSettingPreference.getString(KEY_CLUB_ID, "");
    }

    public static void setClubNativeSwitch(boolean sw) {
        mSettingPreference.edit().putBoolean(KEY_CLUB_NATIVE_SWITCH, sw).apply();
    }

    public static boolean getClubNativeSwitch() {
        return mSettingPreference.getBoolean(KEY_CLUB_NATIVE_SWITCH, false);
    }
}
