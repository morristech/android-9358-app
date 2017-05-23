package com.xmd.manager.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.PayResult;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/7/28.
 */
public class PayActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.pay_code)
    TextView payCode;
    @Bind(R.id.edit_Pay_money)
    EditText editPayMoney;
    @Bind(R.id.btn_pay)
    Button btnPay;
    @Bind(R.id.user_pay_code)
    LinearLayout userCode;

    private String mQrNo;
    private String mRid;
    private String mTime;
    private Subscription getThePayResultSubscription;
    private String money;


    public static void startPayActivity(Activity activity, String mPhoneNoOrCouponNo) {
        Intent intent = new Intent(activity, PayActivity.class);
        intent.putExtra(RequestConstant.KEY_PAY_QRNO, mPhoneNoOrCouponNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        setTitle(ResourceUtils.getString(R.string.to_pay));
        mQrNo = intent.getStringExtra(RequestConstant.KEY_PAY_QRNO);
        mRid = intent.getStringExtra(RequestConstant.KEY_PAY_RID);
        mTime = intent.getStringExtra(RequestConstant.KEY_TIME);
        getThePayResultSubscription = RxBus.getInstance().toObservable(PayResult.class).subscribe(
                result -> handlerThePayResult(result)
        );
        editPayMoney.addTextChangedListener(this);
        if (TextUtils.isEmpty(mTime)) {
            payCode.setText(mQrNo);
            userCode.setVisibility(View.VISIBLE);
        } else {
            userCode.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_pay)
    public void toPay() {
        money = editPayMoney.getText().toString();
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showToastShort(PayActivity.this, "请输入核销金额");
            return;
        }
        int payMoney = (int) (Float.parseFloat(money) * 100);
        if (Utils.isNotFastClick()) {
            if (TextUtils.isEmpty(mTime) && Utils.isNotEmpty(mQrNo)) {
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_PAY_CODE, mQrNo);
                params.put(RequestConstant.KEY_PAY_AMOUNT_BY_CODE, String.valueOf(payMoney));
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_PAY_BY_CONSUME, params);
            } else {
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_PAY_AMOUNT, String.valueOf(payMoney));
                params.put(RequestConstant.KEY_PAY_QRNO, mQrNo);
                params.put(RequestConstant.KEY_PAY_RID, mRid);
                params.put(RequestConstant.KEY_TIME, mTime);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_PAY_DETAIL, params);
            }
        }

    }

    private void handlerThePayResult(PayResult result) {
        if (result.statusCode == 200) {
            showResultDialog(result.statusCode, ResourceUtils.getString(R.string.pay_result_successed), result.msg);
        } else {

            if (Utils.isNotEmpty(result.msg) && result.msg.length() > 6) {
                if (ResourceUtils.getString(R.string.pay_result_failed_short_age).equals(result.msg.substring(0, 6))) {
                    showResultDialog(result.statusCode, ResourceUtils.getString(R.string.pay_result_failed_short_age), result.msg);
                    return;
                }
            }
            if (result.msg.length() > 20) {
                showResultDialog(result.statusCode, ResourceUtils.getString(R.string.pay_result_failed), "服务器异常");
                makeShortToast(result.msg);
                return;
            } else {
                showResultDialog(result.statusCode, ResourceUtils.getString(R.string.pay_result_failed), result.msg);
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (Utils.isNotEmpty(editPayMoney.getText().toString()) && Integer.parseInt(editPayMoney.getText().toString()) > 0) {
            btnPay.setEnabled(true);
        } else {
            btnPay.setEnabled(false);
        }

    }

    private void showResultDialog(int resultCode, String title, String message) {
        new AlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> doResult(resultCode))
                .setCancelable(false)
                .show();
    }

    private void doResult(int resultCode) {
        if (resultCode == 200) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getThePayResultSubscription.unsubscribe();
    }
}
