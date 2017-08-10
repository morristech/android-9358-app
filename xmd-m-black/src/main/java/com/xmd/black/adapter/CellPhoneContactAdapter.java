package com.xmd.black.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.app.CharacterParser;
import com.xmd.black.R;
import com.xmd.black.bean.PhoneContact;

import java.util.List;

/**
 * Created by Lhj on 17-7-25.
 */

public class CellPhoneContactAdapter extends RecyclerView.Adapter<CellPhoneContactAdapter.ViewHolder> {

    private List<PhoneContact> mData;
    private Context mContext;
    private AddContactListener mListener;
    private String mFirstLetter;
    private String mPreFirstLetter;
    private CharacterParser mCharacterParser;

    public interface AddContactListener {
        void addToContact(String name, String phone);
    }

    public CellPhoneContactAdapter(Context context, List<PhoneContact> data, AddContactListener listener) {
        this.mData = data;
        this.mContext = context;
        this.mListener = listener;
        mCharacterParser = CharacterParser.getInstance();
    }

    public void setData(List<PhoneContact> data){
        this.mData = data;
        notifyDataSetChanged();
    }


    @Override
    public CellPhoneContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(mContext).inflate(R.layout.item_contact_layout, parent, false);
        return new ViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(CellPhoneContactAdapter.ViewHolder holder, int position) {
        final PhoneContact contact = mData.get(position);
        if (!TextUtils.isEmpty(contact.name)) {
            if (position == 0) {
                mFirstLetter = mCharacterParser.getSelling(contact.name).substring(0, 1);
                holder.mPinyin.setText(mFirstLetter.toUpperCase());
                holder.mPinyin.setVisibility(View.VISIBLE);
            } else {
                mFirstLetter = mCharacterParser.getSelling(mData.get(position - 1).name).substring(0, 1);
                mPreFirstLetter = mCharacterParser.getSelling(contact.name).substring(0, 1);
                if (mFirstLetter.equals(mPreFirstLetter)) {
                    holder.mPinyin.setVisibility(View.GONE);
                } else {
                    holder.mPinyin.setText(mPreFirstLetter.toUpperCase());
                    holder.mPinyin.setVisibility(View.VISIBLE);
                }
            }
            holder.mName.setText("姓名：" + contact.name);
            holder.mTelephone.setText("电话："+ contact.telephone);
            holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addToContact(contact.name,contact.telephone);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mTelephone;
        Button mBtnAdd;
        TextView mPinyin;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.contact_name);
            mTelephone = (TextView) itemView.findViewById(R.id.contact_phone);
            mPinyin = (TextView) itemView.findViewById(R.id.tv_pinyin);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
}
