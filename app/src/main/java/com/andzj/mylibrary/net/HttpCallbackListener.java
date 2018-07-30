package com.andzj.mylibrary.net;

/**
 * Created by zj on 2016/11/15.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
