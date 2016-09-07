package com.xmd.technician.window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xmd.technician.Adapter.SortCustomerAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.PinyinCompartorUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CustomerListFragment extends Fragment implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener{
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
    SideBar sildebar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Subscription mGetCustomerListSubscription;
    private SortCustomerAdapter adapter;
    private LayoutInflater layoutInflater;
    private View  viewM;
    private View view;
    private PopupWindow window = null;


    public static CustomerListFragment getInstance() {
        return new CustomerListFragment();
    }

    private int lastFisrstVisibleItem = -1;
    private CharacterParser characterParser;
    private List<CustomerInfo> mCustomerList = new ArrayList<CustomerInfo>();
    private PinyinCompartorUtil pinyinComparator;
    private List<CustomerInfo> customerInfos;
    private Map<String,String> params = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customers_list, container, false);
        initView(view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void initView(View view) {
        params.put(RequestConstant.KEY_CONTACT_TYPE,"");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST,params);
        characterParser = CharacterParser.getInstance();
        mGetCustomerListSubscription = RxBus.getInstance().toObservable(CustomerListResult.class).subscribe(customer -> {
            handlerCustomerInfoList(customer);
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void handlerCustomerInfoList(CustomerListResult customerResult) {
        mCustomerList.clear();
        mCustomerList.addAll(customerResult.respData);
        if(mCustomerList.size()>0){
                       titleLayout.setVisibility(View.VISIBLE);
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
            mSwipeRefreshLayout.setRefreshing(false);
            search.setOnClickListener(this);
            sildebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
                @Override
                public void onTouchingLetterChanged(String s) {
                    sildebar.setTextView(contentDialog);
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
                    intent.putExtra(RequestConstant.CONTACT_TYPE,mCustomerList.get(position).customerType);
                    intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, "customer");
                    startActivity(intent);
                }
            });
            pinyinComparator = new PinyinCompartorUtil();
            Collections.sort(mCustomerList, pinyinComparator);
            if (mCustomerList.size() <=0) {
                return;
            }
            adapter = new SortCustomerAdapter(getActivity(), mCustomerList);
            listView.setAdapter(adapter);
            alertMessage.setVisibility(View.GONE);
            if (mCustomerList.size() == 1) {
                title.setVisibility(View.GONE);
            }
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
            editText.setFilters(new InputFilter[]{filter});

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
                        titleLayout.setVisibility(View.GONE);
                        searchCustomer();
                    }else{
                        titleLayout.setVisibility(View.VISIBLE);
                        closeSearch();
                    }

                }
            });

        }else{
            titleLayout.setVisibility(View.GONE);
            adapter = new SortCustomerAdapter(getActivity(), new ArrayList<>());
            listView.setAdapter(adapter);
            mSwipeRefreshLayout.setRefreshing(false);
            Utils.makeShortToast(getActivity(),"未查询到相关联系人");

        }

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
            alertMessage.setVisibility(View.GONE);
        } else {
            alertMessage.setVisibility(View.GONE);
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
            adapter.updateListView(customerInfos,true);
        } else {
            titleLayout.setVisibility(View.GONE);
            alertMessage.setVisibility(View.VISIBLE);
        }
    }

    public int getSectionForPosition(int position) {

        if(position<mCustomerList.size()){
            return mCustomerList.get(position).sortLetters.charAt(0);
        }else{
            return -1;
        }

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
        adapter.updateListView(mCustomerList ,false);
    }



    @Override
    public void onPause() {
        super.onPause();
        if(window!=null){
            window.dismiss();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        TextView tv = (TextView) getActivity().findViewById(R.id.table_contact);
        if(tv.getText().equals(ResourceUtils.getString(R.string.phone_contact))){
            requestContactList(RequestConstant.TECH_ADD);
        }else if(tv.getText().equals(ResourceUtils.getString(R.string.fans_contact))){
            requestContactList(RequestConstant.FANS_USER);
        }else if(tv.getText().equals(ResourceUtils.getString(R.string.wx_contact))){
            requestContactList(RequestConstant.WX_USER);
        }else{
            params.put(RequestConstant.KEY_CUSTOMER_TYPE,"");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST,params);
        }

    }
    private void requestContactList(String type){
        params.clear();
        params.put(RequestConstant.KEY_CUSTOMER_TYPE,type);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST,params);
    }
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source.equals(" ")||source.toString().contentEquals("\n"))return "";
            else return null;
        }
    };
}
