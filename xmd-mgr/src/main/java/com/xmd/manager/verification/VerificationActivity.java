package com.xmd.manager.verification;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.Gson;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.PayOrderDetailBean;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.beans.VerificationSomeBean;
import com.xmd.manager.chat.CommonUtils;
import com.xmd.manager.databinding.ActivityVerificationBinding;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.window.BaseActivity;

import org.xml.sax.XMLReader;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by lhj on 2016/12/6.
 */

public class VerificationActivity extends BaseActivity implements VerificationListener {

    private CheckInfo mData;
    private ActivityVerificationBinding mBinding;
    private ViewDataBinding mSubBinding;

    public static final String EXTRA_DATA = "extra_data";

    public Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        mData = getIntent().getParcelableExtra(EXTRA_DATA);
        if (mData == null) {
            XToast.show("缺少参数！");
            finish();
            return;
        }
        setTitle(mData.getTitle());
        mData.setShowDetail(true);
        //设置显示信息
        int layoutId;
        switch (CommonUtils.verifyInfoTypeToViewType(mData.getInfoType())) {
            case Constant.VERIFICATION_VIEW_ORDER:
                layoutId = R.layout.check_info_list_item_sub_order;
                mData.setInfo(new Gson().fromJson((String) mData.getInfo(), PayOrderDetailBean.class));
                break;
            case Constant.VERIFICATION_VIEW_COUPON:
                layoutId = R.layout.check_info_list_item_sub_coupon;
                mData.setInfo(new Gson().fromJson((String) mData.getInfo(), VerificationCouponDetailBean.class));
                break;
            default:
                layoutId = R.layout.check_info_list_item_sub_default;
                break;
        }
        mSubBinding = DataBindingUtil.inflate(getLayoutInflater(), layoutId, mBinding.layoutVerificationInfo, false);
        mSubBinding.setVariable(BR.data, mData);
        mBinding.layoutVerificationInfo.addView(mSubBinding.getRoot());
        mBinding.setData(mData);
        mBinding.setHandler(this);

        subscription = RxBus.getInstance()
                .toObservable(VerificationSomeBean.class)
                .subscribe(
                        result -> {
                            hideLoading();
                            if (result.verificationSucceed) {
                                XToast.show("核销成功！");
                                Intent data = new Intent();
                                data.putExtra(Intent.EXTRA_DATA_REMOVED, mData.getCode());
                                setResult(RESULT_OK, data);
                                finish();
                            } else {
                                XToast.show("核销失败：" + result.message);
                            }
                        });

        if (!TextUtils.isEmpty(mData.getDescription())) {
            mBinding.description.setText(Html.fromHtml(mData.getDescription(), null, new Html.TagHandler() {
                @Override
                public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                    if (tag.equals("ul") && !opening) {
                        output.append("\n");
                    }
                    if (tag.equals("li") && opening) {
                        output.append("\n\t•  ");
                    }
                }
            }));
        }

        if (mData.getInfo() instanceof VerificationCouponDetailBean) {
            VerificationCouponDetailBean info = (VerificationCouponDetailBean) mData.getInfo();

            if (info.itemNames != null && info.itemNames.size() > 0) {
                StringBuilder itemsBuild = new StringBuilder();
                for (String item : info.itemNames) {
                    itemsBuild.append(item).append(",");
                }
                itemsBuild.setLength(itemsBuild.length() - 1);
                mBinding.tipItemsLimit.setVisibility(View.VISIBLE);
                mBinding.itemsLimit.setVisibility(View.VISIBLE);
                mBinding.itemsLimit.setText(itemsBuild.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(subscription);
    }

    @Override
    public void onVerify(CheckInfo checkInfo) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, "0");
        params.put(RequestConstant.KEY_VERIFICATION_CODE, mData.getCode());
        params.put(RequestConstant.KEY_VERIFICATION_TYPE, "");
        params.put(RequestConstant.KEY_VERIFICATION_SOME, "some");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE, params);
        showLoading("正在核销 " + mData.getTitle());
    }
}
