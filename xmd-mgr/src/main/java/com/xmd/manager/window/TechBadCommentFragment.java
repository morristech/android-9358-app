package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.xmd.manager.R;
import com.xmd.manager.beans.TechBadComment;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.TechBadCommentListResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/11/21.
 */
public class TechBadCommentFragment extends BaseListFragment<TechBadComment> {


    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.tech_bad_comment_progress)
    ProgressBar techBadCommentProgress;
    private View view;

    private Subscription getTechBadCommentListSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tech_bad_comment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        AllTechBadCommentActivity activity = (AllTechBadCommentActivity) getActivity();
        activity.getData();
    }

    @Override
    protected void initView() {
        getTechBadCommentListSubscription = RxBus.getInstance().toObservable(TechBadCommentListResult.class).subscribe(
                result -> handleTechListResult(result)
        );
    }

    private void handleTechListResult(TechBadCommentListResult result) {
        techBadCommentProgress.setVisibility(View.GONE);
        if (result.statusCode == 200) {
            onGetListSucceeded(0, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getTechBadCommentListSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
