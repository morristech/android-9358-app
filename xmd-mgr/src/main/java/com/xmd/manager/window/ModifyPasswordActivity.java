package com.xmd.manager.window;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ModifyPasswordResult;
import com.xmd.manager.widget.LoginFailDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/11/18.
 */
public class ModifyPasswordActivity extends BaseActivity implements TextWatcher, InputFilter {
    public final static int PASSWORD_MAX_LEN = 20;

    @Bind(R.id.old_password)
    EditText mOldPassWordEdt;
    @Bind(R.id.new_password)
    EditText mNewPassWordEdt;
    @Bind(R.id.confirm_password)
    EditText mConfirmPassWordEdt;
    @Bind(R.id.confirm)
    Button mChangePassWordBtn;

    private Subscription mModifyPasswordSubscription;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        ButterKnife.bind(this);

        setTitle(R.string.settings_activity_modify_password);
        setLeftVisible(true, R.drawable.actionbar_back);
        mModifyPasswordSubscription = RxBus.getInstance().toObservable(ModifyPasswordResult.class).subscribe(
                result -> handleChangeResult(result));

        mOldPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mOldPassWordEdt.addTextChangedListener(this);
        mNewPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mNewPassWordEdt.addTextChangedListener(this);
        mConfirmPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mConfirmPassWordEdt.addTextChangedListener(this);
    }

    private void handleChangeResult(ModifyPasswordResult result) {
        if (result.statusCode == 200) {
            this.finish();
        } else {
            new LoginFailDialog(ModifyPasswordActivity.this, ResourceUtils.getString(R.string.modify_password_failed), result.msg).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mModifyPasswordSubscription);
    }

    @OnClick(R.id.confirm)
    public void modifyPassword() {
        String nPassword = null, oPassword = null;

        nPassword = mNewPassWordEdt.getText().toString();
        oPassword = mOldPassWordEdt.getText().toString();
        mPassword = mConfirmPassWordEdt.getText().toString();
        if (!nPassword.equals(mPassword)) {
            new LoginFailDialog(this, R.string.modify_password_failed, R.string.modify_password_change_confirm_err).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_OLD_PASSWORD, oPassword);
        params.put(RequestConstant.KEY_NEW_PASSWORD, nPassword);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MODIFY_PASSWORD, params);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String oldPW = mOldPassWordEdt.getText().toString();
        String newPW = mNewPassWordEdt.getText().toString();
        String confirmPW = mConfirmPassWordEdt.getText().toString();

        if (Utils.matchPassWordFormat(confirmPW) && Utils.matchPassWordFormat(newPW) && Utils.matchPassWordFormat(oldPW)) {
            mChangePassWordBtn.setEnabled(true);
        } else {
            mChangePassWordBtn.setEnabled(false);
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
