package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerDiscountListAdapter;
import com.xmd.cashier.adapter.InnerVerifiedListAdapter;
import com.xmd.cashier.adapter.TagAdapter;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerDiscountContract;
import com.xmd.cashier.dal.bean.OrderDiscountInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.presenter.InnerDiscountPresenter;
import com.xmd.cashier.widget.CustomEditText;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomMoneyEditText;
import com.xmd.cashier.widget.FlowLayout;
import com.xmd.cashier.widget.FullyGridLayoutManager;
import com.xmd.cashier.widget.OnMyKeyboardCallback;
import com.xmd.cashier.widget.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-11.
 */

public class InnerDiscountActivity extends BaseActivity implements InnerDiscountContract.View {
    private InnerDiscountContract.Presenter mPresenter;

    private CustomKeyboardView mKeyboardView;
    private CustomMoneyEditText mReductionText;
    private CustomEditText mVerifyText;
    private ImageView mVerifySearch;
    private ImageView mVerifyDelete;

    private InnerDiscountListAdapter mInnerDiscountListAdapter;
    private InnerVerifiedListAdapter mInnerVerifiedListAdapter;
    private RecyclerView mVerifyList;
    private LinearLayout mVerifiedTitle;
    private TextView mVerifiedTitleDesc;
    private RecyclerView mVerifiedList;

    private LinearLayout mOperateLayout;
    private TextView mDiscountAmountText;
    private TextView mDiscountConfirm;

    private TagFlowLayout mFlowLayout;
    private TagAdapter<String> mFlowAdapter;
    private List<String> mTagList = new ArrayList<>();
    private TextView mTagView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_discount);
        mPresenter = new InnerDiscountPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "添加优惠");

        mInnerDiscountListAdapter = new InnerDiscountListAdapter(this);
        mInnerVerifiedListAdapter = new InnerVerifiedListAdapter(this);

        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setKeyLableMap("收银", "完成");
        mKeyboardView.setKeyTextColor("完成", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("完成", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));

        mOperateLayout = (LinearLayout) findViewById(R.id.layout_operate);

        mReductionText = (CustomMoneyEditText) findViewById(R.id.tv_reduction_discount);
        mReductionText.setInputType(0);
        mReductionText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(InnerDiscountActivity.this, new OnMyKeyboardCallback.Callback() {
                    @Override
                    public boolean onKeyEnter() {
                        hideKeyboard();
                        return true;
                    }
                }));
                showKeyboard();
                mReductionText.requestFocus();
                return false;
            }
        });
        mReductionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.onReductionChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVerifyText = (CustomEditText) findViewById(R.id.edt_search_discount);
        mVerifyText.setInputType(0);
        mVerifyText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(InnerDiscountActivity.this, new OnMyKeyboardCallback.Callback() {
                    @Override
                    public boolean onKeyEnter() {
                        hideKeyboard();
                        if (!TextUtils.isEmpty(getSearchContent())) {
                            mPresenter.searchDiscount();
                        }
                        return true;
                    }
                }));
                showKeyboard();
                mVerifyText.requestFocus();
                return false;
            }
        });

        mVerifySearch = (ImageView) findViewById(R.id.img_search_confirm);
        mVerifyDelete = (ImageView) findViewById(R.id.img_search_delete);
        mVerifyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(InnerDiscountActivity.this, new OnMyKeyboardCallback.Callback() {
                    @Override
                    public boolean onKeyEnter() {
                        hideKeyboard();
                        if (!TextUtils.isEmpty(getSearchContent())) {
                            mPresenter.searchDiscount();
                        }
                        return true;
                    }
                }));
                showKeyboard();
                mVerifyText.setText("");
                mVerifyText.requestFocus();
            }
        });
        mVerifySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mPresenter.searchDiscount();
            }
        });

        mVerifyList = (RecyclerView) findViewById(R.id.rv_search_discount);
        mVerifiedTitle = (LinearLayout) findViewById(R.id.ll_verified_title);
        mVerifiedTitleDesc = (TextView) findViewById(R.id.tv_verified_title_desc);
        mVerifiedList = (RecyclerView) findViewById(R.id.rv_verified_discount);

        mInnerDiscountListAdapter.setListener(new InnerDiscountListAdapter.OnClickListener() {
            @Override
            public void onItemSelect(VerificationItem item, int position) {
                mPresenter.onVerifySelect(item, position);
            }

            @Override
            public void onItemClick(VerificationItem item, int position) {
                hideKeyboard();
                mPresenter.onVerifyClick(item, position);
            }
        });
        mVerifyList.setNestedScrollingEnabled(false);
        mVerifyList.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mVerifyList.setAdapter(mInnerDiscountListAdapter);
        mVerifyList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mVerifiedList.setNestedScrollingEnabled(false);
        mVerifiedList.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mVerifiedList.setAdapter(mInnerVerifiedListAdapter);
        mVerifiedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mDiscountAmountText = (TextView) findViewById(R.id.tv_discount_amount);
        mDiscountConfirm = (TextView) findViewById(R.id.tv_discount_confirm);
        mDiscountConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mPresenter.confirmDiscount();
            }
        });

        mFlowLayout = (TagFlowLayout) findViewById(R.id.flow_tag_verify);
        mFlowAdapter = new TagAdapter<String>(mTagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                mTagView = new TextView(InnerDiscountActivity.this);
                mTagView.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
                mTagView.setBackgroundResource(R.drawable.bg_area_tag);
                mTagView.setPadding(ResourceUtils.getDimenInt(R.dimen.activity_normal_margin),
                        ResourceUtils.getDimenInt(R.dimen.activity_little_margin),
                        ResourceUtils.getDimenInt(R.dimen.activity_normal_margin),
                        ResourceUtils.getDimenInt(R.dimen.activity_little_margin));
                mTagView.setText(s);
                return mTagView;
            }
        };
        mFlowLayout.setAdapter(mFlowAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(InnerDiscountContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setReductionMoney(int value) {
        mReductionText.setMoney(value);
    }

    @Override
    public void setDiscountMoney(int value) {
        mDiscountAmountText.setText("￥" + Utils.moneyToStringEx(value));
    }

    @Override
    public int getReductionMoney() {
        return mReductionText.getMoney();
    }

    @Override
    public String getSearchContent() {
        return mVerifyText.getText().toString().trim();
    }

    @Override
    public void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
        mOperateLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mOperateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVerifyData(List<VerificationItem> list) {
        mInnerDiscountListAdapter.setData(list);
        mInnerDiscountListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showVerifyList() {
        mVerifyList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVerifyList() {
        mVerifyList.setVisibility(View.GONE);
    }

    @Override
    public void updateVerifyItem(int position) {
        mInnerDiscountListAdapter.notifyItemChanged(position);
    }

    @Override
    public void showVerifiedLayout(List<OrderDiscountInfo> list) {
        mVerifiedTitle.setVisibility(View.VISIBLE);
        String title = "已核销券(" + list.size() + ")张";
        mVerifiedTitleDesc.setText(Utils.changeColor(title, getResources().getColor(R.color.colorAccent), 5, title.length() - 2));
        mVerifiedList.setVisibility(View.VISIBLE);
        mInnerVerifiedListAdapter.setData(list);
        mInnerVerifiedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideVerifiedLayout() {
        mVerifiedTitle.setVisibility(View.GONE);
        mVerifiedList.setVisibility(View.GONE);
    }

    @Override
    public void showFlowData(List<String> list) {
        if (list.isEmpty()) {
            mFlowLayout.setVisibility(View.GONE);
        } else {
            mFlowLayout.setVisibility(View.VISIBLE);
            mTagList.clear();
            mTagList.addAll(list);
            mFlowAdapter.notifyDataChanged();
        }
    }
}
