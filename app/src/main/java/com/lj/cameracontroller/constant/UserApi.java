package com.lj.cameracontroller.constant;

import java.util.HashMap;

/**
 * Created by 刘劲松 on 2017/7/10.
 * 用户信息相关的API定义
 */

public class UserApi {

    public static final HashMap<String ,String> map=new HashMap<String,String >();
    // 统一服务器
    public static final String URL = "http://x03.gigetto.cn:803/api/";


    //登录接口
    public static final String LOGIN =URL+ "api/mobile/user_info.aspx";
    public static final String LOGIN_KEY_NO = "PhoneNum";
    public static final String LOGIN_KEY_MD5_PWD = "PassWord";

    //注销接口
    public static final String LOGIN_OUT = "User/LoginOut";

    //摄像头登录信息接口
    public static final String IPC_LOGIN_INFO = "/api/mobile/ipc_info.aspx";
}
