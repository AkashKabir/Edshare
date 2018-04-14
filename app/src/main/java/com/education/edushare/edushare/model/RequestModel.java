package com.education.edushare.edushare.model;

/**
 * Created by Akash Kabir on 10-12-2017.
 */

public class RequestModel {
    String requid;
    String reqpuid;
    String reqname;
    String name;
    String publickey;
    public String getStatus() {
        return status;
    }

    String status;

    public String getRequid() {
        return requid;
    }

    public void setRequid(String requid) {
        this.requid = requid;
    }

    public String getReqpuid() {
        return reqpuid;
    }

    public void setReqpuid(String reqpuid) {
        this.reqpuid = reqpuid;
    }

    public String getReqname() {
        return reqname;
    }

    public void setReqname(String reqname) {
        this.reqname = reqname;
    }

    public String getPublickey() {
        return publickey;
    }

    public RequestModel(String requid, String reqpuid, String reqname, String status, String name, String publickey) {

        this.requid = requid;
        this.reqpuid = reqpuid;
        this.name=name;
        this.status=status;
        this.reqname = reqname;
        this.publickey=publickey;

    }

    public String getName() {
        return name;
    }
}
