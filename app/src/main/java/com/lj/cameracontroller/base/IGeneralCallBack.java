package com.lj.cameracontroller.base;

/**
 * Created by 刘劲松 on 2017/6/29.
 * 通用泛型回调接口，用于异步执行回调
 */

public interface IGeneralCallBack {
    public <T> void OnResult(T resule);
}
