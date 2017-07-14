package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.xmd.technician.Adapter.ServiceAdapter;
import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.UpdateServiceResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ServiceItemActivity extends BaseActivity {

    @BindView(R.id.list_view)
    RecyclerView mListView;
    @BindView(R.id.confirm)
    Button mConfirmBtn;
    @BindView(R.id.header_container)
    View mHearContainer;
    @BindView(R.id.empty_view_widget)
    EmptyView mEmptyView;

    private ServiceAdapter mAdapter;

    private Subscription mSubscription;
    private Subscription mUpdateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_service);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_service_items);
        setBackVisible(true);

        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ServiceAdapter();
        mListView.setAdapter(mAdapter);

        mEmptyView.setEmptyTip(getString(R.string.no_service_item));

        mSubscription = RxBus.getInstance().toObservable(ServiceResult.class).subscribe(
                serviceResult -> initData(serviceResult));

        mUpdateSubscription = RxBus.getInstance().toObservable(UpdateServiceResult.class).subscribe(result -> finish());

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSubscription);
        RxBus.getInstance().unsubscribe(mUpdateSubscription);
    }

    private void initData(ServiceResult result) {
        if (result.respData == null || result.respData.isEmpty()) {
            mEmptyView.setStatus(EmptyView.Status.Empty);
            mConfirmBtn.setVisibility(View.INVISIBLE);
            mHearContainer.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setStatus(EmptyView.Status.Gone);
            mConfirmBtn.setVisibility(View.VISIBLE);
            mHearContainer.setVisibility(View.VISIBLE);
            mAdapter.refreshDataSet(result.respData);
        }
    }

    @OnClick(R.id.confirm)
    public void updateServiceList() {
        String ids = mAdapter.getSelectedIds();
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IDS, ids);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_SERVICE_ITEM_LIST, params);
    }
}
