package com.joey.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.joey.utils.LogUtils;


/**
 * Created by Administrator on 2016/7/22.
 */
public abstract class ResponseListener<T> implements Response.Listener<String> {


    private ResponseHandler handler;

    public ResponseListener(ResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onResponse(String s) {
        convert(s);
    }

    private void convert(String s) {
        LogUtils.a(getClass().getName(), "convert obj = " + s);
        try {
            JSONObject obj = JSON.parseObject(s);
            int status = obj.getInteger("resultCode");
            String msg = obj.getString("resultMessage");
            if (0 == status || 1 == status) {
                T t = (T) obj.get("result");
                LogUtils.a(getClass().getName(), "result = " + t.toString());
                onSuccess(t, status);
                if (handler != null)
                    handler.onSuccess();
                return;
            }
            ResponseError error = new ResponseError(status, msg);
            onError(error, status);
            if (handler != null)
                handler.onError();
            return;
        } catch (Exception e) {
            ResponseError error = new ResponseError(ResponseError.ERROR_BY_PARSE, e.getMessage());
            error.setJson(s);
            onError(error, -1);
            if (handler != null)
                handler.onError();
            LogUtils.e(getClass().getName(), "error =" + e.getMessage());
            return;
        }
    }

    public void onLoading(long total, long current, boolean isUploading) {

    }

    public abstract void onSuccess(T t, int status);

    public abstract void onError(ResponseError error, int status);

}
