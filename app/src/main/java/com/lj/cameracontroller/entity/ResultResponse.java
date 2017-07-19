package com.lj.cameracontroller.entity;

import java.io.Serializable;

/**
 * Created by ljw on 2017/7/18 0018.
 */

public class ResultResponse implements Serializable {
    /**数据返回*/
    public String result;

    /**返回消息*/
    public String message;

    /**状态码*/
    public String code;

    /**数据返回*/
    public String getResult() {
        return result;
    }

    /**数据返回*/
    public void setResult(String result) {
        this.result = result;
    }

    /**状态码*/
    public String getCode() {
        return code;
    }

    /**状态码*/
    public void setCode(String code) {
        this.code = code;
    }

    /**返回消息*/
    public String getMessage() {
        return message;
    }

    /**返回消息*/
    public void setMessage(String message) {
        this.message = message;
    }
}
