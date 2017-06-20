package com.xmd.technician.window;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ContactHandlerBean;
import com.xmd.technician.bean.CurrentSelectPage;
import com.xmd.technician.common.FragmentController;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.ClearableEditText;
import com.xmd.technician.widget.DropDownMenuDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-26.
 */

public class ContactsSummaryFragment extends BaseFragment {

    @Bind(R.id.ed_search_contact)
    ClearableEditText edSearchContact;
    @Bind(R.id.img_btn_search)
    ImageView imgBtnSearch;
    @Bind(R.id.tv_customer_all)
    TextView tvCustomerAll;
    @Bind(R.id.tv_customer_register)
    TextView tvCustomerRegister;
    @Bind(R.id.tv_customer_visitor)
    TextView tvCustomerVisitor;
    @Bind(R.id.tv_customer_technician)
    TextView tvCustomerTechnician;
    @Bind(R.id.contact_fragment_view)
    FrameLayout contactFragmentView;


    @Bind(R.id.contact_more)
    LinearLayout llContactMore;

    private List<View> tableViews;
    private FragmentController mFragmentController;
    private View view;
    private  CircleImageView techHead;

    private Subscription mGetTechCurrentInfoSubscription;
    private Subscription getCurrentSelectedPageSubscription;
    private Subscription mContactHandlerSubscription; // 对用户进行了操作

    private int mCurrentFragmentIndex;
    private String mSearchText = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_contacts_summary, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        techHead = (CircleImageView) view.findViewById(R.id.toolbar_left_head);
        techHead.setVisibility(View.VISIBLE);
        if(Utils.isNotEmpty(SharedPreferenceHelper.getUserAvatar())){
            Glide.with(getActivity()).load(SharedPreferenceHelper.getUserAvatar()).placeholder(R.drawable.icon22).error(R.drawable.icon22).into(techHead);
        }else{
           Glide.with(getActivity()).load(R.drawable.icon22).into(techHead);
        }
        view.findViewById(R.id.contact_more).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.main_conversion);
        tableViews = new ArrayList<>();
        tableViews.add(tvCustomerAll);
        tableViews.add(tvCustomerRegister);
        tableViews.add(tvCustomerVisitor);
        tableViews.add(tvCustomerTechnician);
        mFragmentController = FragmentController.getInstance(this, R.id.contact_fragment_view);
        changeViewState(tvCustomerAll);

        mCurrentFragmentIndex = Constant.CONTACT_ALL_INDEX;
        edSearchContact.setCleanTextListener(new ClearableEditText.CleanTextListener() {
            @Override
            public void cleanText() {
                SearchOrFilterContact("");
                hideKeyboard();

            }
        });
        mGetTechCurrentInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class).subscribe(
                techCurrentResult -> handleTechCurrentResult(techCurrentResult));
        getCurrentSelectedPageSubscription = RxBus.getInstance().toObservable(CurrentSelectPage.class).subscribe(
                selectedPage -> {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (selectedPage.selectItem) {
                                case 1:
                                    changeViewState(tvCustomerRegister);
                                    break;
                                case 2:
                                    changeViewState(tvCustomerVisitor);
                                    break;

                            }
                        }
                    });
                }
        );
        mContactHandlerSubscription = RxBus.getInstance().toObservable(ContactHandlerBean.class).subscribe(
                result -> handlerContact()
        );
    }

    private void handleTechCurrentResult(TechInfoResult techCurrentResult) {
        if (techCurrentResult.respData == null) {
            Glide.with(getActivity()).load(R.drawable.icon22).into(techHead);
            return;
        }
        Glide.with(getActivity()).load(techCurrentResult.respData.imageUrl).into(techHead);
    }

    //技师对用户进行了操作,刷新相关列表
    private void handlerContact() {
        ((ContactsAllFragment) getChildFragmentManager().getFragments().get(0)).filterCustomer("");
        ((ContactsRegisterFragment) getChildFragmentManager().getFragments().get(1)).filterCustomer("");
        ((ContactsVisitorsFragment) getChildFragmentManager().getFragments().get(2)).filterCustomer("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetTechCurrentInfoSubscription, getCurrentSelectedPageSubscription, mContactHandlerSubscription);
    }

    @OnClick(R.id.contact_more)
    public void showMore() {
        final String[] items = new String[]{ResourceUtils.getString(R.string.contact_add_contact), ResourceUtils.getString(R.string.contact_blacklist_manager)};
        DropDownMenuDialog.getDropDownMenuDialog(getActivity(), items, (index -> {
            switch (index) {
                case 0:
                    Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1 = new Intent(getActivity(), EmchatBlacklistActivity.class);
                    startActivity(intent1);
                    break;
            }
        })).show(llContactMore);
    }

    @OnClick({R.id.toolbar_left_head, R.id.img_btn_search, R.id.tv_customer_all, R.id.tv_customer_register, R.id.tv_customer_visitor, R.id.tv_customer_technician})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_left_head:
                Intent intent = new Intent(getActivity(), TechInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.img_btn_search:
                mSearchText = edSearchContact.getText().toString();
                if (Utils.isEmpty(mSearchText)) {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.contact_search_is_null_alter));
                } else {
                    hideKeyboard();
                    SearchOrFilterContact(mSearchText);
                }
                break;
            case R.id.tv_customer_all:
                changeViewState(tvCustomerAll);
                break;
            case R.id.tv_customer_register:
                changeViewState(tvCustomerRegister);
                break;
            case R.id.tv_customer_visitor:
                changeViewState(tvCustomerVisitor);
                break;
            case R.id.tv_customer_technician:
                changeViewState(tvCustomerTechnician);
                break;


        }
    }


    private void changeViewState(View view) {
        for (View v : tableViews) {
            v.setSelected(false);
        }
        switch (view.getId()) {

            case R.id.tv_customer_all:
                tvCustomerAll.setSelected(true);
                mFragmentController.showFragment(0);
                mCurrentFragmentIndex = Constant.CONTACT_ALL_INDEX;
                break;
            case R.id.tv_customer_register:
                tvCustomerRegister.setSelected(true);
                mFragmentController.showFragment(1);
                mCurrentFragmentIndex = Constant.CONTACT_REGISTER_INDEX;
                break;
            case R.id.tv_customer_visitor:
                tvCustomerVisitor.setSelected(true);
                mFragmentController.showFragment(2);
                mCurrentFragmentIndex = Constant.CONTACT_VISITOR_INDEX;

                break;
            case R.id.tv_customer_technician:
                tvCustomerTechnician.setSelected(true);
                mFragmentController.showFragment(3);
                mCurrentFragmentIndex = Constant.CONTACT_CLUB_INDEX;
                break;
        }

    }

    private void SearchOrFilterContact(String userName) {
        switch (mCurrentFragmentIndex) {
            case Constant.CONTACT_ALL_INDEX:
                ((ContactsAllFragment) getChildFragmentManager().getFragments().get(mCurrentFragmentIndex)).filterCustomer(userName);
                break;
            case Constant.CONTACT_REGISTER_INDEX:
                ((ContactsRegisterFragment) getChildFragmentManager().getFragments().get(mCurrentFragmentIndex)).filterCustomer(userName);
                break;
            case Constant.CONTACT_VISITOR_INDEX:
                ((ContactsVisitorsFragment) getChildFragmentManager().getFragments().get(mCurrentFragmentIndex)).filterCustomer(userName);
                break;
            case Constant.CONTACT_CLUB_INDEX:
                ((ContactsTechnicianFragment) getChildFragmentManager().getFragments().get(mCurrentFragmentIndex)).filterCustomer(userName);
                break;
        }
    }

    private void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentController.destoryController();
    }


}
