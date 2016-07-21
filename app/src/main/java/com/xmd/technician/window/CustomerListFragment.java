package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.technician.Adapter.SortCustomerAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.PinyinCompartorUtil;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CustomerListFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.search_contact)
    EditText editText;
    @Bind(R.id.btn_search)
    ImageView search;
    @Bind(R.id.list_customer)
    ListView listView;
    @Bind(R.id.title_layout_no_friends)
    TextView alertMessage;
    @Bind(R.id.title_layout_catalog)
    TextView title;
    @Bind(R.id.title_layout)
    LinearLayout titleLayout;
    @Bind(R.id.content_dialog)
    TextView contentDialog;
    @Bind(R.id.contact_sidebar)
    SideBar silebar;
    private Subscription mGetCustomerListSubscription;
    private SortCustomerAdapter adapter;

    public static CustomerListFragment getInstance() {
        return new CustomerListFragment();
    }

    private int lastFisrstVisibleItem = -1;
    private CharacterParser characterParser;
    private List<CustomerInfo> mCustomerList = new ArrayList<CustomerInfo>();
    private PinyinCompartorUtil pinyinComparator;
    private List<CustomerInfo> customerInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers_list, container, false);
        initView(view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void initView(View view) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST);
        characterParser = CharacterParser.getInstance();
        mGetCustomerListSubscription = RxBus.getInstance().toObservable(CustomerListResult.class).subscribe(customer -> {
            handlerCustomerInfoList(customer);
        });
    }

    private void handlerCustomerInfoList(CustomerListResult customerResult) {
        mCustomerList.clear();
        mCustomerList.addAll(customerResult.respData);
        String name;
        for (int i = 0; i < mCustomerList.size(); i++) {
            if (Utils.isNotEmpty(mCustomerList.get(i).userNoteName)) {
                name = mCustomerList.get(i).userNoteName;
            } else if (Utils.isNotEmpty(mCustomerList.get(i).userName)) {
                name = mCustomerList.get(i).userName;
            } else {
                name = "匿名";
            }
            String pinyin = characterParser.getSelling(name);
            if (pinyin.length() > 0) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    mCustomerList.get(i).sortLetters = sortString;
                } else {
                    mCustomerList.get(i).sortLetters = "#";
                }
            }
        }
        search.setOnClickListener(this);
        silebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                silebar.setTextView(contentDialog);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
                if(customerInfos!=null&&customerInfos.size()>0){
                    intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, customerInfos.get(position).id);
                }else{
                    intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, mCustomerList.get(position).id);
                }
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, "customer");
                startActivity(intent);
            }
        });
        pinyinComparator = new PinyinCompartorUtil();
        Collections.sort(mCustomerList, pinyinComparator);
        if (mCustomerList.size() < 0) {
            return;
        }
        adapter = new SortCustomerAdapter(getActivity(), mCustomerList);
        listView.setAdapter(adapter);
        alertMessage.setVisibility(View.GONE);
        if (mCustomerList.size() == 1) {
            title.setVisibility(View.GONE);
        } else if(mCustomerList.size()>1){
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int section = getSectionForPosition(firstVisibleItem);
                    int nextSection = getSectionForPosition(firstVisibleItem + 1);
                    int nextSecPositon = getPositonForSection(+nextSection);
                    if (firstVisibleItem != lastFisrstVisibleItem) {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                        params.topMargin = 0;
                        titleLayout.setLayoutParams(params);
                        title.setText(mCustomerList.get(getPositonForSection(section)).sortLetters);
                    }
                    if (nextSecPositon == firstVisibleItem + 1) {
                        View childView = view.getChildAt(0);
                        if (childView != null) {
                            int titleHeight = titleLayout.getHeight();
                            int bottom = childView.getBottom();
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                            if (bottom < titleHeight) {
                                float pushedDistance = bottom - titleHeight;
                                params.topMargin = (int) pushedDistance;
                                titleLayout.setLayoutParams(params);
                            } else {
                                if (params.topMargin != 0) {
                                    params.topMargin = 0;
                                    titleLayout.setLayoutParams(params);
                                }
                            }
                        }
                    }
                    lastFisrstVisibleItem = firstVisibleItem;
                }
            });
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    searchCustomer();
                }else{
                    closeSearch();
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetCustomerListSubscription);
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        closeSearch();

    }

    private void searchCustomer() {
        String editStr = editText.getText().toString();
        customerInfos = new ArrayList<>();
        if (TextUtils.isEmpty(editStr)) {
            alertMessage.setVisibility(View.VISIBLE);
            alertMessage.setVisibility(View.GONE);
        } else {
            customerInfos.clear();
            for (CustomerInfo sortCustomer : mCustomerList) {
                String name = sortCustomer.userNoteName;
                if (name.indexOf(editStr.toString()) != -1 || characterParser.getSelling(name)
                        .startsWith(editStr.toString())) {
                    customerInfos.add(sortCustomer);
                }
            }
        }
        Collections.sort(customerInfos, pinyinComparator);
        if (customerInfos.size() > 0) {
            adapter.updateListView(customerInfos);
        } else {
            titleLayout.setVisibility(View.GONE);
            alertMessage.setVisibility(View.VISIBLE);
            Log.i("TAG", "为查询到...");
        }
    }

    public int getSectionForPosition(int position) {
        return mCustomerList.get(position).sortLetters.charAt(0);
    }

    public int getPositonForSection(int section) {
        for (int i = 0; i < mCustomerList.size(); i++) {
            String sortStr = mCustomerList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private void closeSearch(){
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            editText.setText("");
        }
        customerInfos =null;
        alertMessage.setVisibility(View.GONE);
        adapter.updateListView(mCustomerList);
    }

}
