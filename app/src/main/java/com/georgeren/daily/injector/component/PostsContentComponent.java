package com.georgeren.daily.injector.component;

import com.georgeren.daily.injector.module.PostsContentModule;
import com.georgeren.daily.mvp.postscontent.PostsContentView;

import dagger.Component;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Component(modules = PostsContentModule.class)
public interface PostsContentComponent {
    void inject(PostsContentView view);
}
