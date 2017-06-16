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
import com.xmd.technician.http.gson.ContactAllListResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.BottomContactFilterPopupWindow;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-27.
 */

public class ContactsAllFragment extends BaseListFragment<ContactAllBean> {

    @Bind(R.id.btn_nearby_people)
    Button btnNearbyPeople;
    @Bind(R.id.ll_contact_none)
    LinearLayout llContactNone;
    @Bind(R.id.img_screen_contact)
    ImageView imgScreenContact;

    private Map<String, String> params;

    private String mUserName;
    private int mTotalCount;
    private int mBlackListCount;
    private List<ContactAllBean> contacts;
    private BottomContactFilterPopupWindow contactFilter;
    private String mCurrentFilterType;
    private Subscription mContactsAllSubscription;
    private Subscription mGetNearbyCusCountSubscription;    // 附近的人:获取会所附近客户数量;
    private boolean hasNearbyPeople;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_all, container, false);
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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_CUSTOMER_USER_ALL_LIST, params);
    }

    @Override
    protected void initView() {
        params = new HashMap<>();
        mUserName = "";
        contacts = new ArrayList<>();
        mContactsAllSubscription = RxBus.getInstance().toObservable(ContactAllListResult.class).subscribe(
                result -> handlerContactAllListResult(result)
        );
        mGetNearbyCusCountSubscription = RxBus.getInstance().toObservable(NearbyCusCountResult.class).subscribe(
                this::handleNearbyStatus);
    }

    // 附近的人
    private void handleNearbyStatus(NearbyCusCountResult result) {

        if (result.statusCode == 200) {
            if (result.respData <= 0) {
                hasNearbyPeople = false;
                btnNearbyPeople.setText(ResourceUtils.getString(R.string.contact_to_develop_customer));
            } else {
                hasNearbyPeople = true;
                btnNearbyPeople.setText(ResourceUtils.getString(R.string.contact_to_nearby_people));
            }
        }
    }

    private void handlerContactAllListResult(ContactAllListResult result) {

        if (result.statusCode == 200) {
            if (result.respData == null) {
                return;
            }
            contacts.clear();
            mTotalCount = result.respData.totalCount;
            mBlackListCount = result.respData.blackListCount;
            if (Utils.isEmpty(mCurrentFilterType) && Utils.isEmpty(mUserName) && result.respData.userList.size()==0) {
                llContactNone.setVisibility(View.VISIBLE);
                imgScreenContact.setVisibility(View.GONE);
            } else {
                llContactNone.setVisibility(View.GONE);
                imgScreenContact.setVisibility(View.VISIBLE);
            }
            if (mTotalCount > 0 && Utils.isEmpty(mCurrentFilterType) && Utils.isEmpty(mUserName)) {
                mListAdapter.SetDataLoadCompleteDes(String.format("共%s名客户,已拉黑%s人", mTotalCount, mBlackListCount));
            } else {
                mListAdapter.SetDataLoadCompleteDes("");
            }
            if (Utils.isNotEmpty(result.respData.serviceStatus) && result.respData.serviceStatus.equals("Y")) {
                for (int i = 0; i < result.respData.userList.size(); i++) {
                    result.respData.userList.get(i).isService = true;
                    contacts.add(result.respData.userList.get(i));
                }
            } else {
                contacts.addAll(result.respData.userList);
            }

            onGetListSucceeded(result.pageCount, contacts);

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

    @OnClick({R.id.img_screen_contact, R.id.btn_nearby_people})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_screen_contact:
                if (null == contactFilter) {
                    initPopupWindow();
                    contactFilter.show();
                } else {
                    contactFilter.show();
                }
                break;
            case R.id.btn_nearby_people:
                // 打开附近的人
                if (hasNearbyPeople) {
                    Intent intent = new Intent(getActivity(), NearbyActivity.class);
                    startActivity(intent);
                } else {
                    // 跳转到营销页面
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.switchFragment(mainActivity.getFragmentSize() - 1);
                }

                break;
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
        RxBus.getInstance().unsubscribe(mContactsAllSubscription, mGetNearbyCusCountSubscription);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
