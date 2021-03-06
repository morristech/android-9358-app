package com.xmd.technician.model;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.event.EventRequestSyncPermission;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.TechInfo;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.SpaService;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPersonalDataResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by heyangya on 16-12-20.
 */
public class LoginTechnician {
    public static final int LOGIN_TYPE_PHONE = 1;
    public static final int LOGIN_TYPE_TECH_NO = 2;
    public static final String GENDER_FEMALE = "female";
    public static final String GENDER_MALE = "male";
    private static LoginTechnician ourInstance = new LoginTechnician();
    public String innerProvider;
    private int loginType;
    private String token;

    //用户信息
    private String userId;
    private String gender;
    private String phoneNumber;
    private String nickName;
    private String avatarId;
    private String avatarUrl;
    private String description;

    //聊天账号
    private String emchatId;
    private String emchatPassword;


    private String chatModel;

    //技师信息
    private String techNo;
    private String clubInviteCode;
    private String inviteCode;
    private String qrCodeUrl;
    private String status; // "busy"|"free"|"uncert"|"valid"|"reject"|"rest"
    private String clubId;
    private String clubName;
    private String clubPosition;    //会所所在位置 add ZR
    private int credit; //积分信息
    private String shareUrl;
    private float amount; //以元为单位
    private int commentCount;
    private int unreadCommentCount;
    private int orderCount;
    private String roles;
    private String customerService;//rest,working
    private String clubImageUrl;

    private LoginTechnician() {

        loginType = SharedPreferenceHelper.getLoginType();
        token = SharedPreferenceHelper.getUserToken();

        userId = SharedPreferenceHelper.getUserId();
        gender = SharedPreferenceHelper.getTechGender();
        phoneNumber = SharedPreferenceHelper.getUserAccount();
        nickName = SharedPreferenceHelper.getUserName();
        avatarUrl = SharedPreferenceHelper.getUserAvatar();
        avatarId = SharedPreferenceHelper.getAvatarId();
        description = SharedPreferenceHelper.getTechDescription();

        emchatId = SharedPreferenceHelper.getEmchatId();
        emchatPassword = SharedPreferenceHelper.getEMchatPassword();
        chatModel = SharedPreferenceHelper.getXmdChatModel();

        techNo = SharedPreferenceHelper.getTechNo();
        clubInviteCode = SharedPreferenceHelper.getClubInviteCode();
        inviteCode = SharedPreferenceHelper.getInviteCode();
        qrCodeUrl = SharedPreferenceHelper.getTechQrDownloadUrl();
        clubId = SharedPreferenceHelper.getUserClubId();
        clubName = SharedPreferenceHelper.getUserClubName();
        clubPosition = SharedPreferenceHelper.getUserClubPosition();

        roles = SharedPreferenceHelper.getRoles();

        status = SharedPreferenceHelper.getTechStatus();

        //客服消息
        customerService = SharedPreferenceHelper.getCustomerService();
        RxBus.getInstance().toObservable(TechPersonalDataResult.class).subscribe(this::onGetTechPersonalData);

        EventBusSafeRegister.register(this);
    }

    public static LoginTechnician getInstance() {
        return ourInstance;
    }

    //切换账号时，清除所有数据
    private void cleanAllDataWhenSwitchAccount() {
        setUserId(null);
        setGender(null);
        setPhoneNumber(null);
        setNickName(null);
        setAvatarId(null);
        setAvatarUrl(null);
        setDescription(null);
        setEmchatId(null);
        setEmchatPassword(null);

        setTechNo(null);
        setClubInviteCode(null);
        setInviteCode(null);
        setQrCodeUrl(null);
        setClubId(null);
        setClubName(null);
        setClubPosition(null);
        setRoles(null);
        setStatus(null);
        setCustomerService(null);
    }


    public boolean isLogin() {
        return !TextUtils.isEmpty(getToken());
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
        if (loginResult == null || loginResult.userId == null) {
            return;
        }
        if (!loginResult.userId.equals(getUserId())) {
            cleanAllDataWhenSwitchAccount();
        }
        setPhoneNumber(loginResult.loginName);
        setInviteCode(loginResult.inviteCode);
        setToken(loginResult.token);
        setUserId(loginResult.userId);
        setEmchatId(loginResult.emchatId);
        setEmchatPassword(loginResult.emchatPassword);
        setNickName(loginResult.name);
        setAvatarUrl(loginResult.avatarUrl);
        setChatModel(loginResult.IMPlatform);
        if (!TextUtils.isEmpty(loginResult.roles)) {
            setRoles(loginResult.roles);
        }


        setClubId(loginResult.clubId);
        setClubName(loginResult.clubName);

        //发送登录事件
        EventBus.getDefault().removeStickyEvent(EventLogout.class);
        EventBus.getDefault().postSticky(new EventLogin(getToken(), getUserInfo()));
        RxBus.getInstance().post(new EventLogin(getToken(), getUserInfo()));
    }

    //备用技师编号登录成功
    public void onTechNoLoginResult(String techNo, String clubInviteCode, LoginResult loginResult) {
        onLoginResult(loginResult);
        setTechNo(techNo);
        setClubInviteCode(clubInviteCode);
    }


    public void clearTechNoLoginResult() {
        setClubInviteCode(null);
        setTechNo(null);
        setClubId(null);
        setClubName(null);
        setUserId(null);
        setRoles(null);
        setChatModel(null);
    }

    //获取技师信息,返回TechInfoResult
    public void loadTechInfo() {
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_INFO);
    }

    //检查有无token缓存，有的话广播登录消息
    public void checkAndLogin() {
        String token = SharedPreferenceHelper.getUserToken();
        if (TextUtils.isEmpty(token)) {
            return;
        }
        EventBus.getDefault().removeStickyEvent(EventLogout.class);
        EventBus.getDefault().postSticky(new EventLogin(getToken(), getUserInfo()));
        RxBus.getInstance().post(new EventLogin(getToken(), getUserInfo()));
    }

    public void onLoadTechInfo(TechInfoResult result) {
        TechInfo techInfo = result.respData;

        setPhoneNumber(phoneNumber);
        setAvatarUrl(techInfo.imageUrl);
        setAvatarId(techInfo.avatar);

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
        setCustomerService(techInfo.customerService);
        setClubImageUrl(techInfo.clubImageUrl);
        setClubInviteCode(techInfo.clubInviteCode);
        UserInfoServiceImpl.getInstance().saveCurrentUser(getUserInfo());
    }

    public User getUserInfo() {
        User user = new User(getUserId());
        user.setName(getNickName());
        user.setAvatar(getAvatarUrl());
        user.setAvatarId(getAvatarId());
        user.setChatId(getEmchatId());
        user.setChatPassword(getEmchatPassword());
      //  user.setChatModel(getChatModel());
        user.setClubId(getClubId());
        user.setClubName(getClubName());
        user.setTechNo(getTechNo());
        user.setTechId(getUserId());
        user.setUserRoles(getRoles());
        return user;
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
        //发送登录事件
        EventBus.getDefault().removeStickyEvent(EventLogout.class);
        EventBus.getDefault().postSticky(new EventLogin(getToken(), getUserInfo()));
        RxBus.getInstance().post(new EventLogin(getToken(), getUserInfo()));
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
    public void sendJoinClubRequest(String inviteCode, String techId, String roleCode) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, token);
        params.put(RequestConstant.KEY_INVITE_CODE, inviteCode);
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        params.put(RequestConstant.KEY_ROLE_CODE, roleCode);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_JOIN_CLUB, params);
    }

    public void sendJoinClubAuditModify(String techId, String roleCode) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_SPARE_TECH_ID, techId);
        params.put(RequestConstant.KEY_ROLE_CODE, roleCode);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_AUDIT_MODIFY, params);
    }

    //加入会所申请成功
    public void onSendJoinClubRequest(String inviteCode, String techNo, JoinClubResult result) {
        setClubInviteCode(inviteCode);
        setClubName(result.name);
        setTechNo(techNo);
        setClubId(result.id);
        setRoles(result.role);
        setStatus(Constant.TECH_STATUS_UNCERT);
    }

    public void onModifyRequest(String techNo, String role) {
        setTechNo(techNo);
        setRoles(role);
        setStatus(Constant.TECH_STATUS_UNCERT);
    }

    //管理员审核通过，正式加入会所
    private void onJoinedClub() {
        //请求同步权限
        EventBus.getDefault().post(new EventRequestSyncPermission());
    }

    //退出会所，返回QuitClubResult
    public void exitClub(String password) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PASSWORD, password);
        if (getStatus().equals(Constant.TECH_STATUS_UNCERT)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_AUDIT_CANCEL, params);
        } else {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB, params);
        }
    }

    public void onExitClub(QuitClubResult result) {
        setTechNo(null);
        setClubId(null);
        setClubInviteCode(null);
        setClubName(null);
        setClubPosition(null);
        setStatus(Constant.TECH_STATUS_VALID);
        setClubImageUrl(null);
        //请求同步权限
        EventBus.getDefault().post(new EventRequestSyncPermission());
    }

    @Subscribe
    public void onTokeExpired(EventTokenExpired expired) {
        cleanWhenLogout();
    }

    private void cleanWhenLogout() {
        if (TextUtils.isEmpty(getToken())) {
            //已经登出了，不再次调用
            return;
        }
        //清除个人数据
        SharedPreferenceHelper.setCustomerService(null);
        SharedPreferenceHelper.setRoles(null);

        //清空当前用户打招呼数据
        HelloSettingManager.getInstance().resetTemplate();

        //清空token
        setToken(null);

        //跳转到登录页面
        UINavigation.gotoLogin(TechApplication.getAppContext());
    }

    //退出登录,返回LogoutResult
    public void logout() {
        String token = getToken();
        String userId = getUserId();
        //发送登出事件
        EventBus.getDefault().postSticky(new EventLogout(token, userId));
        RxBus.getInstance().post(new EventLogout(token, userId));
        cleanWhenLogout();
    }

    @Subscribe(priority = -1)
    public void onLogout(EventLogout eventLogout) {
        Call<LogoutResult> call = XmdNetwork.getInstance().getService(SpaService.class)
                .logout(eventLogout.getToken(), RequestConstant.SESSION_TYPE);
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                try {
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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

    public void setToken(String token) {
        this.token = token;
        SharedPreferenceHelper.setUserToken(token);
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

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
        SharedPreferenceHelper.setAvatarId(avatarId);
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

    public void setNickName(String nickName) {
        this.nickName = nickName;
        SharedPreferenceHelper.setUserName(nickName);
        //XMDEmChatManager.getInstance().updateNickName(nickName);
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
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

    public String getChatModel() {
        return chatModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
        SharedPreferenceHelper.SetXmdChatModel(chatModel);
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
                || TextUtils.equals(status, Constant.TECH_STATUS_BUSY) || TextUtils.equals(status, Constant.TECH_STATUS_REST);
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
        SharedPreferenceHelper.setRoles(roles);
    }

    public String getCustomerService() {
        return customerService;
    }

    public void setCustomerService(String customerService) {
        this.customerService = customerService;
        SharedPreferenceHelper.setCustomerService(customerService);
    }

    public String getClubImageUrl() {
        return clubImageUrl;
    }

    public void setClubImageUrl(String clubUrl) {
        this.clubImageUrl = clubUrl;
    }
}
