package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.model.AlbumInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-31.
 */
public class AlbumAdapter extends RecyclerView.Adapter{

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_ADD = TYPE_PHOTO + 1;

    private List<AlbumInfo> mAlbums;
    private Context mContext;

    public AlbumAdapter(Context context){
        mContext = context;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item_view, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AlbumViewHolder){
            AlbumViewHolder viewHolder = (AlbumViewHolder) holder;
            switch (getItemViewType(position)){
                case TYPE_PHOTO:
                    Glide.with(mContext).load(mAlbums.get(position).imageUrl).into(viewHolder.mImageView);
                    break;
                case TYPE_ADD:
                    viewHolder.mImageView.setImageResource(R.drawable.empty_album_button);
                    viewHolder.mImageView.setOnClickListener(v -> {});
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAlbums != null ? (mAlbums.size() + 1): 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mAlbums != null && position < mAlbums.size()) {
            return TYPE_PHOTO;
        } else {
            return TYPE_ADD;
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.item_image) ImageView mImageView;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
