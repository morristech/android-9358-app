package com.xmd.contact;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.xmd.app.Constants;
import com.xmd.app.utils.Utils;
import com.xmd.contact.adapter.TagListAdapter;
import com.xmd.contact.bean.TagBean;
import com.xmd.contact.bean.TreatedTagList;
import com.xmd.contact.event.ContactUmengStatisticsEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-26.
 */

public class BottomContactFilterPopupWindow {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View view;
    private PopupWindow mPopupWindow;
    private ContactFilterListener mFilterListener;
    private View tagView;
    private TagListAdapter adapter;
    private RecyclerView mExpendRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<TreatedTagList> mTagList;
    private Button tagBtnRest;
    private Button tagBtnSave;
    private View rightView;
    private List<String> mTagNameFilter;
    private List<String> mUserGroupFilter;
    private List<String> mCustomerLevelFilter;
    private List<String> mCustomerTypeFilter;
    private List<String> mSerialNoFilter;

    public BottomContactFilterPopupWindow(Context context, View tagView, List<TreatedTagList> tagList) {
        this.mContext = context;
        this.tagView = tagView;
        this.mTagList = tagList;
        mTagNameFilter = new ArrayList<>();
        mUserGroupFilter = new ArrayList<>();
        mCustomerLevelFilter = new ArrayList<>();
        mCustomerTypeFilter = new ArrayList<>();
        mSerialNoFilter = new ArrayList<>();
    }

    public void show() {
        initView();
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void setContactFilterListener(ContactFilterListener filterListener) {
        this.mFilterListener = filterListener;
    }

    public interface ContactFilterListener {
        void contactFilter(String tagName, String userGroup, String customerLevel, String customerType, String serialNo);
    }

    private void initView() {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mLayoutInflater.inflate(R.layout.tech_search_contacts_layout, null);
        final Activity activity = (Activity) mContext;
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 0.5f;
        activity.getWindow().setAttributes(lp);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mExpendRecyclerView = (RecyclerView) view.findViewById(R.id.tag_expand_list);
        tagBtnRest = (Button) view.findViewById(R.id.tag_btn_rest);
        tagBtnSave = (Button) view.findViewById(R.id.tag_btn_save);
        rightView = view.findViewById(R.id.view_dismiss);
        tagBtnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilterData();
            }
        });
        tagBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilterData();
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        adapter = new TagListAdapter(mContext, mTagList);
        adapter.setOnTagChildrenItemClickedListener(new TagListAdapter.TagChildrenItemClickedListener() {
            @Override
            public void itemClicked(TagBean treatedTag, int childrenPosition) {
                if (treatedTag.tagType.equals("tagName")) {
                    if (childrenPosition == 0) {
                        mTagNameFilter.clear();
                    } else {
                        if (treatedTag.isSelected) {
                            mTagNameFilter.remove(treatedTag.tagString);
                        } else {
                            mTagNameFilter.add(treatedTag.tagString);
                        }
                    }
                }
                if (treatedTag.tagType.equals("userGroup")) {
                    if (childrenPosition == 0) {
                        mUserGroupFilter.clear();
                    } else {
                        if (treatedTag.isSelected) {
                            if (treatedTag.tagString.equals("无分组")) {
                                mUserGroupFilter.remove("-1");
                            } else {
                                mUserGroupFilter.remove(treatedTag.tagString);
                            }

                        } else {
                            if (treatedTag.tagString.equals("无分组")) {
                                mUserGroupFilter.add("-1");
                            } else {
                                mUserGroupFilter.add(treatedTag.tagString);
                            }

                        }
                    }
                }
                if (treatedTag.tagType.equals("customerLevel")) {
                    if (childrenPosition == 0) {
                        mCustomerLevelFilter.clear();
                    } else {
                        if (treatedTag.isSelected) {
                            mCustomerLevelFilter.remove(treatedTag.tagString);
                        } else {
                            mCustomerLevelFilter.add(treatedTag.tagString);
                        }
                    }
                }
                if (treatedTag.tagType.equals("customerType")) {
                    if (childrenPosition == 0) {
                        mCustomerTypeFilter.clear();
                    } else {
                        if (treatedTag.isSelected) {
                            if (treatedTag.tagString.equals("微信")) {
                                mCustomerTypeFilter.remove("weixin");
                            }
                            if (treatedTag.tagString.equals("手机")) {
                                mCustomerTypeFilter.remove("user");
                            }
                            if (treatedTag.tagString.equals("支付宝")) {
                                mCustomerTypeFilter.remove("alipay");
                            }
                            if (treatedTag.tagString.equals("通讯录")) {
                                mCustomerTypeFilter.remove("tech_add");
                            }
                        } else {
                            if (treatedTag.tagString.equals("微信")) {
                                mCustomerTypeFilter.add("weixin");
                            }
                            if (treatedTag.tagString.equals("手机")) {
                                mCustomerTypeFilter.add("user");
                            }
                            if (treatedTag.tagString.equals("支付宝")) {
                                mCustomerTypeFilter.add("alipay");
                            }
                            if (treatedTag.tagString.equals("通讯录")) {
                                mCustomerTypeFilter.add("tech_add");
                            }

                        }
                    }
                }
                if (treatedTag.tagType.equals("serialNo")) {
                    if (childrenPosition == 0) {
                        mSerialNoFilter.clear();
                    } else {
                        if (treatedTag.isSelected) {
                            if (treatedTag.tagString.equals("未归属")) {
                                mSerialNoFilter.remove("-1");
                            } else {
                                mSerialNoFilter.remove(treatedTag.tagString);
                            }

                        } else {
                            if (treatedTag.tagString.equals("未归属")) {
                                mSerialNoFilter.add("-1");
                            } else {
                                mSerialNoFilter.add(treatedTag.tagString);
                            }

                        }
                    }
                }

            }
        });
        mLayoutManager = new LinearLayoutManager(mContext);
        mExpendRecyclerView.setHasFixedSize(true);
        mExpendRecyclerView.setLayoutManager(mLayoutManager);
        mExpendRecyclerView.setAdapter(adapter);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                lp.dimAmount = 1.0f;
                activity.getWindow().setAttributes(lp);
            }
        });

        try {
            if (mPopupWindow != null) {
                mPopupWindow.showAtLocation(tagView, Gravity.RIGHT, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetFilterData() {
        mTagNameFilter.clear();
        mUserGroupFilter.clear();
        mCustomerLevelFilter.clear();
        mCustomerTypeFilter.clear();
        mSerialNoFilter.clear();
        for (int i = 0; i < mTagList.size(); i++) {
            for (int j = 0; j < mTagList.get(i).list.size(); j++) {
                if (j == 0) {
                    mTagList.get(i).list.get(0).isSelected = true;
                } else {
                    mTagList.get(i).list.get(j).isSelected = false;
                }
            }
        }
        adapter.setData(mTagList);
    }

    private void saveFilterData() {
        if (mFilterListener != null) {
            mFilterListener.contactFilter(Utils.list2String(mTagNameFilter, ","), Utils.list2String(mUserGroupFilter, ","),
                    Utils.list2String(mCustomerLevelFilter, ","), Utils.list2String(mCustomerTypeFilter, ","), Utils.list2String(mSerialNoFilter, ","));
        }
        dismiss();
        EventBus.getDefault().post(new ContactUmengStatisticsEvent(Constants.UMENG_STATISTICS_FILTER_BTN_CLICK));

    }


}