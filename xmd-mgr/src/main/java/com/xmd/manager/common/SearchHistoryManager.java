package com.xmd.manager.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-11-1.
 */

public class SearchHistoryManager {

    private static SearchHistoryManager mSearchHistoryManagerInstance;
    private List<String> mHistoryList;

    private SearchHistoryManager(){
        mHistoryList = new ArrayList<>();
    }

    public static SearchHistoryManager getSearchHistoryManagerInstance() {
        if (mSearchHistoryManagerInstance == null) {
            mSearchHistoryManagerInstance = new SearchHistoryManager();
        }
        return mSearchHistoryManagerInstance;
    }

    public void addData(String data){
        if(!mHistoryList.contains(data)){
            mHistoryList.add(0,data);
        }
    }

    public void clecarData(){
        if(mHistoryList != null){
            mHistoryList.clear();
        }
    }

    public List<String> getSearchHistoryData(){
        return mHistoryList;
    }

}
