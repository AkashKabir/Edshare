package com.education.edushare.edushare.model;

import java.io.Serializable;

/**
 * Created by Akash Kabir on 21-11-2017.
 */

public class ChatContactsModel implements Serializable {
    String name;
    String type;
    String uid;
    String publickeysu;

    public ChatContactsModel(String name, String type, String uid, String publickeysu, String aeskey) {
        this.name = name;
        this.type = type;
        this.uid = uid;
        this.publickeysu = publickeysu;
        this.aeskey = aeskey;
    }

    public String getPublickeysu() {

        return publickeysu;
    }

    public void setPublickeysu(String publickeysu) {
        this.publickeysu = publickeysu;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    String aeskey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
