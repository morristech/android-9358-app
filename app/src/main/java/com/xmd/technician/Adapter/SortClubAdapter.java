package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.xmd.technician.bean.CLubMember;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.Manager;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.CircleImageView;

import java.lang.reflect.Member;
import java.util.List;

/**
 * Created by Administrator on 2016/7/13.
 */
public class SortClubAdapter extends BaseAdapter implements SectionIndexer {
    private List<CLubMember> list = null;
    private Context mContext;
    private int mangerTotal;

    public SortClubAdapter(Context context, List<CLubMember> list,int total) {
        this.mContext = context;
        this.list = list;
        this.mangerTotal = total;
    }

    public void updateListView(List<CLubMember> list) {
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
        final CLubMember mMember = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.club_contact_list_item, null);
            viewHolder.tvPinyin = (TextView) convertView.findViewById(R.id.tv_pinyin);
            viewHolder.customerHead = (CircleImageView) convertView.findViewById(R.id.customer_head);
            viewHolder.customerName  = (TextView) convertView.findViewById(R.id.customer_name);
            viewHolder.lineChat = (LinearLayout) convertView.findViewById(R.id.line_chat);
            viewHolder.techNO = (TextView) convertView.findViewById(R.id.customer_serialNo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
       if(position ==0 &&mMember.userType.equals("manager")){
           viewHolder.tvPinyin.setVisibility(View.VISIBLE);
           viewHolder.tvPinyin.setText(ResourceUtils.getString(R.string.contact_manager));
       }else if(position<mangerTotal){
           viewHolder.tvPinyin.setVisibility(View.GONE);
       }else{
           if(position==mangerTotal){
               viewHolder.tvPinyin.setVisibility(View.VISIBLE);
               viewHolder.tvPinyin.setText(mMember.sortLetters);
           }else{
               int section = getSectionForPosition(position);
               if(position==getPositionForSection(section)){
                   viewHolder.tvPinyin.setVisibility(View.VISIBLE);
                   viewHolder.tvPinyin.setText(mMember.sortLetters);
               }else{
                   viewHolder.tvPinyin.setVisibility(View.GONE);
               }
           }

        }

        Glide.with(mContext).load(mMember.avatarUrl).into(viewHolder.customerHead);

        if(TextUtils.isEmpty(mMember.name)){
            viewHolder.customerName.setText("匿名");
        }else{
            viewHolder.customerName.setText(mMember.name);
        }
        if(Utils.isNotEmpty(mMember.serialNo)){
            viewHolder.lineChat.setVisibility(View.VISIBLE);
            viewHolder.techNO.setText(mMember.serialNo);
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
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if(firstChar == sectionIndex){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
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
        TextView techNO;

    }


}
