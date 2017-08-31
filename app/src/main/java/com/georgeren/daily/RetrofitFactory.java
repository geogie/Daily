package com.georgeren.daily;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.georgeren.daily.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by georgeRen on 2017/8/30.
 * 网络请求库封装：缓存策略，无网络用cache的。有网络设置cache，无网缓存1周（7天）
 */

public class RetrofitFactory {
    private static final Object Object = new Object();// 对象：多线程块 安全
    /**
     * 缓存机制
     * 在响应请求之后在 data/data/<包名>/cache 下建立一个response 文件夹，保持缓存数据。
     * 这样我们就可以在请求的时候，如果判断到没有网络，自动读取缓存的数据。
     * 同样这也可以实现，在我们没有网络的情况下，重新打开App可以浏览的之前显示过的内容。
     * 也就是：判断网络，有网络，则从网络获取，并保存到缓存中，无网络，则从缓存中获取。
     * https://werb.github.io/2016/07/29/%E4%BD%BF%E7%94%A8Retrofit2+OkHttp3%E5%AE%9E%E7%8E%B0%E7%BC%93%E5%AD%98%E5%A4%84%E7%90%86/
     */
    private static final Interceptor cacheControlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isNetworkConnected(InitApp.AppContext)) { // 断网
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }

            Response originalResponse = chain.proceed(request);
            if (NetWorkUtil.isNetworkConnected(InitApp.AppContext)) {// 有网
                // 有网络时 设置缓存为默认值
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                // 无网络时 设置超时为1周
                int maxStale = 60 * 60 * 24 * 7;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
    private volatile static Retrofit retrofit;
    public static Retrofit getRetrofit() {
        synchronized (Object) { // 块 http://blog.csdn.net/xxyyww/article/details/5780806
            if (retrofit == null) {
                // 指定缓存路径,缓存大小 50Mb
                Cache cache = new Cache(new File(InitApp.AppContext.getCacheDir(), "HttpCache"),
                        1024 * 1024 * 50);

                // Cookie 持久化
                ClearableCookieJar cookieJar =
                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp.AppContext));

                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .cookieJar(cookieJar) // cookie
                        .cache(cache) //  cache 路径 大小
                        .addInterceptor(cacheControlInterceptor) // 拦截器：
                        .connectTimeout(10, TimeUnit.SECONDS) // 连接超时 http://www.jianshu.com/p/a71a42f4634b
                        .readTimeout(15, TimeUnit.SECONDS) // 读超时
                        .writeTimeout(15, TimeUnit.SECONDS) // 写超时
                        .retryOnConnectionFailure(true); // 是否自动重连

                // Log 拦截器
                if (BuildConfig.DEBUG) {
                    builder = SdkManager.initInterceptor(builder);
                }

                retrofit = new Retrofit.Builder()
                        .baseUrl(IApi.API_BASE)
                        .client(builder.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}
