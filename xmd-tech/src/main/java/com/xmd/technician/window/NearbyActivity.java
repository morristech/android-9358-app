package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.technician.Adapter.NearbyCusAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.NearbyCusInfo;
import com.xmd.technician.bean.SayHiNearbyResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.HelloLeftCountResult;
import com.xmd.technician.http.gson.NearbyCusListResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.FixPagerSnapHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by ZR on 17-2-27.
 * 附近的人
 */

public class NearbyActivity extends BaseActivity {
    @BindView(R.id.tv_nearby_desc)
    TextView mDescText;
    @BindView(R.id.list_nearby_customer)
    RecyclerView mCusRecyclerView;

    private static final int DEFAULT_CUS_PAGE_SIZE = 10;
    private SimpleDateFormat formatter;

    private NearbyCusAdapter mCusAdapter;
    private FixPagerSnapHelper mFixSnapHelper;

    private Subscription mGetHelloLeftCountSubscription;
    private Subscription mGetNearbyCusListSubscription;
    private Subscription mSayHiNearbySubscription;

    private List<NearbyCusInfo> mAdapterList = new ArrayList<>();

    private boolean mHasMore = false;
    private int mPageIndex = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        formatter = new SimpleDateFormat(Constant.FORMAT_DATE_TIME);

        ButterKnife.bind(this);
        initView();

        mGetHelloLeftCountSubscription = RxBus.getInstance().toObservable(HelloLeftCountResult.class).subscribe(helloLeftCountResult -> {
            handleHelloLeftCountResult(helloLeftCountResult);
        });

        mGetNearbyCusListSubscription = RxBus.getInstance().toObservable(NearbyCusListResult.class).subscribe(result -> {
            handleNearbyCusListResult(result);
        });

        mSayHiNearbySubscription = RxBus.getInstance().toObservable(SayHiNearbyResult.class).subscribe(result -> {
            handleSayHiNearbyResult(result);
        });

        getHelloLeftCount();
        getNearbyCusList();
    }

    private void initView() {
        setTitle(R.string.main_nearby_title);
        setBackVisible(true);
        setRightVisible(true, R.string.nearby_bar_right_text);

        mFixSnapHelper = new FixPagerSnapHelper();
        mCusAdapter = new NearbyCusAdapter(this);
        mCusAdapter.setCallback((info, position) -> {
            if (HelloSettingManager.getInstance().getTemplateId() <= 3) {
                Intent intent = new Intent(NearbyActivity.this, HelloSettingActivity.class);
                startActivity(intent);
                XToast.show("请先设置打招呼模板！");
                return;
            }
            // 打招呼
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_REQUEST_SAY_HI_TYPE, Constant.REQUEST_SAY_HI_TYPE_NEARBY);
            params.put(RequestConstant.KEY_NEW_CUSTOMER_ID, info.userId);
            params.put(RequestConstant.KEY_USERNAME, info.userName);
            params.put(RequestConstant.KEY_USER_AVATAR, info.userAvatar);
            params.put(RequestConstant.KEY_USER_TYPE, info.userType);
            params.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, info.userEmchatId);
            params.put(ChatConstant.KEY_SAY_HI_POSITION, String.valueOf(position));
            params.put(RequestConstant.KEY_TIME_STAMP, formatter.format(new Date()));
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SAY_HELLO, params);
        });
        mCusRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCusRecyclerView.setAdapter(mCusAdapter);
        mCusRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast && mHasMore && !isLoading) {
                        //加载更多
                        getNearbyCusList();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dx > 0) {
                    //大于0表示正在向右滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0表示停止或向左滚动
                    isSlidingToLast = false;
                }
            }
        });
        mFixSnapHelper.attachToRecyclerView(mCusRecyclerView);
    }

    // 获取附近客户列表
    private void getNearbyCusList() {
        isLoading = true;
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPageIndex));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(DEFAULT_CUS_PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEARBY_CUS_LIST, params);
    }

    // 获取剩余打招呼次数
    private void getHelloLeftCount() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_HELLO_LEFT_COUNT);
    }

    private void saveChatContact(String chatId) {
        Map<String, String> saveParams = new HashMap<>();
        saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, chatId);
        saveParams.put(RequestConstant.KEY_CHAT_MSG_ID, "");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
    }

    // 处理技师打招呼数量
    private void handleHelloLeftCountResult(HelloLeftCountResult result) {
        if (result != null && result.statusCode == 200) {
            updateHelloLeftCount(result.respData);
        } else {
            mDescText.setText("获取打招呼配置失败...");
        }
    }

    private void updateHelloLeftCount(int count) {
        if (count > 0) {
            String temp = "今天还有" + count + "次打招呼的机会哟";
            mDescText.setText(Utils.changeColor(temp, ResourceUtils.getColor(R.color.text_color_yellow), 4, temp.length() - 8));
        } else if (count == 0) {
            mDescText.setText("Sorry，今天打招呼次数已用完");
        } else {
            // -1:代表打招呼不受限
            mDescText.setText("打招呼次数无限制哟，赶紧打招呼吧");
        }
    }

    // 处理附近客户列表
    private void handleNearbyCusListResult(NearbyCusListResult result) {
        isLoading = false;
        // 如果获取成功才更新界面数据
        if (result != null && result.statusCode == 200 && result.respData != null) {
            if (mPageIndex < result.pageCount) {
                mHasMore = true;
                mPageIndex++;
            } else {
                mHasMore = false;
            }
            mAdapterList.addAll(result.respData);
            mCusAdapter.setData(mAdapterList);

            //缓存用户基本信息
            XLogger.d("userService", "update by nearby data");
            for (NearbyCusInfo info : result.respData) {
                User user = new User(info.userId);
                user.setName(info.userName);
                user.setAvatar(info.userAvatar);
                user.setChatId(info.userEmchatId);
                UserInfoServiceImpl.getInstance().saveUser(user);
            }
        } else {
            makeShortToast(result.msg);
        }
    }

    // 打招呼
    private void handleSayHiNearbyResult(SayHiNearbyResult result) {
        if (result != null && result.statusCode == 200) {
            //环信招呼
            HelloSettingManager.getInstance().sendHelloTemplate(UserInfoServiceImpl.getInstance().getUserByChatId(result.userEmchatId));
            //刷新打招呼次数
            updateHelloLeftCount(result.respData.technicianLeft);
            //保存用户好友关系链
            saveChatContact(result.userEmchatId);
            //更新列表状态
            mAdapterList.get(result.cusPosition).userLeftHelloCount = result.respData.customerLeft;
            mAdapterList.get(result.cusPosition).techHelloRecently = true;
            mAdapterList.get(result.cusPosition).lastTechHelloTime = result.cusSayHiTime;
            mCusAdapter.updateCurrentItem(result.cusPosition, result.respData.customerLeft, result.cusSayHiTime);
            // 成功提示
            showToast("打招呼成功");
            ChatUser chatUser = new ChatUser(result.userEmchatId);
            chatUser.setAvatar(result.userAvatar);
            chatUser.setNick(result.userName);
            chatUser.setUserType(ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER);
            UserUtils.saveUser(chatUser);
        } else {
            // 错误提示
            showToast("向客户打招呼失败:" + result.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetHelloLeftCountSubscription,
                mGetNearbyCusListSubscription,
                mSayHiNearbySubscription);
    }

    // 招呼记录
    @OnClick(R.id.toolbar_right)
    public void onRecordClick(View view) {
        Intent intent = new Intent(NearbyActivity.this, HelloRecordActivity.class);
        startActivity(intent);
    }

    // 打招呼设置
    @OnClick(R.id.img_nearby_setting)
    public void settingTemplate(View view) {
        Intent intent = new Intent(NearbyActivity.this, HelloSettingActivity.class);
        startActivity(intent);
    }
}
