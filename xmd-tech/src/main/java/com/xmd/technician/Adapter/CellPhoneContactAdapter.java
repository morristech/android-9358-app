package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.PhoneContactor;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.ResourceUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CellPhoneContactAdapter extends RecyclerView.Adapter<CellPhoneContactAdapter.MyViewHolder> {
    private List<PhoneContactor> mData;
    private Context mContext;
    private AddClieckedListener mListener;
    private String mFirstLetter;
    private String mPreFirstLetter;
    private String telephone;
    private CharacterParser characterParser;

    public interface AddClieckedListener {
        void addToContactor(String name, String phone);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mTelephone;
        Button mBtnAdd;
        TextView mPinyin;

        public MyViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.contact_name);
            mTelephone = (TextView) itemView.findViewById(R.id.contact_phone);
            mPinyin = (TextView) itemView.findViewById(R.id.tv_pinyin);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_add);
        }
    }

    public CellPhoneContactAdapter(Context context, List<PhoneContactor> data, AddClieckedListener listener) {
        this.mData = data;
        this.mContext = context;
        this.mListener = listener;
        characterParser = CharacterParser.getInstance();
    }

    @Override
    public CellPhoneContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false)
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(CellPhoneContactAdapter.MyViewHolder holder, int position) {
        PhoneContactor contact = mData.get(position);

        if (!TextUtils.isEmpty(contact.name)) {
            if (position == 0) {
                mFirstLetter = characterParser.getSelling(contact.name).substring(0, 1);
                holder.mPinyin.setText(mFirstLetter.toUpperCase());
                holder.mPinyin.setVisibility(View.VISIBLE);
            } else {
                mFirstLetter = characterParser.getSelling(mData.get(position - 1).name).substring(0, 1);
                mPreFirstLetter = characterParser.getSelling(contact.name).substring(0, 1);
                if (mFirstLetter.equals(mPreFirstLetter)) {
                    holder.mPinyin.setVisibility(View.GONE);
                } else {
                    holder.mPinyin.setText(mPreFirstLetter.toUpperCase());
                    holder.mPinyin.setVisibility(View.VISIBLE);
                }
            }
            holder.mName.setText(ResourceUtils.getString(R.string.contact_name) + contact.name);
            holder.mTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + contact.telephone);
            holder.mBtnAdd.setOnClickListener(v -> mListener.addToContactor(contact.name, contact.telephone));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
