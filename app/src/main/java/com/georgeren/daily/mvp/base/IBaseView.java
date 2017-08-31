package com.georgeren.daily.mvp.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by georgeRen on 2017/8/28.
 * 顶级view
 */

public interface IBaseView<T> {
    /**
     * rxLife 生命周期绑定
     * @param <T>
     * @return
     */
    <T> LifecycleTransformer<T> bindToLife();
}
