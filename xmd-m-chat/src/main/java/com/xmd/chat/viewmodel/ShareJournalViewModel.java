package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.beans.Journal;

/**
 * Created by mo on 17-7-10.
 * 电子期刊分享
 */

public class ShareJournalViewModel implements Parcelable {
    private Journal journal;
    public View.OnClickListener listener;
    public ObservableBoolean select = new ObservableBoolean();

    public ShareJournalViewModel(Journal journal) {
        this.journal = journal;
    }


    protected ShareJournalViewModel(Parcel in) {
        journal = in.readParcelable(Journal.class.getClassLoader());
    }

    public static final Creator<ShareJournalViewModel> CREATOR = new Creator<ShareJournalViewModel>() {
        @Override
        public ShareJournalViewModel createFromParcel(Parcel in) {
            return new ShareJournalViewModel(in);
        }

        @Override
        public ShareJournalViewModel[] newArray(int size) {
            return new ShareJournalViewModel[size];
        }
    };

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public Journal getJournal() {
        return journal;
    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(journal, flags);
    }
}
