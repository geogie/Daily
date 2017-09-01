package com.georgeren.daily.mvp.postslist;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.georgeren.daily.InitApp;
import com.georgeren.daily.R;
import com.georgeren.daily.bean.FooterBean;
import com.georgeren.daily.bean.PostsListBean;
import com.georgeren.daily.binder.FooterViewBinder;
import com.georgeren.daily.binder.PostsListViewBinder;
import com.georgeren.daily.injector.component.DaggerPostsListComponent;
import com.georgeren.daily.injector.module.PostsListModule;
import com.georgeren.daily.mvp.base.BaseActivity;
import com.georgeren.daily.utils.DiffCallback;
import com.georgeren.daily.utils.OnLoadMoreListener;
import com.georgeren.daily.utils.SettingUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.georgeren.daily.mvp.bean.ZhuanlanBean.ZHUANLANBEAN_NAME;
import static com.georgeren.daily.mvp.bean.ZhuanlanBean.ZHUANLANBEAN_POSTSCOUNT;
import static com.georgeren.daily.mvp.bean.ZhuanlanBean.ZHUANLANBEAN_SLUG;

/**
 * Created by georgeRen on 2017/8/30.
 * activity: 详情
 */

public class PostsListView extends BaseActivity<IPostsList.Presenter> implements IPostsList.View, SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private int postCount;
    private String slug;
    private Items oldItems = new Items();

    public static void launch(String slug, String name, int postsCount) {
        InitApp.AppContext.startActivity(new Intent(InitApp.AppContext, PostsListView.class)
                .putExtra(ZHUANLANBEAN_SLUG, slug)
                .putExtra(ZHUANLANBEAN_NAME, name)
                .putExtra(ZHUANLANBEAN_POSTSCOUNT, postsCount)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));// 新栈
    }

    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        presenter.doRefresh();
    }

    /**
     * 请求数据
     */
    @Override
    public void onRequestData() {
        onShowLoading();
        presenter.doRequestData(slug, 0);
    }

    @Override
    public void onSetAdapter(final List<PostsListBean> list) {
        new Thread(new Runnable() {
            @Override
            public void run() { // Thread-x，采用在线程中刷新。
                //  根据是否在绘制或者渲染决定是否notify http://blog.csdn.net/android_lyp/article/details/52704717
                //  在线程中刷新 http://www.jianshu.com/p/be89ebfb215e
                Items newItems = new Items(list);
                newItems.add(new FooterBean());
                DiffCallback.create(oldItems, newItems, DiffCallback.POSTSLIST, adapter);
                oldItems.clear();
                oldItems.addAll(newItems);
            }
        }).start();

        canLoadMore = true;

        recyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 列表文章 < 总文章 继续加载 这里要判断recyclerview是否滚动到底再执行 不然后台一直加载
                if ((list.size() < postCount) && canLoadMore) {// 加载更多
                    canLoadMore = false;
                    presenter.doRequestData(slug, list.size());
                } else if ((list.size() == postCount)) {// 没有更多
                    Snackbar.make(refreshLayout, R.string.no_more, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onShowLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onHideLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onShowNetError() {
        Snackbar.make(refreshLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_postslist;
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        initToolBar(toolbar, true, null);// 返回按钮
        toolbar.setOnClickListener(new View.OnClickListener() {// 点击tooBar，item滚动到顶部
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // 设置下拉刷新的按钮的颜色
        refreshLayout.setColorSchemeColors(SettingUtil.getInstance().getColor());
        refreshLayout.setOnRefreshListener(this);

        adapter = new MultiTypeAdapter();
        adapter.register(PostsListBean.class, new PostsListViewBinder());
        adapter.register(FooterBean.class, new FooterViewBinder());
        adapter.setItems(oldItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        slug = intent.getStringExtra(ZHUANLANBEAN_SLUG);
        postCount = intent.getIntExtra(ZHUANLANBEAN_POSTSCOUNT, 0);
        String title = intent.getStringExtra(ZHUANLANBEAN_NAME);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        onRequestData();
    }

    @Override
    protected void initInjector() {
        DaggerPostsListComponent.builder()
                .postsListModule(new PostsListModule(this))
                .build()
                .inject(this);
    }
}
