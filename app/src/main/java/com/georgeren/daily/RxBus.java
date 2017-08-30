package com.georgeren.daily;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by georgeRen on 2017/8/28.
 * RxBus
 */

public class RxBus {
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();// 安全的hashMap

    private RxBus() {

    }

    public static RxBus getInstance() {
        return Holder.instance;
    }

    /**
     * 注册
     * @param clz
     * @param <T>
     * @return
     */
    public <T> Observable<T> register(@NonNull Class<T> clz) {
        return register(clz.getName());
    }

    private <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T> subject = PublishSubject.create();
        subjectList.add(subject);
        return subject;
    }

    public <T> void unregister(@NonNull Class<T> clz, @NonNull Observable observable) {
        unregister(clz.getName(), observable);
    }

    /**
     * 解除注册
     * @param tag
     * @param observable
     */
    private void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove(observable);
            if (subjects.isEmpty()) {
                subjectMapper.remove(tag);
            }
        }
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (!subjects.isEmpty()) {
            for (Subject subject : subjects) {
                subject.onNext(content);
            }
        }
    }

    private static class Holder {
        private static RxBus instance = new RxBus();
    }
}
