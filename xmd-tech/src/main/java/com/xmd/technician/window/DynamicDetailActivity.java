package com.xmd.technician.window;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.technician.R;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.DropDownMenuDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 2016/10/25.
 */
public class DynamicDetailActivity extends BaseListActivity<DynamicDetail> {


    @BindView(R.id.toolbar_right_img)
    ImageView toolbarRightImg;
    @BindView(R.id.contact_more)
    LinearLayout toolbarMore;

    private UserInfoService userService = UserInfoServiceImpl.getInstance();
    private User mUser;
    private Subscription mGetRecentlyStatusListSubscription;
    private int mCurrentType = 0;

    @Override
    protected void dispatchRequest() {
        getDynamicList(mCurrentType);
    }

    @Override
    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.all_recent_status_activity));
        setBackVisible(true);
        toolbarRightImg.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_filter));
        toolbarMore.setVisibility(View.VISIBLE);

        mGetRecentlyStatusListSubscription = RxBus.getInstance().toObservable(DynamicListResult.class).subscribe(
                dynamicListResult -> handleGetPaidCouponUserDetailResult(dynamicListResult)
        );
    }

    private void handleGetPaidCouponUserDetailResult(DynamicListResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            XLogger.d("userService", "update by customer detail data");
            for (DynamicDetail dynamic : result.respData) {
                if (!TextUtils.isEmpty(dynamic.id)) {
                    if (Utils.isNotEmpty(dynamic.userId)) {
                        mUser = new User(dynamic.userId);
                        mUser.setName(dynamic.userName);
                        mUser.setChatId(dynamic.userEmchatId);
                        mUser.setAvatar(dynamic.avatarUrl);
                        mUser.setNoteName("");
                        userService.saveUser(mUser);
                    }
                }
            }

            onGetListSucceeded(result.pageCount, result.respData);
        }
    }

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_dynamic_detail);
    }

    @OnClick(R.id.contact_more)
    public void filtrateDynamic() {
        final String[] items = new String[]{ResourceUtils.getString(R.string.filtrate_recent_status_all), ResourceUtils.getString(R.string.filtrate_recent_status_comment), ResourceUtils.getString(R.string.filtrate_recent_status_collect),
                ResourceUtils.getString(R.string.filtrate_recent_status_coupon), ResourceUtils.getString(R.string.filtrate_recent_status_paid_coupon), ResourceUtils.getString(R.string.filtrate_recent_status_reward)};
        DropDownMenuDialog.getDropDownMenuDialog(DynamicDetailActivity.this, items, (index -> {
            switch (index) {
                case 0:
                    getDynamicList(0);
                    break;
                case 1:
                    getDynamicList(1);
                    break;
                case 2:
                    getDynamicList(2);
                    break;
                case 3:
                    getDynamicList(3);
                    break;
                case 4:
                    getDynamicList(4);
                    break;
                case 5:
                    getDynamicList(5);
                    break;

            }
        })).show(toolbarMore);
    }

    @Override
    public void onSayHiButtonClicked(DynamicDetail bean) {
        super.onSayHiButtonClicked(bean);
//        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(bean.userEmchatId,
//                bean.userName, bean.imageUrl, ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER));
        EventBus.getDefault().post(new EventStartChatActivity(bean.userEmchatId));
    }

    public void getDynamicList(int type) {
        mCurrentType = type;
        String status_type = String.valueOf(type);
        Map<String, String> param = new HashMap<>();
        param.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        param.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        param.put(RequestConstant.KEY_TECH_DYNAMIC_TYPE, status_type);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_DYNAMIC_LIST, param);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRecentlyStatusListSubscription);
    }

}
