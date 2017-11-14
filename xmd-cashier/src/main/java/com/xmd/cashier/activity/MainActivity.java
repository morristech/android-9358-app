package com.xmd.cashier.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.contract.MainContract;
import com.xmd.cashier.presenter.MainPresenter;
import com.xmd.cashier.widget.CircleImageView;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.CustomToolbar;


public class MainActivity extends BaseActivity implements MainContract.View {
    private MainContract.Presenter mPresenter;

    private DrawerLayout mDrawerLayoutView;

    private CustomToolbar mToolbar;
    private CircleImageView mToolBarAvatar;
    private TextView mToolBarTitle;

    private LinearLayout mVerifyHeadLayout;
    private TextView mVerifyScan;
    private ClearableEditText mVerifyInput;
    private TextView mVerifyConfirm;

    private RelativeLayout mMainCashierLayout;
    private RelativeLayout mMainMemberLayout;
    private RelativeLayout mMainOrderLayout;
    private RelativeLayout mMainOnlinePayLayout;
    private RelativeLayout mMainRecordLayout;
    private RelativeLayout mMainAssistCashierLayout;

    private NestedScrollView mScrollView;

    private CircleImageView mDrawerAvatar;
    private TextView mDrawerName;
    private TextView mDrawerClubName;
    private TextView mDrawerVersion;

    private int mHeadHeight;

    private TextView mCashierTitle;
    private TextView mCashierSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this, this);

        initView();
        initScrollListener();
        mPresenter.onStart();
        mPresenter.onCreate();
    }

    private void initScrollListener() {
        ViewTreeObserver observer = mVerifyHeadLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mToolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mHeadHeight = mVerifyHeadLayout.getHeight();
                mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        int colorPrimary = MainApplication.getInstance().getApplicationContext().getResources().getColor(R.color.colorPrimary);
                        if (scrollY <= 0) {
                            //设置标题的背景颜色透明
                            mToolbar.setBackgroundColor(MainApplication.getInstance().getApplicationContext().getResources().getColor(R.color.transparent));
                        } else if (scrollY > 0 && scrollY <= mHeadHeight) {
                            //设置背景颜色透明度渐变
                            float scale = (float) scrollY / mHeadHeight;
                            float alpha = (255 * scale);
                            mToolbar.setBackgroundColor(Color.argb((int) alpha, Color.red(colorPrimary), Color.green(colorPrimary), Color.blue(colorPrimary)));
                        } else {
                            //设置普通颜色
                            mToolbar.setBackgroundColor(colorPrimary);
                        }
                    }
                });
            }
        });
    }

    public void initView() {
        mDrawerLayoutView = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (CustomToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolBarAvatar = (CircleImageView) findViewById(R.id.img_main_club);
        mToolBarTitle = (TextView) findViewById(R.id.tv_title);

        mVerifyHeadLayout = (LinearLayout) findViewById(R.id.ly_verify_head);
        mVerifyScan = (TextView) findViewById(R.id.tv_verify_code_scan);
        mVerifyInput = (ClearableEditText) findViewById(R.id.edt_verify_code_input);
        mVerifyConfirm = (TextView) findViewById(R.id.btn_verify_confirm);

        mMainCashierLayout = (RelativeLayout) findViewById(R.id.layout_main_cashier);
        mCashierTitle = (TextView) findViewById(R.id.tv_main_cashier_title);
        mCashierSubTitle = (TextView) findViewById(R.id.tv_main_cashier_sub_title);
        mMainMemberLayout = (RelativeLayout) findViewById(R.id.layout_main_member);
        mMainOrderLayout = (RelativeLayout) findViewById(R.id.layout_main_order);
        mMainOnlinePayLayout = (RelativeLayout) findViewById(R.id.layout_main_online_pay);
        mMainRecordLayout = (RelativeLayout) findViewById(R.id.layout_main_record);
        mMainAssistCashierLayout = (RelativeLayout) findViewById(R.id.layout_main_assist_cashier);

        mDrawerAvatar = (CircleImageView) findViewById(R.id.imv_club_icon);
        mDrawerClubName = (TextView) findViewById(R.id.tv_club_name);
        mDrawerName = (TextView) findViewById(R.id.tv_userName);
        mDrawerVersion = (TextView) findViewById(R.id.tv_versionName);

        mScrollView = (NestedScrollView) findViewById(R.id.scroll_main);

        mToolBarAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickDrawer();
            }
        });

        mMainCashierLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCashierLayoutClick();
            }
        });

        mMainMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMemberLayoutClick();
            }
        });

        mMainOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onOrderLayoutClick();
            }
        });

        mMainOnlinePayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onOnlinePayLayoutClick();
            }
        });

        mMainRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onRecordLayoutClick();
            }
        });

        mMainAssistCashierLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onAssistCashierLayoutClick();
            }
        });

        mVerifyScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onScanClick();
            }
        });

        mVerifyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerifyClick();
            }
        });

        mDrawerVersion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.onClickVersion();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    public void onClickLogout(View view) {
        mPresenter.onClickLogout();
    }

    public void onClickSetting(View view) {
        mPresenter.onClickSetting();
    }

    @Override
    public void onNavigationMenu() {
        mDrawerLayoutView.openDrawer(GravityCompat.START);
    }

    @Override
    public String getVerifyCode() {
        return mVerifyInput.getText().toString().trim();
    }

    @Override
    public void updateAssistCashier(boolean status) {
        if (status) {
            //开通
            mMainAssistCashierLayout.setVisibility(View.VISIBLE);
            mCashierTitle.setText("内网收银");
            mCashierSubTitle.setText("订单、消费、结账");
        } else {
            //关闭
            mMainAssistCashierLayout.setVisibility(View.GONE);
            mCashierTitle.setText("收银");
            mCashierSubTitle.setText("现金、银联、微信、支付宝");
        }
    }

    @Override
    public void showVersionName(String versionName) {
        mDrawerVersion.setText("版本 " + versionName);
    }

    @Override
    public void showUserName(String userName) {
        mDrawerName.setText(userName);
    }

    @Override
    public void showClubName(String clubName) {
        mDrawerClubName.setText(clubName);
        mToolBarTitle.setText(clubName);
    }

    @Override
    public void showClubIcon(String clubIcon) {
        Glide.with(this).load(clubIcon).dontAnimate().placeholder(R.drawable.ic_club).into(mDrawerAvatar);
        Glide.with(this).load(clubIcon).dontAnimate().placeholder(R.drawable.ic_club).into(mToolBarAvatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyEventBack() {
        return mPresenter.onKeyEventBack();
    }
}
