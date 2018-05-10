package me.gavin.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * ClientAPI
 *
 * @author gavin.xiong 2016/12/9
 */
public interface ClientAPI {

    /**
     * 获取
     */
    @Headers("Accept-Encoding: gzip, deflate, br")
    @GET
    Observable<ResponseBody> get(@Url String url, @Header("Cache-Control") String cacheControl);

    /**
     * 下载
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
