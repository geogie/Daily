package com.georgeren.daily.mvp.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.CircleView;
import com.georgeren.daily.R;
import com.georgeren.daily.utils.SettingUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by georgeRen on 2017/8/28.
 * 顶级baseActivity
 * 状态栏颜色：http://blog.csdn.net/u011936381/article/details/48522537
 * http://blog.csdn.net/my9074/article/details/44306079
 */

public abstract class BaseActivity <T extends IBasePresenter> extends RxAppCompatActivity implements IBaseView<T>{
    @Inject
    protected T presenter;// 注解
    protected MultiTypeAdapter adapter;
    protected boolean canLoadMore;

    protected abstract int attachLayout();
    protected  abstract void initViews();
    protected abstract void initData();
    protected abstract void initInjector();

    /**
     * 初始化主题
     */
    protected void initTheme(){
        boolean isNigtMode = SettingUtil.getInstance().getIsNightMode();
        if (isNigtMode){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.LightTheme);
        }
    }

    /**
     * tooBar 设置
     * @param toolbar
     * @param homeAsUpEnable 是否是返回按钮
     * @param title
     */
    protected void initToolBar(Toolbar toolbar, boolean homeAsUpEnable, String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(homeAsUpEnable);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();// 初始化主题
        initInjector();// 注解presenter dagger2
        setContentView(attachLayout());// 填充布局
        initViews();// 初始化view
        initData();// 初始化数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = SettingUtil.getInstance().getColor();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(color));// 位于状态栏下、内容之上的部分
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(color));// 状态栏部分
            getWindow().setNavigationBarColor(color);// 华为 底部navigationBarColor
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {// toolBar上的返回按钮监听
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 绑定生命周期
     */
    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }
}
