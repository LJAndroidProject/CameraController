package com.lj.cameracontroller.entity;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/9/17.
 */

public class ControlEntity implements Serializable{
    public int state=0;//状态 1成功、-1失败
    public String msg="";//信息
}
