package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.DefaultVerificationBean;
import com.xmd.manager.beans.VerificationInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/12/6.
 */

public class DefaultVerificationFragment extends BaseFragment {

    @Bind(R.id.default_verification_code)
    TextView defaultVerificationCode;
    @Bind(R.id.list_content)
    ListView listContent;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.default_pay_amount)
    LinearLayout defaultPayAmount;
    @Bind(R.id.btn_default_verification)
    Button btnDefaultVerification;
    @Bind(R.id.btn_default_verification_cancel)
    Button btnDefaultVerificationCancel;
    @Bind(R.id.pay_amount)
    ClearableEditText payAmount;
    @Bind(R.id.layout_default_info_remark)
    LinearLayout infoRemark;
    @Bind(R.id.default_verification_title)
    TextView verificationTitle;
    @Bind(R.id.action_default_supplement)
    WebView verificationRemark;

    private DefaultVerificationBean mDefaultBean;
    private int mAmount;
    private String mCode;
    private String mType;
    private int mToPay;
    private MessageAdapter messageAdapter;
    private List<VerificationInfo.Info> infoList;


    public static DefaultVerificationFragment getInstance(DefaultVerificationBean defaultBean) {
        DefaultVerificationFragment df = new DefaultVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VerificationManagementHelper.VERIFICATION_DEFAULT_TYPE, defaultBean);
        df.setArguments(bundle);
        return df;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_verification, container, false);
        ButterKnife.bind(this, view);
        infoList = new ArrayList<>();
        mDefaultBean = (DefaultVerificationBean) getArguments().getSerializable(VerificationManagementHelper.VERIFICATION_DEFAULT_TYPE);
        initFragmentView();
        return view;
    }


    protected void initFragmentView() {
        mCode = mDefaultBean.code;
        mType = mDefaultBean.type;
        verificationRemark.getSettings().setJavaScriptEnabled(false);
        verificationRemark.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        if (mDefaultBean.info != null && Utils.isNotEmpty(mDefaultBean.info.first)) {
            defaultVerificationCode.setText(mDefaultBean.info.first);
            verificationTitle.setText(ResourceUtils.getString(R.string.order_verification_info));
        } else {
            defaultVerificationCode.setText(mCode);
            verificationTitle.setText(ResourceUtils.getString(R.string.verification_order_code));
        }
        if (mDefaultBean.info != null) {
            if (mDefaultBean.info.list.size() > 0) {
                listContent.setVisibility(View.VISIBLE);
                infoList.addAll(mDefaultBean.info.list);
                messageAdapter = new MessageAdapter();
                listContent.setAdapter(messageAdapter);
                setListViewHeightBasedOnChildren(listContent);
                messageAdapter.notifyDataSetChanged();
            }
            if (Utils.isNotEmpty(mDefaultBean.info.remark)) {
                infoRemark.setVisibility(View.VISIBLE);
                verificationRemark.loadDataWithBaseURL(null, mDefaultBean.info.remark, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
            }
            if (Utils.isNotEmpty(mDefaultBean.info.amount)) {
                mToPay = Integer.parseInt(mDefaultBean.info.amount) / 100;
            }

        } else {
            listContent.setVisibility(View.GONE);
        }
        if (mDefaultBean.needAmount) {
            defaultPayAmount.setVisibility(View.VISIBLE);
            if (mToPay > 0) {
                payAmount.setHint("应支付" + mToPay + "元");
            }
        } else {
            defaultPayAmount.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_default_verification, R.id.btn_default_verification_cancel})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_default_verification:
                Map<String, String> params = new HashMap<>();
                if (mDefaultBean.needAmount) {
                    mAmount = Integer.parseInt(payAmount.getText().toString()) * 100;
                    params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, String.valueOf(mAmount));
                } else {
                    params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, "0");
                }
                params.put(RequestConstant.KEY_VERIFICATION_CODE, mCode);
                params.put(RequestConstant.KEY_VERIFICATION_TYPE, mType);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE, params);
                break;
            case R.id.btn_default_verification_cancel:
                getActivity().finish();
                break;
        }
    }

    @Override
    protected void initView() {

    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public Object getItem(int position) {
            return infoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = View.inflate(getActivity(), R.layout.adapter_default_verification_item, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.item_title);
            tvTitle.setText(infoList.get(position).title);
            TextView tvText = (TextView) view.findViewById(R.id.item_text);
            tvText.setText(infoList.get(position).text);
            return view;
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1);
        listView.setLayoutParams(params);
    }
}
