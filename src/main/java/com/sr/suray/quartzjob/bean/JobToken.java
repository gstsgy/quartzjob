package com.sr.suray.quartzjob.bean;

public class JobToken {
    private String userName;
    private Long msgTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Long msgTime) {
        this.msgTime = msgTime;
    }

    public JobToken() {
    }

    public JobToken(String userName, Long msgTime) {
        this.userName = userName;
        this.msgTime = msgTime;
    }

    @Override
    public String toString() {
        return "JobToken{" +
                "userName='" + userName + '\'' +
                ", msgTime=" + msgTime +
                '}';
    }
}
