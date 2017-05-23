package com.xmd.technician.contract;

import android.content.Intent;

/**
 * Created by heyangya on 16-12-19.
 */

public interface IBaseView {
    void finishSelf();

    void showLoading(String message);

    void hideLoading();

    void showToast(String message);

    void showAlertDialog(String message);

    Intent getIntent();

    void setResult(int resultCode, Intent data);
}
