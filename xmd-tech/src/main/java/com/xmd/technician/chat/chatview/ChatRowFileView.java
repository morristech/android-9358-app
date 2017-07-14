package com.xmd.technician.chat.chatview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.FileUtils;
import com.hyphenate.util.TextFormater;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

import java.io.File;


/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowFileView extends BaseEaseChatView {

    protected TextView fileNameView;
    protected TextView fileSizeView;
    protected TextView fileStateView;

    protected EMCallBack sendfileCallBack;

    protected boolean isNotifyProcessed;
    private EMNormalFileMessageBody fileMessageBody;

    public ChatRowFileView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.chat_row_received_file : R.layout.chat_row_sent_file, this);
    }

    @Override
    protected void onFindViewById() {
        fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        mPercentageView = (TextView) findViewById(R.id.percentage);
    }


    @Override
    protected void onSetUpView() {
        fileMessageBody = (EMNormalFileMessageBody) mEMMessage.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
            File file = new File(filePath);
            if (file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }

        // until here, to sending message
        handleSendMessage();
    }

    /**
     * handle sending message
     */
    protected void handleSendMessage() {
        setMessageSendCallback();
        switch (mEMMessage.status()) {
            case SUCCESS:
                mProgressBar.setVisibility(View.INVISIBLE);
                if (mPercentageView != null)
                    mPercentageView.setVisibility(View.INVISIBLE);
                mStatusView.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                mProgressBar.setVisibility(View.INVISIBLE);
                if (mPercentageView != null)
                    mPercentageView.setVisibility(View.INVISIBLE);
                mStatusView.setVisibility(View.VISIBLE);
                String errorCode = mEMMessage.getStringAttribute(ChatConstant.KEY_ERROR_CODE, ChatConstant.ERROR_SERVER_NOT_REACHABLE);
                if (ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)) {
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case INPROGRESS:
                mProgressBar.setVisibility(View.VISIBLE);
                if (mPercentageView != null) {
                    mPercentageView.setVisibility(View.VISIBLE);
                    mPercentageView.setText(mEMMessage.progress() + "%");
                }
                mStatusView.setVisibility(View.INVISIBLE);
                break;
            default:
                mProgressBar.setVisibility(View.INVISIBLE);
                if (mPercentageView != null)
                    mPercentageView.setVisibility(View.INVISIBLE);
                mStatusView.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        String filePath = fileMessageBody.getLocalUrl();
        File file = new File(filePath);
        if (file.exists()) {
            // open files if it exist
            FileUtils.openFile(file, (Activity) mContext);
        } else {
            // download the file
            // mContext.startActivity(new Intent(mContext, EaseShowNormalFileActivity.class).putExtra("msg", mEMMessage));
        }
        if (mEMMessage.direct() == EMMessage.Direct.RECEIVE && !mEMMessage.isAcked() && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
            } catch (HyphenateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


}
