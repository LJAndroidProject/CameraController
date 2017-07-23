package com.lj.cameracontroller.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/7/23.
 */

public class DeviceListEntity implements Serializable {
    private int code = 0;
    private String message = "";
    private List<DeviceEntity> result = new ArrayList<DeviceEntity>();


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

    public List<DeviceEntity> getResult() {
        return result;
    }

    public void setResult(List<DeviceEntity> result) {
        this.result = result;
    }


    public static class DeviceEntity implements Serializable {
        private String ipc_sn = "";//
        private String ipc_name = "";//
        private String ipc_pic = "";//
        private String ipc_remarks = "";//
        private String ipc_status = "";//1在线，0离线
        private String gds_code = "";//供电所编号
        private String gds_name = "";//供电所名称
        private String pdf_code = "";//配电房编号
        private String pdf_name = "";//配电房名称

        public String getIpc_sn() {
            return ipc_sn;
        }

        public void setIpc_sn(String ipc_sn) {
            this.ipc_sn = ipc_sn;
        }

        public String getIpc_name() {
            return ipc_name;
        }

        public void setIpc_name(String ipc_name) {
            this.ipc_name = ipc_name;
        }

        public String getIpc_pic() {
            return ipc_pic;
        }

        public void setIpc_pic(String ipc_pic) {
            this.ipc_pic = ipc_pic;
        }

        public String getIpc_remarks() {
            return ipc_remarks;
        }

        public void setIpc_remarks(String ipc_remarks) {
            this.ipc_remarks = ipc_remarks;
        }

        public String getIpc_status() {
            return ipc_status;
        }

        public void setIpc_status(String ipc_status) {
            this.ipc_status = ipc_status;
        }

        public String getGds_code() {
            return gds_code;
        }

        public void setGds_code(String gds_code) {
            this.gds_code = gds_code;
        }

        public String getGds_name() {
            return gds_name;
        }

        public void setGds_name(String gds_name) {
            this.gds_name = gds_name;
        }

        public String getPdf_code() {
            return pdf_code;
        }

        public void setPdf_code(String pdf_code) {
            this.pdf_code = pdf_code;
        }

        public String getPdf_name() {
            return pdf_name;
        }

        public void setPdf_name(String pdf_name) {
            this.pdf_name = pdf_name;
        }


    }
}
