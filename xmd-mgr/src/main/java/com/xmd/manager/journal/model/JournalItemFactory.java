package com.xmd.manager.journal.model;

import android.text.TextUtils;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalItemFactory {
    public static JournalItemBase create(JournalContentType contentType, String data) {
        if (!TextUtils.isEmpty(data)) {
            switch (contentType.getKey()) {
                case JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING:
                case JournalContentType.CONTENT_KEY_TECHNICIAN:
                    return new JournalItemTechnician(data);
                case JournalContentType.CONTENT_KEY_PHOTO_ALBUM:
                    return new JournalItemPhoto(data);
                case JournalContentType.CONTENT_KEY_COUPON:
                    return new JournalItemCoupon(data);
                case JournalContentType.CONTENT_KEY_SERVICE:
                    return new JournalItemService(data);
                case JournalContentType.CONTENT_KEY_VIDEO:
                    return new JournalItemVideo(data);
                case JournalContentType.CONTENT_KEY_ARTICLE:
                    return new JournalItemArticle(data);
                case JournalContentType.CONTENT_KEY_ACTIVITY:
                    return new JournalItemActivity(data);
                case JournalContentType.CONTENT_KEY_IMAGE_ARTICLE:
                    return new JournalItemImageArticle(data);
            }
        }
        return null;
    }
}
