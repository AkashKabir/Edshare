package com.education.edushare.edushare.model;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class MessageModel {
    private String UserUid,msgTxt,msgType,msgStatus,msgUser,msgID,imgUrl;

    public MessageModel(String userUid, String msgTxt, String msgType, String msgStatus, String msgUser, String msgID, String imgUrl) {
        this.UserUid = userUid;
        this.msgTxt = msgTxt;
        this.msgType = msgType;
        this.msgStatus = msgStatus;
        this.msgUser = msgUser;
        this.msgID = msgID;
        this.imgUrl = imgUrl;

    }

    public MessageModel(){

    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getMsgTxt() {
        return msgTxt;
    }

    public void setMsgTxt(String msgTxt) {
        this.msgTxt = msgTxt;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
