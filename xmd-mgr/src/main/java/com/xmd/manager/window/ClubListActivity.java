package com.xmd.manager.window;


import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;

import com.xmd.manager.AppConfig;
import com.xmd.manager.Manager;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.common.ActivityHelper;
import com.xmd.manager.common.CharacterParserUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubEnterResult;
import com.xmd.manager.service.response.ClubListResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by sdcm on 16-7-15.
 */
public class ClubListActivity extends BaseListActivity<ClubInfo, ClubListResult> implements TextWatcher {

    public static final String EXTRA_SHOW_LEFT = "show_left";

    private Subscription mEnterClubViewSubscription;
    private Subscription mClubListSubscription;
    private List<ClubInfo> mSerchClubInfo;
    private List<ClubInfo> mALlClubInfo;
    private ClearableEditText mEditText;
    private CharacterParserUtil characterParser;

    @Override
    protected void setContentViewLayout() {
        super.setContentViewLayout();
        setContentView(R.layout.activity_club_list);
        mEditText = (ClearableEditText) findViewById(R.id.search_club);
        mEditText.addTextChangedListener(this);
        mEditText.setFilters(new InputFilter[]{filter});
        initView();
    }

    private void initView() {
        setLeftVisible(getIntent().getBooleanExtra(EXTRA_SHOW_LEFT, false), -1);
        setLeftVisible(false, -1);
        Utils.hideKeyboard(this);
        setRightVisible(true, R.drawable.ic_exit_selector, view -> {
            new AlertDialogBuilder(this)
                    .setTitle(ResourceUtils.getString(R.string.logout_confirm_title))
                    .setMessage(ResourceUtils.getString(R.string.logout_confirm_message))
                    .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> {
                        Manager.getInstance().prepareBeforeUserLogout();
                        gotoLoginActivity("");
                    })
                    .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                    .show();
        });
        characterParser = CharacterParserUtil.getInstance();
        mEnterClubViewSubscription = RxBus.getInstance().toObservable(ClubEnterResult.class).subscribe(
                result -> handleLoginResult(result));
        mClubListSubscription = RxBus.getInstance().toObservable(ClubListResult.class).subscribe(
                result -> handleClubList(result)
        );
    }

    private void handleClubList(ClubListResult result) {
        if (result.statusCode == 200) {
            mALlClubInfo = new ArrayList<>();
            mALlClubInfo.addAll(result.respData);
        }
    }


    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_CLUB_NAME, "");
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(1000));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_LIST, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mEnterClubViewSubscription, mClubListSubscription);
    }

    @Override
    public void onItemClicked(ClubInfo clubInfo) {
        if (Utils.isNotEmpty(clubInfo.clubName)) {
            SharedPreferenceHelper.setCurrentClubName(clubInfo.clubName);
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_CLUB_ID, clubInfo.clubId);
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ENTER_CLUB_VIEW, params);
    }

    private void handleLoginResult(ClubEnterResult result) {
        if (result.statusCode == 200) {
            SharedPreferenceHelper.saveUser(result.respData);
            ActivityHelper.getInstance().removeAllActivities();
            startActivity(new Intent(ClubListActivity.this, MainActivity.class));
            finish();
        } else {
            makeShortToast(result.msg);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() > 0) {
            searchClub();
        } else {
            onGetListSucceeded(1, mALlClubInfo);
        }
    }

    private void searchClub() {
        String editStr = mEditText.getText().toString();
        mSerchClubInfo = new ArrayList<>();
        if (Utils.isNotEmpty(editStr)) {
            mSerchClubInfo.clear();
            for (ClubInfo sortClub : mALlClubInfo) {
                String name = sortClub.clubName;
                if (name.indexOf(editStr.toString()) != -1 || characterParser.getSelling(name).startsWith(editStr.toString())) {
                    mSerchClubInfo.add(sortClub);
                }

            }
            onGetListSucceeded(1, mSerchClubInfo);
        }
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
}
