package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by Administrator on 2016/9/12.
 */
public class ChatViewGift extends BaseChatView {
    private ImageView mGifeView;
    private TextView mGiftAmount;

    public ChatViewGift(Context context, EMMessage.Direct direct) {
        super(context, direct);

    }


    @Override
    protected void onFindViewById() {
        findViewById(R.id.credit_gift).setVisibility(View.VISIBLE);
        mGifeView = (ImageView) findViewById(R.id.credit_img);
        mGiftAmount = (TextView) findViewById(R.id.credit_gift_amount);
    }

    @Override
    protected void onSetUpView() {
        {
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            String content = body.getMessage();

            try {
                String giftId = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_ID, "");
                String giftUrl = SharedPreferenceHelper.getGiftImageById(giftId);
                if(giftUrl.contains("gif")){
                    Glide.with(context).load(giftUrl).asGif().error(R.drawable.gift_default).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mGifeView);
                }else{
                    Glide.with(context).load(giftUrl).error(R.drawable.gift_default).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mGifeView);
            }

                String giftValue = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_VALUE);
                mGiftAmount.setText(String.format("收到%s,获得%s积分", content.substring(4, content.length() - 1), giftValue));
            } catch (HyphenateException e) {
                e.printStackTrace();
            }

        }
    }


}
