package com.xmd.manager.msgctrl;

import rx.Observable;
import rx.Subscription;
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

    private Subject bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        return RxBusHolder.sInstance;
    }


    public void post(Object o) {
        bus.onNext(o);
    }

    public <T extends Object> Observable<T> toObservable(final Class<T> eventType) {
        return bus.filter(o -> {
            return eventType.isInstance(o);
        }).cast(eventType);
    }

    public void unsubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

}
