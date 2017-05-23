package com.xmd.manager.window;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.FeedbackResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by sdcm on 15-11-20.
 */
public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.feedback_content)
    EditText mFeedbackContent;
    @Bind(R.id.feedback_submit)
    Button mFeedbackSubmit;

    private Subscription mFeedbackSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        mFeedbackSubscription = RxBus.getInstance().toObservable(FeedbackResult.class).subscribe(
                feedbackResult -> onFeedbackSubmitted()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.getInstance().unsubscribe(mFeedbackSubscription);
    }

    @OnClick(R.id.feedback_submit)
    public void doSubmitFeedback() {
        String comments = mFeedbackContent.getEditableText().toString();
        if (Utils.isEmpty(comments)) {
            makeShortToast(ResourceUtils.getString(R.string.feedback_activity_submit_not_empty));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_COMMENTS, comments);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SUBMIT_FEEDBACK, params);
    }

    public void onFeedbackSubmitted() {
        makeShortToast(ResourceUtils.getString(R.string.feedback_activity_submit_successful));
        mFeedbackContent.setText("");
    }
}
