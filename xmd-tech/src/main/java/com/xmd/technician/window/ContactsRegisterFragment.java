package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.ContactAllBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactRegisterListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.BottomContactFilterPopupWindow;
import com.xmd.technician.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-27.
 */

public class ContactsRegisterFragment extends BaseListFragment<ContactAllBean> {

    @Bind(R.id.btn_nearby_people)
    Button btnNearbyPeople;
    @Bind(R.id.ll_contact_none)
    LinearLayout llContactNone;
    @Bind(R.id.contact_all_emptyView)
    EmptyView contactAllEmptyView;
    @Bind(R.id.img_screen_contact)
    ImageView imgScreenContact;

    private Map<String, String> params;
    private String mUserName;
    private int mTotalCount;
    private int mBlackListCount;
    private BottomContactFilterPopupWindow contactFilter;
    private String mCurrentFilterType;
    private Subscription mContactsRegisterSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_register, container, false);
        ButterKnife.bind(this, view);
        mCurrentFilterType = Constant.USER_ALL;
        return view;
    }

    @Override
    protected void dispatchRequest() {
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, mCurrentFilterType);
        params.put(RequestConstant.KEY_USER_NAME, mUserName);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_CUSTOMER_USER_REGISTER_LIST, params);
    }

    @Override
    protected void initView() {
        params = new HashMap<>();
        mUserName = "";
        mContactsRegisterSubscription = RxBus.getInstance().toObservable(ContactRegisterListResult.class).subscribe(
                result -> handlerContactRegisterListResult(result)
        );
    }

    private void handlerContactRegisterListResult(ContactRegisterListResult result) {
        if (result.statusCode == 200) {
            mTotalCount = result.respData.totalCount;
            mBlackListCount = result.respData.blackListCount;
            if (mTotalCount > 0 && Utils.isEmpty(mCurrentFilterType) && Utils.isEmpty(mUserName)) {
                mListAdapter.SetDataLoadCompleteDes(String.format("共%s名客户,已拉黑%s人", mTotalCount, mBlackListCount));
            } else {
                mListAdapter.SetDataLoadCompleteDes("");
            }
            if (result.respData.userList.size() == 0) {
                imgScreenContact.setVisibility(View.GONE);
            } else {
                imgScreenContact.setVisibility(View.VISIBLE);
            }
            onGetListSucceeded(result.pageCount, result.respData.userList);

        } else {
            onGetListFailed(result.msg);
        }
    }

    public void filterCustomer(String userName) {
        this.mUserName = userName;
        onRefresh();
    }

    @Override
    public void onItemClicked(ContactAllBean bean) throws HyphenateException {
        super.onItemClicked(bean);
        Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
        if (Utils.isNotEmpty(bean.id)) {
            intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, bean.id);
        }
        if (Utils.isNotEmpty(bean.userId)) {
            intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
        }
        if (Utils.isEmpty(bean.id) && Utils.isEmpty(bean.userId)) {
            Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.contact_has_no_information_alter));
            return;
        }
        intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER, true);
        intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER);
        startActivity(intent);
    }

    @OnClick(R.id.img_screen_contact)
    public void onViewClicked() {
        if (null == contactFilter) {
            initPopupWindow();
            contactFilter.show();
        } else {
            contactFilter.show();
        }
    }

    private void initPopupWindow() {
        contactFilter = new BottomContactFilterPopupWindow(getActivity(), imgScreenContact);
        contactFilter.setContactFilterListener(new BottomContactFilterPopupWindow.ContactFilterListener() {
            @Override
            public void contactFilter(int selectedType) {
                switch (selectedType) {
                    case Constant.FILTER_ALL_CONTACT:
                        if (mCurrentFilterType == Constant.USER_ALL) {
                            return;
                        } else {
                            mCurrentFilterType = Constant.USER_ALL;
                            Glide.with(getActivity()).load(R.drawable.image_screen_all).into(imgScreenContact);
                            filterCustomer("");
                        }
                        break;
                    case Constant.FILTER_WX_CONTACT:
                        if (mCurrentFilterType == Constant.USER_WX) {
                            return;
                        } else {
                            mCurrentFilterType = Constant.USER_WX;
                            Glide.with(getActivity()).load(R.drawable.image_screen_wx).into(imgScreenContact);
                            filterCustomer("");
                        }
                        break;
                    case Constant.FILTER_FANS_CONTACT:
                        if (mCurrentFilterType == Constant.USER_FANS) {
                            return;
                        } else {
                            mCurrentFilterType = Constant.USER_FANS;
                            Glide.with(getActivity()).load(R.drawable.image_screen_phone).into(imgScreenContact);
                            filterCustomer("");
                        }
                        break;
                    case Constant.FILTER_FANS_AND_WX_CONTACT:
                        if (mCurrentFilterType == Constant.USER_FANS_WX) {
                            return;
                        } else {
                            mCurrentFilterType = Constant.USER_FANS_WX;
                            Glide.with(getActivity()).load(R.drawable.image_screen_phone_and_wx).into(imgScreenContact);
                            filterCustomer("");
                        }

                        break;
                    case Constant.FILTER_PHONE_CONTACT:
                        if (mCurrentFilterType == Constant.USER_TECH_ADD) {
                            return;
                        } else {
                            mCurrentFilterType = Constant.USER_TECH_ADD;
                            Glide.with(getActivity()).load(R.drawable.image_screen_address_book).into(imgScreenContact);
                            filterCustomer("");
                        }

                        break;
                    default:
                        contactFilter.dismiss();
                        break;


                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mContactsRegisterSubscription);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
