package com.xmd.technician.window;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import com.xmd.technician.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoInputActivity extends BaseActivity {

    @Bind(R.id.serial_no) EditText mSerialNo;
    @Bind(R.id.nick_name) EditText mNickName;
    @Bind(R.id.button_male) RadioButton mMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);

        ButterKnife.bind(this);
        setTitle(R.string.login);
        setBackVisible(true);

        mMale.setChecked(true);
    }

    @OnClick(R.id.confirm)
    public void confirm(){
        String gender = mMale.isChecked()? "male" : "female";
    }
}
