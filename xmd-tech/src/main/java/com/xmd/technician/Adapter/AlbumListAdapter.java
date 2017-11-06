package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumListAdapter extends RecyclerView.Adapter {

    public interface OnItemManagerClickListener {
        void onAddAlbum();

        void onDeleteAlbum(int position);
    }

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_ADD = 1;
    public static final int ALBUM_STATUS_NORMAL = 0; //正常状态
    public static final int ALBUM_STATUS_EDIT = 1; //编辑状态

    private List<AlbumInfo> mAlbums;
    private Context mContext;
    private OnItemManagerClickListener mOnItemClickListener;
    private int mAlbumStatus; //

    public AlbumListAdapter(Context context, OnItemManagerClickListener itemClickListener) {
        mContext = context;
        mAlbums = new ArrayList<>();
        mAlbumStatus = ALBUM_STATUS_NORMAL;
        mOnItemClickListener = itemClickListener;
    }

    public void refreshDataSet(int albumStatus, List<AlbumInfo> albumInfo) {
        if (albumInfo != null) {
            mAlbums.clear();
            mAlbums.addAll(albumInfo);
            mAlbumStatus = albumStatus;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_item_view, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AlbumViewHolder) {
            AlbumViewHolder viewHolder = (AlbumViewHolder) holder;
            switch (getItemViewType(position)) {
                case TYPE_PHOTO:
                    Glide.with(mContext).load(mAlbums.get(position).imageUrl).error(R.drawable.img_default_square).into(viewHolder.mImageView);
                    viewHolder.mItemDelete.setVisibility(mAlbumStatus == ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
                    viewHolder.mItemDelete.setOnClickListener(v -> {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onDeleteAlbum(viewHolder.getAdapterPosition());
                        }
                    });
                    break;
                case TYPE_ADD:
                    viewHolder.mImageView.setImageResource(R.drawable.empty_album_button);
                    viewHolder.mItemDelete.setVisibility(View.GONE);
                    viewHolder.itemView.setOnClickListener(v -> {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onAddAlbum();
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mAlbumStatus == ALBUM_STATUS_NORMAL) {
            count = mAlbums.size() == 0 ? 1 : mAlbums.size();
        } else {
            count = mAlbums != null ? (mAlbums.size() < 8 ? (mAlbums.size() + 1) : mAlbums.size()) : 1;
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mAlbums != null && position < mAlbums.size()) {
            return TYPE_PHOTO;
        } else {
            return TYPE_ADD;
        }
    }

    public AlbumInfo getItem(int position) {
        if (mAlbums != null && mAlbums.size() > position) {
            return mAlbums.get(position);
        }
        return null;
    }

    public void onItemSwap(int fromPosition, int toPosition) {
        if (mAlbums != null && fromPosition < mAlbums.size() && toPosition < mAlbums.size()) {
            Collections.swap(mAlbums, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);

            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_IDS, (String) getAlbumIds());
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SORT_ALBUM, params);
        }
    }

    private CharSequence getAlbumIds() {
        CharSequence ids = "";
        if (mAlbums != null && mAlbums.size() > 0) {
            ids = mAlbums.get(0).id;
            for (int i = 1; i < mAlbums.size(); i++) {
                ids = TextUtils.concat(ids, ",", mAlbums.get(i).id);
            }
        }
        return ids;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_image)
        ImageView mImageView;
        @BindView(R.id.item_delete)
        ImageView mItemDelete;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
