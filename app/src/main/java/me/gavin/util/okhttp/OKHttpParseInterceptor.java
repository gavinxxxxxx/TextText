package me.gavin.util.okhttp;

import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import me.gavin.app.StreamHelper;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

/**
 * OkHttp 拦截器 - 解码
 *
 * @author gavin.xiong 2017/5/2
 */
public final class OKHttpParseInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response != null && response.body() != null) {
            byte[] bytes = response.body().bytes();
            if (bytes == null) {
                bytes = new byte[0];
            }

            if (bytes.length >= 2 && inGZIPFormat(bytes)) { // 反GZIP
                GzipSource gzipSource = new GzipSource(Okio.source(new ByteArrayInputStream(bytes)));
                bytes = Okio.buffer(gzipSource).readByteArray();
            }

            String encoding = StreamHelper.getCharsetByJUniversalCharDet(new ByteArrayInputStream(bytes));
            if (!"UTF-8".equalsIgnoreCase(encoding)) { // 转码成 utf-8
                Source source = Okio.source(new ByteArrayInputStream(bytes));
                bytes = Okio.buffer(source).readString(Charset.forName(encoding)).getBytes();
            }

            response = response.newBuilder()
                    .body(ResponseBody.create(MediaType.parse("text/html"), bytes))
                    .removeHeader("Content-Encoding")
                    .build();
        }
        return response;
    }

    private boolean inGZIPFormat(byte[] bytes) {
        int header = ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
        return header == GZIPInputStream.GZIP_MAGIC;
    }
}
