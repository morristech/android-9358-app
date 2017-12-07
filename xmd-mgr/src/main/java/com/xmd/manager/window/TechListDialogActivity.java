package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.adapter.TechBaseAdapter;
import com.xmd.manager.beans.TechBaseInfo;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.TechBaseListResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-11-22.
 * 技师列表
 */

public class TechListDialogActivity extends BaseActivity {
    @BindView(R.id.rv_tech_list)
    RecyclerView mTechList;
    @BindView(R.id.tv_error_desc)
    TextView mErrorDesc;

    private Subscription mGetTechListSubscription;
    private TechBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_list_dialog);
        ButterKnife.bind(this);

        mAdapter = new TechBaseAdapter(this);
        mTechList.setLayoutManager(new LinearLayoutManager(this));
        mTechList.setHasFixedSize(true);
        mTechList.setAdapter(mAdapter);

        mGetTechListSubscription = RxBus.getInstance().toObservable(TechBaseListResult.class).subscribe(
                techBaseListResult -> handleTechListResult(techBaseListResult)
        );

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_BASE_LIST);
    }

    private void handleTechListResult(TechBaseListResult result) {
        if (result.statusCode == 200) {
            if (result.respData != null && !result.respData.isEmpty()) {
                mErrorDesc.setVisibility(View.GONE);
                mTechList.setVisibility(View.VISIBLE);
                mAdapter.setData(result.respData);
            } else {
                mErrorDesc.setText("暂无数据");
                mErrorDesc.setVisibility(View.VISIBLE);
                mTechList.setVisibility(View.GONE);
            }
        } else {
            mErrorDesc.setText(result.msg);
            mErrorDesc.setVisibility(View.VISIBLE);
            mTechList.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechListSubscription);
    }

    @OnClick({R.id.img_dialog_close, R.id.tv_dialog_close})
    public void onTechCancel() {
        finish();
    }

    @OnClick(R.id.tv_dialog_confirm)
    public void onTechConfirm() {
        TechBaseInfo tech = mAdapter.getSelectInfo();
        if (tech == null) {
            XToast.show("请选择技师");
            return;
        } else {
            RxBus.getInstance().post(tech);
            finish();
        }
    }
}
