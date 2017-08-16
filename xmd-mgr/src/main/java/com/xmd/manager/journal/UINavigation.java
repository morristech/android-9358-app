package com.xmd.manager.journal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.common.Logger;
import com.xmd.manager.journal.activity.ClubServiceChoiceActivity;
import com.xmd.manager.journal.activity.JournalContentEditActivity;
import com.xmd.manager.journal.activity.JournalTemplateChoiceActivity;
import com.xmd.manager.journal.activity.TechnicianChoiceActivity;
import com.xmd.manager.journal.activity.VideoPlayerActivity;
import com.xmd.manager.journal.camera.CameraActivity;
import com.xmd.manager.journal.manager.VideoManager;
import com.xmd.manager.service.response.JournalVideoConfigResult;
import com.xmd.manager.window.BrowserActivity;

import java.util.ArrayList;

/**
 * Created by heyangya on 16-11-2.
 */

public class UINavigation {
    public static final String EXTRA_INT_JOURNAL_ID = "extra_int_journal_id";
    public static final String EXTRA_STRING_LIST_FORBIDDEN_TECHNICIAN_NO = "extra_string_list_forbidden_technician_no";
    public static final String EXTRA_PARCELABLE_SELECTED_SERVICE_ITEMS = "extra_integer_list_forbidden_project_id";
    public static final String EXTRA_STRING_TECHNICIAN_ID = "extra_string_technician_no";
    public static final String EXTRA_INT_MAX_SIZE = "extra_int_max_size";
    public static final String EXTRA_INT_TEMPLATE_ID = "extra_int_template_id";

    public static final int REQUEST_CODE_CHOICE_TECHNICIAN = 1;
    public static final int REQUEST_CODE_CHOICE_SERVICE = 2;
    public static final int REQUEST_CODE_CHOICE_IMAGE = 3;
    public static final int REQUEST_CODE_CROP_IMAGE = 4;
    public static final int REQUEST_CODE_RECORD_VIDEO = 5;
    public static final int REQUEST_CODE_CHOICE_IMAGE_ARTICLE_IMAGE = 6;

    public static void gotoTemplateChoiceActivity(Context context, int journalId) {
        Intent intent = new Intent(context, JournalTemplateChoiceActivity.class);
        intent.putExtra(EXTRA_INT_JOURNAL_ID, journalId);
        context.startActivity(intent);
    }

    public static void gotoContentEditActivity(Context context, int journalId, int templateId) {
        Intent intent = new Intent(context, JournalContentEditActivity.class);
        intent.putExtra(EXTRA_INT_JOURNAL_ID, journalId);
        intent.putExtra(EXTRA_INT_TEMPLATE_ID, String.valueOf(templateId));
        context.startActivity(intent);
    }

    public static void gotoTechnicianChoiceActivityForResult(Activity activity,
                                                             ArrayList<String> forbiddenTechnicianNoList) {
        Intent intent = new Intent(activity, TechnicianChoiceActivity.class);
        intent.putStringArrayListExtra(EXTRA_STRING_LIST_FORBIDDEN_TECHNICIAN_NO, forbiddenTechnicianNoList);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOICE_TECHNICIAN);
    }

    public static void gotoWebBrowse(Context context, String url) {
        Logger.d("gotoWebBrowse " + url);
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
        intent.putExtra(BrowserActivity.EXTRA_URL, url);
        context.startActivity(intent);
    }

    public static void gotoProjectChoiceActivityForResult(Activity activity, ArrayList<ServiceItem> selectedList, int maxSize) {
        Intent intent = new Intent(activity, ClubServiceChoiceActivity.class);
        intent.putExtra(EXTRA_INT_MAX_SIZE, maxSize);
        intent.putParcelableArrayListExtra(EXTRA_PARCELABLE_SELECTED_SERVICE_ITEMS, selectedList);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOICE_SERVICE);
    }

    public static void gotoRecordVideoActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        JournalVideoConfigResult.DATA config = VideoManager.getInstance().getVideoConfig();
        intent.putExtra(CameraActivity.EXTRA_INT_WIDTH, config.width);
        intent.putExtra(CameraActivity.EXTRA_INT_HEIGHT, config.height);
        intent.putExtra(CameraActivity.EXTRA_INT_FRAME_RATE, config.frameRate);
        intent.putExtra(CameraActivity.EXTRA_INT_VIDEO_BIT_RATE, config.bitrate);
        intent.putExtra(CameraActivity.EXTRA_INT_TIME_SECOND, config.videoLength);
        activity.startActivityForResult(intent, REQUEST_CODE_RECORD_VIDEO);
    }

    public static String getRecordVideoPath(Intent intent) {
        return CameraActivity.getVideoPath(intent);
    }

    public static void gotoPlayVideo(Context context, String path) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.setData(Uri.parse(path));
        context.startActivity(intent);
    }
}
