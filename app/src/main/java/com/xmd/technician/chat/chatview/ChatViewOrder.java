package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;

/**
 * Created by sdcm on 16-4-12.
 */
public class ChatViewOrder extends BaseChatView{

    private TextView mTitle;
    private TextView mTimeHint;
    private TextView mTime;
    private TextView mserviceHint;
    private TextView mServiceitem;

    private OrderManagerListener mOrderManagerListener;

    public ChatViewOrder(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

   /* @Override
    protected void onInflateView() {

    }*/

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

        try {
            mTitle.setText(str[1]);
            mTimeHint.setText(str[3]);
            mTime.setText(str[4]);
            mserviceHint.setText(str[6]);
            mServiceitem.setText(str[7]);
        }catch (IndexOutOfBoundsException e){

        }
        findViewById(R.id.order_accept).setOnClickListener(v -> {
            if(mOrderManagerListener != null){
                mOrderManagerListener.onAccept(message);
            }
        });

        findViewById(R.id.order_refuse).setOnClickListener(v -> {
            if(mOrderManagerListener != null){
                mOrderManagerListener.onRefuse(message);
            }
        });
    }

    public void setOrderManagerListener(OrderManagerListener listener){
        this.mOrderManagerListener = listener;
    }

    public interface OrderManagerListener{
        void onAccept(EMMessage message);
        void onRefuse(EMMessage message);
    }
}
