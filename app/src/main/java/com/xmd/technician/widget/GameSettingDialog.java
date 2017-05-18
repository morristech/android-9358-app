package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/22.
 */
public class GameSettingDialog extends Dialog {
    @Bind(R.id.comment_game_one)
    TextView mCommentGameOne;
    @Bind(R.id.comment_game_ten)
    TextView mCommentGameTen;
    @Bind(R.id.comment_game_fifty)
    TextView mCommentGameFifty;
    @Bind(R.id.comment_game_one_hundred)
    TextView mCommentGameOneHundred;
    @Bind(R.id.comment_game_five_hundred)
    TextView mCommentGameFiveHundred;
    @Bind(R.id.comment_game_ten_hundred)
    TextView mCommentGameTenHundred;
    @Bind(R.id.dialog_alert_cancel_btn)
    Button mCommentGaemCancelBtn;
    @Bind(R.id.dialog_alert_ok_btn)
    Button mCommentOkBtn;

    public int integralNum = 1;



    public interface ConfirmClickInterface{
        void ConfirmClicked(int num);
    }

    private static List<View> mText = new ArrayList<>();

    private ConfirmClickInterface mConfirmClickInterface;

    public GameSettingDialog(Context context) {
        this(context, R.style.default_dialog_style);
    }




    public void setConfirmClickInterface(ConfirmClickInterface confirmClickInterface){
        this.mConfirmClickInterface = confirmClickInterface;
    }

    public GameSettingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GameSettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_setting_dialog);
        ButterKnife.bind(this);
        mText.add(mCommentGameOne);
        mText.add(mCommentGameTen);
        mText.add(mCommentGameFifty);
        mText.add(mCommentGameOneHundred);
        mText.add(mCommentGameFiveHundred);
        mText.add(mCommentGameTenHundred);
        mCommentGameOne.setSelected(true);
    }

    @OnClick(R.id.dialog_alert_cancel_btn)
    public void cancel() {
        dismiss();
    }

    @OnClick(R.id.dialog_alert_ok_btn)
    public void onConfirmClick() {
        mConfirmClickInterface.ConfirmClicked(integralNum);
        dismiss();
    }

    @OnClick({R.id.comment_game_one, R.id.comment_game_ten, R.id.comment_game_fifty, R.id.comment_game_one_hundred,
            R.id.comment_game_five_hundred, R.id.comment_game_ten_hundred})
    public void checkGameIntegral(View view) {
        switch (view.getId()) {
            case R.id.comment_game_one:
                selectText();
                mCommentGameOne.setSelected(true);
                integralNum = 1;
                break;
            case R.id.comment_game_ten:
                selectText();
                mCommentGameTen.setSelected(true);
                integralNum = 10;
                break;
            case R.id.comment_game_fifty:
                selectText();
                mCommentGameFifty.setSelected(true);
                integralNum = 50;
                break;
            case R.id.comment_game_one_hundred:
                selectText();
                mCommentGameOneHundred.setSelected(true);
                integralNum = 100;
                break;
            case R.id.comment_game_five_hundred:
                selectText();
                mCommentGameFiveHundred.setSelected(true);
                integralNum = 500;
                break;
            case R.id.comment_game_ten_hundred:
                selectText();
                mCommentGameTenHundred.setSelected(true);
                integralNum = 1000;
                break;


        }
    }

    private static void selectText() {
        for (int i = 0; i < mText.size(); i++) {
            mText.get(i).setSelected(false);
        }
    }
}
