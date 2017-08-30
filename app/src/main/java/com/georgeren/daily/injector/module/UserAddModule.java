package com.georgeren.daily.injector.module;

import com.georgeren.daily.mvp.useradd.IUserAdd;
import com.georgeren.daily.mvp.useradd.UserAddPresenter;
import com.georgeren.daily.mvp.useradd.UserAddView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Module
public class UserAddModule {
    private final UserAddView view;

    public UserAddModule(UserAddView view) {
        this.view = view;
    }

    @Provides
    public IUserAdd.Presenter providePresenter() {
        return new UserAddPresenter(view);
    }
}
