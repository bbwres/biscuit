package cn.bbwres.biscuit.utils.okhttp.intercept;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * OkHttp 日志拦截器：打印请求参数和响应参数
 *
 * @author zlf
 * @version 1.0
 * @since 2026-01-04  17:29
 */
@Slf4j
public class HttpLogInterceptor implements OrderInterceptor {
    /**
     * 日志标签，便于筛选
     */
    private final String TAG;
    /**
     * 是否开启日志（生产环境可关闭）
     */
    private final boolean enableLog;

    // 构造方法：支持自定义标签和调试开关
    public HttpLogInterceptor(String tag, boolean enableLog) {
        this.TAG = tag == null || tag.trim().isEmpty() ? "OkHttp-Log" : tag;
        this.enableLog = enableLog;
    }

    /**
     * 拦截处理
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        // 若关闭调试模式，直接执行请求，不打印日志
        if (!enableLog) {
            return chain.proceed(chain.request());
        }

        // 记录请求开始时间（用于计算耗时）
        long startTime = System.nanoTime();
        Request request = chain.request();
        Response response;

        try {
            // 1. 打印请求信息
            printRequestLog(request);
            // 2. 打印响应信息
            response = printResponseLog(request, chain.proceed(request), startTime);
        } catch (Exception e) {
            log.warn("{}-> 请求/响应异常：{}", TAG, e.getMessage());
            throw e;
        }

        return response;
    }

    /**
     * 打印请求参数
     *
     * @param request 请求对象
     */
    private void printRequestLog(Request request) {
        try {
            String method = request.method();
            HttpUrl url = request.url();
            Headers headers = request.headers();
            RequestBody requestBody = request.body();

            // 拼接请求基本信息
            StringBuilder requestLog = new StringBuilder();
            requestLog.append(TAG).append(" -> 【请求开始】")
                    .append("\n请求方法：").append(method)
                    .append("\n请求URL：").append(url)
                    .append("\n请求头：");

            // 打印所有请求头
            for (int i = 0; i < headers.size(); i++) {
                String name = headers.name(i);
                String value = headers.value(i);
                requestLog.append("\n  - ").append(name).append("：").append(value);
            }

            // 打印请求体（仅非 GET 方法有请求体）
            if (requestBody != null && !"GET".equalsIgnoreCase(method)) {
                if (isJsonMediaType(requestBody.contentType())) {
                    // JSON 类型：打印请求体
                    requestLog.append("\n请求体：").append(getRequestBodyString(requestBody));
                } else {
                    // 非 JSON 类型：跳过请求体打印，可添加提示
                    requestLog.append("\n请求体：【非 JSON 类型，忽略打印】");
                }
            }

            requestLog.append("\n").append(TAG).append(" -> 【请求结束】");
            log.info(requestLog.toString());
        } catch (Exception e) {
            log.warn(" {} -> 打印请求日志异常：{}", TAG, e.getMessage());
        }
    }

    /**
     * 打印响应参数
     *
     * @param request   请求对象
     * @param response  响应对象
     * @param startTime 请求开始时间（用于计算耗时）
     */
    private Response printResponseLog(Request request, Response response, long startTime) {
        try {
            // 计算请求耗时
            long endTime = System.nanoTime();
            long duration = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);

            ResponseBody responseBody = response.body();


            // 拼接响应基本信息
            StringBuilder responseLog = new StringBuilder();
            responseLog.append(TAG).append(" -> 【响应开始】")
                    .append("\n请求方法：").append(request.method())
                    .append("\n请求URL：").append(request.url())
                    .append("\n响应状态码：").append(response.code())
                    .append("（").append(response.message()).append("）")
                    .append("\n请求耗时：").append(duration).append(" 毫秒")
                    .append("\n响应头：");

            // 打印所有响应头
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                String name = headers.name(i);
                String value = headers.value(i);
                responseLog.append("\n  - ").append(name).append("：").append(value);
            }


            // 1. 核心判断：响应体存在 + JSON 类型，才读取响应体
            if (responseBody != null) {
                MediaType mediaType = responseBody.contentType();
                if (isJsonMediaType(mediaType)) {
                    // JSON 类型：复制响应体并读取（避免消耗原始响应体）
                    Buffer buffer = new Buffer();
                    responseBody.source().readAll(buffer);
                    String responseBodyStr = buffer.readString(StandardCharsets.UTF_8);
                    // 重新构建响应体，供业务层读取
                    response = response.newBuilder()
                            .body(ResponseBody.create( responseBodyStr,mediaType))
                            .build();
                    responseLog.append("\n响应体：").append(responseBodyStr);
                }
            }

            responseLog.append("\n").append(TAG).append(" -> 【响应结束】");
            log.info(responseLog.toString());
        } catch (Exception e) {
            log.warn(" {} -> 打印响应日志异常：{}", TAG, e.getMessage());
        }
        return response;
    }

    /**
     * 获取请求体字符串（解决请求体只能读取一次的问题）
     *
     * @param requestBody 请求体
     * @return 请求体文本
     * @throws IOException 读取异常
     */
    @Nullable
    private String getRequestBodyString(RequestBody requestBody) throws IOException {
        // 复制请求体数据
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readString(StandardCharsets.UTF_8);
    }

    /**
     * 判断是否为 JSON 类型（基于 Media Type）
     *
     * @param mediaType 媒体类型（请求体/响应体的 Content-Type）
     * @return true=JSON 类型，false=非 JSON 类型
     */
    private boolean isJsonMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        // 转为小写，避免大小写不一致导致判断失效
        String mediaTypeStr = mediaType.toString().toLowerCase();
        // 核心判断：包含 application/json 关键字即视为 JSON 类型
        return mediaTypeStr.contains("application/json");
    }

    @Override
    public int getOrder() {
        return 99;
    }
}