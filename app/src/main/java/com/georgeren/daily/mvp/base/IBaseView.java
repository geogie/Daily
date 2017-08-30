package com.georgeren.daily.mvp.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by georgeRen on 2017/8/28.
 */

public interface IBaseView<T> {
    <T> LifecycleTransformer<T> bindToLife();
}
