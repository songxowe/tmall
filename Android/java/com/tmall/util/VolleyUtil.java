package com.tmall.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * 创建 Volley 单例
 * 使用volley时,必须要创建一个请求队列RequestQueue
 * 使用请求队列的最佳方式就是将它做成一个单例,整个app使用此个请求队列
 */
public class VolleyUtil {
    // 设置 Volley 标记
    public static final String TAG = VolleyUtil.class.getSimpleName();
    // 云端服务器的基本路径 http://ip:port/projectName
    public static final String BASE_URL = "http://192.168.43.39:8086/tmall/";

    private static VolleyUtil mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private Context context;

    /**
     * 构造方法 (不能直接new)
     *
     * @param context
     */
    private VolleyUtil(Context context) {
        this.context = context;
        this.mRequestQueue = getRequestQueue();
        this.mImageLoader = new ImageLoader(this.mRequestQueue,
                new ImageLoader.ImageCache() {
                    // 设置缓存图片的最大值:10MiB
                    private final int cacheSize = 10 * 1024 * 1024;
                    // 最近最少使用算法
                    // LruCache:主要算法原理是把最近使用的对象用强引用存储在 LinkedHashMap 中，
                    // 并且把最近最少使用的对象在缓存值达到预设定值之前从内存中移除
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<>(cacheSize);

                    @Override
                    public Bitmap getBitmap(String url) {
                        // 从缓存中根据url获得图片
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        // 缓存对应url的图片
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * 通过此方法获得Volley单例,且同步锁
     *
     * @param context
     * @return
     */
    public synchronized static VolleyUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtil(context);
        }
        return mInstance;
    }

    /**
     * 获得请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * 图片加载
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    /**
     * 把 request 对象加入到 请求队列,且设置标记
     *
     * @param request
     * @param tag
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        // set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        this.getRequestQueue().add(request);
    }

    /**
     * 把 request 对象加入到 请求队列
     *
     * @param request
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        this.getRequestQueue().add(request);
    }

    /**
     * 取消指定标记的 request 对象
     *
     * @param tag
     */
    public void cancelRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * 获得云端服务器的绝对路径
     *
     * @param relativeUrl 相对路径
     * @return http://ip:port/projectName/xxxServlet
     */
    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}