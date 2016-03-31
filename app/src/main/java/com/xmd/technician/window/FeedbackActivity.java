package com.xmd.technician.window;

import android.os.Bundle;
import android.text.TextUtils;
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

public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.feedback_content) EditText mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        setTitle(R.string.settings_activity_suggest);
        setBackVisible(true);
    }

    @OnClick(R.id.confirm)
    public void submitFeeedback(){
        String content = mContent.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_COMMENTS,content);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SUBMIT_FEEDBACK, params);
    }
    
}
