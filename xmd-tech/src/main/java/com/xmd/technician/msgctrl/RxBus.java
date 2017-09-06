package com.xmd.technician.msgctrl;

import android.os.Looper;

import com.shidou.commonlibrary.helper.ThreadPoolManager;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by sdcm on 16-1-12.
 */
public class RxBus {
    private static class RxBusHolder {
        private static RxBus sInstance = new RxBus();
    }

    private Subject<Object, Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        return RxBusHolder.sInstance;
    }


    public void post(final Object o) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            bus.onNext(o);
        } else {
            ThreadPoolManager.postToUI(new Runnable() {
                @Override
                public void run() {
                    bus.onNext(o);
                }
            });
        }
    }

    public <T> Observable<T> toObservable(final Class<T> eventType) {
        return bus.ofType(eventType);
    }

    public void unsubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}
