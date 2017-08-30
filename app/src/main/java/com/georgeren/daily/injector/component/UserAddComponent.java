package com.georgeren.daily.injector.component;

import com.georgeren.daily.injector.module.UserAddModule;
import com.georgeren.daily.mvp.useradd.UserAddView;

import dagger.Component;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Component(modules = UserAddModule.class)
public interface UserAddComponent {
    void inject(UserAddView view);
}
