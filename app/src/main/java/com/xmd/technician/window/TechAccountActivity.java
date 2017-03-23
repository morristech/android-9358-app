package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.TechAccountListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.TechAccountBean;
import com.xmd.technician.http.gson.TechAccountListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by sdcm on 17-3-10.
 */

public class TechAccountActivity extends BaseActivity {

    @Bind(R.id.view_recycler)
    RecyclerView viewRecycler;

    private Subscription mTechAccountListSubscription;
    private TechAccountListAdapter mTechAccountListAdapter;
    private List<TechAccountBean> mTechAccountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_account);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(R.string.personal_fragment_layout_account);
        setBackVisible(true);
        mTechAccountList = new ArrayList<>();
        mTechAccountListSubscription = RxBus.getInstance().toObservable(TechAccountListResult.class).subscribe(
                techAccountListResult -> handleTechAccountList(techAccountListResult)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_ACCOUNT_LIST);
        viewRecycler.setLayoutManager(new LinearLayoutManager(this));
        viewRecycler.setItemAnimator(new DefaultItemAnimator());
        viewRecycler.setHasFixedSize(true);
        mTechAccountListAdapter = new TechAccountListAdapter(TechAccountActivity.this, mTechAccountList);
        viewRecycler.setAdapter(mTechAccountListAdapter);
        mTechAccountListAdapter.setOnWithDrawClickedListener(new TechAccountListAdapter.CallBack() {
            @Override
            public void onWithDrawClicked(String type) {
                showDrawMoneyView();
            }

            @Override
            public void onItemClicked(TechAccountBean bean) {
                gotoDetailActivity(bean.accountType,bean.name);
            }
        });
    }

    private void handleTechAccountList(TechAccountListResult result) {
        if (result.statusCode == 200) {
            mTechAccountList.clear();
            mTechAccountList.addAll(result.respData.accountList);
            mTechAccountListAdapter.setData(mTechAccountList, result.respData.withdrawal);
        } else {
            makeShortToast(result.msg);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mTechAccountListSubscription);
    }

    private void gotoDetailActivity(String type,String name) {
        Intent intent = new Intent(TechAccountActivity.this, ConsumeDetailActivity.class);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_TYPE, type);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_NAME,name);
        startActivity(intent);
    }

    private void showDrawMoneyView() {
        Intent intent = new Intent(TechAccountActivity.this, IntroduceAccountActivity.class);
        startActivity(intent);
    }
}
