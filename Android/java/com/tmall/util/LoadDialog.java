package com.tmall.util;

import java.lang.reflect.Method;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * 封装ProgressDialog对话框
 *
 * @author Administrator
 *
 */
public class LoadDialog extends ProgressDialog {
    private String title = "进度对话框";
    private String message = "文件上传中。。。";

    public LoadDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 用默认的标题和内容来创建对话框
     *
     * @param context
     */
    public LoadDialog(Context context) {
        super(context);
        initDialog();
    }

    /**
     * 用指定的标题和内容来创建对话框
     *
     * @param context
     * @param title
     * @param message
     */
    public LoadDialog(Context context, String title, String message) {
        super(context);
        if (title != null) {
            this.title = title;
        }
        if (message != null) {
            this.message = message;
        }
        initDialog();
    }

    /**
     * 初始化对话框参数，默认对话框不可以取消
     */
    public void initDialog() {
        setTitle(title);
        setMessage(message);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);
    }

    /**
     * 打开对话框，设置回调方法，传递需要执行业务方法的类模板，方法名和参数列表
     *
     * @param callback
     *            回调方法，该方法在对话框关闭后回调，并获取返回的数据
     * @param serviceClass
     *            执行业务方法的类模板
     * @param method
     *            执行业务方法的方法名
     * @param params
     *            执行业务方法的参数列表
     */
    public void execute(Callback callback, Class serviceClass, String method,
                        Object... params) {
        super.show();
        ServiceAsyncTask task = new ServiceAsyncTask(callback, serviceClass,
                method);
        task.execute(params);
    }

    /**
     * 回调方法的接口
     *
     * @author Administrator
     *
     */
    public interface Callback {
        public void getResult(Object obj);
    }

    /**
     * 与远程服务通信的线程类
     *
     * @author Administrator
     *
     */
    private class ServiceAsyncTask extends AsyncTask<Object, Object, Object> {
        private Class serviceClass;
        private String method;
        private Callback callback;

        public ServiceAsyncTask(Callback callback, Class serviceClass,
                               String method) {
            this.callback = callback;
            this.serviceClass = serviceClass;
            this.method = method;
        }

        @Override
        protected Object doInBackground(Object... params) {
            Object resultObj = null;
            try {
                Object obj = serviceClass.newInstance();
                Class[] paramTypes = new Class[params.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramTypes[i] = params[i].getClass();
                }
                Method m = serviceClass.getMethod(method, paramTypes);
                resultObj = m.invoke(obj, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoadDialog.this.cancel();
            return resultObj;
        }

        @Override
        protected void onPostExecute(Object resultObj) {
            super.onPostExecute(resultObj);
            callback.getResult(resultObj);
        }
    }
}
