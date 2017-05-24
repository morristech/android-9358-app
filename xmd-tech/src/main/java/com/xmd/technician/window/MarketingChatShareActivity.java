package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.technician.Adapter.ExpandableListViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.MarketingChatShareBean;
import com.xmd.technician.http.gson.MarketingListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-15.
 */

public class MarketingChatShareActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.expandable_list_view)
    ExpandableListView expandableListView;
    @Bind(R.id.toolbar_right_share)
    TextView toolbarRightShare;
    @Bind(R.id.view_emptyView)
    EmptyView viewEmptyView;

    private Subscription mMarketingChatShareActivitySubscription;
    private ExpandableListViewAdapter expandableAdapter;
    private List<List<MarketingChatShareBean>> marketingList;
    private List<MarketingChatShareBean> mSelectedBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_chat_share);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackVisible(true);
        setTitle("营销活动");
        toolbarRightShare.setVisibility(View.VISIBLE);
        toolbarRightShare.setOnClickListener(this);
        toolbarRightShare.setEnabled(false);
        mMarketingChatShareActivitySubscription = RxBus.getInstance().toObservable(MarketingListResult.class).subscribe(
                result -> handlerMarketingResult(result)
        );
        mSelectedBeans = new ArrayList<>();
        expandableListView.setGroupIndicator(null);
        expandableListView.setDivider(null);
        expandableAdapter = new ExpandableListViewAdapter(MarketingChatShareActivity.this);
        expandableListView.setAdapter(expandableAdapter);
        expandableAdapter.setChildrenClickedInterface(new ExpandableListViewAdapter.OnChildrenClicked() {
            @Override
            public void onChildrenClickedListener(MarketingChatShareBean bean, int groupPosition, int childPosition, boolean isSelected) {
                if (isSelected) {
                    mSelectedBeans.add(bean);
                    marketingList.get(groupPosition).get(childPosition).selectedStatus = 1;
                    expandableAdapter.refreshChildData(marketingList);
                } else {
                    marketingList.get(groupPosition).get(childPosition).selectedStatus = 0;
                    expandableAdapter.refreshChildData(marketingList);
                    mSelectedBeans.remove(bean);
                }
                if (mSelectedBeans.size() > 0) {
                    toolbarRightShare.setEnabled(true);
                    toolbarRightShare.setText(String.format("分享(%s)", mSelectedBeans.size()));
                } else {
                    toolbarRightShare.setEnabled(false);
                    toolbarRightShare.setText("分享");
                }

            }
        });
        viewEmptyView.setStatus(EmptyView.Status.Loading);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_MARKETING_LIST);
    }

    private void handlerMarketingResult(MarketingListResult result) {
        if (result.statusCode == 200) {
            if(result.respData.size()==0){
                viewEmptyView.setStatus(EmptyView.Status.Empty);
                return;
            }
            viewEmptyView.setStatus(EmptyView.Status.Gone);
            if (marketingList == null) {
                marketingList = new ArrayList<>();
            } else {
                marketingList.clear();
            }
            for (int i = 0; i < result.respData.size(); i++) {
                marketingList.add(result.respData.get(i).list);
            }
            expandableAdapter.setData(result.respData, marketingList);
            if(marketingList.size()>0){
                expandableListView.expandGroup(0, true);
            }else{
                viewEmptyView.setStatus(EmptyView.Status.Empty);
            }


        }else{
            viewEmptyView.setStatus(EmptyView.Status.Failed);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_right_share) {
            Intent intent = new Intent();
            intent.putExtra(TechChatActivity.REQUEST_MARKETING_TYPE, (ArrayList<? extends Parcelable>) mSelectedBeans);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mMarketingChatShareActivitySubscription);
    }
}
