package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.technician.Adapter.ChatShareAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.OnceCardHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-15.
 */

public class MallDiscountChatShareActivity extends BaseActivity implements View.OnClickListener, ChatShareAdapter.OnItemClickListener {

    @Bind(R.id.img_index)
    ImageView imgIndex;
    @Bind(R.id.ll_title_view)
    LinearLayout llTitleView;
    @Bind(R.id.view_emptyView)
    EmptyView viewEmptyView;
    @Bind(R.id.list)
    RecyclerView mListView;

    private static List<OnceCardItemBean> OnceCardItemBeanList;

    private ChatShareAdapter adapter;
    private List<OnceCardItemBean> mSelectedOnceCardItemBean;
    private TextView sentMessage;
    private OnceCardItemBean info;
    protected LinearLayoutManager mLayoutManager;
    private OnceCardHelper mOnceCardHelper;
    private Subscription mOnceCardListSubscription;
    private boolean isOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_discount_chat_share);
        ButterKnife.bind(this);
        sentMessage = (TextView) findViewById(R.id.toolbar_right_share);
        sentMessage.setVisibility(View.VISIBLE);
        sentMessage.setEnabled(false);
        sentMessage.setOnClickListener(this);
        initView();
    }

    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.mall_discount_title));
        setBackVisible(true);
        mOnceCardListSubscription = RxBus.getInstance().toObservable(OnceCardResult.class).subscribe(
                onceCardResult -> handleOnceCardListResult(onceCardResult)
        );
        isOpen = true;
        OnceCardItemBeanList = new ArrayList<>();
        mSelectedOnceCardItemBean = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatShareAdapter(MallDiscountChatShareActivity.this, OnceCardItemBeanList);
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        viewEmptyView.setStatus(EmptyView.Status.Loading);
        getOnceCardListData();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.toolbar_right_share) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(TechChatActivity.REQUEST_PREFERENTIAL_TYPE, (ArrayList<? extends Parcelable>) mSelectedOnceCardItemBean);
            setResult(RESULT_OK, resultIntent);
        }
        this.finish();

    }


    private void handleOnceCardListResult(OnceCardResult onceCardResult) {
        if (mOnceCardHelper == null) {
            mOnceCardHelper = new OnceCardHelper();
        }

        if (onceCardResult.statusCode == 200) {
            if (onceCardResult.respData.activityList.size() == 0) {
                viewEmptyView.setStatus(EmptyView.Status.Empty);
            }
            viewEmptyView.setStatus(EmptyView.Status.Gone);
            OnceCardItemBeanList = mOnceCardHelper.getCardItemBeanList(onceCardResult);
            for (OnceCardItemBean info : OnceCardItemBeanList) {
                info.selectedStatus = 1;
            }
            adapter.setData(OnceCardItemBeanList);
        } else {
            viewEmptyView.setStatus(EmptyView.Status.Failed);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOnceCardListSubscription);
    }


    private void getOnceCardListData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(50));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL, params);
    }

    @Override
    public void onItemCheck(Object bean, int position, boolean isChecked) {
        info = (OnceCardItemBean) bean;
        if (isChecked) {
            for (int i = 0; i < mSelectedOnceCardItemBean.size(); i++) {
                if (mSelectedOnceCardItemBean.get(i).id.equals(info.id)) {
                    mSelectedOnceCardItemBean.remove(mSelectedOnceCardItemBean.get(i));
                    break;
                }
            }
            info.selectedStatus = 1;
            OnceCardItemBeanList.set(position, info);
        } else {
            info.selectedStatus = 2;
            OnceCardItemBeanList.set(position, info);
            mSelectedOnceCardItemBean.add(info);
        }
        if (mSelectedOnceCardItemBean.size() > 0) {
            sentMessage.setEnabled(true);
            sentMessage.setText(String.format("分享(%s)", String.valueOf(mSelectedOnceCardItemBean.size())));
        } else {
            sentMessage.setEnabled(false);
            sentMessage.setText("分享");
        }
        adapter.notifyItemChanged(position);
    }

    @OnClick(R.id.ll_title_view)
    public void onViewClicked() {
        if (isOpen) {
            mListView.setVisibility(View.INVISIBLE);
            imgIndex.setImageResource(R.drawable.icon_up);
            isOpen = false;
        } else {
            mListView.setVisibility(View.VISIBLE);
            imgIndex.setImageResource(R.drawable.arrow_down);
            isOpen = true;

        }
    }
}
