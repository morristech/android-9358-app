package com.xmd.app.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.BR;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.app.R;

import java.util.ArrayList;

/**
 * Created by mo on 17-8-11.
 * 选择并电话界面
 */

public class TelephoneDialogFragment extends DialogFragment {
    private String telephone;

    public static TelephoneDialogFragment newInstance(ArrayList<String> telephoneList) {
        TelephoneDialogFragment fragment = new TelephoneDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.EXTRA_DATA, telephoneList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.telephone_dialog_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        CommonRecyclerViewAdapter<String> adapter = new CommonRecyclerViewAdapter<>();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setHandler(BR.handler, this);
        adapter.setData(R.layout.list_item_telephone, BR.data, getArguments().getStringArrayList(Constants.EXTRA_DATA));
        return rootView;
    }

    public void onCall(String telephone) {
        this.telephone = telephone;
        PermissionTool.requestPermission(this,
                new String[]{Manifest.permission.CALL_PHONE},
                new String[]{"拨打电话"},
                1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri call = Uri.parse("tel:" + telephone);
            intent.setData(call);
            startActivity(intent);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp=getDialog().getWindow().getAttributes();
        lp.width= ScreenUtils.getScreenWidth();
        getDialog().getWindow().setAttributes(lp);
    }
}
