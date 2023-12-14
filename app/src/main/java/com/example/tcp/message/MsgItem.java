package com.example.tcp.message;

public class MsgItem {
    private String msgName;
    private String msgContent;
    private String msgTime;

    public MsgItem(String msgName, String msgContent, String msgTime) {
        this.msgName = msgName;
        this.msgContent = msgContent;
        this.msgTime = msgTime;
    }

    public MsgItem(String[] msgArray) {
        this.msgName = msgArray[0];
        this.msgContent = msgArray[1];
        this.msgTime = msgArray[2];
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
}
