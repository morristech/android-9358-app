package com.xmd.manager.journal.model;

import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.journal.manager.ClubServiceManager;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalItemService extends JournalItemBase {
    private ServiceItem mServiceItem;

    public JournalItemService(ServiceItem item) {
        super(null);
        mServiceItem = item;
    }

    public JournalItemService(String data) {
        super(data);
        mServiceItem = ClubServiceManager.getInstance().getServiceItem(data);
    }

    @Override
    public String contentToString() {
        return mServiceItem == null ? null : mServiceItem.id;
    }

    public ServiceItem getServiceItem() {
        return mServiceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.mServiceItem = serviceItem;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (mServiceItem != null) {
            hashCode += mServiceItem.hashCode();
        } else {
            hashCode = super.hashCode();
        }
        return hashCode;
    }
}
