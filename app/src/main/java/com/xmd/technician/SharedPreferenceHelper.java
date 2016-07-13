package com.xmd.technician;

import android.app.Activity;
import android.content.SharedPreferences;

import com.xmd.technician.http.RequestConstant;

/**
 * Created by sdcm on 15-10-23.
 */
public class SharedPreferenceHelper {

    private static final String SETTING_PREFERENCE = "00DF60D934C0D482F0C950B6D3605F50"; //setting_preference

    private static SharedPreferences mSettingPreference;

    private static final String KEY_USER_ACCOUNT = "4E6EF539AAF119D82AC4C2BC84FBA21F";//userAccount
    private static final String KEY_USER_TOKEN = "CE4A0B029C785BFAA2B398C06E1D94C0"; //userToken
    private static final String KEY_USER_NAME = "435E0648D634175C46BD40AC366545A8";//userName
    private static final String KEY_SERVER_HOST = "1867ABFF59547D665AA22BDC2AB31BBD";//serverHost
    private static final String KEY_USER_ID = "8E44F0089B076E18A718EB9CA3D94674"; //userId
    private static final String KEY_EMCHAT_ID = "F211BBAA010D00B863BE64B3F5EFD983";//emchatId
    private static final String KEY_EMCHAT_PASSWORD = "7660E9BC2D136134F0FBC2856ABFDDAD";//emchatPassword
    private static final String KEY_USER_AVATAR = "DC03806303F221B804777E64B24B654C";//avatarUrl
    private static final String KEY_SERIAL_NO = "492B6C37356A803DCB43795618DB5DCA";//serialNo
    private static final String KEY_UPDATE_SERVER = "EB8329FF8AAC026D206E3C0A811D0D96";//updateServer
    /**
     * Last time to check the upgrade automatically
     */
    private static final String KEY_LAST_AUTO_CHECK_UPGRADE = "85A570AE7CC0F2E82A156E9C61C9E493";//lastAutoCheckUpgrade
    private static final String KEY_LAST_UPLOAD_STAT = "07208C27204A99B2112E6868030C55DC";//lastUploadStat

    private static final String KEY_CLIENT_ID = "F3FFE8E1F804F2074AA5CC55233673B8";//clientId

    public static void initialize() {
        mSettingPreference = TechApplication.getAppContext().getSharedPreferences(SETTING_PREFERENCE, Activity.MODE_PRIVATE);
    }

    public static void setUserAccount(String userAccount) {
        mSettingPreference.edit().putString(KEY_USER_ACCOUNT, userAccount).apply();
    }

    /**
     * Get the last login user account
     * @return
     */
    public static String getUserAccount(){
        return mSettingPreference.getString(KEY_USER_ACCOUNT, "");
    }

    public static void setUserToken(String userToken) {
        mSettingPreference.edit().putString(KEY_USER_TOKEN, userToken).apply();
    }

    public static void clearUserInfo(){
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
     * @return
     */
    public static String getUserToken(){
        return mSettingPreference.getString(KEY_USER_TOKEN, "");
    }

    public static void setUserName(String username) {
        mSettingPreference.edit().putString(KEY_USER_NAME, username).apply();
    }

    public static String getUserName() {
        return mSettingPreference.getString(KEY_USER_NAME, "");
    }

    public static void setEmchatId(String emchatId) {
        mSettingPreference.edit().putString(KEY_EMCHAT_ID, emchatId).apply();
    }

    public static String getEmchatId() {
        return mSettingPreference.getString(KEY_EMCHAT_ID, "");
    }

    public static void setEMchatPassword(String emchatPassword) {
        mSettingPreference.edit().putString(KEY_EMCHAT_PASSWORD, emchatPassword).apply();
    }

    public static String getEMchatPassword() {
        return mSettingPreference.getString(KEY_EMCHAT_PASSWORD, "");
    }

    public static void setUserAvatar(String avatar) {
        mSettingPreference.edit().putString(KEY_USER_AVATAR, avatar).apply();
    }

    public static String getUserAvatar() {
        return mSettingPreference.getString(KEY_USER_AVATAR, "");
    }

    public static void setUserId(String userId) {
        mSettingPreference.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static String getUserId() {
        return mSettingPreference.getString(KEY_USER_ID, "");
    }

    public static void setSerialNo(String serialNo) {
        mSettingPreference.edit().putString(KEY_SERIAL_NO, serialNo).apply();
    }

    public static String getSerialNo() {
        return mSettingPreference.getString(KEY_SERIAL_NO, "");
    }

    public static void setServerHost(String serverHost) {
        mSettingPreference.edit().putString(KEY_SERVER_HOST, serverHost).apply();
    }

    /**
     * get the server host
     * @return
     */
    public static String getServerHost(){
        return mSettingPreference.getString(KEY_SERVER_HOST, RequestConstant.SERVER_HOST);
    }

    public static void setLastAutoCheckUpgrade(long mill) {
        mSettingPreference.edit().putLong(KEY_LAST_AUTO_CHECK_UPGRADE, mill).apply();
    }

    /**
     * Get the last time when auto upgrade checking was invoked
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

    public static void setUpdateServer(String server){
        mSettingPreference.edit().putString(KEY_UPDATE_SERVER,server).apply();
    }

    public static String getUpdateServer(){
        return mSettingPreference.getString(KEY_UPDATE_SERVER,Constant.DEFAULT_UPDATE_SERVER);
    }
}
