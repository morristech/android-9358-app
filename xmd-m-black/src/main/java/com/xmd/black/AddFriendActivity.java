package com.xmd.black;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.app.widget.FlowLayout;
import com.xmd.black.bean.CreateCustomerResult;
import com.xmd.black.bean.MarkBean;
import com.xmd.black.bean.MarkResult;
import com.xmd.black.httprequest.ConstantResource;
import com.xmd.black.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.MarginLayoutParams;

/**
 * Created by Lhj on 17-7-24.
 */

public class AddFriendActivity extends BaseActivity implements TextWatcher {

    @BindView(R2.id.et_customer_name)
    ClearableEditText etCustomerName;
    @BindView(R2.id.et_customer_telephone)
    ClearableEditText etCustomerTelephone;
    @BindView(R2.id.limit_project_list)
    FlowLayout limitProjectList;
    @BindView(R2.id.et_customer_remark)
    ClearableEditText etCustomerRemark;
    @BindView(R2.id.text_remark_num)
    TextView textRemarkNum;
    @BindView(R2.id.btn_save_customer)
    Button btnSaveCustomer;
    @BindView(R2.id.ll_flow_layout)
    LinearLayout llFlowLayout;

    private static final int REQUEST_CODE_CONTACTS_PERMISSION = 0x1;
    private static final int REQUEST_CODE_PICK_CONTACT = 0x2;

    private boolean isFromManager;
    private String customerName;
    private String customerPhone;
    private String customerRemark;
    private String impression;
    private String text;
    private List<String> markList = new ArrayList<>();
    private List<String> markSelectList = new ArrayList<>();
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    private Map<String, String> map = new HashMap<>();

    public static void startAddFriendActivity(Activity activity, boolean fromManager) {
        Intent intent = new Intent(activity, AddFriendActivity.class);
        intent.putExtra(ConstantResource.INTENT_APP_TYPE, fromManager);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        isFromManager = getIntent().getBooleanExtra(ConstantResource.INTENT_APP_TYPE, false);
    }

    private void initView() {
        setTitle("添加客户");
        setBackVisible(true);
        etCustomerName.addTextChangedListener(this);
        etCustomerRemark.addTextChangedListener(this);
        etCustomerTelephone.addTextChangedListener(this);
        getImpressionData();
        if (isFromManager) {
            llFlowLayout.setVisibility(View.GONE);
        } else {
            llFlowLayout.setVisibility(View.VISIBLE);
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
        MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.rightMargin = Utils.dip2px(AddFriendActivity.this, 10);
        lp.bottomMargin = Utils.dip2px(AddFriendActivity.this, 10);
        for (int i = 0; i < Mark.size(); i++) {
            final TextView view = new TextView(this);
            view.setPadding(Utils.dip2px(AddFriendActivity.this, 8), Utils.dip2px(AddFriendActivity.this, 2), Utils.dip2px(AddFriendActivity.this, 8), Utils.dip2px(AddFriendActivity.this, 2));
            view.setText(Mark.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            view.setBackgroundResource(R.drawable.limit_project_item_bg);
            limitProjectList.addView(view, lp);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text = view.getText().toString();
                    if (map.containsKey(text)) {
                        view.setBackgroundResource(R.drawable.limit_project_item_bg);
                        view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
                        map.remove(text);
                        markSelectList.remove(text);
                    } else {
                        view.setBackgroundResource(R.drawable.limit_project_item_select_bg);
                        view.setTextColor(ResourceUtils.getColor(R.color.contact_marker));
                        map.put(text, text);
                        markSelectList.add(text);
                    }
                }

            });

        }
    }

    @OnClick(R2.id.btn_search_telephone)
    public void onBtnSearchTelephoneClicked() {
        //添加通讯录客户
        PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_CONTACTS}, new String[]{"读取联系人权限"}, REQUEST_CODE_CONTACTS_PERMISSION);
    }

    @OnClick(R2.id.btn_save_customer)
    public void onBtnSaveCustomerClicked() {
        //保存添加
        if (!Utils.matchPhoneNumFormat(customerPhone)) {
            XToast.show("请输入正确的手机号");
            return;
        }
        if (markSelectList.size() == 1) {
            impression = markSelectList.get(0);
        } else if (markSelectList.size() > 1) {
            for (int i = 0; i < markSelectList.size() - 1; i++) {
                if (i == 0) {
                    impression = markSelectList.get(0) + "、";
                } else if (i < markSelectList.size() - 1) {
                    impression = impression + markSelectList.get(i) + "、";
                }
            }
            impression = impression + markSelectList.get(markSelectList.size() - 1);
        } else {
            impression = "";
        }
        saveCreateCustomer();

    }

    private void saveCreateCustomer() {
        DataManager.getInstance().doCreateCustomer(customerName, customerPhone, impression, customerRemark, new NetworkSubscriber<CreateCustomerResult>() {
            @Override
            public void onCallbackSuccess(CreateCustomerResult result) {
                XToast.show("添加成功");
                AddFriendActivity.this.finish();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show("添加失败" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        customerName = etCustomerName.getText().toString();
        customerPhone = etCustomerTelephone.getText().toString();
        customerRemark = etCustomerRemark.getText().toString();
        if (!TextUtils.isEmpty(customerName) && !TextUtils.isEmpty(customerPhone)) {
            btnSaveCustomer.setEnabled(true);
        } else {
            btnSaveCustomer.setEnabled(false);
        }
        if (customerRemark.length() == 30) {
            XToast.show("备注信息不可超过30个字符");
        }
        if (customerRemark.length() <= 30) {
            textRemarkNum.setText(String.valueOf(30 - customerRemark.length()));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                ContentResolver resolver = AddFriendActivity.this.getContentResolver();
                Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
                if (phoneCursor != null) {
                    Intent intent = new Intent(this, CellphoneContactListActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
                } else {
                    XToast.show("查询联系人列表失败，或联系人列表为空");
                }
            }
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_CONTACT && resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            etCustomerName.setText(name);
            etCustomerTelephone.setText(getPhone(phone));
        }

    }

    private String getPhone(String s) {
        //1 31-2414-5614
        if (s.length() == 14) {
            String s1 = s.substring(0, 1);
            String s2 = s.substring(2, 5);
            String s3 = s.substring(6, 9);
            String s4 = s.substring(10);
            s = s1 + s2 + s3 + s4;
        } else if (s.length() == 13) {
            String s1 = s.substring(0, 3);
            String s2 = s.substring(4, 8);
            String s3 = s.substring(9);
            s = s1 + s2 + s3;
        }
        return s;
    }
}
