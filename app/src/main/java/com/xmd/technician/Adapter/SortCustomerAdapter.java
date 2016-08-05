package com.xmd.technician.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/13.
 */
public class SortCustomerAdapter extends BaseAdapter implements SectionIndexer {
    private List<CustomerInfo> list = null;
    private Context mContext;
    public SortCustomerAdapter(Context context, List<CustomerInfo> list) {
        this.mContext = context;
        this.list = list;
    }
    public void updateListView(List<CustomerInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final CustomerInfo mCustomer = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_list_item, null);
            viewHolder.tvPinyin = (TextView) convertView.findViewById(R.id.tv_pinyin);
            viewHolder.customerHead = (CircleImageView) convertView.findViewById(R.id.customer_head);
            viewHolder.customerName  = (TextView) convertView.findViewById(R.id.customer_name);
            viewHolder.lineChat = (LinearLayout) convertView.findViewById(R.id.line_chat);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(list.size()==1){
            viewHolder.tvPinyin.setVisibility(View.VISIBLE);
            viewHolder.tvPinyin.setText(mCustomer.sortLetters);
        }else{
            int section = getSectionForPosition(position);
            if(position==getPositionForSection(section)){
                viewHolder.tvPinyin.setVisibility(View.VISIBLE);
                viewHolder.tvPinyin.setText(mCustomer.sortLetters);
            }else{
                if(position==0){
                    viewHolder.tvPinyin.setVisibility(View.VISIBLE);
                    viewHolder.tvPinyin.setText(mCustomer.sortLetters);
                }else {
                    viewHolder.tvPinyin.setVisibility(View.GONE);
                }

            }
        }
        Glide.with(mContext).load(mCustomer.avatarUrl).into(viewHolder.customerHead);

        if(TextUtils.isEmpty(mCustomer.userNoteName)){
            viewHolder.customerName.setText(mCustomer.userName);
        }else  if(Utils.isNotEmpty(mCustomer.userNoteName)){
            viewHolder.customerName.setText(mCustomer.userNoteName);
        }else{
            viewHolder.customerName.setText(ResourceUtils.getString(R.string.default_user_name));
        }
        if(Utils.isNotEmpty(mCustomer.userId)){
            viewHolder.lineChat.setVisibility(View.VISIBLE);
        }else{
            viewHolder.lineChat.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i <getCount() ; i++) {
            String sortStr = list.get(i).sortLetters;
            if(Utils.isNotEmpty(sortStr)){
                char firstChar = sortStr.toUpperCase().charAt(0);
                if(firstChar == sectionIndex){
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        if(position>0){
            return list.get(position).sortLetters.charAt(0);
        }else {
            return -1;
        }

    }

    private String getAlpha(String str){
        String sortStr = str.trim().substring(0,1).toUpperCase();
        if(sortStr.matches("[A-Z]")){
            return sortStr;
        }else{
            return "#";
        }
    }




    final static class ViewHolder {

        TextView tvPinyin;
        CircleImageView customerHead;
        TextView customerName;
        TextView bracketLeft;
        LinearLayout lineChat;

    }


}
