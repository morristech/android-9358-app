package com.xmd.black;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.app.widget.FlowLayout;
import com.xmd.black.bean.EditCustomerResult;
import com.xmd.black.bean.ManagerEditCustomerResult;
import com.xmd.black.bean.MarkBean;
import com.xmd.black.bean.MarkResult;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.black.event.EditOrAddCustomerStatisticsEvent;
import com.xmd.black.httprequest.ConstantResource;
import com.xmd.black.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-7-12.
 */

public class EditCustomerInformationActivity extends BaseActivity implements TextWatcher {

    @BindView(R2.id.edit_remark_name)
    ClearableEditText etRemarkName;
    @BindView(R2.id.limit_remark_project_list)
    FlowLayout limitProjectList;
    @BindView(R2.id.edit_remark_message)
    EditText edRemarkMessage;
    @BindView(R2.id.tv_remark_num)
    TextView textRemarkNum;
    @BindView(R2.id.btn_remark_save_edit)
    Button btnSaveEdit;


    private List<String> markList = new ArrayList<>();
    private List<String> markSelectList = new ArrayList<>();
    private String text;
    private Map<String, String> map = new HashMap<>();

    private String[] oldImpression;

    private String mUserId;
    private String mCustomerId;
    private String mCustomerFromType;
    private String mCustomerName;
    private String mCustomerNoteName;
    private String mCustomerPhone;
    private String mCustomerMessage;
    private String mImpression;

    private String mRemarkNoteName;
    private String mRemarkMessage;
    private String mRemarkImpression;
    private String mRemarkPhoneNum;


    public static void startEditCustomerInformationActivity(Activity activity, String userId, String id, String formType, String nickName, String remarkName, String phoneNum, String remarkMessage,
                                                            String remarkImpression) {
        Intent intent = new Intent(activity, EditCustomerInformationActivity.class);
        intent.putExtra(ConstantResource.KEY_USER_ID, userId);
        intent.putExtra(ConstantResource.KEY_CUSTOMER_ID, id);
        intent.putExtra(ConstantResource.KEY_FROM_TYPE, formType);
        intent.putExtra(ConstantResource.KEY_USERNAME, nickName);
        intent.putExtra(ConstantResource.KEY_NOTE_NAME, remarkName);
        intent.putExtra(ConstantResource.KEY_MARK_IMPRESSION, remarkImpression);
        intent.putExtra(ConstantResource.KEY_REMARK, remarkMessage);
        intent.putExtra(ConstantResource.KEY_PHONE_NUMBER, phoneNum);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_information);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent data = getIntent();
        mUserId = data.getStringExtra(ConstantResource.KEY_USER_ID);
        mCustomerId = data.getStringExtra(ConstantResource.KEY_CUSTOMER_ID);
        mCustomerFromType = data.getStringExtra(ConstantResource.KEY_FROM_TYPE);
        mCustomerName = data.getStringExtra(ConstantResource.KEY_USERNAME);
        mCustomerNoteName = data.getStringExtra(ConstantResource.KEY_NOTE_NAME);
        mCustomerPhone = data.getStringExtra(ConstantResource.KEY_PHONE_NUMBER);
        mCustomerMessage = data.getStringExtra(ConstantResource.KEY_REMARK);
        mImpression = data.getStringExtra(ConstantResource.KEY_MARK_IMPRESSION);
    }

    private void initView() {
        setTitle("修改备注");
        setBackVisible(true);
        etRemarkName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 10) {
                    XToast.show("备注名不可超过10个字符");
                }
            }
        });
        edRemarkMessage.addTextChangedListener(this);
        if (!TextUtils.isEmpty(mImpression)) {
            oldImpression = SplitSentence(mImpression);
        }
        if (!TextUtils.isEmpty(mCustomerNoteName)) {
            etRemarkName.setText(mCustomerNoteName);
        } else {
            etRemarkName.setText(mCustomerName);
            etRemarkName.setEnabled(true);
        }
        if (!TextUtils.isEmpty(mCustomerMessage)) {
            edRemarkMessage.setText(mCustomerMessage);
        }
        if (mCustomerFromType.equals(ConstantResource.INTENT_TYPE_MANAGER)) {
            limitProjectList.setVisibility(View.GONE);
        } else {
            limitProjectList.setVisibility(View.VISIBLE);
            getImpressionData();
        }

    }

    private void getImpressionData() {
        DataManager.getInstance().getImpressionDetail(new NetworkSubscriber<MarkResult>() {
            @Override
            public void onCallbackSuccess(MarkResult result) {
                handlerMarkResult(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void handlerMarkResult(List<MarkBean> respData) {
        for (int i = 0; i < respData.size(); i++) {
            markList.add(respData.get(i).tag);
        }
        initChildViews(markList);
    }

    private void initChildViews(List<String> Mark) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = 18;
        lp.bottomMargin = 18;
        if (oldImpression != null) {
            for (int i = 0; i < oldImpression.length; i++) {
                map.put(oldImpression[i], oldImpression[i]);
                markSelectList.add(oldImpression[i]);
            }
        }

        for (int i = 0; i < Mark.size(); i++) {
            final TextView view = new TextView(this);
            view.setPadding(36, 5, 36, 5);
            view.setText(Mark.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.limit_project_item_bg));
            limitProjectList.addView(view, lp);
            text = view.getText().toString();
            if (map.containsKey(text)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_select_bg));
                }
                view.setTextColor(ResourceUtils.getColor(R.color.contact_marker));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_bg));
                }
                view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text = view.getText().toString();
                    if (map.containsKey(text)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_bg));
                        }
                        view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
                        map.remove(text);
                        markSelectList.remove(text);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_select_bg));
                        }
                        view.setTextColor(ResourceUtils.getColor(R.color.contact_marker));
                        map.put(text, text);
                        markSelectList.add(text);
                    }
                }

            });


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
        if (s.length() > 30) {
            XToast.show("备注信息不可超过30个字符");
            btnSaveEdit.setEnabled(false);
        } else {
            btnSaveEdit.setEnabled(true);
            if ((30 - s.length()) > 0) {
                textRemarkNum.setText(30 - s.length() + "");
            }
        }
    }

    @OnClick(R2.id.btn_remark_save_edit)
    public void onViewClicked() {
        mRemarkNoteName = etRemarkName.getText().toString();
        if (mRemarkNoteName.equals(mCustomerName)) {
            mRemarkNoteName = "";
        }
        mRemarkPhoneNum = mCustomerPhone;
        mRemarkMessage = edRemarkMessage.getText().toString();
        mRemarkImpression = Utils.listToString(markSelectList, "、");
        if (mCustomerFromType.equals(ConstantResource.INTENT_TYPE_MANAGER)) {
            managerEditMark();
        } else {
            techEditMark();
        }

    }

    private void techEditMark() {
        DataManager.getInstance().SaveCustomerRemark(mUserId, mCustomerId, mRemarkPhoneNum, mRemarkMessage, mRemarkNoteName, mRemarkImpression, new NetworkSubscriber<EditCustomerResult>() {
            @Override
            public void onCallbackSuccess(EditCustomerResult result) {
                XToast.show("保存成功");
                EventBus.getDefault().post(new EditCustomerRemarkSuccessEvent(mCustomerName, mRemarkNoteName, mRemarkMessage, mRemarkImpression));
                EventBus.getDefault().post(new EditOrAddCustomerStatisticsEvent(Constants.UMENG_STATISTICS_CUSTOMER_SAVE_CLICK));
                EditCustomerInformationActivity.this.finish();
                User user = UserInfoServiceImpl.getInstance().getUserByUserId(mUserId);
                if (user != null) {
                    user.setNoteName(mRemarkNoteName);
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void managerEditMark() {
        DataManager.getInstance().managerEditRemark(mCustomerId, mRemarkNoteName, mCustomerPhone, mRemarkMessage, new NetworkSubscriber<ManagerEditCustomerResult>() {
            @Override
            public void onCallbackSuccess(ManagerEditCustomerResult result) {
                XToast.show("保存成功");
                EventBus.getDefault().post(new EditCustomerRemarkSuccessEvent(mCustomerName, mRemarkNoteName, mRemarkMessage, ""));
                EditCustomerInformationActivity.this.finish();
                User user = UserInfoServiceImpl.getInstance().getUserByUserId(mUserId);
                if (user != null) {
                    user.setNoteName(mRemarkNoteName);
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public String[] SplitSentence(String paragraph) {
        String[] result = null;
        result = paragraph.split("、");
        return result;
    }
}
