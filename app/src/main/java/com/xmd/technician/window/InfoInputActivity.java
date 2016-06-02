package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class InfoInputActivity extends BaseActivity implements TextWatcher{

    public final static String EXTRA_PHONE_NUM = "username";

    @Bind(R.id.serial_no) EditText mSerialNo;
    @Bind(R.id.nick_name) EditText mNickName;
    @Bind(R.id.button_male) RadioButton mMale;
    @Bind(R.id.confirm) Button mConfirmBtn;

    private Subscription mSubscription;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);

        ButterKnife.bind(this);
        setTitle(R.string.register);
        setBackVisible(true);

        mPhoneNum = getIntent().getExtras().getString(EXTRA_PHONE_NUM);

        mMale.setChecked(true);

        mNickName.addTextChangedListener(this);
        mSerialNo.addTextChangedListener(this);

        mSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                result -> startActivity(new Intent(InfoInputActivity.this, UploadAvatarActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSubscription);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(TextUtils.isEmpty(mNickName.getText()) || TextUtils.isEmpty(mSerialNo.getText())){
            mConfirmBtn.setEnabled(false);
        }else {
            mConfirmBtn.setEnabled(true);
        }
    }

    @OnClick(R.id.confirm)
    public void confirm(){
        String gender = mMale.isChecked()? "male" : "female";
        String user = "{\"name\":\"" + mNickName.getText().toString() + "\",\"serialNo\":\"" + mSerialNo.getText().toString()+ "\",\"phoneNum\":\"" + mPhoneNum + "\",\"gender\":\"" + gender + "\"}";

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, user);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);
    }
}
