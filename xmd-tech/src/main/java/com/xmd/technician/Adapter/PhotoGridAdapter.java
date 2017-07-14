package com.xmd.technician.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.AlbumInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-4-1.
 */
public class PhotoGridAdapter extends BaseAdapter{
    public static final int TYPE_PHOTO = 0;
    public static final int TYPE_ADD = TYPE_PHOTO + 1;
    private static final int TYPE_COUNT = TYPE_ADD + 1;

    // photos
    private List<AlbumInfo> mAlbums;
    private Activity mActivity;

    public PhotoGridAdapter(Activity activity){
        mActivity = activity;
        mAlbums = new ArrayList<>();
    }

    public void refreshDataSet(List<AlbumInfo> albumInfos){
        if(albumInfos != null){
            mAlbums.clear();
            mAlbums.addAll(albumInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (mAlbums != null && position < mAlbums.size()) {
            return TYPE_PHOTO;
        } else {
            return TYPE_ADD;
        }
    }

    @Override
    public int getCount() {
        return mAlbums != null ? (mAlbums.size() + 1): 1;
    }

    @Override
    public AlbumInfo getItem(int position) {
        if(mAlbums != null && mAlbums.size() > position){
            return mAlbums.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item_view, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (getItemViewType(position)) {
            case TYPE_PHOTO:
                Glide.with(mActivity).load(mAlbums.get(position).imageUrl).into(holder.mImageView);
                break;
            case TYPE_ADD:
                holder.mImageView.setImageResource(R.drawable.empty_album_button);
            default:
                break;
        }
        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.item_image) ImageView mImageView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
