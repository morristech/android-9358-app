package com.xmd.technician.model;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.DataRefreshService;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.TechInfo;
import com.xmd.technician.chat.EmchatManager;
import com.xmd.technician.chat.IEmchat;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.event.EventLoginSuccess;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.Util;
import com.xmd.technician.event.EventExitClub;
import com.xmd.technician.event.EventJoinedClub;
import com.xmd.technician.event.EventLogin;
import com.xmd.technician.event.EventLogout;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPersonalDataResult;
import com.xmd.technician.http.gson.TokenExpiredResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 16-12-20.
 */
public class LoginTechnician {
    private static LoginTechnician ourInstance = new LoginTechnician();

    private IEmchat emchatManager = EmchatManager.getInstance();

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
    private String techNo;
    private String clubInviteCode;
    private String inviteCode;
    private String qrCodeUrl;
    private String status; // "busy"|"free"|"uncert"|"valid"|"reject"
    private String clubId;
    private String clubName;
    private String clubPosition;    //会所所在位置 add ZR
    private int credit;
    public String innerProvider;
    private String shareUrl;

    private float amount; //以元为单位
    private int commentCount;
    private int unreadCommentCount;
    private int orderCount;

    private String roles;


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
        qrCodeUrl = SharedPreferenceHelper.getTechQrDownloadUrl();
        clubId = SharedPreferenceHelper.getUserClubId();
        clubName = SharedPreferenceHelper.getUserClubName();
        clubPosition = SharedPreferenceHelper.getUserClubPosition();

        status = SharedPreferenceHelper.getTechStatus();

        RxBus.getInstance().toObservable(TechPersonalDataResult.class).subscribe(this::onGetTechPersonalData);
        RxBus.getInstance().toObservable(EventLoginSuccess.class).subscribe(
                eventEmChatLogin -> {
                    emchatManager.updateNickName(getNickName());
                }
        );
        RxBus.getInstance().toObservable(TokenExpiredResult.class).subscribe(
                tokenExpiredResult -> {
                    emchatManager.logout();
                }
        );
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
        setEmchatId(loginResult.emchatId);
        setEmchatPassword(loginResult.emchatPassword);
        setNickName(loginResult.name);
        setAvatarUrl(loginResult.avatarUrl);
        setRoles(loginResult.roles);

        RxBus.getInstance().post(new EventLogin());
    }

    //备用技师编号登录成功
    public void onTechNoLoginResult(String techNo, String inviteCode, LoginResult result) {
        setClubInviteCode(inviteCode);
        setTechNo(techNo);
        setClubId(result.clubId);
        setClubName(result.clubName);
        setUserId(result.spareTechId);
        setRoles(result.roles);
    }

    public void clearTechNoLoginResult() {
        setClubInviteCode(null);
        setTechNo(null);
        setClubId(null);
        setClubName(null);
        setUserId(null);
        setRoles(null);
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
        setClubPosition(techInfo.clubPosition);
        setCredit(techInfo.creditAmount);
        setInnerProvider(techInfo.innerProvider);
        setStatus(techInfo.status);
        setQrCodeUrl(techInfo.qrCodeUrl);
        setShareUrl(techInfo.shareUrl);

        //登录环信
        emchatManager.login(getEmchatId(), getEmchatPassword());

        //开始刷新个人数据
        DataRefreshService.refreshPersonalData(true);

        //开始刷新回复
        DataRefreshService.refreshHelloReply(true);

        if (isActiveStatus()) {
            //开始刷新买单通知
            DataRefreshService.refreshPayNotify(true);
        }

        UserProfileProvider.getInstance().updateCurrentUserInfo(getNickName(), getAvatarUrl());
    }

    //获取短信验证码
    public void getVerificationCode(String phoneNumber) {
        setPhoneNumber(phoneNumber);
    /*    Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, phoneNumber);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);*/
        String mobile = phoneNumber;
        String sign = DESede.encrypt(mobile + RequestConstant.KEY_WHICH_VALUE);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mobile);
        params.put(RequestConstant.KEY_WHICH, RequestConstant.KEY_WHICH_VALUE);
        params.put(RequestConstant.KEY_SIGN, sign);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
    }

    //注册,返回RegisterResult
    public void register(String phoneNumber, String password, String verificationCode, String inviteCode, String techId, String techNo, String roleCode) {
        setPhoneNumber(phoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, phoneNumber);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_ICODE, verificationCode);
        params.put(RequestConstant.KEY_ROLE_CODE, roleCode);
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        params.put(RequestConstant.KEY_LOGIN_CHANEL, "android" + AppConfig.getAppVersionCode());
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);
    }

    //处理注册成功
    public void onRegisterResult(RegisterResult result) {
        if (hasClub()) {
            setStatus(Constant.TECH_STATUS_UNCERT);
        } else {
            setStatus(Constant.TECH_STATUS_VALID);
        }
        setToken(result.token);
        setNickName(result.name);
        setUserId(result.userId);
        setEmchatId(result.emchatId);
        setEmchatPassword(result.emchatPassword);

        DataRefreshService.refreshPersonalData(true);
    }

    //上传头像,返回AvatarResult
    public void uploadAvatar(String path) {
        uploadAvatarOrAlbumImage(path, new AvatarResult(), 1024, true);
    }

    public void onUploadAvatarResult(AvatarResult result) {
        setAvatarUrl(result.respData);
    }

    public void uploadAlbumImage(String path) {
        uploadAvatarOrAlbumImage(path, new AlbumResult(), 1024, false);
    }

    private void uploadAvatarOrAlbumImage(String path, BaseResult result, int maxSize, boolean isAvatar) {
        Bitmap mPhotoTake;
        //检查文件是否存在
        File file = new File(path);
        if (!file.exists()) {
            result.statusCode = 400;
            result.msg = "文件不存在！";
            RxBus.getInstance().post(result);
            return;
        }
        //解码文件
        mPhotoTake = ImageLoader.getBitmapFromFile(path, maxSize);
        if (mPhotoTake == null) {
            result.statusCode = 400;
            result.msg = "解码文件失败！";
            RxBus.getInstance().post(result);
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mPhotoTake.compress(Bitmap.CompressFormat.PNG, 100, baos);

        String mImageFile = Util.bytes2base64(baos.toByteArray());
        mPhotoTake.recycle();
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IMG_FILE, mImageFile);
        if (isAvatar) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR, params);
        } else {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_ALBUM, params);
        }
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
    public void sendJoinClubRequest(String inviteCode, String techId, String roleCode) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, token);
        params.put(RequestConstant.KEY_INVITE_CODE, inviteCode);
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        params.put(RequestConstant.KEY_ROLE_CODE, roleCode);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_JOIN_CLUB, params);
    }

    //加入会所申请成功
    public void onSendJoinClubRequest(String inviteCode, String techNo, JoinClubResult result) {
        setClubInviteCode(inviteCode);
        setClubName(result.name);
        setTechNo(techNo);
        setClubId(result.id);
        setStatus(Constant.TECH_STATUS_UNCERT);
    }

    //管理员审核通过，正式加入会所
    private void onJoinedClub() {
        RxBus.getInstance().post(new EventJoinedClub(getClubName()));
    }

    //退出会所，返回QuitClubResult
    public void exitClub(String password) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PASSWORD, password);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB, params);
    }

    public void onExitClub(QuitClubResult result) {
        setTechNo(null);
        setClubId(null);
        setClubInviteCode(null);
        setClubName(null);
        setClubPosition(null);
        //关闭刷新买单通知
        DataRefreshService.refreshPayNotify(false);
        if (isActiveStatus()) {
            RxBus.getInstance().post(new EventExitClub());
        }
        setStatus(Constant.TECH_STATUS_VALID);
    }

    //退出登录,返回LogoutResult
    public void logout() {
        //停止刷新个人数据
        DataRefreshService.refreshPersonalData(false);
        //停止刷新买单通知
        DataRefreshService.refreshPayNotify(false);
        //停止刷新回复
        DataRefreshService.refreshHelloReply(false);

        //退出环信
        emchatManager.logout();

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, token);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID, params);

        //清空当前用户打招呼数据
        HelloSettingManager.getInstance().resetTemplate();

        //直接清空token，不用等待是否成功
        UserProfileProvider.getInstance().reset();
        setToken(null);



        RxBus.getInstance().post(new EventLogout());
    }

    public void onLogout() {

    }

    //获取技师当前数据，包括账户金额，所有评论数，积分，订单数量，状态，状态描述，未读评论
    public void getTechPersonalData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_PERSONAL_DATA);
    }

    public void onGetTechPersonalData(TechPersonalDataResult result) {
        if (result.respData != null) {
            setAmount(result.respData.accountAmount);
            setCredit(result.respData.credits);
            setCommentCount(result.respData.allCommentCount);
            setUnreadCommentCount(result.respData.unreadCommentCount);
            setStatus(result.respData.techStatus);
            setOrderCount(result.respData.orderCount);
        }
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

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
        SharedPreferenceHelper.setTechQrDownloadUrl(qrCodeUrl);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String workStatus) {
        boolean isVerifyStatus = isVerifyStatus();
        this.status = workStatus;
        SharedPreferenceHelper.setTechStatus(workStatus);
        if (isVerifyStatus && isActiveStatus(workStatus)) {
            //通过审核
            onJoinedClub();
        }
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

    public String getClubPosition() {
        return clubPosition;
    }

    public void setClubPosition(String clubPosition) {
        this.clubPosition = clubPosition;
        SharedPreferenceHelper.setUserClubPosition(clubPosition);
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

    public String getInnerProvider() {
        return innerProvider;
    }

    public void setInnerProvider(String innerProvider) {
        this.innerProvider = innerProvider;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getUnreadCommentCount() {
        return unreadCommentCount;
    }

    public void setUnreadCommentCount(int unreadCommentCount) {
        this.unreadCommentCount = unreadCommentCount;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    //技师是否有会所（包括已申请但还没有处理的情况）
    public boolean hasClub() {
        return !TextUtils.isEmpty(clubId);
    }

    //目前是否处于会所审核状态
    public boolean isVerifyStatus() {
        return TextUtils.equals(status, Constant.TECH_STATUS_UNCERT);
    }

    //目前是否处于在职状态
    public boolean isActiveStatus() {
        return isActiveStatus(status);
    }

    public boolean isActiveStatus(String status) {
        return TextUtils.equals(status, Constant.TECH_STATUS_FREE)
                || TextUtils.equals(status, Constant.TECH_STATUS_BUSY);
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isLoginEmchat() {
        return emchatManager.isConnected();
    }
}
