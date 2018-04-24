package me.gavin.net;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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
     * 衍墨轩
     *
     * @param query
     * @return Result
     */
    @Headers({
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: zh-CN,zh;q=0.9",
            "Connection: keep-alive",
            "Cookie: Hm_lvt_1a9198c3e677b37553bfcb256c8565ff=1524584199; Hm_lpvt_1a9198c3e677b37553bfcb256c8565ff=1524586489; Hm_lvt_1a9198c3e677b37553bfcb256c8565ff=1524584199; Hm_lpvt_1a9198c3e677b37553bfcb256c8565ff=1524586472; QINGCLOUDELB=1987b7501e9d73a323375db9113157e3782e7375b929dfff96a84f49768ce246; koa.sid.sig=b7rSMnltToMvCAQDfmMNAvKtFiA; koa.sid=n89OeqydGGeK6O2KAd48VbQc9htV_coi8FEaA1MJnjE; Hm_lvt_f38b9a4f588b4bac6fed0bbfeb2d0d60=1522557002,1522717036,1522803167,1522979988; Hm_lpvt_f38b9a4f588b4bac6fed0bbfeb2d0d60=1523194596",
            "Host: www.ymoxuan.com",
            "Referer: https: //www.ymoxuan.com/search.htm?keyword=%E4%BB%8E%E9%9B%B6%E5%BC%80%E5%A7%8B",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36"
    })
    @GET("https://www.ymoxuan.com/search.htm")
    Observable<RequestBody> yanmoxuanQuery(@Query("keyword") String query);


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
