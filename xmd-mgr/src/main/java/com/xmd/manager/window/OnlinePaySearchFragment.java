package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.OnlinePayBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OnlinePayListResult;
import com.xmd.manager.widget.ClearableEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-4-27.
 */

public class OnlinePaySearchFragment extends BaseListFragment<OnlinePayBean> {

    @BindView(R.id.search_word)
    ClearableEditText mCetSearchWord;

    private String mSearchUserName = "";

    private Map<String, String> params;

    String startTime;
    String endTime;
    private View view;
    private boolean isLoad;

    private Subscription mOnlinePaySubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online_pay_search, container, false);
        ButterKnife.bind(this, view);
        isLoad = false;
        mCetSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchUserName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    @Override
    protected void dispatchRequest() {
        if (params == null) {
            params = new HashMap<>();
        } else {
            params.clear();
        }
        if (isLoad) {
            params.put(RequestConstant.KEY_START_DATE, startTime);
            params.put(RequestConstant.KEY_END_DATE, endTime);
            params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
            params.put(RequestConstant.KEY_PAGE_SIZE, "1000");
            params.put(RequestConstant.KEY_STATUS, Constant.ONLINE_PAY_STATUS);
            params.put(RequestConstant.KEY_ONLINE_PAY_TECH_NAME, mSearchUserName);
            params.put(RequestConstant.KEY_IS_SEARCH, "1");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FAST_PAY_ORDER_LIST, params);
        }

    }

    @Override
    protected void initView() {

        if (Utils.isNotEmpty(SharedPreferenceHelper.getCurrentClubCreateTime())) {
            startTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        } else {
            startTime = "2015-01-01";
        }
        endTime = DateUtil.getCurrentDate();

        mOnlinePaySubscription = RxBus.getInstance().toObservable(OnlinePayListResult.class).subscribe(
                listResult -> handlerMarketingIncomeList(listResult)
        );
    }

    @OnClick({R.id.iv_back, R.id.iv_search})
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.iv_search:

                mSearchUserName = mCetSearchWord.getText().toString();
                if (Utils.isEmpty(mSearchUserName)) {
                    Utils.makeShortToast(getActivity(), "输入客户手机或技师编号");
                    return;
                }
                isLoad = true;
                onRefresh();
                break;
        }
    }

    private void handlerMarketingIncomeList(OnlinePayListResult listResult) {
        if (listResult.statusCode == 200) {
            if (listResult.respData == null) {
                return;
            }
            if (listResult.isSearch.equals("1") && listResult.respData.fastPayOrderList.size() > 0) {
                onGetListSucceeded(listResult.pageCount, listResult.respData.fastPayOrderList);
            } else {
                Utils.makeShortToast(getActivity(), "未查到相关信息");
            }

        } else {
            onGetListFailed(listResult.msg);
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mOnlinePaySubscription);
    }

}
