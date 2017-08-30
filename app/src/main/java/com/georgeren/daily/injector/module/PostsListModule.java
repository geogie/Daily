package com.georgeren.daily.injector.module;

import com.georgeren.daily.mvp.postslist.IPostsList;
import com.georgeren.daily.mvp.postslist.PostsListPresenter;
import com.georgeren.daily.mvp.postslist.PostsListView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Module
public class PostsListModule {
    private final PostsListView view;

    public PostsListModule(PostsListView view) {
        this.view = view;
    }

    @Provides
    public IPostsList.Presenter providePresenter() {
        return new PostsListPresenter(view);
    }
}
