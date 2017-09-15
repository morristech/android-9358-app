package com.xmd.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.RelativeDateFormatUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.contact.R;
import com.xmd.contact.bean.ContactAllBean;
import com.xmd.contact.bean.ContactRecentBean;
import com.xmd.contact.bean.ContactRegister;
import com.xmd.contact.bean.ManagerContactAllBean;
import com.xmd.contact.bean.ManagerContactRecentBean;
import com.xmd.contact.httprequest.ConstantResources;

import java.util.List;


/**
 * Created by Lhj on 17-7-1.
 */

public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callback<T> {
        void onItemClicked(T bean, String appType);

        void onPositiveButtonClicked(T bean, int position, boolean isThanks);

        boolean isPaged();
    }

    private static final int TYPE_TECH_CONTACT_ALL_ITEM = 1;
    private static final int TYPE_TECH_CONTACT_REGISTER_ITEM = 2;
    private static final int TYPE_TECH_CONTACT_VISITOR_ITEM = 3;
    private static final int TYPE_MANAGER_CLUB_CONTACT_ALL_ITEM = 4;
    private static final int TYPE_MANAGER_CLUB_CONTACT_VISITOR_ITEM = 5;
    private static final int TYPE_FOOTER = 99;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<T> mData;
    private Callback mCallback;
    private Context mContext;
    private boolean isManager;
    private String mType;


    public ListRecycleViewAdapter(Context context, List<T> data, Callback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
    }

    //type
    public void setContactData(List<T> data, boolean isManager) {
        mData = data;
        mIsEmpty = data.isEmpty();
        this.isManager = isManager;
        ThreadPoolManager.postToUI(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (mCallback.isPaged() && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (mData.get(position) instanceof ContactAllBean) {
            return TYPE_TECH_CONTACT_ALL_ITEM;
        } else if (mData.get(position) instanceof ContactRegister) {
            return TYPE_TECH_CONTACT_REGISTER_ITEM;
        } else if (mData.get(position) instanceof ContactRecentBean) {
            return TYPE_TECH_CONTACT_VISITOR_ITEM;
        } else if (mData.get(position) instanceof ManagerContactRecentBean) {
            return TYPE_MANAGER_CLUB_CONTACT_VISITOR_ITEM;
        } else if (mData.get(position) instanceof ManagerContactAllBean) {
            return TYPE_MANAGER_CLUB_CONTACT_ALL_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        if (mCallback.isPaged()) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TECH_CONTACT_ALL_ITEM:
                View contactAllView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_all_item, parent, false);
                return new ContactAllViewHolder(contactAllView);
            case TYPE_TECH_CONTACT_REGISTER_ITEM:
                View contactRegister = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_register_item, parent, false);
                return new ContactRegisterViewHolder(contactRegister);
            case TYPE_TECH_CONTACT_VISITOR_ITEM:
                View contactVisitView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_visit_item, parent, false);
                return new ContactVisitorListItemViewHolder(contactVisitView);
            case TYPE_MANAGER_CLUB_CONTACT_ALL_ITEM:
                View clubContactAllView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_club_contact_all_item, parent, false);
                return new ClubContactAllViewHolder(clubContactAllView);
            case TYPE_MANAGER_CLUB_CONTACT_VISITOR_ITEM:
                View clubVisitView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_club_contact_visit_item, parent, false);
                return new ClubContactVisitorListItemViewHolder(clubVisitView);
            case TYPE_FOOTER:
                View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_footer_view_item, parent, false);
                return new FooterViewHolder(footerView);
            default:
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_footer_view_item, parent, false);
                return new FooterViewHolder(viewDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactAllViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ContactAllBean)) {
                return;
            }
            final ContactAllBean contactBean = (ContactAllBean) obj;
            if (TextUtils.isEmpty(contactBean.createTime)) {
                return;
            }
            ContactAllViewHolder viewHolder = (ContactAllViewHolder) holder;
            viewHolder.contactName.setText(TextUtils.isEmpty(contactBean.userNoteName) ? contactBean.name : contactBean.userNoteName);
            Glide.with(mContext).load(contactBean.avatarUrl).into(viewHolder.contactAvatar);
            if (contactBean.isService) {
                viewHolder.contactServiceTime.setVisibility(View.VISIBLE);
                if (position == 0) {
                    viewHolder.contactServiceTime.setVisibility(View.VISIBLE);
                    if (contactBean.createTime.substring(0, 10).equals(DateUtils.getCurrentDate())) {
                        viewHolder.contactServiceTime.setText("今天新增");
                    } else if (contactBean.createTime.substring(0, 10).equals(DateUtils.getYestData())) {
                        viewHolder.contactServiceTime.setText("昨天新增");
                    } else {
                        viewHolder.contactServiceTime.setText(String.format("%s新增", contactBean.createTime.substring(5, 10)));
                    }
                } else {
                    if (Utils.StrSubstring(10, contactBean.createTime, false).equals(Utils.StrSubstring(10, ((ContactAllBean) mData.get(position - 1)).createTime, false))) {
                        viewHolder.contactServiceTime.setVisibility(View.GONE);
                    } else {
                        viewHolder.contactServiceTime.setVisibility(View.VISIBLE);
                        if (contactBean.createTime.substring(0, 10).equals(DateUtils.getYestData())) {
                            viewHolder.contactServiceTime.setText("昨天新增");
                        } else {
                            viewHolder.contactServiceTime.setText(String.format("%s新增", contactBean.createTime.substring(5, 10)));
                        }
                    }
                }
            } else {
                viewHolder.contactServiceTime.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(contactBean.remark)) {
                viewHolder.llContactTypeView.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llContactTypeView.setVisibility(View.VISIBLE);
                initTypeLabelView(viewHolder.llContactTypeView, Utils.StringToList(contactBean.remark, ","));
            }
//            if (TextUtils.isEmpty(contactBean.tagName)) {
//                viewHolder.llContactTypeView.setVisibility(View.INVISIBLE);
//            } else {
//                viewHolder.llContactTypeView.setVisibility(View.VISIBLE);
//                initTypeLabelView(viewHolder.llContactTypeView, Utils.StringToList(contactBean.tagName, ","));
//            }


            if (contactBean.customerType.equals(ConstantResources.USER_FANS)) {
                viewHolder.ivContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_WX)) {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_FANS_WX)) {
                viewHolder.ivContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_ZFB)) {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(contactBean, "tech");
                }
            });
            return;
        }

        if (holder instanceof ContactRegisterViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ContactRegister)) {
                return;
            }
            final ContactRegister contactBean = (ContactRegister) obj;


            ContactRegisterViewHolder viewHolder = (ContactRegisterViewHolder) holder;
            viewHolder.contactName.setText(TextUtils.isEmpty(contactBean.userNoteName) ? contactBean.name : contactBean.userNoteName);
            Glide.with(mContext).load(contactBean.avatarUrl).into(viewHolder.contactAvatar);
            if (mData.size() > 1) {
                if (position == 0 && !contactBean.clubId.equals(((ContactRegister) mData.get(1)).id)) {
                    viewHolder.contactServiceClub.setVisibility(View.GONE);
                } else {
                    final ContactRegister lastContactBean = (ContactRegister) mData.get(position - 1);
                    if (lastContactBean.clubId.equals(contactBean.clubId)) {
                        viewHolder.contactServiceClub.setVisibility(View.GONE);
                    } else {
                        viewHolder.contactServiceClub.setVisibility(View.VISIBLE);
                        viewHolder.contactServiceClub.setText(contactBean.clubName);
                    }

                }
            } else {
                viewHolder.contactServiceClub.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(contactBean.remark)) {
                viewHolder.llContactTypeView.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llContactTypeView.setVisibility(View.VISIBLE);
                initTypeLabelView(viewHolder.llContactTypeView, Utils.StringToList(contactBean.remark, ","));
            }
            if (contactBean.customerType.equals(ConstantResources.USER_FANS)) {
                viewHolder.ivContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_WX)) {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_FANS_WX)) {
                viewHolder.ivContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_ZFB)) {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivContactTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactTypeZFB.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(contactBean, "tech");
                }
            });
            return;
        }

        if (holder instanceof ContactVisitorListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ContactRecentBean)) {
                return;
            }
            final ContactRecentBean userRecent = (ContactRecentBean) obj;
            userRecent.intListPosition = position;
            ContactVisitorListItemViewHolder viewHolder = (ContactVisitorListItemViewHolder) holder;

            if (!TextUtils.isEmpty(userRecent.userId) && !userRecent.userId.equals("-1")) {
                if (TextUtils.isEmpty(userRecent.userNoteName) && TextUtils.isEmpty(userRecent.name)) {
                    viewHolder.contactRecentName.setText("游客");
                    viewHolder.llContactVisitorToChat.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.contactRecentName.setText(TextUtils.isEmpty(userRecent.userNoteName) ? userRecent.name : userRecent.userNoteName);
                    viewHolder.llContactVisitorToChat.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.contactRecentName.setText("游客");
                viewHolder.llContactVisitorToChat.setVisibility(View.INVISIBLE);
            }


            Glide.with(mContext).load(userRecent.avatarUrl).into(viewHolder.contactRecentAvatar);
            viewHolder.contactRecentRemark.setText(TextUtils.isEmpty(userRecent.remark) ? "访问了我" : userRecent.remark);

            switch (userRecent.visitType) {
                case ConstantResources.CONTACT_RECENT_TYPE_NORMAL:
                    Glide.with(mContext).load(R.drawable.icon_recent_visit).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_COMMENT:
                    Glide.with(mContext).load(R.drawable.icon_recent_comments).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_COLLECTION:
                    Glide.with(mContext).load(R.drawable.icon_recent_collect).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_COUPON:
                    Glide.with(mContext).load(R.drawable.icon_recent_ticket).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_PAID_COUPON:
                    Glide.with(mContext).load(R.drawable.icon_recent_ticket).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_REWARD:
                    Glide.with(mContext).load(R.drawable.icon_recent_reward).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_VISIT_CLUB:
                    Glide.with(mContext).load(R.drawable.icon_recent_visit).into(viewHolder.ivContactRecentVisitType);
                    break;
                case ConstantResources.CONTACT_RECENT_TYPE_CONSUME:
                    Glide.with(mContext).load(R.drawable.icon_recent_club).into(viewHolder.ivContactRecentVisitType);
                    break;
                default:
                    Glide.with(mContext).load(R.drawable.icon_recent_visit).into(viewHolder.ivContactRecentVisitType);
            }
            if (userRecent.status == 0) {//打招呼
                if (!TextUtils.isEmpty(userRecent.canSayHello) && userRecent.canSayHello.equals("Y")) {
                    viewHolder.visitorToChat.setText("打招呼");
                    viewHolder.visitorToChat.setEnabled(true);
                } else {
                    viewHolder.visitorToChat.setText("已招呼");
                    viewHolder.visitorToChat.setEnabled(false);
                }
            } else {
                viewHolder.visitorToChat.setEnabled(true);
                viewHolder.visitorToChat.setText("感谢");
            }

            if (TextUtils.isEmpty(userRecent.tagName)) {
                viewHolder.llRecentVisitTagType.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llRecentVisitTagType.setVisibility(View.VISIBLE);
                initTypeLabelView(viewHolder.llRecentVisitTagType, Utils.StringToList(userRecent.tagName, ","));
            }
            if (userRecent.distance > 0) {
                if (userRecent.distance > 1000) {
                    viewHolder.tvRecentDistance.setText(String.format("%1.1fkm", userRecent.distance / 1000f));
                } else {
                    viewHolder.tvRecentDistance.setText(userRecent.distance + "m");
                }
            } else {
                viewHolder.tvRecentDistance.setText("--");
            }


            if (userRecent.customerType.equals(ConstantResources.USER_FANS)) {
                viewHolder.ivContactRecentTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactRecentTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeZFB.setVisibility(View.GONE);
            } else if (userRecent.customerType.equals(ConstantResources.USER_WX)) {
                viewHolder.ivContactRecentTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactRecentTypeZFB.setVisibility(View.GONE);
            } else if (userRecent.customerType.equals(ConstantResources.USER_FANS_WX)) {
                viewHolder.ivContactRecentTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactRecentTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactRecentTypeZFB.setVisibility(View.GONE);
            } else if (userRecent.customerType.equals(ConstantResources.USER_ZFB)) {
                viewHolder.ivContactRecentTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeZFB.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivContactRecentTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactRecentTypeZFB.setVisibility(View.GONE);
            }
            viewHolder.contactVisitorTime.setText(RelativeDateFormatUtils.getTimestampString(RelativeDateFormatUtils.StringToDate(userRecent.createTime, "yyyy-MM-dd HH:mm:ss")));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(userRecent, "tech");
                }
            });
            viewHolder.visitorToChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPositiveButtonClicked(userRecent, position, userRecent.status==1);
                }
            });
        }
        if (holder instanceof ClubContactAllViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ManagerContactAllBean)) {
                return;
            }
            final ManagerContactAllBean contactBean = (ManagerContactAllBean) obj;
            ClubContactAllViewHolder viewHolder = (ClubContactAllViewHolder) holder;
            viewHolder.contactName.setText(TextUtils.isEmpty(contactBean.userNoteName) ? contactBean.name : contactBean.userNoteName);
            Glide.with(mContext).load(contactBean.avatarUrl).into(viewHolder.contactAvatar);
            if (position == 0) {
                viewHolder.contactServiceTime.setVisibility(View.VISIBLE);
                if (contactBean.createTime.substring(0, 10).equals(DateUtils.getCurrentDate())) {
                    viewHolder.contactServiceTime.setText("今天新增");
                } else if (contactBean.createTime.substring(0, 10).equals(DateUtils.getYestData())) {
                    viewHolder.contactServiceTime.setText("昨天新增");
                } else {
                    viewHolder.contactServiceTime.setText(String.format("%s新增", contactBean.createTime.substring(5, 10)));
                }
            } else {
                if (Utils.StrSubstring(10, contactBean.createTime, false).equals(Utils.StrSubstring(10, ((ManagerContactAllBean) mData.get(position - 1)).createTime, false))) {
                    viewHolder.contactServiceTime.setVisibility(View.GONE);
                } else {
                    viewHolder.contactServiceTime.setVisibility(View.VISIBLE);
                    if (contactBean.createTime.substring(0, 10).equals(DateUtils.getYestData())) {
                        viewHolder.contactServiceTime.setText("昨天新增");
                    } else {
                        viewHolder.contactServiceTime.setText(String.format("%s新增", contactBean.createTime.substring(5, 10)));
                    }
                }
            }
            if (TextUtils.isEmpty(contactBean.tagName)) {
                viewHolder.llContactTypeView.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llContactTypeView.setVisibility(View.VISIBLE);
                initTypeLabelView(viewHolder.llContactTypeView, Utils.StringToList(contactBean.tagName, ","));
            }
            if (contactBean.customerType.equals(ConstantResources.USER_FANS)) {
                viewHolder.imgContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.imgContactTypeWx.setVisibility(View.GONE);
                viewHolder.imgContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_WX)) {
                viewHolder.imgContactTypeFans.setVisibility(View.GONE);
                viewHolder.imgContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.imgContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_FANS_WX)) {
                viewHolder.imgContactTypeFans.setVisibility(View.VISIBLE);
                viewHolder.imgContactTypeWx.setVisibility(View.VISIBLE);
                viewHolder.imgContactTypeZFB.setVisibility(View.GONE);
            } else if (contactBean.customerType.equals(ConstantResources.USER_ZFB)) {
                viewHolder.imgContactTypeFans.setVisibility(View.GONE);
                viewHolder.imgContactTypeWx.setVisibility(View.GONE);
                viewHolder.imgContactTypeZFB.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgContactTypeFans.setVisibility(View.GONE);
                viewHolder.imgContactTypeWx.setVisibility(View.GONE);
                viewHolder.imgContactTypeZFB.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(contactBean, "manager");
                }
            });
            return;
        }
        if (holder instanceof ClubContactVisitorListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ManagerContactRecentBean)) {
                return;
            }
            final ManagerContactRecentBean managerRecent = (ManagerContactRecentBean) obj;
            ClubContactVisitorListItemViewHolder viewHolder = (ClubContactVisitorListItemViewHolder) holder;
            Glide.with(mContext).load(managerRecent.avatarUrl).into(viewHolder.contactVisitAvatar);
            if ((!TextUtils.isEmpty(managerRecent.userId) && !managerRecent.userId.equals("-1"))) {
                if (TextUtils.isEmpty(managerRecent.userNoteName) && TextUtils.isEmpty(managerRecent.name)) {
                    viewHolder.contactVisitName.setText(TextUtils.isEmpty(managerRecent.emchatId) ? "游客" : "匿名用户");
                } else {
                    viewHolder.contactVisitName.setText(TextUtils.isEmpty(managerRecent.userNoteName) ? managerRecent.name : managerRecent.userNoteName);
                }

            } else {
                viewHolder.contactVisitName.setText("游客");
                viewHolder.llContactVisitChat.setVisibility(View.INVISIBLE);
            }
            if (managerRecent.customerType.equals(ConstantResources.USER_WX)) {
                viewHolder.ivContactVisitTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactVisitTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeZf.setVisibility(View.GONE);
            } else if (managerRecent.customerType.equals(ConstantResources.USER_FANS)) {
                viewHolder.ivContactVisitTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactVisitTypeZf.setVisibility(View.GONE);
            } else if (managerRecent.customerType.equals(ConstantResources.USER_FANS_WX)) {
                viewHolder.ivContactVisitTypeWx.setVisibility(View.VISIBLE);
                viewHolder.ivContactVisitTypeFans.setVisibility(View.VISIBLE);
                viewHolder.ivContactVisitTypeZf.setVisibility(View.GONE);
            } else if (managerRecent.customerType.equals(ConstantResources.USER_ZFB)) {
                viewHolder.ivContactVisitTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeZf.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivContactVisitTypeWx.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeFans.setVisibility(View.GONE);
                viewHolder.ivContactVisitTypeZf.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(managerRecent.tagName)) {
                viewHolder.llContactTagType.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llContactTagType.setVisibility(View.VISIBLE);
                initTypeLabelView(viewHolder.llContactTagType, Utils.StringToList(managerRecent.tagName, ","));
            }
            if (managerRecent.visitType == 10) {
                viewHolder.ivContactVisitType.setImageResource(R.drawable.icon_recent_visit);
            } else {
                viewHolder.ivContactVisitType.setImageResource(R.drawable.icon_recent_club);
            }
            viewHolder.contactVisitRemark.setText(managerRecent.remark);
            //    viewHolder.contactVisitTime.setText(RelativeDateFormatUtils.getTimestampString(RelativeDateFormatUtils.StringToDate(managerRecent.createTime, "yyyy-MM-dd HH:mm:ss")));
            if (TextUtils.isEmpty(managerRecent.createTime)) {
                viewHolder.contactVisitTime.setText("");
            } else {
                viewHolder.contactVisitTime.setText(managerRecent.createTime.substring(0, managerRecent.createTime.length() - 3));
            }
            if (managerRecent.distance > 0) {
                if (managerRecent.distance > 1000) {
                    viewHolder.contactVisitDistance.setText(String.format("%1.1fkm", managerRecent.distance / 1000f));
                } else {
                    viewHolder.contactVisitDistance.setText(managerRecent.distance + "m");
                }
            } else {
                viewHolder.contactVisitDistance.setText("");
            }
            if (TextUtils.isEmpty(managerRecent.emchatId)) {
                viewHolder.llContactVisitChat.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.llContactVisitChat.setVisibility(View.VISIBLE);
            }
            viewHolder.contactVisitChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPositiveButtonClicked(managerRecent, position, true);
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(managerRecent, ConstantResources.APP_TYPE_MANAGER);
                }
            });


        }
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            if (mIsEmpty) {
                viewHolder.textView.setText("----------还没有数据哦----------");
            } else if (mIsNoMore) {
                viewHolder.textView.setText(ResourceUtils.getString(R.string.all_data_load_finish));
            } else {
                viewHolder.textView.setText("----------上拉加载更多----------");
            }
            return;
        }
    }

    private void initTypeLabelView(LinearLayout customerTypeLabel, List<String> userTagList) {
        if (userTagList == null || userTagList.size() == 0) {
            return;
        }
        customerTypeLabel.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        for (String bean : userTagList) {
            TextView tv = new TextView(mContext);
            tv.setText(bean);
            tv.setTextSize(14);
            if (bean.equals("通讯录")) {
                tv.setTextColor(Color.parseColor("#4d98df"));
                tv.setBackgroundResource(com.xmd.m.R.drawable.bg_contact_tech_add_mark);
            } else {
                tv.setTextColor(Color.parseColor("#ff8909"));
                tv.setBackgroundResource(com.xmd.m.R.drawable.bg_contact_mark);
            }
            tv.setPadding(14, 0, 14, 0);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            customerTypeLabel.addView(tv);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.contact_footer_textView);
        }
    }

    static class ContactAllViewHolder extends RecyclerView.ViewHolder {
        RoundImageView contactAvatar;
        TextView contactName;
        ImageView ivContactTypeFans;
        ImageView ivContactTypeWx;
        ImageView ivContactTypeZFB;
        LinearLayout llContactTypeView;
        TextView contactServiceTime;

        public ContactAllViewHolder(View contactAllView) {
            super(contactAllView);
            contactAvatar = (RoundImageView) contactAllView.findViewById(R.id.contact_all_avatar);
            contactName = (TextView) contactAllView.findViewById(R.id.contact_name);
            ivContactTypeFans = (ImageView) contactAllView.findViewById(R.id.iv_contact_type_fans);
            ivContactTypeWx = (ImageView) contactAllView.findViewById(R.id.iv_contact_type_wx);
            ivContactTypeZFB = (ImageView) contactAllView.findViewById(R.id.iv_contact_type_zfb);
            llContactTypeView = (LinearLayout) contactAllView.findViewById(R.id.ll_customer_type_view);
            contactServiceTime = (TextView) contactAllView.findViewById(R.id.contact_service_time);
        }
    }

    //ContactRegisterViewHolder
    static class ContactRegisterViewHolder extends RecyclerView.ViewHolder {
        RoundImageView contactAvatar;
        TextView contactName;
        ImageView ivContactTypeFans;
        ImageView ivContactTypeWx;
        ImageView ivContactTypeZFB;
        LinearLayout llContactTypeView;
        TextView contactServiceClub;

        public ContactRegisterViewHolder(View contactAllView) {
            super(contactAllView);
            contactAvatar = (RoundImageView) contactAllView.findViewById(R.id.img_contact_register_avatar);
            contactName = (TextView) contactAllView.findViewById(R.id.tv_contact_register_name);
            ivContactTypeFans = (ImageView) contactAllView.findViewById(R.id.img_contact_register_type_fans);
            ivContactTypeWx = (ImageView) contactAllView.findViewById(R.id.img_contact_register_type_wx);
            ivContactTypeZFB = (ImageView) contactAllView.findViewById(R.id.img_contact_register_type_zfb);
            llContactTypeView = (LinearLayout) contactAllView.findViewById(R.id.ll_customer_register_type_view);
            contactServiceClub = (TextView) contactAllView.findViewById(R.id.tv_contact_register_service_club);
        }
    }

    static class ContactVisitorListItemViewHolder extends RecyclerView.ViewHolder {

        RoundImageView contactRecentAvatar;
        TextView contactRecentName;
        ImageView ivContactRecentTypeFans;
        ImageView ivContactRecentTypeWx;
        ImageView ivContactRecentTypeZFB;
        ImageView ivContactRecentVisitType;
        TextView contactRecentRemark;
        TextView visitorToChat;
        TextView contactVisitorTime;
        LinearLayout llContactVisitorToChat;
        LinearLayout llRecentVisitTagType;
        TextView tvRecentDistance;

        public ContactVisitorListItemViewHolder(View contactVisitView) {
            super(contactVisitView);
            contactRecentAvatar = (RoundImageView) contactVisitView.findViewById(R.id.contact_recent_avatar);
            contactRecentName = (TextView) contactVisitView.findViewById(R.id.contact_recent_name);
            ivContactRecentTypeFans = (ImageView) contactVisitView.findViewById(R.id.iv_contact_recent_type_fans);
            ivContactRecentTypeWx = (ImageView) contactVisitView.findViewById(R.id.iv_contact_recent_type_wx);
            ivContactRecentTypeZFB = (ImageView) contactVisitView.findViewById(R.id.iv_contact_recent_type_zfb);
            ivContactRecentVisitType = (ImageView) contactVisitView.findViewById(R.id.iv_contact_recent_visit_type);
            contactRecentRemark = (TextView) contactVisitView.findViewById(R.id.contact_recent_remark);
            visitorToChat = (TextView) contactVisitView.findViewById(R.id.contact_visitor_to_chat);
            contactVisitorTime = (TextView) contactVisitView.findViewById(R.id.contact_recent_time);
            llContactVisitorToChat = (LinearLayout) contactVisitView.findViewById(R.id.ll_contact_visitor_to_chat);
            llRecentVisitTagType = (LinearLayout) contactVisitView.findViewById(R.id.ll_recent_visit_tag_type);
            tvRecentDistance = (TextView) contactVisitView.findViewById(R.id.contact_recent_distance);
        }
    }

    static class ClubContactAllViewHolder extends RecyclerView.ViewHolder {
        RoundImageView contactAvatar;
        TextView contactName;
        ImageView imgContactTypeFans;
        ImageView imgContactTypeWx;
        ImageView imgContactTypeZFB;
        LinearLayout llContactTypeView;
        TextView contactServiceTime;

        public ClubContactAllViewHolder(View contactAllView) {
            super(contactAllView);
            contactAvatar = (RoundImageView) contactAllView.findViewById(R.id.img_contact_all_avatar);
            contactName = (TextView) contactAllView.findViewById(R.id.tv_contact_name);
            imgContactTypeFans = (ImageView) contactAllView.findViewById(R.id.img_contact_type_fans);
            imgContactTypeWx = (ImageView) contactAllView.findViewById(R.id.img_contact_type_wx);
            imgContactTypeZFB = (ImageView) contactAllView.findViewById(R.id.img_contact_type_zfb);
            llContactTypeView = (LinearLayout) contactAllView.findViewById(R.id.ll_customer_type_view);
            contactServiceTime = (TextView) contactAllView.findViewById(R.id.tv_contact_service_time);
        }
    }

    static class ClubContactVisitorListItemViewHolder extends RecyclerView.ViewHolder {

        RoundImageView contactVisitAvatar;
        TextView contactVisitName;
        ImageView ivContactVisitTypeFans;
        ImageView ivContactVisitTypeWx;
        ImageView ivContactVisitTypeZf;
        LinearLayout llContactTagType;
        ImageView ivContactVisitType;
        TextView contactVisitRemark;
        TextView contactVisitTime;
        TextView contactVisitChat;
        LinearLayout llContactVisitChat;
        TextView contactVisitDistance;

        public ClubContactVisitorListItemViewHolder(View itemView) {
            super(itemView);
            contactVisitAvatar = (RoundImageView) itemView.findViewById(R.id.contact_visit_avatar);
            contactVisitName = (TextView) itemView.findViewById(R.id.contact_visit_name);
            ivContactVisitTypeFans = (ImageView) itemView.findViewById(R.id.iv_contact_visit_type_fans);
            ivContactVisitTypeWx = (ImageView) itemView.findViewById(R.id.iv_contact_visit_type_wx);
            ivContactVisitTypeZf = (ImageView) itemView.findViewById(R.id.iv_contact_visit_type_zf);
            llContactTagType = (LinearLayout) itemView.findViewById(R.id.ll_customer_type_view);
            ivContactVisitType = (ImageView) itemView.findViewById(R.id.iv_contact_visit_type);
            contactVisitRemark = (TextView) itemView.findViewById(R.id.contact_visit_remark);
            contactVisitTime = (TextView) itemView.findViewById(R.id.contact_visit_time);
            contactVisitChat = (TextView) itemView.findViewById(R.id.contact_visit_chat);
            llContactVisitChat = (LinearLayout) itemView.findViewById(R.id.ll_contact_visit_chat);
            contactVisitDistance = (TextView) itemView.findViewById(R.id.contact_visit_distance);
        }
    }
}
