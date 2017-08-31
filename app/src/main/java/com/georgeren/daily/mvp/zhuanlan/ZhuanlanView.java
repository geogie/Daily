package com.georgeren.daily.mvp.zhuanlan;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.georgeren.daily.R;
import com.georgeren.daily.RxBus;
import com.georgeren.daily.binder.ZhuanlanViewBinder;
import com.georgeren.daily.injector.component.DaggerZhuanlanComponent;
import com.georgeren.daily.injector.module.ZhuanlanModule;
import com.georgeren.daily.mvp.base.BaseFragment;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;
import com.georgeren.daily.utils.RecyclerViewUtil;
import com.georgeren.daily.utils.SettingUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by georgeRen on 2017/8/28.
 * 复用：产品、生活、音乐、健康、专业、知乎等复用
 */

public class ZhuanlanView extends BaseFragment<IZhuanlan.Presenter> implements IZhuanlan.View, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "ZhuanlanView";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout root;
    private int type;
    private Observable<Boolean> observable;

    /**
     * 初始化fragment模版：
     * @param type 类型：产品、生活、音乐、健康、专业、知乎等复用
     * @return
     */
    public static ZhuanlanView newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TAG, type);
        ZhuanlanView fragment = new ZhuanlanView();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observable = RxBus.getInstance().register(Boolean.class);// 注册 rxBus
        observable.subscribe(new Consumer<Boolean>() {// 订阅事件：白天／夜晚 切换
            @Override
            public void accept(Boolean isNightMode) throws Exception {
                refreshUI();
            }
        });
    }

    @Override
    public void onStop() {
        presenter.onDestroy();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        RxBus.getInstance().unregister(Boolean.class, observable);// 解除注册 rxBus
        super.onDestroy();
    }

    /**
     * 刷新：presenter 去刷新
     */
    @Override
    public void onRefresh() {
        presenter.doRefresh();
    }

    /**
     * 请求数据：presenter
     */
    @Override
    public void onRequestData() {
        presenter.doLoading();
    }

    /**
     * 设置 adapter
     * @param list
     */
    @Override
    public void onSetAdapter(List<ZhuanlanBean> list) {
        if (adapter == null) {// recyclerView的 adapter 绑定item
            adapter = new MultiTypeAdapter(list);
            adapter.register(ZhuanlanBean.class, new ZhuanlanViewBinder());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示loading：refreshLayout显示，recyclerView隐藏
     */
    @Override
    public void onShowLoading() {
        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * 隐藏loading：refreshLayout隐藏，recyclerView显示
     */
    @Override
    public void onHideLoading() {
        refreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 网络异常提示：SnackBar，refreshLayout设置可刷新
     */
    @Override
    public void onShowNetError() {
        Snackbar.make(refreshLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();
        refreshLayout.setEnabled(true);
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.fragment_zhuanlan;
    }

    /**
     * 初始化view
     * @param view
     */
    @Override
    protected void initViews(View view) {
        root = view.findViewById(R.id.root);
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);// 固定item大小，不随notify改变
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));// 线性单列垂直item
        // 设置下拉刷新的按钮的颜色，监听
        refreshLayout.setColorSchemeColors(SettingUtil.getInstance().getColor());
        refreshLayout.setOnRefreshListener(this);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        onRequestData();
    }

    /**
     * 注入：presenter
     */
    @Override
    protected void initInjector() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt(TAG);
            DaggerZhuanlanComponent.builder()
                    .zhuanlanModule(new ZhuanlanModule(this, type))
                    .build()
                    .inject(this);
        }
    }

    /**
     * 白天／夜晚 改变 刷新ui：
     * fragment 背景色
     * item背景色
     * item中文字颜色
     *
     */
    private void refreshUI() {
        Log.d(TAG, "refreshUI: ");
        Resources.Theme theme = getActivity().getTheme();
        TypedValue rootViewBackground = new TypedValue();
        TypedValue itemViewBackground = new TypedValue();
        TypedValue textColorPrimary = new TypedValue();
        theme.resolveAttribute(R.attr.rootViewBackground, rootViewBackground, true);
        theme.resolveAttribute(R.attr.itemViewBackground, itemViewBackground, true);
        theme.resolveAttribute(R.attr.textColorPrimary, textColorPrimary, true);
        root.setBackgroundResource(rootViewBackground.resourceId);// fragment根布局背景色修改

        Resources resources = getResources();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CardView cardView = recyclerView.getChildAt(i).findViewById(R.id.cardview);
            cardView.setBackgroundResource(itemViewBackground.resourceId);// item背景色

            TextView tv_name = cardView.findViewById(R.id.tv_name);// item文本字体色
            tv_name.setTextColor(resources.getColor(textColorPrimary.resourceId));

            TextView tv_followersCount = cardView.findViewById(R.id.tv_followersCount);
            tv_followersCount.setTextColor(resources.getColor(textColorPrimary.resourceId));

            TextView tv_postsCount = cardView.findViewById(R.id.tv_postsCount);
            tv_postsCount.setTextColor(resources.getColor(textColorPrimary.resourceId));

            TextView tv_intro = cardView.findViewById(R.id.tv_intro);
            tv_intro.setTextColor(resources.getColor(textColorPrimary.resourceId));
        }

        RecyclerViewUtil.invalidateCacheItem(recyclerView);
    }

}
