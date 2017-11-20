package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.ExInnerRoomAdapter;
import com.xmd.cashier.adapter.ExInnerTechStatusAdapter;
import com.xmd.cashier.adapter.InnerHandAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerSelectContract;
import com.xmd.cashier.dal.bean.ExInnerRoomInfo;
import com.xmd.cashier.dal.bean.ExInnerTechStatusInfo;
import com.xmd.cashier.dal.bean.InnerHandInfo;
import com.xmd.cashier.dal.bean.InnerRoomInfo;
import com.xmd.cashier.dal.bean.InnerTechInfo;
import com.xmd.cashier.presenter.InnerSelectPresenter;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.StepView;

import java.util.List;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by zr on 17-11-2.
 * 结帐方式
 */

public class InnerSelectActivity extends BaseActivity implements InnerSelectContract.View {
    private InnerSelectContract.Presenter mPresenter;

    private ClearableEditText mSearchText;
    private Button mSearchBtn;

    private Button mConfirmBtn;

    private RecyclerView mContentList;

    private LinearLayout mSumLayout;
    private TextView mSumText;

    private InnerHandAdapter mHandAdapter;
    private ExInnerRoomAdapter mExRoomAdapter;
    private ExInnerTechStatusAdapter mExTechStatusAdapter;

    private TextView mNaviRoom;
    private TextView mNaviHand;
    private TextView mNaviTech;

    private StepView mStepView;

    private ImageView mRecordImg;
    private QBadgeView mBadgeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_select);
        mPresenter = new InnerSelectPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    private void initView() {
        showToolbar(R.id.toolbar, "收银");

        mStepView = (StepView) findViewById(R.id.sv_step_select);
        mRecordImg = (ImageView) findViewById(R.id.img_record);
        mBadgeView = new QBadgeView(this);

        mNaviRoom = (TextView) findViewById(R.id.tv_navigation_room);
        mNaviHand = (TextView) findViewById(R.id.tv_navigation_hand);
        mNaviTech = (TextView) findViewById(R.id.tv_navigation_tech);

        mSumLayout = (LinearLayout) findViewById(R.id.layout_select_sum);
        mSumText = (TextView) findViewById(R.id.tv_select_sum);
        mSumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onOrderSelect();
            }
        });

        mConfirmBtn = (Button) findViewById(R.id.btn_select_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onOrderConfirm();
            }
        });
        mSearchText = (ClearableEditText) findViewById(R.id.edt_search_content);
        mSearchBtn = (Button) findViewById(R.id.btn_on_search);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onOrderSearch();
            }
        });

        mContentList = (RecyclerView) findViewById(R.id.rv_select_content);

        mNaviRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onNaviRoomClick();
            }
        });

        mNaviHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onNaviHandClick();
            }
        });

        mNaviTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onNaviTechClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setPresenter(InnerSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public String returnSearchText() {
        return mSearchText.getText().toString().trim();
    }

    @Override
    public void showExRoomData(List<ExInnerRoomInfo> list) {
        if (mExRoomAdapter != null) {
            mExRoomAdapter.setData(list);
        }
    }

    @Override
    public void updateRoom(int position) {
        if (mExRoomAdapter != null) {
            mExRoomAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void updateAllRoom() {
        if (mExRoomAdapter != null) {
            mExRoomAdapter.updateData();
        }
    }

    @Override
    public void showHandData(List<InnerHandInfo> list) {
        if (mHandAdapter != null) {
            mHandAdapter.setData(list);
        }
    }

    @Override
    public void updateHand(int position) {
        if (mHandAdapter != null) {
            mHandAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void updateAllHand() {
        if (mHandAdapter != null) {
            mHandAdapter.updateData();
        }
    }


    @Override
    public void showTechStatusData(List<ExInnerTechStatusInfo> techStatusInfos) {
        if (mExTechStatusAdapter != null) {
            mExTechStatusAdapter.setData(techStatusInfos);
        }
    }

    @Override
    public void showSum(String text) {
        mSumLayout.setVisibility(View.VISIBLE);
        mSumText.setText(text);
    }

    @Override
    public void hideSum() {
        mSumLayout.setVisibility(View.GONE);
        mSumText.setText("");
    }

    @Override
    public void initRoom() {
        mNaviRoom.setBackgroundColor(ResourceUtils.getColor(R.color.colorAccent));
        mNaviRoom.setTextColor(ResourceUtils.getColor(R.color.colorWhite));

        mNaviHand.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviHand.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mHandAdapter = null;
        mNaviTech.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviTech.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mExTechStatusAdapter = null;

        mSearchText.setText("");
        mSearchText.setHint("请输入房间号");
        mExRoomAdapter = new ExInnerRoomAdapter(this);
        mExRoomAdapter.setExCallBack(new ExInnerRoomAdapter.ExCallBack() {
            @Override
            public void onSectionClick(InnerRoomInfo roomInfo, int sectionPosition) {
                mPresenter.onRoomSelect(roomInfo, sectionPosition);
            }
        });
        mContentList.setAdapter(mExRoomAdapter);
        mContentList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initHand() {
        mNaviHand.setBackgroundColor(ResourceUtils.getColor(R.color.colorAccent));
        mNaviHand.setTextColor(ResourceUtils.getColor(R.color.colorWhite));

        mNaviRoom.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviRoom.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mExRoomAdapter = null;
        mNaviTech.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviTech.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mExTechStatusAdapter = null;

        mSearchText.setText("");
        mSearchText.setHint("请输入手牌号");
        mHandAdapter = new InnerHandAdapter(this);
        mHandAdapter.setCallBack(new InnerHandAdapter.CallBack() {
            @Override
            public void onItemClick(InnerHandInfo info, int position) {
                mPresenter.onHandSelect(info, position);
            }
        });
        mContentList.setLayoutManager(new GridLayoutManager(this, 4));
        mContentList.setAdapter(mHandAdapter);
    }

    @Override
    public void initTech() {
        mNaviTech.setBackgroundColor(ResourceUtils.getColor(R.color.colorAccent));
        mNaviTech.setTextColor(ResourceUtils.getColor(R.color.colorWhite));

        mNaviRoom.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviRoom.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mExRoomAdapter = null;
        mNaviHand.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        mNaviHand.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mHandAdapter = null;

        mSearchText.setText("");
        mSearchText.setHint("请输入技师编号");
        mExTechStatusAdapter = new ExInnerTechStatusAdapter(this);
        mExTechStatusAdapter.setCallBack(new ExInnerTechStatusAdapter.ExInnerStatusCallBack() {
            @Override
            public void onExStatusClick(InnerTechInfo info) {
                mPresenter.onTechSelect(info);
            }
        });
        mContentList.setLayoutManager(new LinearLayoutManager(this));
        mContentList.setAdapter(mExTechStatusAdapter);
    }

    public void onClickInnerRecord(View view) {
        mPresenter.onClickRecord();
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    @Override
    public void showExitAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void showStepView() {
        mStepView.setSteps(AppConstants.INNER_PAY_STEPS);
        mStepView.selectedStep(1);
    }

    @Override
    public void initSearch() {
        mSearchText.setText("");
    }

    @Override
    public void showBadgeView(int count) {
        mBadgeView.bindTarget(mRecordImg)
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setBadgeNumber(count);
    }

    @Override
    public void hideBadgeView() {
        if (mBadgeView != null) {
            mBadgeView.hide(true);
        }
    }
}
