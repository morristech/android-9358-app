package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.CheckInfoAdapter;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyConfirmContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.presenter.VerifyConfirmPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-3-14.
 * 批量核销结果
 */

public class VerifyConfirmActivity extends BaseActivity implements VerifyConfirmContract.View {
    private VerifyConfirmContract.Presenter mPresenter;
    private CheckInfoAdapter mAdapter;

    private TextView mSuccessText;
    private TextView mFailedText;
    private RecyclerView mResultListView;
    private Button mContinueBtn;
    private Button mCancelBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_confirm);
        mPresenter = new VerifyConfirmPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "核销结果");

        mAdapter = new CheckInfoAdapter(this);
        mSuccessText = (TextView) findViewById(R.id.tv_success_desc);
        mFailedText = (TextView) findViewById(R.id.tv_failed_desc);
        mContinueBtn = (Button) findViewById(R.id.btn_verify_continue);
        mCancelBtn = (Button) findViewById(R.id.btn_verify_cancel);
        mResultListView = (RecyclerView) findViewById(R.id.rc_list_result);
        mAdapter.setCallBack(new CheckInfoAdapter.OnCheckItemCallBack() {
            @Override
            public void onInfoClick(CheckInfo info) {
            }

            @Override
            public void onInfoSelect(CheckInfo info, boolean selected) {
                mPresenter.onInfoSelected(info, selected);
            }

            @Override
            public void onInfoSelectValid(CheckInfo info) {
                mPresenter.onInfoSelectedValid(info);
            }
        });

        mResultListView.setLayoutManager(new LinearLayoutManager(this));
        mResultListView.addItemDecoration(new CustomRecycleViewDecoration(32));
        mResultListView.setAdapter(mAdapter);
    }

    public void onVerifyContinue(View view) {
        mPresenter.onVerifyContinue();
    }

    public void onVerifyCancel(View view) {
        finishSelf();
    }

    @Override
    public void setPresenter(VerifyConfirmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyEventBack() {
        setResult(RESULT_OK);
        return super.onKeyEventBack();
    }

    @Override
    public void setSuccessText(int count) {
        String temp = "成功核销" + count + "张券";
        mSuccessText.setText(Utils.changeColor(temp, getResources().getColor(R.color.colorPink), 4, temp.length() - 2));
    }

    @Override
    public void setFailedText(int count) {
        String temp = "以下" + count + "张券核销失败，点击继续核销";
        mFailedText.setText(Utils.changeColor(temp, getResources().getColor(R.color.colorPink), 2, temp.length() - 13));
    }

    @Override
    public void setButtonText(int count) {
        mContinueBtn.setText("已选择" + count + "张，继续核销");
        // 未选中时不可操作
        mContinueBtn.setEnabled(count > 0);
    }

    @Override
    public void showVerifyResultList(List<CheckInfo> list) {
        mAdapter.setData(list);
    }
}
