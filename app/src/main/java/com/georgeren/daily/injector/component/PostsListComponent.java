package com.georgeren.daily.injector.component;

import com.georgeren.daily.injector.module.PostsListModule;
import com.georgeren.daily.mvp.postslist.PostsListView;

import dagger.Component;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Component(modules = PostsListModule.class)
public interface PostsListComponent {
    void inject(PostsListView view);
}
