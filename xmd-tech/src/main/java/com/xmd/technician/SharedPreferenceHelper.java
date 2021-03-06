package com.xmd.technician;

import android.app.Activity;
import android.content.SharedPreferences;

import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.m.network.XmdNetwork;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.model.LoginTechnician;

import java.util.Set;

/**
 * Created by sdcm on 15-10-23.
 */
public class SharedPreferenceHelper {

    private static final String SETTING_PREFERENCE = "00DF60D934C0D482F0C950B6D3605F50"; //setting_preference
    private static final String KEY_DEV_MODE = "e9fbb7c44116f423bbf68ce03f8d1096"; //开发者模式(开发+测试）
    private static final String KEY_USER_ACCOUNT = "4E6EF539AAF119D82AC4C2BC84FBA21F";//userAccount
    private static final String KEY_USER_TOKEN = "CE4A0B029C785BFAA2B398C06E1D94C0"; //userToken
    private static final String KEY_USER_NAME = "435E0648D634175C46BD40AC366545A8";//userName
    private static final String KEY_SERVER_HOST = "1867ABFF59547D665AA22BDC2AB31BBD";//serverHost
    private static final String KEY_USER_ID = "8E44F0089B076E18A718EB9CA3D94674"; //userId
    private static final String KEY_EMCHAT_ID = "F211BBAA010D00B863BE64B3F5EFD983";//emchatId
    private static final String KEY_EMCHAT_PASSWORD = "7660E9BC2D136134F0FBC2856ABFDDAD";//emchatPassword
    private static final String KEY_XMD_CHAT_MODEL = "F211BBAA010D20B863BE64B3F5EFD983";//chatModel
    private static final String KEY_USER_AVATAR = "DC03806303F221B804777E64B24B654C";//avatarUrl
    private static final String KEY_SERIAL_NO = "492B6C37356A803DCB43795618DB5DCA";//serialNo
    private static final String KEY_USER_CLUB_ID = "7660E9BC2D136134F0FBC2856A803DC";//clubID
    private static final String KEY_USER_CLUB_NAME = "7660E9BC2D136134F25XD7F56A803DC";//clubName
    private static final String KEY_USER_CLUB_POSITION = "daf34286f22d3e3ab9b95029d8fb98d0";    // add zr clubPosition
    private static final String KEY_CLUB_GAME_TIMEOUT = "7660E9BC2D136134F25XD67d6A803DC";
    private static final String KEY_VERIFICATION_CODE_TIME = "16024c10323c6f642892cc17bac62192";
    private static final String KEY_CLUB_INVITE_CODE = "76a072c99affd6902b8505443f1bf2ac";
    private static final String KEY_INVITE_CODE = "fced902e58a478ca5e535c2986f95cb1";
    private static final String KEY_TECH_NO = "fc9bab349dc493c2a8f6656e1ba66752";
    private static final String KEY_LOGIN_TYPE = "b7ac3a93823c9a27460b8852c11bc1f1";
    private static final String KEY_TECH_DESCRIPTION = "5ceaf3dc66e692657f7e45e889e9b0ef";
    private static final String KEY_TECH_QR_DOWNLOAD_URL = "3625d662fe5f3926011a8b7cacc96da9";
    private static final String KEY_TECH_GENDER = "d70798608c15d7af0f2cd381ea775fa9";
    private static final String KEY_PAY_NOTIFY_HIDE_ID = "456af6df9b184d2b7c78deb88d288942";
    private static final String KEY_PAY_NOTIFY_IS_FIRST_HIDE = "3ce13929dfff8cf8b8592ef34d3bffd9";
    private static final String KEY_TECH_STATUS = "d70798608c15e7af0f4cd381ea775fa9";
    private static final String KEY_PERMISSION = "0b43e8e104bfbdbf3a67587e5154dc0c";
    private static final String KEY_PERMISSION_SYNC_DATE = "5d2de249318aa422e6937cb56b8952b3";
    private static final String KEY_ROLES = "850492d1891afe4a11982b73224b7e40";
    private static final String KEY_CUSTOMER_SERVICE = "fb776cf319f0518d402d9da706264898";
    private static final String KEY_AVATAR_ID = "bdeed5eb7f43a441ecbde1df9539943a";
    /**
     * Last time to check the upgrade automatically
     */
    private static final String KEY_LAST_AUTO_CHECK_UPGRADE = "85A570AE7CC0F2E82A156E9C61C9E493";//lastAutoCheckUpgrade
    private static final String KEY_LAST_UPLOAD_STAT = "07208C27204A99B2112E6868030C55DC";//lastUploadStat
    private static final String KEY_CLIENT_ID = "F3FFE8E1F804F2074AA5CC55233673B8";//clientId
    private static SharedPreferences mSettingPreference;

    public static void initialize() {
        mSettingPreference = TechApplication.getAppContext().getSharedPreferences(SETTING_PREFERENCE, Activity.MODE_PRIVATE);
    }

    public static boolean isDevelopMode() {
        return mSettingPreference.getBoolean(KEY_DEV_MODE, false);
    }

    public static void setDevelopMode(boolean debugMode) {
        mSettingPreference.edit().putBoolean(KEY_DEV_MODE, debugMode).apply();
    }

    /**
     * Get the last login user account
     *
     * @return
     */
    public static String getUserAccount() {
        return mSettingPreference.getString(KEY_USER_ACCOUNT, "");
    }

    public static void setUserAccount(String userAccount) {
        mSettingPreference.edit().putString(KEY_USER_ACCOUNT, userAccount).apply();
    }

    public static void clearUserInfo() {
        setUserToken("");
        setUserName("");
        setUserId("");
        setUserAvatar("");
        setSerialNo("");
        setEmchatId("");
        setEMchatPassword("");
    }

    /**
     * get the userToken, it will be empty when user logs out
     *
     * @return
     */
    public static String getUserToken() {
        return mSettingPreference.getString(KEY_USER_TOKEN, "");
    }

    public static void setUserToken(String userToken) {
        mSettingPreference.edit().putString(KEY_USER_TOKEN, userToken).apply();
    }

    public static String getUserName() {
        return mSettingPreference.getString(KEY_USER_NAME, "");
    }

    public static void setUserName(String username) {
        mSettingPreference.edit().putString(KEY_USER_NAME, username).apply();
    }

    public static String getUserClubId() {
        return mSettingPreference.getString(KEY_USER_CLUB_ID, "");
    }

    public static void setUserClubId(String clubId) {
        mSettingPreference.edit().putString(KEY_USER_CLUB_ID, clubId).apply();
    }

    public static String getUserClubName() {
        return mSettingPreference.getString(KEY_USER_CLUB_NAME, "");
    }

    public static void setUserClubName(String clubName) {
        mSettingPreference.edit().putString(KEY_USER_CLUB_NAME, clubName).apply();
    }

    public static String getUserClubPosition() {
        return mSettingPreference.getString(KEY_USER_CLUB_POSITION, "");
    }

    public static void setUserClubPosition(String clubPosition) {
        mSettingPreference.edit().putString(KEY_USER_CLUB_POSITION, clubPosition).apply();
    }

    public static String getEmchatId() {
        return mSettingPreference.getString(KEY_EMCHAT_ID, "");
    }

    public static void setEmchatId(String emchatId) {
        mSettingPreference.edit().putString(KEY_EMCHAT_ID, emchatId).apply();
    }

    public static String getEMchatPassword() {
        return mSettingPreference.getString(KEY_EMCHAT_PASSWORD, "");
    }

    public static void setEMchatPassword(String emchatPassword) {
        mSettingPreference.edit().putString(KEY_EMCHAT_PASSWORD, emchatPassword).apply();
    }

    public static String getXmdChatModel() {
        return mSettingPreference.getString(KEY_XMD_CHAT_MODEL, "");
    }

    public static void SetXmdChatModel(String chatModel) {
        mSettingPreference.edit().putString(KEY_XMD_CHAT_MODEL, chatModel).apply();
    }


    public static String getUserAvatar() {
        return mSettingPreference.getString(KEY_USER_AVATAR, "");
    }

    public static void setUserAvatar(String avatar) {
        mSettingPreference.edit().putString(KEY_USER_AVATAR, avatar).apply();
    }

    public static String getAvatarId() {
        return mSettingPreference.getString(KEY_AVATAR_ID, null);
    }

    public static void setAvatarId(String avatarId) {
        mSettingPreference.edit().putString(KEY_AVATAR_ID, avatarId).apply();
    }

    public static String getUserId() {
        return mSettingPreference.getString(KEY_USER_ID, "");
    }

    public static void setUserId(String userId) {
        mSettingPreference.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static String getSerialNo() {
        return mSettingPreference.getString(KEY_SERIAL_NO, "");
    }

    public static void setSerialNo(String serialNo) {
        mSettingPreference.edit().putString(KEY_SERIAL_NO, serialNo).apply();
    }

    /**
     * get the server host
     *
     * @return
     */
    public static String getServerHost() {
        // return "http://192.168.1.100:9880";
        //  return "http://192.168.2.59:9090";
        return mSettingPreference.getString(KEY_SERVER_HOST, HttpRequestConstant.SERVER_HOST);
    }

    public static void setServerHost(String serverHost) {
        XmdNetwork.getInstance().changeServer(serverHost);
        mSettingPreference.edit().putString(KEY_SERVER_HOST, serverHost).apply();
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

    public static long getLastUploadStatDate() {
        return mSettingPreference.getLong(KEY_LAST_UPLOAD_STAT, 0);
    }

    public static void setLastUploadStatDate(long lastDate) {
        mSettingPreference.edit().putLong(KEY_LAST_UPLOAD_STAT, lastDate).apply();
    }

    public static String getClientId() {
        return mSettingPreference.getString(KEY_CLIENT_ID, AppConfig.sClientId);
    }

    public static void setClientId(String clientId) {
        mSettingPreference.edit().putString(KEY_CLIENT_ID, clientId).apply();
    }

    public static String getUserWXUnionid() {
        return mSettingPreference.getString(RequestConstant.KEY_USER_WX_UNION_ID, "");
    }

    public static void setUserWXUnionid(String unionId) {
        mSettingPreference.edit().putString(RequestConstant.KEY_USER_WX_UNION_ID, unionId).apply();
    }

    public static void setUserWXOpenId(String openId) {
        mSettingPreference.edit().putString(RequestConstant.KEY_USER_WX_OPEN_ID, openId).apply();
    }

    public static String getUserWXOpenid() {
        return mSettingPreference.getString(RequestConstant.KEY_USER_WX_OPEN_ID, "");
    }

    public static boolean getBindSuccess() {
        return mSettingPreference.getBoolean(RequestConstant.KEY_BIND_WX_SUCCESS, false);
    }

    public static void setBindSuccess(Boolean successed) {
        mSettingPreference.edit().putBoolean(RequestConstant.KEY_BIND_WX_SUCCESS, successed).apply();
    }

//    public static void setUserIsTech(String userChatId, String isTech) {
//
//        mSettingPreference.edit().putString(userChatId + "11", isTech).apply();
//    }
//
//    public static String getUserIsTech(String userChatId) {
//        return mSettingPreference.getString(userChatId + "11", "");
//    }

    public static void setUserRemarkName(String userChatId, String remark) {
        mSettingPreference.edit().putString(userChatId, remark).apply();
    }

    public static String getUserRemark(String userChatId) {
        return mSettingPreference.getString(userChatId, "");
    }

    public static void setGameStatus(String gameId, String gameStatus) {
        mSettingPreference.edit().putString(gameId, gameStatus).apply();
    }

    public static String getGameStatus(String gameId) {
        return mSettingPreference.getString(gameId, "");
    }

    public static void setGameMessageId(String gameId, String messageId) {
        mSettingPreference.edit().putString(gameId + "dice", messageId).apply();
    }

    public static String getGameMessageId(String gameId) {
        return mSettingPreference.getString(gameId + "dice", "");
    }

    public static Integer getGameTimeout() {
        return mSettingPreference.getInt(KEY_CLUB_GAME_TIMEOUT, 86400 * 1000);
    }

    public static void setGameTimeout(int timeoutSecond) {
        mSettingPreference.edit().putInt(KEY_CLUB_GAME_TIMEOUT, timeoutSecond * 1000).apply();
    }

    public static void setTechNoOld(String techId, String techNo) {
        mSettingPreference.edit().putString(techId + "techNo", techNo).apply();
    }

    public static void setGiftImageById(String id, String url) {
        mSettingPreference.edit().putString(id, url).apply();
    }

    public static String getGiftImageById(String id) {
        return mSettingPreference.getString(id, "");
    }

    public static long getVerificationCodeTime() {
        return mSettingPreference.getLong(KEY_VERIFICATION_CODE_TIME, 0);
    }

    public static void setVerificationCodeTime(long time) {
        mSettingPreference.edit().putLong(KEY_VERIFICATION_CODE_TIME, time).apply();
    }

    public static String getTechNo() {
        return mSettingPreference.getString(KEY_TECH_NO, "");
    }

    public static void setTechNo(String value) {
        mSettingPreference.edit().putString(KEY_TECH_NO, value).apply();
    }

    public static String getInviteCode() {
        return mSettingPreference.getString(KEY_INVITE_CODE, "");
    }

    public static void setInviteCode(String value) {
        mSettingPreference.edit().putString(KEY_INVITE_CODE, value).apply();
    }

    public static int getLoginType() {
        return mSettingPreference.getInt(KEY_LOGIN_TYPE, LoginTechnician.LOGIN_TYPE_PHONE);
    }

    public static void setLoginType(int loginType) {
        mSettingPreference.edit().putInt(KEY_LOGIN_TYPE, loginType).apply();
    }

    public static String getClubInviteCode() {
        return mSettingPreference.getString(KEY_CLUB_INVITE_CODE, "");
    }

    public static void setClubInviteCode(String value) {
        mSettingPreference.edit().putString(KEY_CLUB_INVITE_CODE, value).apply();
    }

    public static String getTechDescription() {
        return mSettingPreference.getString(KEY_TECH_DESCRIPTION, "");
    }

    public static void setTechDescription(String value) {
        mSettingPreference.edit().putString(KEY_TECH_DESCRIPTION, value).apply();
    }

    public static String getTechQrDownloadUrl() {
        return mSettingPreference.getString(KEY_TECH_QR_DOWNLOAD_URL, "");
    }

    public static void setTechQrDownloadUrl(String value) {
        mSettingPreference.edit().putString(KEY_TECH_QR_DOWNLOAD_URL, value).apply();
    }

    public static String getTechGender() {
        return mSettingPreference.getString(KEY_TECH_GENDER, "");
    }

    public static void setTechGender(String value) {
        mSettingPreference.edit().putString(KEY_TECH_GENDER, value).apply();
    }

    public static void setPayNotifyArchivedIds(String value) {
        mSettingPreference.edit().putString(KEY_PAY_NOTIFY_HIDE_ID + "-" + getUserId(), value).apply();
    }

    public static String getPayNotifyHideIds() {
        return mSettingPreference.getString(KEY_PAY_NOTIFY_HIDE_ID + "-" + getUserId(), "");
    }

    public static String getTechStatus() {
        return mSettingPreference.getString(KEY_TECH_STATUS + "_" + getUserId(), "");
    }

    public static void setTechStatus(String status) {
        mSettingPreference.edit().putString(KEY_TECH_STATUS + "_" + getUserId(), status).apply();
    }

    public static boolean getPayNotifyIsFirstHide() {
        return mSettingPreference.getBoolean(KEY_PAY_NOTIFY_IS_FIRST_HIDE, true);
    }

    public static void setPayNotifyIsFirstHide(boolean first) {
        mSettingPreference.edit().putBoolean(KEY_PAY_NOTIFY_IS_FIRST_HIDE, first).apply();
    }

    public static String getRoles() {
        return mSettingPreference.getString(KEY_ROLES, "");
    }

    public static void setRoles(String roles) {
        mSettingPreference.edit().putString(KEY_ROLES, roles).apply();
    }

    /******************************
     * 权限
     *************************************/
    public static Set<String> getPermissionList() {
        return mSettingPreference.getStringSet(KEY_PERMISSION, null);
    }

    public static void setPermissionList(Set<String> permissionList) {
        mSettingPreference.edit().putStringSet(KEY_PERMISSION, permissionList).apply();
    }

    public static long getPermissionSyncDate() {
        return mSettingPreference.getLong(KEY_PERMISSION_SYNC_DATE, 0);
    }

    public static void setPermissionSyncDate(Long utcTime) {
        mSettingPreference.edit().putLong(KEY_PERMISSION_SYNC_DATE, utcTime).apply();
    }

    public static String getCustomerService() {
        return mSettingPreference.getString(KEY_CUSTOMER_SERVICE, "");
    }

    /************************
     * 客服
     *****************************************/
    public static void setCustomerService(String roles) {
        mSettingPreference.edit().putString(KEY_CUSTOMER_SERVICE, roles).apply();
    }
}
