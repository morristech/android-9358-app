package com.xmd.app.net;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 17-5-25.
 */

public class NetworkEngine {
    public static <T> Subscription doRequest(Observable<T> observable, NetworkSubscriber<T> subscriber) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
