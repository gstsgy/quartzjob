package com.sr.suray.quartzjob.bean;

import org.springframework.stereotype.Component;

/**
 * @ClassName Response
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/4 下午5:46
 **/
@Component
public class ResponseBean {
    private int code;

    private String message;

    private Object data;

    public ResponseBean() {
    }

    public ResponseBean(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseBean getSuccess(Object data){
        this.code = 0;
        this.message = "success";
        this.data = data;
        return this;
    }

    public ResponseBean getError(String message){
        this.code = -1;
        this.message =message;
        this.data = null;
        return this;
    }
}
