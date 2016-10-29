package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 16-4-6.
 */
public class InviteDialog extends Dialog implements TextWatcher{

    @Bind(R.id.confirm) Button mButton;
    @Bind(R.id.invite_tech) EditText mTechInviteEdt;
    @Bind(R.id.invite_club) EditText mClubInviteEdt;

    public InviteDialog(Context context) {
        super(context);
    }

    public InviteDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_input_dialog);
        ButterKnife.bind(this);
        mClubInviteEdt.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(TextUtils.isEmpty(mClubInviteEdt.getText())){
            mButton.setEnabled(false);
        }else {
            mButton.setEnabled(true);
        }
    }

    @OnClick(R.id.confirm)
    public void submitInvite(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_INVITE_CODE, mClubInviteEdt.getText().toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SUBMIT_INVITE_CODE, params);
        dismiss();
    }
}
