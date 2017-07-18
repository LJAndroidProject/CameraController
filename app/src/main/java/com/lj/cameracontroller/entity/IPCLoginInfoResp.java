package com.lj.cameracontroller.entity;

/**
 * Created by ljw on 2017/7/17 0017.
 */

public class IPCLoginInfoResp {
    /**设备号*/
    public String ipc_sn;
    /**摄像头登录用户名*/
    public String user_name;
    /**摄像头登录密码*/
    public String user_password;
    /**摄像头登录服务器地址*/
    public String server_address;
    /**摄像头登录服务器端口*/
    public String server_port;

    /**设备号*/
    public String getIpc_sn() {
        return ipc_sn;
    }
    /**设备号*/
    public void setIpc_sn(String ipc_sn) {
        this.ipc_sn = ipc_sn;
    }

    /**摄像头登录用户名*/
    public String getUser_name() {
        return user_name;
    }
    /**摄像头登录用户名*/
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**摄像头登录密码*/
    public String getUser_password() {
        return user_password;
    }
    /**摄像头登录密码*/
    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    /**摄像头登录服务器地址*/
    public String getServer_address() {
        return server_address;
    }
    /**摄像头登录服务器地址*/
    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    /**摄像头登录服务器端口*/
    public String getServer_port() {
        return server_port;
    }
    /**摄像头登录服务器端口*/
    public void setServer_port(String server_port) {
        this.server_port = server_port;
    }
}
