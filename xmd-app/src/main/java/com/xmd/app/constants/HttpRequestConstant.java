package com.xmd.app.constants;

/**
 * Created by Lhj on 18-3-13.
 * 统一管理请求接口
 */

public class HttpRequestConstant {
    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/
    public static final String SERVER_HOST = "http://spa.93wifi.com";

    public static final String BASE_URL = "/spa-manager/api";

    public static final String URL_LOGIN = BASE_URL + "/v1/tech/login";
    public static final String URL_LOGIN_BY_TECH_NO = BASE_URL + "/v1/spare_tech/login";
    public static final String URL_LOGOUT = BASE_URL + "/v1/tech/logout";
    public static final String URL_REGISTER = BASE_URL + "/v1/tech/register";
    public static final String URL_EDIT_INFO = BASE_URL + "/v1/profile/tech/editInfo";
    public static final String URL_UPDATE_TECH_INFO = BASE_URL + "/v1/profile/tech";
    public static final String URL_CURRENT_INFO = BASE_URL + "/v1/profile/tech/current";
    public static final String URL_TECH_PERSONAL_DATA = BASE_URL + "/v2/tech/profile/notice";
    public static final String URL_MODIFY_PASSWORD = BASE_URL + "/v1/profile/tech/modifyPassword";
    public static final String URL_RESET_PASSWORD = BASE_URL + "/v1/tech/resetPassword";
    public static final String URL_FEEDBACK_CREATE = BASE_URL + "/v2/tech/feedback/create";
    public static final String URL_HIDE_ORDER = BASE_URL + "/v2/tech/hide/order";

    public static final String URL_GET_ICODE = BASE_URL + "/v1/appVerifyCode";
    public static final String URL_JOIN_CLUB = BASE_URL + "/v1/profile/tech/club/change";
    public static final String URL_GET_WORKTIME = BASE_URL + "/v1/profile/tech/calendar";
    public static final String URL_UPDATE_WORKTIME = BASE_URL + "/v1/profile/tech/calendar/update";
    public static final String URL_DELETE_ALBUM = BASE_URL + "/v1/profile/tech/album/delete";
    public static final String URL_UPLOAD_ALBUM = BASE_URL + "/v1/profile/tech/album/base64/upload";
    public static final String URL_SORT_ALBUM = BASE_URL + "/v1/profile/tech/album/sort";
    public static final String URL_UPLOAD_AVATAR = BASE_URL + "/v1/profile/tech/avatar/base64/upload";


    public static final String URL_QUIT_CLUB = BASE_URL + "/v2/tech/check/password";
    public static final String URL_GET_SERVICE_LIST = BASE_URL + "/v2/tech/profile/service";
    public static final String URL_UPDATE_SERVICE_LIST = BASE_URL + "/v1/profile/tech/service/update";
    public static final String URL_GET_ACCOUNT_MONEY = BASE_URL + "/v1/profile/tech/money";
    public static final String URL_CONSUME_DETAIL = BASE_URL + "/v2/tech/profile/account/details";

    public static final String URL_GET_COUPON_LIST = BASE_URL + "/v1/profile/redpack/list";
    public static final String URL_GET_COUPON_INFO = BASE_URL + "/v1/profile/club/redpacketinfo";
    public static final String URL_COUPON_SHARE_QR_CODE = BASE_URL + "/v1/profile/redpacket/useqrcode";
    public static final String URL_PAID_COUPON_USER_DETAIL = BASE_URL + "/v1/profile/tech/coupon/userdetails";
    public static final String URL_ADD_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";
    public static final String URL_GET_CUSTOMER_LIST = BASE_URL + "/v2/tech/customer/user/list";
    public static final String URL_GET_CUSTOMER_INFO_DETAIL = BASE_URL + "/v2/tech/customer/user/view";
    public static final String URL_GET_RECENTLY_VISITOR_LIST = BASE_URL + "/v2/tech/customer/recent/list";
    // public static final String URL_DELETE_CONTACT = BASE_URL + "/v2/tech/customer/delete";
    public static final String URL_GAME_DICE_SUBMIT = BASE_URL + "/v2/credit/game/dice/submit";
    public static final String URL_GAME_DICE_ACCEPT_OR_REJECT = BASE_URL + "/v2/credit/game/dice/accept";
    public static final String URL_GET_CREDIT_GIFT_LIST = BASE_URL + "/v2/credit/gift/list";
    public static final String URL_CUSTOMER_VIEW_VISIT = BASE_URL + "/v2/tech/customer/user/view/visit";


    public static final String URL_GET_CREDIT_SWITCH_STATUS = BASE_URL + "/v2/credit/switch/status";
    public static final String URL_GET_CREDIT_EXCHANGE_APPLY = BASE_URL + "/v2/credit/exchange/apply";
    public static final String URL_GET_CREDIT_USER_RECORDS = BASE_URL + "/v2/credit/user/records";
    public static final String URL_GET_CREDIT_USER_ACCOUNT = BASE_URL + "/v2/credit/user/account";
    public static final String URL_GET_CREDIT_USER_EXCHANGE_APPLICATIONS = BASE_URL + "/v2/credit/user/exchange/applications";
    public static final String URL_INTRODUCE_BIND = "/spa-manager/follow9358/index.html";
    public static final String URL_SAVE_CONTACT_MARK_CHATTO_USER = BASE_URL + "/v1/emchat/markchattouser";

    public static final String URL_GET_USER_SWITCHES = BASE_URL + "/v2/user/switches";
    public static final String URL_COUPON_SHARE_EVENT_COUNT = BASE_URL + "/v1/profile/redpacket/share";
    public static final String URL_GET_TECH_INFO = BASE_URL + "/v2/tech/current";
    public static final String URL_GET_TECH_ORDER_LIST = BASE_URL + "/v2/tech/order/list";
    public static final String URL_GET_TECH_STATISTICS_DATA = BASE_URL + "/v2/tech/statisticsData";
    public static final String URL_UPDATE_WORK_STATUS = BASE_URL + "/v1/profile/tech/status/update";
    public static final String URL_MANAGE_ORDER = BASE_URL + "/v2/tech/profile/order/manage";
    public static final String URL_GET_TECH_RANK_INDEX = BASE_URL + "/v2/tech/tech_rank_index";
    public static final String URL_GET_RECENT_DYNAMICS_LIST = BASE_URL + "/v2/tech/recent/dynamics/list";
    public static final String URL_ORDER_INNER_READ = BASE_URL + "/v2/tech/order/inner/read";
    public static final String URL_GET_PROFILE_TECH_ACCOUNT_LIST = BASE_URL + "/v1/profile/tech/account/list";
    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";
    public static final String URL_GET_UNUSED_TECH_NO = BASE_URL + "/v1/get/tech_no";
    /**
     * 自动领取优惠券
     */
    public static final String URL_GET_CLUB_USER_GET_COUPON = BASE_URL + "/v2/club/user/get/coupon";

    //买单记录查询
    public static final String URL_GET_PAY_NOTIFY_LIST = BASE_URL + "/v2/tech/fastpay/order/list";
    public static final String URL_CHECK_PAY_NOTIFY = BASE_URL + "/v2/club/order/notify/check";

    //技师功能权限
    public static final String URL_ROLE_PERMISSION = BASE_URL + "/v2/tech/menu/list"; //权限
    public static final String URL_ROLE_LIST = BASE_URL + "/v2/tech/role/list"; //角色列表
    /**
     * 营销列表
     */
    public static final String URL_GET_CARD_SHARE_LIST_INFO = BASE_URL + "/v1/techshare/cardListInfo";        //卡券列表
    public static final String URL_GET_ACTIVITY_LIST_INFO = BASE_URL + "/v1/techshare/activityListInfo";     //热门活动列表
    public static final String URL_GET_PROPAGANDA_LIST_INFO = BASE_URL + "/v1/techshare/propagandaListInfo";//会所宣传列表
    public static final String URL_GET_CARD_LIST_DETAIL = BASE_URL + "/v1/techshare/cardListDetail";       //点钟券、优惠券列表
    public static final String URL_GET_ONCE_CARD_LIST_DETAIL = BASE_URL + "/v2/club/item_card/activity/list";//次卡列表
    public static final String URL_GET_SERVICE_ITEM_LIST = BASE_URL + "/v1/techshare/serviceItemListDetail";    //限时抢列表
    public static final String URL_GET_PAY_FOR_ME_LIST = BASE_URL + "/v1/techshare/oneYuanListDetail";//谁替我买单列表
    public static final String URL_GET_CLUB_JOURNAL_LIST_DETAIL = BASE_URL + "/v1/techshare/journalListDetail";//会所期刊列表
    public static final String URL_GET_REWARD_ACTIVITY_LIST = BASE_URL + "/v1/techshare/darwActListDetail";//抽奖活动列表
    public static final String URL_GET_INVITATION_REWARD_ACTIVITY_LIST = BASE_URL + "/v2/club/user/invite/activity/active";//邀请有礼
    public static final String URL_DO_USER_JOURNAL_SHARE_COUNT = BASE_URL + "/v2/user/journal/share/count";//期刊统计
    public static final String URL_GET_GROUP_BUY_ONLINE_LIST = BASE_URL + "/v2/manager/group/buy/online/list";
    /**
     * PK排行榜
     */
    public static final String URL_GET_TECH_PK_ACTIVITY_RANKING = BASE_URL + "/v1/tech/pk/activity/ranking/index";
    public static final String URL_GET_TECH_PK_ACTIVITY_LIST = BASE_URL + "/v1/tech/pk/activity/list";
    public static final String URL_GET_PK_TEAM_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/team/ranking/list";
    public static final String URL_GET_PK_PERSONAL_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/personal/ranking/list";
    public static final String URL_GET_PERSONAL_RANKING_LIST = BASE_URL + "/v2/manager/ranking_list";
    /**
     * 订单数量
     */

    public static final String URL_GET_TECH_ORDER_COUNT = BASE_URL + "/v2/tech/order/count";
    /**
     * 分享次数统计
     */
    public static final String URL_TECH_SHARE_COUNT_UPDATE = BASE_URL + "/v2/tech/share/count/update";
    /**
     * --------------------------------------> 附近的人:url <----------------------------------------
     */
    // 查看会所位置(未使用)
    public static final String URL_GET_CLUB_POSITION = BASE_URL + "/v1/position/club";
    // 获取所属会所附近客户数量
    //   public static final String URL_GET_NEARBY_CUS_COUNT = BASE_URL + "/v2/tech/club/nearby/user/count";
    // 获取会所附近客户列表
    public static final String URL_GET_NEARBY_CUS_LIST = BASE_URL + "/v2/tech/club/nearby/user/list";
    // 技师打招呼
    public static final String URL_TECH_SAY_HELLO = BASE_URL + "/v2/tech/hello/{customerId}";
    // 获取技师打招呼剩余数量
    public static final String URL_GET_HELLO_LEFT_COUNT = BASE_URL + "/v2/tech/hello/number/left";
    // 获取技师打招呼列表
    public static final String URL_GET_HELLO_RECORD_LIST = BASE_URL + "/v2/tech/hello/list";
    // 查询最新回复列表
    public static final String URL_CHECK_HELLO_REPLY = BASE_URL + "/v2/tech/hello/list/reply/new";
    // 查询同客户的联系限制
//    public static final String URL_GET_CONTACT_PERMISSION = BASE_URL + "/v2/tech/contact/permission/{id}";
    // 保存打招呼内容 post
    public static final String URL_SAVE_HELLO_TEMPLATE = BASE_URL + "/v2/tech/hello/template";
    // 获取打招呼内容 get
    public static final String URL_GET_HELLO_TEMPLATE = BASE_URL + "/v2/tech/hello/template";
    // 查询系统模版列表
    public static final String URL_GET_HELLO_TEMPLATE_LIST = BASE_URL + "/v2/tech/hello/template/list";
    // 上传打招呼图片
    public static final String URL_UPLOAD_HELLO_TEMPLATE_IMAGE = BASE_URL + "/v2/tech/hello/template/image";
    //获取会所位置
    public static final String URL_GET_MARK_CHAT_TO_USER = BASE_URL + "/v2/club/location/staticmap";
    public static final String URL_GET_TECH_CHAT_CATEGORY_LIST = BASE_URL + "/v2/tech/chat/category/list";
    public static final String URL_GET_TECH_MARKETING_ITEM_LIST = BASE_URL + "/v2/tech/marketing_item/list";
    /**
     * 提成说明
     */
    public static final String URL_TECH_WITHDRAW_RULE = BASE_URL + "/v1/withdraw/rule";

    /**
     * --------------------------------------> 技师海报 <----------------------------------------
     */
    //创建海报
    public static final String URL_TECH_POSTER_SAVE = BASE_URL + "/v2/tech/poster/save";
    //删除海报
    public static final String URL_TECH_POSTER_DELETE = BASE_URL + "/v2/tech/poster/delete";
    //图片上传
    public static final String URL_TECH_POSTER_IMAGE_UPLOAD = BASE_URL + "/v2/tech/poster/image/upload";
    //海报列表
    public static final String URL_TECH_POSTER_LIST = BASE_URL + "/v2/tech/poster/list";
    //海报详情
    public static final String URL_TECH_POSTER_DETAIL = BASE_URL + "/v2/tech/poster/detail";
    /**
     * --------------------------------------> 加入会所 <----------------------------------------
     */
    //修改加入会所申请
    public static final String URL_TECH_AUDIT_MODIFY = BASE_URL + "/v1/profile/tech/audit/modify";
    //取消加入会所申请
    public static final String URL_TECH_AUDIT_CANCEL = BASE_URL + "/v1/profile/tech/audit/cancel";
    //被拒绝后确认
    public static final String URL_TECH_AUDIT_CONFIRM = BASE_URL + "/v1/profile/tech/audit/confirm";

    /**
     * --------------------------------------> app模块 <----------------------------------------
     */
    //心跳上报
    public static final String URL_APP_HEART_BEAT = BASE_URL + "/v2/app/heartbeat";
    //获取用户信息
    public static final String URL_CHAT_USER_INFO = BASE_URL + "/v2/chat/userInfo/{id}";
    //获取积分
    public static final String URL_CREDIT_USER_ACCOUNT = BASE_URL + "/v2/credit/user/account";
    /**
     * --------------------------------------> appointment模块 <----------------------------------------
     */
    //技师列表
    public static final String URL_ORDER_TECHNICIAN_LIST = BASE_URL + "/v2/tech/order/technician/list";
    //获取项目列表
    public static final String URL_ORDER_SERVICE_ITEM_LIST = BASE_URL + "/v2/tech/order/serviceItem/list";
    //获取更多预约信息
    public static final String URL_TECH_ORDER_EDIT = BASE_URL + "/v2/tech/order/edit";
    //生成或查询预约
    public static final String URL_TECH_ORDER_SAVE = BASE_URL + "/v2/tech/order/save";
    /**
     * --------------------------------------> black 黑名单模块 <----------------------------------------
     */
    // 将联系人加入黑名单
    public static final String URL_ADD_TO_BLACKLIST = BASE_URL + "/v2/tech/customer/add/emchat/blacklist";
    // 将联系人移出黑名单
    public static final String URL_REMOVE_FROM_BLACKLIST = BASE_URL + "/v2/tech/customer/remove/emchat/blacklist";
    // 联系人是否在黑名单中
    public static final String URL_IN_BLACKLIST = BASE_URL + "/v2/tech/customer/in/emchat/blacklist";
    // 获取黑名单列表
    public static final String URL_GET_TECH_BLACKLIST = BASE_URL + "/v2/tech/customer/emchat/blacklist";
    // 是否在联系人黑名单中
    public static final String URL_IN_USER_BLACKLIST = BASE_URL + "/v2/tech/customer/in/user/blacklist";
    //标签
    public static final String URL_GET_CONTACT_MARK = BASE_URL + "/v2/club/impression/list";
    //技师修改备注
    public static final String URL_EDIT_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";
    //添加
    public static final String URL_ADD_CUSTOMER_CREATE = BASE_URL + "/v2/tech/customer/create";
    //管理者修改备注
    public static final String URL_MANAGER_USER_EDIT = BASE_URL + "/v2/manager/user/edit";
    /**
     * --------------------------------------> chat 聊天模块 <----------------------------------------
     */
    //会所位置
    public static final String URL_CHAT_POSITION_CLUB = BASE_URL + "/v1/position/club";
    //电子期刊分享数据列表
    public static final String URL_CHAT_JOURNAL_LIST = BASE_URL + "/v1/techshare/journalListDetail";
    //次卡分享数据列表
    public static final String URL_CHAT_ACTIVITY_LIST = BASE_URL + "/v2/club/item_card/activity/list";
    //营销活动列表
    public static final String URL_CHAT_MARKETING_ITEM_LIST = BASE_URL + "/v2/tech/marketing_item/list";
    //积分礼物列表
    public static final String URL_CHAT_CREDIT_GIFT_LIST = BASE_URL + "/v2/credit/gift/list";
    //订单操作
    public static final String URL_CHAT_ORDER_MANAGE = BASE_URL + "/v2/tech/profile/order/manage";
    //通知用户有消息
    public static final String URL_CHAT_MARK_CHAT_TO_USER = BASE_URL + "/v1/emchat/markchattouser";
    //获取及保存快速回复(get 请求)
    public static final String URL_CHAT_SETTING_FAST_REPLY = BASE_URL + "/v2/chat/setting/fastReply";
    //是否在线邀请有礼
    public static final String URL_CHAT_INVITE_ENABLE = BASE_URL + "/v2/club/user/invite/enable";
    //统计技师分享数据
    public static final String URL_CHAT_SHARE_COUNT_UPDATE = BASE_URL + "/v2/tech/share/count/update";
    /**
     * --------------------------------------> comment 评论模块 <----------------------------------------
     */
    public static final String URL_COMMENT_LIST = BASE_URL + "/v2/comment/list";
    public static final String URL_COMMENT_TECH_LIST = BASE_URL + "/v2/manager/tech/base/list";
    public static final String URL_USER_BAD_COMMENT_STATUS_UPDATE = BASE_URL + "/v2/manager/user/badcomment/status/update";
    public static final String URL_CUSTOMER_USER_DETAIL = BASE_URL + "/v2/manager/user/detail";
    public static final String URL_USER_CONSUME_LIST = BASE_URL + "/v2/manager/user/consume/list";
    public static final String URL_USER_SHOP_LIST = BASE_URL + "/v2/manager/user/shop/list";
    public static final String URL_USER_REWARD_LIST = BASE_URL + "/v2/manager/user/reward/list";
    public static final String URL_TECH_CUSTOMER_DETAIL = BASE_URL + "/v2/tech/customer/detail";
    public static final String URL_TECH_USER_CONSUME_LIST = BASE_URL + "/v2/tech/customer/consume/list";
    public static final String URL_TECH_USER_SHOP_LIST = BASE_URL + "/v2/tech/customer/shop/list";
    public static final String URL_TECH_USER_REWARD_LIST = BASE_URL + "/v2/tech/customer/reward/list";
    //本店同事详情
    public static final String URL_CLUB_COLLEAGUE_DETAIL = BASE_URL + "/v2/tech/colleague/detail";
    // 查询同客户的联系限制
    public static final String URL_GET_CONTACT_PERMISSION = BASE_URL + "/v2/tech/contact/permission/{id}";
    //删除联系人
    public static final String URL_DELETE_CONTACT = BASE_URL + "/v2/tech/customer/delete";

    public static final String URL_DO_GROUP_USER_EDIT_GROUP = BASE_URL + "/v2/manager/group/user/editGroup";
    // 近期是否打过招呼
    public static final String URL_CHECK_HELLO_RECENTLY = BASE_URL + "/v2/tech/hello/{customerId}/status";

    // 获取所属会所附近客户数量
    public static final String URL_GET_NEARBY_CUS_COUNT = BASE_URL + "/v2/tech/club/nearby/user/count";
    /**
     * --------------------------------------> contact 联系人 <----------------------------------------
     */

    //本店同事列表
    public static final String URL_GET_CLUB_EMPLOYEE_LIST = BASE_URL + "/v2/tech/colleague/list";
    //技师全部联系人
    public static final String URL_GET_TECH_CUSTOMER_USER_ALL_LIST = BASE_URL + "/v2/tech/customer/user/all/list";
    //技师拓客联系人
    public static final String URL_GET_TECH_CUSTOMER_USER_REGISTER_LIST = BASE_URL + "/v2/tech/customer/user/register/list";
    //最近访客
    public static final String URL_GET_CLUB_CUSTOMER_USER_RECENT_LIST = BASE_URL + "/v2/tech/customer/user/recent/list";
    //管理者全部联系人
    public static final String URL_GET_MANAGER_CUSTOMER_USER_ALL_LIST = BASE_URL + "/v2/manager/user/customer/all/list";
    //管理者最近访客
    public static final String URL_GET_MANAGER_CUSTOMER_USER_RECENT_LIST = BASE_URL + "/v2/manager/user/recentvisit/all/list";
    //标签列表
    public static final String URL_GET_MANAGER_TAG_ALL_LIST = BASE_URL + "/v2/manager/user/tag/all/list";
    /**
     * --------------------------------------> notify 通知，推送 <----------------------------------------
     */
    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";
    /**
     * --------------------------------------> permission 权限 <----------------------------------------
     */
    //"/spa-manager/api"
    public static final String URL_MENU_LIST = BASE_URL + "/v2/menu/list";

    public static final String URL_CONTACT_PERMISSION = BASE_URL + "/v2/tech/contact/permission/{id}";
    /**
     * --------------------------------------> 已废弃 <----------------------------------------
     */
    //获取游戏设定
    public static final String URL_CHAT_CREDIT_GAME_SETTING = BASE_URL + "/v2/credit/game/setting";
    //发起游戏邀请
    public static final String URL_CHAT_GAME_DICE_SUBMIT = BASE_URL + "/v2/credit/game/dice/submit";
    //接受游戏
    public static final String URL_CHAT_GAME_DICE_ACCEPT = BASE_URL + "/v2/credit/game/dice/accept";
    //    //标签
//    public static final String URL_GET_CONTACT_MARK = BASE_URL + "/v2/club/impression/list";
//    //修改备注
//    public static final String URL_EDIT_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";
}
