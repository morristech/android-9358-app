package com.xmd.manager.journal.model;

import com.xmd.manager.journal.manager.TechnicianManager;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalItemTechnician extends JournalItemBase {

    private Technician mTechnician;

    public JournalItemTechnician(Technician technician) {
        super(null);
        mTechnician = technician;
    }

    public JournalItemTechnician(String data) {
        super(data);
        mTechnician = TechnicianManager.getInstance().getTechnician(data);
        if (mTechnician == null) {
            mTechnician = TechnicianManager.getInstance().getTechnicianFromRecommendRanking(data);
        }
    }

    @Override
    public String contentToString() {
        if (mTechnician != null) {
            return mTechnician.getId();
        }
        return null;
    }

    public Technician getTechnician() {
        return mTechnician;
    }

    public void setTechnician(Technician technician) {
        this.mTechnician = technician;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (mTechnician != null) {
            hashCode += mTechnician.hashCode();
        } else {
            hashCode = super.hashCode();
        }
        return hashCode;
    }
}
