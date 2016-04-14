package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 16-4-12.
 */
public class ChatViewOrder extends BaseChatView implements View.OnClickListener{

    private TextView mTitle;
    private TextView mTimeHint;
    private TextView mTime;
    private TextView mserviceHint;
    private TextView mServiceitem;

    public ChatViewOrder(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {

    }

    @Override
    protected void onFindViewById() {
        findViewById(R.id.order_container).setVisibility(VISIBLE);
        mTitle = (TextView) findViewById(R.id.order_title);
        mTime = (TextView) findViewById(R.id.order_time);
        mTimeHint = (TextView) findViewById(R.id.order_time_hint);
        mserviceHint = (TextView) findViewById(R.id.order_service_hint);
        mServiceitem = (TextView) findViewById(R.id.order_service);
        if(mDirect == EMMessage.Direct.SEND){
            findViewById(R.id.order_menu_container).setVisibility(GONE);
        }
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        String[] str = content.split("<b>|<span>|</span>|<br>|</b>");

        mTitle.setText(str[1]);
        mTimeHint.setText(str[3]);
        mTime.setText(str[4]);
        mserviceHint.setText(str[6]);
        mServiceitem.setText(str[7]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_accept:
                break;
            case R.id.order_refuse:
                break;
        }

        Map<String, String> params = new HashMap<>();
        try {
            params.put(RequestConstant.KEY_ID, message.getStringAttribute("orderId"));
            params.put(RequestConstant.KEY_PROCESS_TYPE, "");
            params.put(RequestConstant.KEY_REASON, "");

        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }
}
