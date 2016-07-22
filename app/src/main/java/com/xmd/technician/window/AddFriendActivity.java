package com.xmd.technician.window;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import com.xmd.technician.R;
import com.xmd.technician.bean.AddOrEditResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ClearableEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
/**
 * Created by Administrator on 2016/7/4.
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
    private String customerName;
    private String customerPhone;
    private String customerRemark;

    private Subscription getAddresultSubscription;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

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
        mSearchTelephone.addTextChangedListener(this);
        getAddresultSubscription = RxBus.getInstance().toObservable(AddOrEditResult.class).subscribe(
              result ->{
                  if(result.resultcode==200){
                      MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST);
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
    }
    @OnClick(R.id.btn_search_telephone)
    public void getCustomerFromAddressBook() {
        if(getContactSuccess()){
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
            if(TextUtils.isEmpty(mCustomerName.getText().toString())){
                mCustomerName.setText(name);
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
        customerName = mCustomerName.getText().toString();
        customerPhone = mCustomerTelephone.getText().toString();
        customerRemark = mCustomerRemark.getText().toString();
        if (!TextUtils.isEmpty(customerPhone) && !TextUtils.isEmpty(customerName) && !TextUtils.isEmpty(customerRemark)) {
            mSaveCustomer.setEnabled(true);
        } else {
            mSaveCustomer.setEnabled(false);
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
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_NOTE_NAME, customerName);
            params.put(RequestConstant.KEY_PHONE_NUMBER, customerPhone);
            params.put(RequestConstant.KEY_REMARK, customerRemark);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER,params);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getAddresultSubscription);
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
    private boolean getContactSuccess(){
        ContentResolver resolver = AddFriendActivity.this.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if(phoneCursor!=null){
            return phoneCursor.moveToNext();
        }
      return false;
    }

}
