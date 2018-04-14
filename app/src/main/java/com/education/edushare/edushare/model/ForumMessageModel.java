package com.education.edushare.edushare.model;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class ForumMessageModel {
    String uid,message,username,uidreply,repmsg;
    int likes;
    int rep;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUidreply() {
        return uidreply;
    }

    public void setUidreply(String uidreply) {
        this.uidreply = uidreply;
    }

    public String getRepmsg() {
        return repmsg;
    }

    public void setRepmsg(String repmsg) {
        this.repmsg = repmsg;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public ForumMessageModel(String uid, String message, String username, int likes, int rep, String uidreply, String repmsg) {

        this.uid = uid;
        this.message = message;
        this.username = username;
        this.likes = likes;
        this.rep=rep;
        this.uidreply=uidreply;
        this.repmsg=repmsg;
    }
}
