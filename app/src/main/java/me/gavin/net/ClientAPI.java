package me.gavin.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * ClientAPI
 *
 * @author gavin.xiong 2016/12/9
 */
public interface ClientAPI {


    /* **************************************************************************** *
     * *********************************** 搜索 *************************** *
     * **************************************************************************** */


    /**
     * 搜索 - 衍墨轩
     *
     * @param query
     * @return Result
     */
    @Headers("Accept-Encoding: gzip, deflate, br")
    @GET("https://www.ymoxuan.com/search.htm")
    Observable<ResponseBody> yanmoxuanQuery(@Query("keyword") String query);


    /**
     * 详情 - 衍墨轩
     *
     * @param id
     * @return Result
     */
    @Headers("Accept-Encoding: gzip, deflate, br")
    @GET("https://www.ymoxuan.com/text_{id}.html")
    Observable<ResponseBody> yanmoxuanDetail(@Path("id") String id);


    /**
     * 目录 - 衍墨轩
     *
     * @param id 书籍id
     * @return Result
     */
    @Headers("Accept-Encoding: gzip, deflate, br")
    @GET("https://www.ymoxuan.com/book/0/{id}/index.html")
    Observable<ResponseBody> yanmoxuanDirectory(@Path("id") String id);


    /**
     * 章节 - 衍墨轩
     *
     * @param id  书籍id
     * @param cid 章节id
     * @return Result
     */
    @Headers("Accept-Encoding: gzip, deflate, br")
    @GET("https://www.ymoxuan.com/book/0/{id}/{cid}.html")
    Observable<ResponseBody> yanmoxuanChapter(@Path("id") String id, @Path("cid") String cid);


    /* **************************************************************************** *
     * *********************************** 干货集中营福利 *************************** *
     * **************************************************************************** */


    /**
     * 获取福利
     *
     * @param limit 分页大小
     * @param no    页码
     * @return Result
     */
//    @Headers("Cache-Control: max-stale=1800")
//    @GET("http://gank.io/api/data/福利/{limit}/{no}")
//    Observable<Result> getGankImage(@Path("limit") int limit, @Path("no") int no);


    /* **************************************************************************** *
     * *********************************** 设置 ************************************ *
     * **************************************************************************** */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

}
