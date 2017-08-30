package com.georgeren.daily.injector.component;

import com.georgeren.daily.injector.module.ZhuanlanModule;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanView;

import dagger.Component;

/**
 * Created by georgeRen on 2017/8/29.
 */
@Component(modules = ZhuanlanModule.class)
public interface ZhuanlanComponent {
    void inject(ZhuanlanView view);
}
