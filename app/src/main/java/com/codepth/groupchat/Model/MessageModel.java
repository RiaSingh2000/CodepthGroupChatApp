package com.codepth.groupchat.Model;

public class MessageModel {
    String msg,imgUri,sender,senderName,timestamp;

    public MessageModel(){}



    public MessageModel(String imgUri, String msg, String sender, String senderName,String timestamp) {
        this.msg = msg;
        this.imgUri = imgUri;
        this.sender = sender;
        this.senderName=senderName;
        this.timestamp=timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public String getImgUri() {
        return imgUri;
    }

    public String getSender() {
        return sender;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
