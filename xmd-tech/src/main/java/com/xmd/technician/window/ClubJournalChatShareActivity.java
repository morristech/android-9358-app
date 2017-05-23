package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xmd.technician.Adapter.ChatShareAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.JournalListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-15.
 */

public class ClubJournalChatShareActivity extends BaseActivity implements View.OnClickListener, ChatShareAdapter.OnItemClickListener {


    @Bind(R.id.list)
    RecyclerView mListView;
    @Bind(R.id.view_emptyView)
    EmptyView viewEmptyView;

    private static List<ClubJournalBean> ClubJournalBeanList;
    private ChatShareAdapter adapter;
    private List<ClubJournalBean> mSelectedClubJournalBean;
    private TextView sentMessage;
    private ClubJournalBean info;
    protected LinearLayoutManager mLayoutManager;
    private Subscription mClubJournalListSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_journal_chat_share);
        ButterKnife.bind(this);
        sentMessage = (TextView) findViewById(R.id.toolbar_right_share);
        sentMessage.setVisibility(View.VISIBLE);
        sentMessage.setEnabled(false);
        sentMessage.setOnClickListener(this);
        initView();
    }

    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.club_journal_title));
        setBackVisible(true);
        mClubJournalListSubscription = RxBus.getInstance().toObservable(JournalListResult.class).subscribe(
                journalListResult -> handleJournalListResult(journalListResult)
        );
        ClubJournalBeanList = new ArrayList<>();
        mSelectedClubJournalBean = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatShareAdapter(ClubJournalChatShareActivity.this, ClubJournalBeanList);
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        viewEmptyView.setStatus(EmptyView.Status.Loading);
        getClubJournalData();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.toolbar_right_share) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(TechChatActivity.REQUEST_JOURNAL_TYPE, (ArrayList<? extends Parcelable>) mSelectedClubJournalBean);
            setResult(RESULT_OK, resultIntent);
        }
        this.finish();

    }


    private void handleJournalListResult(JournalListResult result) {

        if (result.statusCode == 200) {
            if (result.respData != null) {
                if(result.respData.size()==0){
                    viewEmptyView.setStatus(EmptyView.Status.Empty);
                    return;
                }
                viewEmptyView.setStatus(EmptyView.Status.Gone);
                ClubJournalBeanList.clear();
                for (ClubJournalBean info : result.respData) {
                    info.selectedStatus = 1;
                    ClubJournalBeanList.add(info);
                }
                if (ClubJournalBeanList != null) {
                    adapter.setData(ClubJournalBeanList);
                }

            }
        }else{
            viewEmptyView.setStatus(EmptyView.Status.Failed);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mClubJournalListSubscription);
    }


    private void getClubJournalData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(20));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_JOURNAL_LIST, params);
    }

    @Override
    public void onItemCheck(Object bean, int position, boolean isChecked) {
        info = (ClubJournalBean) bean;
        if (isChecked) {
            for (int i = 0; i < mSelectedClubJournalBean.size(); i++) {
                if (mSelectedClubJournalBean.get(i).journalId.equals(info.journalId)) {
                    mSelectedClubJournalBean.remove(mSelectedClubJournalBean.get(i));
                    break;
                }
            }
            info.selectedStatus = 1;
            ClubJournalBeanList.set(position, info);
        } else {
            info.selectedStatus = 2;
            ClubJournalBeanList.set(position, info);
            mSelectedClubJournalBean.add(info);
        }
        if (mSelectedClubJournalBean.size() > 0) {
            sentMessage.setEnabled(true);
            sentMessage.setText(String.format("分享(%s)", String.valueOf(mSelectedClubJournalBean.size())));
        } else {
            sentMessage.setEnabled(false);
            sentMessage.setText("分享");
        }
        adapter.notifyItemChanged(position);
    }
}
