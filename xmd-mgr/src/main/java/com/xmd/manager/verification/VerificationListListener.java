package com.xmd.manager.verification;

import com.xmd.manager.beans.CheckInfo;

/**
 * Created by heyangya on 17-5-16.
 */

public interface VerificationListListener {
    void onItemClicked(CheckInfo checkInfo);

    void onItemChecked(boolean isChecked, CheckInfo checkInfo);
}
