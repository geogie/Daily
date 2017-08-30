package com.georgeren.daily;

import com.georgeren.daily.bean.PostsContentBean;
import com.georgeren.daily.bean.PostsListBean;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by georgeRen on 2017/8/30.
 */

public interface IApi {
    String API_BASE = "https://zhuanlan.zhihu.com/";
    String POST_URL = "https://zhuanlan.zhihu.com/api/posts/";

    /**
     * 获取专栏信息
     * https://zhuanlan.zhihu.com/api/columns/design
     *
     * @param slug 专栏ID
     * @return
     */
    @GET("api/columns/{slug}")
    Call<ZhuanlanBean> getZhuanlanBean(@Path("slug") String slug);

    /**
     * 获取专栏信息 Retrofit + RxJava
     * https://zhuanlan.zhihu.com/api/columns/design
     *
     * @param slug
     * @return
     */
    @GET("api/columns/{slug}")
    Observable<ZhuanlanBean> getZhuanlanBeanRx(@Path("slug") String slug);

    /**
     * 获取专栏文章 Retrofit + RxJava
     * https://zhuanlan.zhihu.com/api/columns/design/posts?limit=10&offset=10
     *
     * @param slug   专栏ID
     * @param offset 偏移量
     * @return
     */
    @GET("api/columns/{slug}/posts?limit=10")
    Observable<List<PostsListBean>> getPostsListRx(
            @Path("slug") String slug,
            @Query("offset") int offset);


    /**
     * 获取文章内容 Retrofit + RxJava
     * https://zhuanlan.zhihu.com/api/posts/25982605
     *
     * @param slug 文章ID
     * @return
     */
    @GET("api/posts/{slug}")
    Observable<PostsContentBean> getPostsContentBeanRx(@Path("slug") int slug);

}
