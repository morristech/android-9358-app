package com.xmd.technician.clubinvite;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.widget.TelephoneDialogFragment;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.event.EventRequestSyncPermission;
import com.xmd.technician.BR;
import com.xmd.technician.R;
import com.xmd.technician.clubinvite.beans.ClubInvite;
import com.xmd.technician.widget.AlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class ClubInviteActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CommonRecyclerViewAdapter<ClubInvite> adapter;
    private List<ClubInvite> dataList = new ArrayList<>();
    private int page = 0;
    private int pageSize = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_invite);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommonRecyclerViewAdapter<>();
        adapter.setHandler(BR.handler, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, ScreenUtils.dpToPx(8), 0, ScreenUtils.dpToPx(8));
            }
        });

        setTitle("入职邀请");

        page = 1;
        pageSize = 10;
        loadData();
    }

    private void loadData() {
        Observable<BaseBean<List<ClubInvite>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listInvite(page, pageSize);
        if (page == 1) {
            showLoading();
        }
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<ClubInvite>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<ClubInvite>> result) {
                hideLoading();
                if (page == 1) {
                    dataList.clear();
                }
                dataList.addAll(result.getRespData());
                adapter.setData(R.layout.list_item_club_invite, BR.data, dataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                showToast("加载失败：" + e.getMessage());
            }
        });
    }

    public void onAccept(ClubInvite invite) {
        acceptOrRefuse(invite, "accept");
    }

    public void onRefuse(ClubInvite invite) {
        acceptOrRefuse(invite, "refuse");
    }

    private void acceptOrRefuse(ClubInvite invite, String operate) {
        new AlertDialogBuilder(this)
                .setMessage("确定" + (operate.equals("accept") ? "接受" : "拒绝") + "此职位吗?")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Observable<BaseBean<ClubInvite>> observable = XmdNetwork.getInstance()
                                .getService(NetService.class)
                                .acceptOrRefuseInvite(invite.getId(), operate);
                        showLoading();
                        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<ClubInvite>>() {
                            @Override
                            public void onCallbackSuccess(BaseBean<ClubInvite> result) {
                                hideLoading();
                                int i = dataList.indexOf(invite);
                                if (i >= 0) {
                                    dataList.set(i, result.getRespData());
                                    adapter.notifyItemChanged(i);
                                }
                                if (operate.equals("accept")) {
                                    EventBus.getDefault().post(new EventRequestSyncPermission());
                                }
                            }

                            @Override
                            public void onCallbackError(Throwable e) {
                                hideLoading();
                                showToast("操作失败：" + e.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void onCall(ClubInvite invite) {
        String[] telephones = invite.getClubTelephone().split(",|，");
        if (telephones.length > 0) {
            ArrayList<String> phoneList = new ArrayList<>();
            phoneList.addAll(Arrays.asList(telephones));
            TelephoneDialogFragment fragment = TelephoneDialogFragment.newInstance(phoneList);
            fragment.show(getSupportFragmentManager().beginTransaction(), TelephoneDialogFragment.class.getName());
        } else {
            showToast("会所没有设置电话!");
        }
    }
}
