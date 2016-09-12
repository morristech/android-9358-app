package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.bean.CreditGift;
import com.xmd.technician.bean.GiftListResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Administrator on 2016/9/12.
 */
public class ChatViewGift extends BaseChatView {
    private ImageView mGifeView;
    private Subscription giftResultSubscription;
    private List<CreditGift> giftList = new ArrayList<>();


    public ChatViewGift(Context context, EMMessage.Direct direct) {
        super(context, direct);
        giftResultSubscription = RxBus.getInstance().toObservable(GiftListResult.class).subscribe(
                result ->{
                   giftList.addAll(result.respData);
                }
        );
    }


    @Override
    protected void onFindViewById() {
        findViewById(R.id.content_gift).setVisibility(View.VISIBLE);
        mGifeView = (ImageView) findViewById(R.id.content_gift);

    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        try {
            String giftId = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_ID);
            for (int i = 0; i <giftList.size() ; i++) {
                if(giftList.get(i).id.equals(giftId)){
                    Glide.with(context).load(giftList.get(i).iconUrl).error(R.drawable.gift_default).into(mGifeView);
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

}
