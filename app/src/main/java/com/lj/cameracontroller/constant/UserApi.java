package com.lj.cameracontroller.constant;

import java.io.File;
import java.util.HashMap;

/**
 * Created by 刘劲松 on 2017/7/10.
 * 用户信息相关的API定义
 */

public class UserApi {

    public static final HashMap<String ,String> map=new HashMap<String,String >();
    /**
     * Bugly AppId
     */
    public static final String Bugly_ID = "eb5cdd21e0";
    // 统一服务器
//    public static final String URL = "http://dljk.st.somantou365.online/";//测试服务器
    public static final String URL = "http://120.78.77.26:9001/";  //正式服务器


    //登录接口
    public static final String LOGIN =URL+ "api/mt/user_login.aspx";
    public static final String LOGIN_USERNAME = "login_userName"; //用户账号
    public static final String LOGIN_USER_PWD = "PassWord";  //用户密码
    public static final  String USERINFOR="userInfor";  //用户信息
    public static final String ISFORGETPWD="IsForgetpwd"; //是否记住密码
    public static int notificationId = 0; //nofification唯一标识
    public static File file=null; //更新文件


    //注销接口
    public static final String LOGIN_OUT = "User/LoginOut";

    //摄像头登录信息接口
    public static final String IPC_LOGIN_INFO = URL+"/api/mt/ipc_info.aspx";

    /**设备列表接口**/
    public static final String IPC_INFO=URL+"api/mt/ipc_info.aspx";

    public static final  String MAINWEBURL = URL+"/apps/mt/index.aspx";

    public static final  String SETTINGWEBURL = URL+"/apps/mt/info.aspx";

    //版本更新
    public static  final  String UPDATA=URL+"api/mt/app_info.aspx";

    //机器人控制接口
    public static  final  String CONTROR=URL+"api/robot/control.aspx";



}
