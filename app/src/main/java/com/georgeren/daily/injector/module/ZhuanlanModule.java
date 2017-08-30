package com.georgeren.daily.injector.module;

import com.georgeren.daily.mvp.zhuanlan.IZhuanlan;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanPresenter;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by georgeRen on 2017/8/29.
 */
@Module
public class ZhuanlanModule {
    private final ZhuanlanView view;
    private final int type;

    public ZhuanlanModule(ZhuanlanView view, int type) {
        this.view = view;
        this.type = type;
    }

    @Provides
    public IZhuanlan.Presenter providePresenter() {
        return new ZhuanlanPresenter(view, type);
    }
}
