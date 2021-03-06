package com.xmd.technician.window;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.Adapter.TechPosterListAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.PosterBean;
import com.xmd.technician.common.DateUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.DeleteTechPosterResult;
import com.xmd.technician.http.gson.TechPosterListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.TechPosterDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-6-20.
 */

public class TechPersonalPosterActivity extends BaseActivity implements TechPosterListAdapter.PosterCallBack, TechPosterDialog.PosterShareOrSaveListener {

    private static final long ONE_MONTH_DAY_MILLISECOND = 30 * 24 * 60 * 60 * 1000l;
    @BindView(R.id.ll_tech_poster_empty_view)
    LinearLayout llTechPosterEmptyView;
    @BindView(R.id.tech_poster_recycler_view)
    RecyclerView techPosterRecyclerView;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    private List<PosterBean> mPosterBeanList;
    private TechPosterListAdapter mPosterListAdapter;
    private Subscription mPosterListSubscription;
    private Subscription mPosterDeleterSubscription;
    private PosterBean mPosterBean;
    private TechPosterDialog mDialog;
    private String mQrCodeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_personal_poster);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackVisible(true);
        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setText(ResourceUtils.getString(R.string.tech_poster_new_add));
        mPosterBeanList = new ArrayList<>();
        mPosterListAdapter = new TechPosterListAdapter(TechPersonalPosterActivity.this, mPosterBeanList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        techPosterRecyclerView.setLayoutManager(layoutManager);
        techPosterRecyclerView.setAdapter(mPosterListAdapter);
        mPosterListAdapter.setPosterCallBackListener(this);
        mPosterListSubscription = RxBus.getInstance().toObservable(TechPosterListResult.class).subscribe(
                result -> handlerTechPosterListResult(result)
        );
        mPosterDeleterSubscription = RxBus.getInstance().toObservable(DeleteTechPosterResult.class).subscribe(
                result -> handlerTechPosterDeleterResult(result)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
    }


    private void handlerTechPosterListResult(TechPosterListResult result) {
        if (result.statusCode == 200) {
            if (null == result.respData || result.respData.list.size() == 0) {
                llTechPosterEmptyView.setVisibility(View.VISIBLE);
                return;
            } else {
                llTechPosterEmptyView.setVisibility(View.GONE);
            }

            mPosterBeanList.clear();
            mQrCodeUrl = result.respData.qrCodeUrl;
            for (PosterBean bean : result.respData.list) {
                bean.qrCodeUrl = result.respData.qrCodeUrl;
                mPosterBeanList.add(bean);
            }
            mPosterListAdapter.setData(mPosterBeanList);
        }
    }

    private void handlerTechPosterDeleterResult(DeleteTechPosterResult result) {
        if (result.statusCode == 200) {
            XToast.show(ResourceUtils.getString(R.string.tech_poster_deleter_success));
        } else {
            XToast.show(ResourceUtils.getString(R.string.tech_poster_deleter_failed));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
    }

    @OnClick({R.id.toolbar_right, R.id.btn_create_poster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right:
                createOrEditTechPoster(true);
                break;
            case R.id.btn_create_poster:
                createOrEditTechPoster(true);
                break;
        }
    }

    private void createOrEditTechPoster(boolean isCreate) {
        Intent createPosterIntent = new Intent(this, EditTechPosterActivity.class);
        if (isCreate) {
            mPosterBean = new PosterBean();
        }
        createPosterIntent.putExtra(Constant.KEY_CURRENT_POSTER, mPosterBean);
        createPosterIntent.putExtra(Constant.KEY_QR_CODE_URL, Utils.isNotEmpty(mQrCodeUrl) ? mQrCodeUrl : "");
        startActivity(createPosterIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mPosterListSubscription, mPosterDeleterSubscription);
    }

    @Override
    public void itemClicked(PosterBean bean) {
        mPosterBean = bean;
        showShareWindow(bean);
    }

    private void showShareWindow(PosterBean bean) {

        int mCurrentModel = Constant.TECH_POSTER_FLOWER_MODEL;
        switch (bean.style) {
            case Constant.TECH_POSTER_TYPE_SQUARE:
                mCurrentModel = Constant.TECH_POSTER_SQUARE_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_CIRCULAR:
                mCurrentModel = Constant.TECH_POSTER_CIRCULAR_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_FLOWER:
                mCurrentModel = Constant.TECH_POSTER_FLOWER_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_BLUE:
                mCurrentModel = Constant.TECH_POSTER_BLUE_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_EARNEST:
                mCurrentModel = Constant.TECH_POSTER_EARNEST_MODEL;
                break;
        }
        mDialog = new TechPosterDialog(this, mCurrentModel, true, true);
        mDialog.show();
        mDialog.setViewDate(bean.title, bean.subTitle, bean.name, bean.techNo, bean.clubName, null, bean.imageUrl, bean.qrCodeUrl);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setPosterListener(this);
    }

    @Override
    public void deleteClicked(PosterBean bean) {
        new RewardConfirmDialog(TechPersonalPosterActivity.this, getString(R.string.tech_poster_alter_message), getString(R.string.tech_poster_alter_delete_message), "", true) {
            @Override
            public void onConfirmClick() {
                super.onConfirmClick();
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_DELETE, bean.id);
            }
        }.show();
    }

    @Override
    public void editClicked(PosterBean bean) {
        mPosterBean = bean;
        createOrEditTechPoster(false);
    }

    @Override
    public void shareClicked(PosterBean bean) {
        mPosterBean = bean;
        showShareWindow(bean);
    }

    @Override
    public void posterSave(View view, View dismiss) {
        if ((mPosterBean.createTime + ONE_MONTH_DAY_MILLISECOND) < System.currentTimeMillis()) {
            XToast.show(ResourceUtils.getString(R.string.qr_code_invalid_alter_message));
            return;
        }
        if (!PermissionTool.hasPermissions(TechPersonalPosterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"需要访问存储器"}, 1);
            return;
        }
        new RewardConfirmDialog(this, ResourceUtils.getString(R.string.tech_poster_alter_message), String.format(ResourceUtils.getString(R.string.tech_poster_save_alter_message),
                DateUtil.getCurrentDate(mPosterBean.createTime + ONE_MONTH_DAY_MILLISECOND)), "", true) {

            @Override
            public void onConfirmClick() {
                super.onConfirmClick();
                if (mDialog != null) {
                    mDialog.dismiss();
                    dismiss.setVisibility(View.GONE);
                    saveImage(view, true);
                }

            }
        }.show();
    }

    @Override
    public void posterEdit() {
        createOrEditTechPoster(false);
    }

    @Override
    public void posterShare(View view, View dismiss) {
        if (!PermissionTool.hasPermissions(TechPersonalPosterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"需要访问存储器"}, 1);
            return;
        }
        if ((mPosterBean.createTime + ONE_MONTH_DAY_MILLISECOND) < System.currentTimeMillis()) {
            XToast.show(ResourceUtils.getString(R.string.qr_code_invalid_alter_message));
            return;
        }
        new RewardConfirmDialog(this, ResourceUtils.getString(R.string.tech_poster_alter_message),
                String.format(ResourceUtils.getString(R.string.tech_poster_share_alter_message),
                        DateUtil.getCurrentDate(mPosterBean.createTime + ONE_MONTH_DAY_MILLISECOND)), "", true) {

            @Override
            public void onConfirmClick() {
                super.onConfirmClick();
                if (mDialog != null) {
                    mDialog.dismiss();
                    dismiss.setVisibility(View.GONE);
                    String localFile = saveImage(view, false);
                    ShareController.doShareImage(localFile);
                }

            }
        }.show();
    }

    private String saveImage(View v, boolean saveToGallery) {
        Bitmap bitmap;
        String name = "技师海报.png";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + "技师海报");
        if (!file.exists()) {
            file.mkdir();
        }
        View view = mDialog.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        mDialog.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        File picFile = new File(file, name);
        try {
            if (bitmap.getWidth() >= location[0] + view.getWidth()) {
                bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], view.getWidth(), view.getHeight() - Utils.dip2px(TechPersonalPosterActivity.this, 45));
            } else {
                bitmap = Bitmap.createBitmap(bitmap, 0, location[1], bitmap.getWidth(), view.getHeight() - Utils.dip2px(TechPersonalPosterActivity.this, 45));
            }
            bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], view.getWidth(), view.getHeight() - Utils.dip2px(this, 45));
            FileOutputStream fo = new FileOutputStream(picFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fo);
            if (saveToGallery) {
                saveImageToGallery(picFile);
            }
            return picFile.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XToast.show("图片保存失败，请重新尝试");
        } finally {
            view.destroyDrawingCache();
        }

        return null;
    }

    public void saveImageToGallery(File file) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), "code", null);
            // 最后通知图库更新
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + file)));
            Utils.makeShortToast(this, "保存成功，可在图库查看");

        } catch (FileNotFoundException e) {
            Utils.makeShortToast(this, "保存失败");
            e.printStackTrace();
        }
    }


}
