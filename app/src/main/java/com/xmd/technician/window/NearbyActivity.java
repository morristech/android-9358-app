package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.Adapter.NearbyCusAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.NearbyCusInfo;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.HelloLeftCountResult;
import com.xmd.technician.http.gson.HelloTechSayResult;
import com.xmd.technician.http.gson.NearbyCusListResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.FixLinearSnapHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by ZR on 17-2-27.
 * 附近的人
 */

public class NearbyActivity extends BaseActivity {
    @Bind(R.id.tv_nearby_desc)
    TextView mDescText;
    @Bind(R.id.list_nearby_customer)
    RecyclerView mCusRecyclerView;

    private static final int DEFAULT_CUS_PAGE_SIZE = 10;

    private NearbyCusAdapter mCusAdapter;
    private FixLinearSnapHelper mFixSnapHelper;

    private Subscription mGetHelloLeftCountSubscription;
    private Subscription mGetNearbyCusListSubscription;
    private Subscription mTechSayHelloSubscription;

    private List<NearbyCusInfo> mAdapterList = new ArrayList<>();

    private boolean mHasMore = false;
    private int mPageIndex = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        ButterKnife.bind(this);
        initView();

        mGetHelloLeftCountSubscription = RxBus.getInstance().toObservable(HelloLeftCountResult.class).subscribe(helloLeftCountResult -> {
            handleHelloLeftCountResult(helloLeftCountResult);
        });

        mGetNearbyCusListSubscription = RxBus.getInstance().toObservable(NearbyCusListResult.class).subscribe(result -> {
            handleNearbyCusListResult(result);
        });

        mTechSayHelloSubscription = RxBus.getInstance().toObservable(HelloTechSayResult.class).subscribe(helloTechSayResult -> {
            handleTechSayHelloResult(helloTechSayResult);
        });

        getHelloLeftCount();
        getNearbyCusList();
    }

    private void initView() {
        setTitle(R.string.main_nearby_title);
        setBackVisible(true);
        setRightVisible(true, R.string.nearby_bar_right_text);

        mFixSnapHelper = new FixLinearSnapHelper();
        mCusAdapter = new NearbyCusAdapter(this);
        mCusAdapter.setCallback(info -> {
            // 打招呼
            Map<String, Object> params = new HashMap<>();
            params.put(RequestConstant.KEY_NEARBY_CUSTOMER_INFO, info);
            params.put(RequestConstant.KEY_HELLO_TEMPLATE_ID, String.valueOf(HelloSettingManager.getInstance().getTemplateId()));
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

    // 处理技师打招呼数量
    private void handleHelloLeftCountResult(HelloLeftCountResult result) {
        if (result != null && result.statusCode == 200) {
            if (result.respData > 0) {
                String temp = "今天还有" + result.respData + "次打招呼的机会哟";
                mDescText.setText(Utils.changeColor(temp, ResourceUtils.getColor(R.color.text_color_yellow), 4, temp.length() - 8));
            } else if (result.respData == 0) {
                mDescText.setText("Sorry，今天打招呼次数已用完");
            } else {
                // -1:代表打招呼不受限
                mDescText.setText("打招呼次数无限制哟，赶紧打招呼吧");
            }
        } else {
            mDescText.setText("获取打招呼配置失败...");
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
        }
    }

    // 打招呼
    private void handleTechSayHelloResult(HelloTechSayResult result) {
        if (result != null && result.statusCode == 200) {
            NearbyCusInfo info = result.customerInfo;
            // 调用环信打招呼,如果有图片需要发送图片
            emSendTextMessage(HelloSettingManager.getInstance().getTemplateContentText().replace(getResources().getString(R.string.hello_setting_content_replace), info.userName), info.userEmchatId);
            if (!TextUtils.isEmpty(HelloSettingManager.getInstance().getTemplateImageCachePath())) {
                emSendImageMessage(HelloSettingManager.getInstance().getTemplateImageCachePath(), info.userEmchatId);
            }
            showToast("打招呼成功");
            // 刷新打招呼次数
            getHelloLeftCount();
        } else {
            // 错误提示
            showToast("向客户打招呼失败:" + result.msg);
        }
    }

    // 使用环信发送图片
    private void emSendImageMessage(String imagePath, String emChatTarget) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, emChatTarget);
        emSendMessage(message);
    }


    // 使用环信发送文本消息
    private void emSendTextMessage(String content, String emChatTarget) {
        EMMessage message = EMMessage.createTxtSendMessage(content, emChatTarget);
        emSendMessage(message);
    }

    private void emSendMessage(EMMessage message) {
        message.setAttribute(ChatConstant.KEY_TECH_ID, SharedPreferenceHelper.getUserId());
        message.setAttribute(ChatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(ChatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(ChatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        message.setAttribute(ChatConstant.KEY_SERIAL_NO, SharedPreferenceHelper.getSerialNo());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetHelloLeftCountSubscription,
                mGetNearbyCusListSubscription,
                mTechSayHelloSubscription);
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
