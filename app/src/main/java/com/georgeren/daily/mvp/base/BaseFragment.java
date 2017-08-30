package com.georgeren.daily.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by georgeRen on 2017/8/28.
 */

public abstract class BaseFragment<T extends IBasePresenter> extends RxFragment implements IBaseView<T> {
    @Inject
    protected T presenter;
    protected MultiTypeAdapter adapter;

    protected abstract int attachLayoutId();

    protected abstract void initViews(View view);

    protected abstract void initData();

    protected abstract void initInjector();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(attachLayoutId(), container, false);
        initViews(view);
        initData();
        return view;
    }

    @Override
    public <T1> LifecycleTransformer<T1> bindToLife() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }
}
