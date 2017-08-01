package com.xmd.cashier.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.McardInfoContract;
import com.xmd.cashier.dal.event.CardFinishEvent;
import com.xmd.cashier.presenter.McardInfoPresenter;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.StepView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * Created by zr on 17-7-11.
 * 开卡填写资料
 */

public class McardInfoActivity extends BaseActivity implements McardInfoContract.View {
    private McardInfoContract.Presenter mPresenter;

    private ClearableEditText mNameInput;
    private ClearableEditText mPhoneInput;
    private RadioGroup mGenderGroup;
    private RadioButton mMaleButton;
    private RadioButton mFemaleButton;
    private TextView mBirthText;

    private Button mInfoConfirm;

    private TimePickerView mPickerView;

    private StepView mStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcard_info);
        mPresenter = new McardInfoPresenter(this, this);
        initView();
        initTimePicker();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员开卡");
        mStepView = (StepView) findViewById(R.id.sv_step_info);
        mNameInput = (ClearableEditText) findViewById(R.id.edt_info_name);
        mPhoneInput = (ClearableEditText) findViewById(R.id.edt_info_phone);
        mGenderGroup = (RadioGroup) findViewById(R.id.rg_info_gender);
        mFemaleButton = (RadioButton) findViewById(R.id.rb_info_gender_female);
        mMaleButton = (RadioButton) findViewById(R.id.rb_info_gender_male);
        mBirthText = (TextView) findViewById(R.id.tv_info_birthday);

        mInfoConfirm = (Button) findViewById(R.id.btn_mcard_info_confirm);

        mInfoConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
            }
        });

        mBirthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mNameInput.getWindowToken(), 0);
                }
                mPickerView.show();
            }
        });

        mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                String gender = AppConstants.MEMBER_GENDER_MALE;
                int buttonId = group.getCheckedRadioButtonId();
                if (buttonId == mFemaleButton.getId()) {
                    gender = AppConstants.MEMBER_GENDER_FEMALE;
                }
                mPresenter.onGenderSelect(gender);
            }
        });
        mMaleButton.setChecked(true);

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onNameChange(mNameInput.getText().toString());
            }
        });
    }

    private void initTimePicker() {
        mPickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mPresenter.onBirthSelect(Utils.getFormatString(date, DateUtils.DF_JUST_DAY));
                mPickerView.dismiss();
            }
        })
                .setLayoutRes(R.layout.layout_picker_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView tvAll = (TextView) v.findViewById(R.id.tv_picker_all);
                        TextView tvFinish = (TextView) v.findViewById(R.id.tv_picker_finish);
                        tvAll.setText("清除");
                        tvAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.onBirthDelete();
                                mPickerView.dismiss();
                            }
                        });

                        tvFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.returnData();
                            }
                        });
                    }
                })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .isCenterLabel(false)
                .setTextColorCenter(getResources().getColor(R.color.colorPink))
                .setDividerColor(getResources().getColor(R.color.colorPink))
                .build();
    }

    @Override
    public void showInfo(String phone, String name) {
        // 初始化时不允许变更
        mPhoneInput.setText(phone);
        mPhoneInput.setEnabled(false);
        if (!TextUtils.isEmpty(name)) {
            mNameInput.setText(name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void showBirth(String birth) {
        mBirthText.setText(birth);
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
    public void showStepView(int cardModel) {
        switch (cardModel) {
            case AppConstants.MEMBER_CARD_MODEL_NORMAL:
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS_NORMAL);
                mStepView.selectedStep(2);
                break;
            default:
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS_WITHOUT);
                mStepView.selectedStep(2);
                break;
        }
    }

    @Override
    public void setPresenter(McardInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public boolean onKeyEventBack() {
        finishSelf();
        showExitAnim();
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CardFinishEvent event) {
        finishSelf();
    }
}

