package com.georgeren.daily;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.georgeren.daily.mvp.base.BaseActivity;
import com.georgeren.daily.mvp.base.IBasePresenter;
import com.georgeren.daily.mvp.useradd.UserAddView;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanPresenter;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanView;
import com.georgeren.daily.utils.SettingUtil;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 透明化系统状态栏
 * 透明化系统状态栏，使得布局侵入系统栏的后面，必须启用fitsSystemWindows属性来调整布局才不至于被系统栏覆盖
 * http://www.jianshu.com/p/34a8b40b9308
 */
public class MainActivity extends BaseActivity<IBasePresenter> implements NavigationView.OnNavigationItemSelectedListener
        , ColorChooserDialog.ColorCallback {
    private DrawerLayout drawerLayout; // 抽屉外
    private NavigationView navigationView;// 抽屉内
    private long exitTime;// 双击退出记时
    private Observable<Boolean> observable;// rxBus
    private SwitchCompat switchInput;// 开关：夜晚／白天

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observable = RxBus.getInstance().register(Boolean.class);// 初始化 rxBus 注册
        observable.subscribe(new Consumer<Boolean>() {// rxBus 订阅事件
            @Override
            public void accept(Boolean isNightMode) throws Exception {// 切换 白天／夜晚 事件。换色：过度渐变动画
                showAnimation();// 渐变过度动画
                refreshUI();// navigationView 夜晚／白天 换色
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(Boolean.class, observable);// rxBus 解除注册
        super.onDestroy();
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar, false, null);// 测试：在这里设置成true和false一个性质，受ActionBarDrawerToggle影响。最好设置成false，null时会默认用label

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// 抽屉外,http://blog.csdn.net/chencehnggq/article/details/21492417
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);// tooBar和抽屉关联
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();// 这个必须要，没有的话进去的默认是个箭头。。正常应该是三横杠的,http://blog.csdn.net/chencehnggq/article/details/21492417

        navigationView = (NavigationView) findViewById(R.id.nav_view);// 抽屉内
        navigationView.setNavigationItemSelectedListener(this);// 选择事件

        switchInput = navigationView.getMenu().findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.switch_input);// 开关：白天／夜晚
        switchInput.setChecked(SettingUtil.getInstance().getIsNightMode());// 开关状态初始化

        setUpSwitch();

        switchInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isNightMode) {
                SettingUtil.getInstance().setIsNightMode(isNightMode);// 存储 当前 白天／夜晚 切换后的状态
                if (isNightMode) {// 改变 白天／夜晚 主题
                    setTheme(R.style.DarkTheme);
                } else {
                    setTheme(R.style.LightTheme);
                }
                setUpSwitch();// 开关变色
                RxBus.getInstance().post(isNightMode);// rxBus 发送 白天／夜晚 改变事件
            }
        });
    }

    /**
     * 初始化：显示fragment，navigation选择的状态。
     */
    @Override
    protected void initData() {
        replaceFragment(ZhuanlanPresenter.TYPE_PRODUCT);
        navigationView.setCheckedItem(R.id.nav_product);
    }

    @Override
    protected void initInjector() {

    }

    /**
     * navigationView 选择事件
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_product) {// 产品
            replaceFragment(ZhuanlanPresenter.TYPE_PRODUCT);

        } else if (id == R.id.nav_life) {// 生活
            replaceFragment(ZhuanlanPresenter.TYPE_LIFE);

        } else if (id == R.id.nav_music) {// 音乐
            replaceFragment(ZhuanlanPresenter.TYPE_MUSIC);

        } else if (id == R.id.nav_emotion) {// 健康
            replaceFragment(ZhuanlanPresenter.TYPE_EMOTION);

        } else if (id == R.id.nav_profession) {// 专业
            replaceFragment(ZhuanlanPresenter.TYPE_FINANCE);

        } else if (id == R.id.nav_zhihu) {// 知乎
            replaceFragment(ZhuanlanPresenter.TYPE_ZHIHU);

        } else if (id == R.id.nav_user_add) {// 自定义
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new UserAddView()).commit();

        } else if (id == R.id.nav_color_chooser) {// 选择主题颜色
            createColorChooserDialog();// 选择主题颜色 dialog 创建

        } else if (id == R.id.nav_about) {// 关于
            startActivity(new Intent(this, AboutActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);// 收起位置：左边
        return true;
    }

    /**
     * 主题颜色选择监听
     * @param dialog
     * @param selectedColor
     */
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(selectedColor));// actionBar换色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(selectedColor));// statusBar换色
            getWindow().setNavigationBarColor(selectedColor);// NavigationBar换色
        }
        if (!dialog.isAccentMode()) {// accentMode因为没有设置，默认false，所以都会执行的。判断没什么作用，when true, will display accent palette instead of primary palette
            SettingUtil.getInstance().setColor(selectedColor);// 存储转换后的主题色
        }
    }

    /**
     * 选择主题色的dialog消失监听
     * @param dialog
     */
    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
        setUpSwitch();// 对开关着色
    }

    /**
     * 夜晚／白天 切换时渐变过度动画
     */
    private void showAnimation() {
        final View decorview = getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorview);
        if (decorview instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(this);
            view.setBackground(new BitmapDrawable((getResources()), cacheBitmap));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorview).addView(view, layoutParams);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorview).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    /**
     * view 转 bitmap
     * @param view
     * @return
     */
    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnable = true;
        view.setDrawingCacheEnabled(drawingCacheEnable);
        view.buildDrawingCache(drawingCacheEnable);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 主题切换后：根据主题的rootViewBackground（背景色），textColorPrimary（字体色）。渲染 navigationView 的背景色、item背景色、item字体色、item的icon（svg）色。
     */
    protected void refreshUI() {
        Resources.Theme theme = getTheme();
        TypedValue rootViewBackground = new TypedValue();
        TypedValue textColorPrimary = new TypedValue();
        theme.resolveAttribute(R.attr.rootViewBackground, rootViewBackground, true);
        theme.resolveAttribute(R.attr.textColorPrimary, textColorPrimary, true);
        Resources resources = getResources();
        navigationView.setBackgroundResource(rootViewBackground.resourceId);
        navigationView.setItemBackgroundResource(rootViewBackground.resourceId);
        navigationView.setItemTextColor(resources.getColorStateList(textColorPrimary.resourceId));
        navigationView.setItemIconTintList(resources.getColorStateList(textColorPrimary.resourceId));
    }

    /**
     * 复用：产品、生活、音乐、健康、专业、知乎等复用
     *
     * @param type
     */
    private void replaceFragment(int type) {
        ZhuanlanView fragment = ZhuanlanView.newInstance(type);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
    }

    /**
     * 初始化，开关变动，主题色选取 触发
     * 开关：白天／夜晚
     * 开关着色
     */
    private void setUpSwitch() {
        boolean isNightMode = SettingUtil.getInstance().getIsNightMode();
        if (isNightMode) {// 夜晚模式：用主题色来对开关着色
            switchInput.setThumbTintList(ColorStateList.valueOf(SettingUtil.getInstance().getColor()));
        } else {// 白天模式：根据当前主题色来对开关着色（其实就textColorPrimary 字体色）。因为有 夜晚／白天 所以通过获取当前 textColorPrimary 对开关渲染
            Resources.Theme theme = getTheme();// 主题
            Resources resources = getResources();
            TypedValue textColorPrimary = new TypedValue();
            theme.resolveAttribute(R.attr.textColorPrimary, textColorPrimary, true);
            switchInput.setThumbTintList(resources.getColorStateList(textColorPrimary.resourceId));
        }
    }

    /**
     * 切换主题色 dialog：
     */
    private void createColorChooserDialog() {
        new ColorChooserDialog.Builder(this, R.string.md_color_chooser_title)
                .doneButton(R.string.md_done)
                .cancelButton(R.string.md_cancel)
                .allowUserColorInput(true) // 允许自定义书写颜色
                .allowUserColorInputAlpha(false) // 不允许自定义书写透明度
                .theme(SettingUtil.getInstance().getIsNightMode() ? Theme.DARK : Theme.LIGHT)
                .customButton(R.string.md_custom)
                .show();
    }

    /**
     * 硬件返回键的处理
     */
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if ((currentTime - exitTime) < 2000) {
            super.onBackPressed();
        } else {
            Snackbar.make(drawerLayout, getString(R.string.double_click_exit), Snackbar.LENGTH_SHORT).show();
            exitTime = currentTime;
        }
    }
}
