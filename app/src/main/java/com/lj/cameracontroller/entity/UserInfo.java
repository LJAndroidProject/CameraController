package com.lj.cameracontroller.entity;

import java.io.Serializable;

/**
 * Created by 刘劲松 on 2017/7/22.
 */

public class UserInfo implements Serializable {

    private  int code=0;
    private String message="";
    private userModel result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public userModel getResult() {
        return result;
    }

    public void setResult(userModel result) {
        this.result = result;
    }

    public static  class userModel implements Serializable{
        private  String user_id="";
        private String access_token="";

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }



}
