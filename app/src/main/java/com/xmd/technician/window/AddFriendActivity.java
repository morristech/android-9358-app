package com.xmd.technician.window;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xmd.technician.R;
import com.xmd.technician.bean.AddOrEditResult;
import com.xmd.technician.bean.MarkResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ClearableEditText;
import com.xmd.technician.widget.FlowLayout;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
/**
 * Created by LHJ on 2016/7/4.
 */
public class AddFriendActivity extends BaseActivity implements TextWatcher {
    private static final int REQUEST_CONTANT_CODE = 0x001;
    @Bind(R.id.et_customer_name)
    EditText mCustomerName;
    @Bind(R.id.et_customer_telephone)
    ClearableEditText mCustomerTelephone;
    @Bind(R.id.btn_search_telephone)
    Button mSearchTelephone;
    @Bind(R.id.et_customer_remark)
    EditText mCustomerRemark;
    @Bind(R.id.btn_save_customer)
    Button mSaveCustomer;
    @Bind(R.id.limit_project_list)
    FlowLayout mFlowLayout;
    @Bind(R.id.text_remark_num)
    TextView textRemark;

    private String customerName;
    private String customerPhone;
    private String customerRemark;
    private String impression;
    private Map<String ,String> params = new HashMap<>();

    private Subscription ContactMarkSubscriotion;
    private List<String> markList = new ArrayList<>();
    private List<String> markSelectList = new ArrayList<>();
    private String   text;
    private static final int REQUEST_CODE_CONTACTS = 0x0001;
    private boolean isHasContacts;
    private Subscription getAddresultSubscription;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    private Map<String,String> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        initView();
    }
    private void initView() {
        setTitle(R.string.add_contact);
        setBackVisible(true);
        mCustomerName.addTextChangedListener(this);
        mCustomerRemark.addTextChangedListener(this);
        mCustomerTelephone.addTextChangedListener(this);
        getAddresultSubscription = RxBus.getInstance().toObservable(AddOrEditResult.class).subscribe(
              result ->{
                  if(result.resultcode==200){
                      params.put(RequestConstant.KEY_CONTACT_TYPE,"");
                      MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST,params);
                      makeShortToast(ResourceUtils.getString(R.string.add_ontact_successed));
                      ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                          @Override
                          public void run() {
                              AddFriendActivity.this.finish();
                          }
                      }, 1500);
                  }else{
                      makeShortToast(result.msg.toString());
                  }
              }

        );
        ContactMarkSubscriotion = RxBus.getInstance().toObservable(MarkResult.class).subscribe(
                markResult -> handlerMarkResult(markResult)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_MARK);
    }
    @OnClick(R.id.btn_search_telephone)
    public void getCustomerFromAddressBook() {
        MPermissions.requestPermissions(AddFriendActivity.this,REQUEST_CODE_CONTACTS, Manifest.permission.READ_CONTACTS);
        if(isHasContacts){
            Intent intent = new Intent(this, CellphoneContactListActivity.class);
            startActivityForResult(intent, REQUEST_CONTANT_CODE);
        }else{
            makeShortToast(ResourceUtils.getString(R.string.not_have_authority_or_contact_is_empty));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CONTANT_CODE) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            mCustomerTelephone.setText(getPhone(phone));
            mCustomerName.setText(name);
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
        customerName = mCustomerName.getText().toString();
        customerPhone = mCustomerTelephone.getText().toString();
        customerRemark = mCustomerRemark.getText().toString();

        if(Utils.isNotEmpty(customerName)&&Utils.isNotEmpty(customerPhone)){
            mSaveCustomer.setEnabled(true);
        }else{
            mSaveCustomer.setEnabled(false);
        }

        if(customerRemark.length()==30){
            this.makeShortToast(ResourceUtils.getString(R.string.limit_input_text));
        }
        if(customerRemark.length()<=30){
            textRemark.setText(String.valueOf(30-customerRemark.length()));
        }
    }
   @OnClick(R.id.btn_save_customer)
    public void saveCustomer() {
        customerName = mCustomerName.getText().toString();
        customerPhone = mCustomerTelephone.getText().toString();
        customerRemark = mCustomerRemark.getText().toString();
        if (TextUtils.isEmpty(customerName) || TextUtils.isEmpty(customerPhone)) {
            makeShortToast(ResourceUtils.getString(R.string.edit_usable_name_or_phone));
        }else if(!Utils.matchPhoneNumFormat(customerPhone)){
            makeShortToast(ResourceUtils.getString(R.string.toast_useful_telephone));
        } else {
            if(markSelectList.size()==1){
                impression = markSelectList.get(0);
            }else if(markSelectList.size()>1){
                for (int i = 0; i <markSelectList.size()-1 ; i++) {
                    if(i==0){
                        impression=markSelectList.get(0)+"、";
                    }else if(i<markSelectList.size()-1){
                        impression =impression+markSelectList.get(i)+"、";
                    }
                }
                impression = impression+markSelectList.get(markSelectList.size()-1);
            }

            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_NOTE_NAME, customerName);
            params.put(RequestConstant.KEY_PHONE_NUMBER, customerPhone);
            params.put(RequestConstant.KEY_REMARK, customerRemark);
            params.put(RequestConstant.KEY_MARK_IMPRESSION,impression);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER,params);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getAddresultSubscription,ContactMarkSubscriotion);
    }
    private String getPhone(String s){
        //1 31-2414-5614
        if(s.length()==14){
            String s1 =s.substring(0,1);
            String s2 = s.substring(2,5);
            String s3 = s.substring(6,9);
            String s4 = s.substring(10);
            s = s1+s2+s3+s4;
        }else if(s.length()==13){
         String s1 = s.substring(0,3);
         String s2 = s.substring(4,8);
         String s3 = s.substring(9);
            s = s1+s2+s3;
        }
        return  s;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUEST_CODE_CONTACTS)
    public void requestSdcardSuccess()
    {
        ContentResolver resolver = AddFriendActivity.this.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if(phoneCursor!=null){
            isHasContacts = true;
        }else{
            isHasContacts = false;
        }
    }

    @PermissionDenied(REQUEST_CODE_CONTACTS)
    public void requestSdcardFailed()
    {
        Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    private void handlerMarkResult(MarkResult result){
        for (int i = 0; i <result.respData.size() ; i++) {
            markList.add(result.respData.get(i).tag);
        }
        initChildViews(markList);
    }
    private void initChildViews(List<String> Mark){
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = 18;
        lp.bottomMargin = 18;

        for(int i = 0; i < Mark.size(); i ++){
            TextView view = new TextView(this);
            view.setPadding(36,5,36,5);
            view.setText(Mark.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.limit_project_item_bg));
            mFlowLayout.addView(view,lp);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text = view.getText().toString();
                    if(map.containsKey(text)){
                        view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_bg));
                        view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
                        map.remove(text);
                        markSelectList.remove(text);
                    }else{
                        view.setBackground(getResources().getDrawable(R.drawable.limit_project_item_select_bg));
                        view.setTextColor(ResourceUtils.getColor(R.color.contact_marker));
                        map.put(text,text);
                        markSelectList.add(text);
                    }
                }

            });

        }
    }



}
