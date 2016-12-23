package com.xmd.technician.model;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.TechInfo;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 16-12-20.
 */
public class LoginTechnician {
    private static LoginTechnician ourInstance = new LoginTechnician();

    public static LoginTechnician getInstance() {
        return ourInstance;
    }

    public static final int LOGIN_TYPE_PHONE = 1;
    public static final int LOGIN_TYPE_TECH_NO = 2;
    public static final String GENDER_FEMALE = "female";
    public static final String GENDER_MALE = "male";

    private int loginType;
    private String token;

    //用户信息
    private String userId;
    private String gender;
    private String phoneNumber;
    private String nickName;
    private String avatarUrl;
    private String description;

    //聊天账号
    private String emchatId;
    private String emchatPassword;

    //技师信息
    private String techId;
    private String techNo;
    private String clubInviteCode;
    private String inviteCode;
    private String qrCodeDownloadUrl;
    private String workStatus; // "busy"|"free"|"rest"|"valid"
    private String clubId;
    private String clubName;
    private int credit;
    public String innerProvider;

    private LoginTechnician() {
        loginType = SharedPreferenceHelper.getLoginType();
        token = SharedPreferenceHelper.getUserToken();

        userId = SharedPreferenceHelper.getUserId();
        gender = SharedPreferenceHelper.getTechGender();
        phoneNumber = SharedPreferenceHelper.getUserAccount();
        nickName = SharedPreferenceHelper.getUserName();
        avatarUrl = SharedPreferenceHelper.getUserAvatar();
        description = SharedPreferenceHelper.getTechDescription();

        emchatId = SharedPreferenceHelper.getEmchatId();
        emchatPassword = SharedPreferenceHelper.getEMchatPassword();

        techNo = SharedPreferenceHelper.getTechNo();
        clubInviteCode = SharedPreferenceHelper.getClubInviteCode();
        inviteCode = SharedPreferenceHelper.getInviteCode();
        qrCodeDownloadUrl = SharedPreferenceHelper.getTechQrDownloadUrl();
        clubId = SharedPreferenceHelper.getUserClubId();
        clubName = SharedPreferenceHelper.getUserClubName();
    }


    //使用手机号码登录，返回LoginResult
    public void loginByPhoneNumber(String phoneNumber, String password) {
        setPhoneNumber(phoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, phoneNumber);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    //使用技师编号登录，返回LoginResult
    public void loginByTechNo(String inviteCode, String techNo, String password) {
        setClubInviteCode(inviteCode);
        setTechNo(techNo);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        params.put(RequestConstant.KEY_TECH_No, techNo);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_BY_TECH_NO, params);
    }

    //登录成功后，保存数据
    public void onLoginResult(LoginResult loginResult) {
        setToken(loginResult.token);
        setUserId(loginResult.userId);

        emchatId = loginResult.emchatId;
        SharedPreferenceHelper.setEmchatId(emchatId);

        emchatPassword = loginResult.emchatPassword;
        SharedPreferenceHelper.setEMchatPassword(emchatPassword);

        nickName = loginResult.name;
        SharedPreferenceHelper.setUserName(nickName);

        avatarUrl = loginResult.avatarUrl;
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
    }

    //获取技师信息,返回TechInfoResult
    public void loadTechInfo() {
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_INFO);
    }

    public void onLoadTechInfo(TechInfoResult result) {
        TechInfo techInfo = result.respData;

        setPhoneNumber(phoneNumber);
        if (!TextUtils.isEmpty(techInfo.imageUrl)) {
            setAvatarUrl(techInfo.imageUrl);
        } else {
            setAvatarUrl(techInfo.avatar);
        }
        setUserId(techInfo.id);
        setNickName(techInfo.userName);
        setDescription(techInfo.description);

        setTechNo(techInfo.serialNo);
        setInviteCode(techInfo.inviteCode);
        setClubId(techInfo.clubId);
        setClubName(techInfo.clubName);
        setCredit(techInfo.creditAmount);
        setInnerProvider(techInfo.innerProvider);
        setWorkStatus(techInfo.status);
        setQrCodeDownloadUrl(techInfo.qrCodeUrl);
    }

    //登录聊天账号
    public void loginEmChatAccount() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, null);
    }

    public void onLoginEmChatAccount() {

    }

    //获取短信验证码
    public void getVerificationCode(String phoneNumber) {
        setPhoneNumber(phoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, phoneNumber);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
    }

    //注册,返回RegisterResult
    public void register(String phoneNumber, String password, String verificationCode, String inviteCode, String techId, String techNo) {
        setPhoneNumber(phoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, phoneNumber);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_ICODE, verificationCode);
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        params.put(RequestConstant.KEY_LOGIN_CHANEL, "android" + AppConfig.getAppVersionCode());
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);
    }

    //处理注册成功
    public void onRegisterResult(RegisterResult result) {
        setToken(result.token);
        setNickName(result.name);
        setUserId(result.userId);
        setEmchatId(result.emchatId);
        setEmchatPassword(result.emchatPassword);
    }

    //上传头像,返回AvatarResult
    public void uploadAvatar(String path) {
        AvatarResult result = new AvatarResult();
        Bitmap mPhotoTake = null;
        //检查文件是否存在
        File file = new File(path);
        if (!file.exists()) {
            result.statusCode = 400;
            result.msg = "文件不存在！";
            RxBus.getInstance().post(result);
            return;
        }
        //解码文件
        mPhotoTake = ImageLoader.getBitmapFromFile(path, 1024);
        if (mPhotoTake == null) {
            result.statusCode = 400;
            result.msg = "解码文件失败！";
            RxBus.getInstance().post(result);
            return;
        }

        String mImageFile = Util.bitmap2base64(mPhotoTake, false);
        mPhotoTake.recycle();
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IMG_FILE, mImageFile);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR, params);
    }

    public void onUploadAvatarResult(AvatarResult result) {
        setAvatarUrl(result.respData);
    }

    //更新技师信息,返回UpdateTechInfoResult
    public void updateTechNickNameAndGender(String nickName, String gender) {
        String user = "{\"name\":\"" + nickName + "\",\"gender\":\"" + gender + "\"}";
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, user);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);
    }

    public void onUpdateTechNickNameAndGender(String nickName, boolean female, UpdateTechInfoResult result) {
        setNickName(nickName);
        setGender(female ? LoginTechnician.GENDER_FEMALE : LoginTechnician.GENDER_MALE);
    }


    //加入会所，返回JoinClubResult
    public void joinClub(String inviteCode, String techId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, token);
        params.put(RequestConstant.KEY_INVITE_CODE, inviteCode);
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_JOIN_CLUB, params);
    }

    public void onJoinClub(String inviteCode, String techNo, JoinClubResult result) {
        setClubInviteCode(inviteCode);
        setTechNo(techNo);
        setTechId(result.id);
    }

    //退出会所，返回QuitClubResult
    public void exitClub() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB);
    }

    public void onExitClub(QuitClubResult result) {
        setTechNo(null);
        setClubId(null);
        setClubInviteCode(null);
    }

    //退出登录,返回LogoutResult
    public void logout() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, token);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID, params);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT, params);
        //直接清空token，不用等待是否成功
        UserProfileProvider.getInstance().reset();
        EMClient.getInstance().logout(true);
        setToken(null);
    }

    public void onLogout() {

    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        SharedPreferenceHelper.setUserAccount(phoneNumber);
    }

    public String getTechNo() {
        return techNo;
    }

    public void setTechNo(String techNo) {
        this.techNo = techNo;
        SharedPreferenceHelper.setTechNo(techNo);
    }

    public String getClubInviteCode() {
        return clubInviteCode;
    }

    public void setClubInviteCode(String clubInviteCode) {
        this.clubInviteCode = clubInviteCode;
        SharedPreferenceHelper.setClubInviteCode(clubInviteCode);
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        SharedPreferenceHelper.setUserId(userId);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        SharedPreferenceHelper.setTechGender(gender);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        SharedPreferenceHelper.setUserName(nickName);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
    }

    public void setToken(String token) {
        this.token = token;
        SharedPreferenceHelper.setUserToken(token);
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
        SharedPreferenceHelper.setLoginType(loginType);
    }

    public String getNickName() {
        return nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getQrCodeDownloadUrl() {
        return qrCodeDownloadUrl;
    }

    public void setQrCodeDownloadUrl(String qrCodeDownloadUrl) {
        this.qrCodeDownloadUrl = qrCodeDownloadUrl;
        SharedPreferenceHelper.setTechQrDownloadUrl(qrCodeDownloadUrl);
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getEmchatId() {
        return emchatId;
    }

    public void setEmchatId(String emchatId) {
        this.emchatId = emchatId;
        SharedPreferenceHelper.setEmchatId(emchatId);
    }

    public String getEmchatPassword() {
        return emchatPassword;
    }

    public void setEmchatPassword(String emchatPassword) {
        this.emchatPassword = emchatPassword;
        SharedPreferenceHelper.setEMchatPassword(emchatPassword);
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
        SharedPreferenceHelper.setUserClubId(clubId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        SharedPreferenceHelper.setTechDescription(description);
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
        SharedPreferenceHelper.setUserClubName(clubName);
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
        SharedPreferenceHelper.setInviteCode(inviteCode);
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getInnerProvider() {
        return innerProvider;
    }

    public void setInnerProvider(String innerProvider) {
        this.innerProvider = innerProvider;
    }
}
