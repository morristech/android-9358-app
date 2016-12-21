package com.xmd.technician.model;

import android.graphics.Bitmap;

import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.RegisterResult;
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

    private LoginTechnician() {
        phoneNumber = SharedPreferenceHelper.getUserAccount();
        inviteCode = SharedPreferenceHelper.getInviteCode();
        techNo = SharedPreferenceHelper.getTechNo();
    }

    private String techNo;
    private String inviteCode;
    private String phoneNumber;
    private String techId;

    private String token;
    private String userId;
    private String emchatId;
    private String emchatPassword;
    private String nickName;
    private String avatarUrl;

    public static final String GENDER_FEMALE = "female";
    public static final String GENDER_MALE = "male";
    private String gender;

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
        setInviteCode(inviteCode);
        setTechNo(techNo);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        params.put(RequestConstant.KEY_TECH_No, techNo);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_BY_TECH_NO, params);
    }

    //登录成功后，保存数据
    public void saveLoginResult(LoginResult loginResult) {
        token = loginResult.token;
        SharedPreferenceHelper.setUserToken(token);

        userId = loginResult.userId;
        SharedPreferenceHelper.setUserId(userId);

        emchatId = loginResult.emchatId;
        SharedPreferenceHelper.setEmchatId(emchatId);

        emchatPassword = loginResult.emchatPassword;
        SharedPreferenceHelper.setEMchatPassword(emchatPassword);

        nickName = loginResult.name;
        SharedPreferenceHelper.setUserName(nickName);

        avatarUrl = loginResult.avatarUrl;
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
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
        setInviteCode(inviteCode);
        setTechNo(techNo);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, phoneNumber);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_ICODE, verificationCode);
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        params.put(RequestConstant.KEY_LOGIN_CHANEL, "android" + AppConfig.getAppVersionCode());
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);
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

    //更新技师信息,返回UpdateTechInfoResult
    public void updateTechNickNameAndGender(String nickName, String gender) {
        String user = "{\"name\":\"" + nickName + "\",\"gender\":\"" + gender + "\"}";
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, user);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);
    }

    public void saveRegisterResult(RegisterResult result) {
        token = result.token;
        SharedPreferenceHelper.setUserToken(token);

        nickName = result.name;
        SharedPreferenceHelper.setUserName(nickName);

        userId = result.userId;
        SharedPreferenceHelper.setUserId(userId);

        emchatId = result.emchatId;
        SharedPreferenceHelper.setEmchatId(emchatId);

        emchatPassword = result.emchatPassword;
        SharedPreferenceHelper.setEMchatPassword(emchatPassword);
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        SharedPreferenceHelper.setUserName(nickName);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
    }
}
