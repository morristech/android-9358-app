package com.xmd.technician.window;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmd.technician.Adapter.SortClubAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CLubMember;
import com.xmd.technician.bean.ClubContactResult;
import com.xmd.technician.bean.Manager;
import com.xmd.technician.bean.Tech;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.PinyinClubUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;
import com.xmd.technician.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MyClubListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.list_customer)
    ListView mListView;
    @Bind(R.id.title_layout_no_friends)
    TextView mAlterMessage;
    @Bind(R.id.title_layout_catalog)
    TextView mTitle;
    @Bind(R.id.title_layout)
    LinearLayout mTitleLayout;
    @Bind(R.id.search_contact)
    EditText editText;
    @Bind(R.id.btn_search)
    ImageView search;
    @Bind(R.id.content_dialog)
    TextView contentDialog;
    @Bind(R.id.contact_sidebar)
    SideBar sildebar;
    @Bind(R.id.status_progressbar)
    ProgressBar statusBar;
    @Bind(R.id.empty_view_widget)
    EmptyView emptyView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Subscription mGetCustomerListSubscription;
    private SortClubAdapter adapter;

    public static MyClubListFragment getInstance() {
        return new MyClubListFragment();
    }

    private int lastFirstVisibleItem = -1;
    private CharacterParser characterParser;
    private List<Manager> mManagerList = new ArrayList<Manager>();
    private List<Tech> mTechList = new ArrayList<Tech>();
    private List<CLubMember> mClubList = new ArrayList<CLubMember>();
    private PinyinClubUtil pinyinComparator;
    List<CLubMember> customerInfos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers_list, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    protected void initView(View view) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_LIST);
        characterParser = CharacterParser.getInstance();
        search.setOnClickListener(this);
        mGetCustomerListSubscription = RxBus.getInstance().toObservable(ClubContactResult.class).subscribe(customer -> {
            handlerClubInfoList(customer);
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void handlerClubInfoList(ClubContactResult clubResult) {
        if (clubResult.respData == null) {
            return;
        }
        if (mTitleLayout.getVisibility() == View.GONE) {
            mTitleLayout.setVisibility(View.VISIBLE);
        }
        statusBar.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        CLubMember clubMember;
        mClubList.clear();
        mManagerList.clear();
        mTechList.clear();
        mManagerList.addAll(clubResult.respData.managers);
        mTechList.addAll(clubResult.respData.techs);
        for (int i = 0; i < mTechList.size(); i++) {
            clubMember = new CLubMember(mTechList.get(i).id, mTechList.get(i).userType, mTechList.get(i).avatarUrl,
                    mTechList.get(i).name, mTechList.get(i).serialNo, mTechList.get(i).phoneNum
            );
            mClubList.add(clubMember);
        }
        pinyinComparator = new PinyinClubUtil();
        Collections.sort(mClubList, pinyinComparator);
        for (int i = 0; i < mManagerList.size(); i++) {
            clubMember = new CLubMember(mManagerList.get(i).id, mManagerList.get(i).userType, mManagerList.get(i).avatarUrl, mManagerList.get(i).name, "", mManagerList.get(i).phoneNum);
            mClubList.add(0, clubMember);
        }
        sildebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                sildebar.setTextView(contentDialog);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
            if (customerInfos != null && customerInfos.size() > 0) {
                intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, customerInfos.get(position).id);
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, customerInfos.get(position).userType.equals(Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER) ? Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER : Constant.CONTACT_INFO_DETAIL_TYPE_TECH);
                if (customerInfos.get(position).userType.equals(Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER)) {
                    intent.putExtra(RequestConstant.KEY_MANAGER_URL, customerInfos.get(position).avatarUrl);
                }

            } else {
                intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, mClubList.get(position).id);
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, mClubList.get(position).userType.equals(Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER) ? Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER : Constant.CONTACT_INFO_DETAIL_TYPE_TECH);
                if (mClubList.get(position).userType.equals(Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER)) {
                    intent.putExtra(RequestConstant.KEY_MANAGER_URL, mClubList.get(position).avatarUrl);
                }
            }
            startActivity(intent);
        });
        adapter = new SortClubAdapter(getActivity(), mClubList, mManagerList.size());
        mListView.setAdapter(adapter);
        mTitle.setText(ResourceUtils.getString(R.string.contact_manager));

        if (mClubList.size() > 0) {
            emptyView.setStatus(EmptyView.Status.Gone);
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem > 0) {

                        int section = getSectionForPosition(firstVisibleItem);
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);
                        int nextSecPosition = getPositonForSection(+nextSection);
                        if (firstVisibleItem != lastFirstVisibleItem) {
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                            params.topMargin = 0;
                            mTitleLayout.setLayoutParams(params);

                            mTitle.setText(mClubList.get(getPositonForSection(section)).sortLetters);
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = mTitleLayout.getHeight();
                                int bottom = childView.getBottom();
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                                if (bottom < titleHeight) {
                                    float pushedDistance = bottom - titleHeight;
                                    params.topMargin = (int) pushedDistance;
                                    mTitleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        mTitleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                    } else {
                        mTitle.setText("店长");
                    }

                    lastFirstVisibleItem = firstVisibleItem;
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

                    if (s.length() > 0) {
                        mTitleLayout.setVisibility(View.GONE);
                        searchContact();
                    } else {
                        mTitleLayout.setVisibility(View.VISIBLE);
                        closeSearch();
                    }

                }
            });
        } else {
            emptyView.setStatus(EmptyView.Status.Gone);
            emptyView.setEmptyPic(R.drawable.empty);
            emptyView.setEmptyTip("");
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

    private void searchContact() {
        String editStr = editText.getText().toString();
        customerInfos = new ArrayList<>();
        if (TextUtils.isEmpty(editStr)) {
            mAlterMessage.setVisibility(View.GONE);
        } else {
            mAlterMessage.setVisibility(View.GONE);
            customerInfos.clear();
            for (CLubMember sortCustomer : mClubList) {
                String name = sortCustomer.name;
                if (name.indexOf(editStr.toString()) != -1 || characterParser.getSelling(name)
                        .startsWith(editStr.toString())) {
                    customerInfos.add(sortCustomer);
                }
            }
        }
        Collections.sort(customerInfos, pinyinComparator);
        if (customerInfos.size() > 0) {
            adapter.updateListView(customerInfos, true);
        } else {
            mTitleLayout.setVisibility(View.GONE);
            mAlterMessage.setVisibility(View.VISIBLE);

        }
    }

    public int getSectionForPosition(int position) {
        if (position < mClubList.size()) {
            return mClubList.get(position).getSortLetters() == null ? -1 : mClubList.get(position).getSortLetters().charAt(0);
        } else {
            return -1;
        }

    }

    public int getPositonForSection(int section) {
        for (int i = 0; i < mClubList.size(); i++) {
            String sortStr = mClubList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private void closeSearch() {
        customerInfos = null;
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            editText.setText("");
        }
        mAlterMessage.setVisibility(View.GONE);
        adapter.updateListView(mClubList, false);
    }

    @Override
    public void onRefresh() {

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_LIST);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
}