package com.georgeren.daily.injector.module;

import com.georgeren.daily.mvp.postscontent.IPostsContent;
import com.georgeren.daily.mvp.postscontent.PostsContentPresenter;
import com.georgeren.daily.mvp.postscontent.PostsContentView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by georgeRen on 2017/8/30.
 */
@Module
public class PostsContentModule {
    private final PostsContentView view;

    public PostsContentModule(PostsContentView view) {
        this.view = view;
    }

    @Provides
    public IPostsContent.Presenter providePresenter() {
        return new PostsContentPresenter(view);
    }
}
