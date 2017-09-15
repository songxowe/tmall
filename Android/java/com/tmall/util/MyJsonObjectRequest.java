package com.tmall.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 自定义 JsonObject 请求类 (提交表单参数)
 */
public class MyJsonObjectRequest extends Request<JSONObject> {
    private Listener<JSONObject> listener;
    private Map<String, String> params;

    /**
     * 构造方法 (默认请求方式 GET)
     *
     * @param url
     * @param params           表单参数
     * @param responseListener
     * @param errorListener
     */
    public MyJsonObjectRequest(String url, Map<String, String> params, Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    /**
     * 构造方法
     *
     * @param method           请求方式
     * @param url
     * @param params           表单参数
     * @param responseListener
     * @param errorListener
     */
    public MyJsonObjectRequest(int method, String url, Map<String, String> params, Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    /**
     * 获得表单参数
     *
     * @return 表单参数
     * @throws AuthFailureError
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    /**
     * 解析网络响应的数据
     *
     * @param response
     * @return
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    /**
     * 传递响应
     *
     * @param response
     */
    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
