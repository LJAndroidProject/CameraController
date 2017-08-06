package com.lj.cameracontroller.entity;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/8/6.
 */

public class UpdateEntity implements Serializable{

    private int code=0;
    private String message = "";
    private  dataEntity result=null;

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

    public dataEntity getResult() {
        return result;
    }

    public void setResult(dataEntity result) {
        this.result = result;
    }


    public class dataEntity implements Serializable{
        private int type=-1;                //1=android  2=ios 3=pc
        private int version=0;             //最新版本号
        private  String version_name="";  //最新版本号名称
        private int version_type=0;    //版本类型，1正式，0测试
        private String description=""; //新版本描述
        private String path="";       //程序下载路径
        private int force_update=-1; //强制更新，1是，0否
        private String publish_date="";//发布时间

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public int getVersion_type() {
            return version_type;
        }

        public void setVersion_type(int version_type) {
            this.version_type = version_type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getForce_update() {
            return force_update;
        }

        public void setForce_update(int force_update) {
            this.force_update = force_update;
        }

        public String getPublish_date() {
            return publish_date;
        }

        public void setPublish_date(String publish_date) {
            this.publish_date = publish_date;
        }


    }
}

