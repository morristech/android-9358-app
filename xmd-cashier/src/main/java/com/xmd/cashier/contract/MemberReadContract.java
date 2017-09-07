package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberReadContract {
    interface Presenter extends BasePresenter {
        void onResume();

        void onPause();

        void onNewIntent(Intent intent);

        void onConfirm(String readType);

        void onClickScan();

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface View extends BaseView<Presenter> {
        void setInputContent(String content);

        String getInputContent();

        void showEnterAnim();

        void showExitAnim();

        String getReadType();
    }
}
