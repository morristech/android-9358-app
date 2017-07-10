package com.xmd.chat.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.chat.AccountManager;
import com.xmd.chat.BR;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.beans.Journal;
import com.xmd.chat.databinding.ActivityJournalShareBinding;
import com.xmd.chat.viewmodel.ShareJournalViewModel;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class JournalShareActivity extends BaseActivity {
    public ObservableBoolean loading = new ObservableBoolean();
    private Subscription subscription;

    private ActivityJournalShareBinding binding;
    private CommonRecyclerViewAdapter<ShareJournalViewModel> adapter;

    private List<ShareJournalViewModel> selectList = new ArrayList<>();
    public ObservableInt selectCount = new ObservableInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_share);

        adapter = new CommonRecyclerViewAdapter<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.setData(this);

        loadData();
    }

    private void loadData() {
        loading.set(true);
        Observable<BaseBean<List<Journal>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listClubJournal(AccountManager.getInstance().getUser().getClubId(), "0", "1000");
        subscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<Journal>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<Journal>> result) {
                subscription = null;
                loading.set(false);
                List<ShareJournalViewModel> dataList = new ArrayList<>();
                for (Journal journal : result.getRespData()) {
                    final ShareJournalViewModel data = new ShareJournalViewModel(journal);
                    data.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            data.select.set(!data.select.get());
                            if (data.select.get()) {
                                selectList.add(data);
                                selectCount.set(selectCount.get() + 1);
                            } else {
                                selectList.remove(data);
                                selectCount.set(selectCount.get() - 1);
                            }
                        }
                    });
                    dataList.add(data);
                }
                adapter.setData(R.layout.list_item_share_journal, BR.data, dataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCallbackError(Throwable e) {
                subscription = null;
                loading.set(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void onClick() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_CHAT_ID, getIntent().getStringExtra(Constants.EXTRA_CHAT_ID));
        intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) selectList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
