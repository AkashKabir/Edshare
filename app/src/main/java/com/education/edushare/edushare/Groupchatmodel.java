package com.education.edushare.edushare;

import java.io.Serializable;

/**
 * Created by Akash Kabir on 25-12-2017.
 */

public class Groupchatmodel implements Serializable{
    String senname,senuid,msgtxt;

    public Groupchatmodel(){}

    public String getSenname() {
        return senname;
    }

    public void setSenname(String senname) {
        this.senname = senname;
    }

    public String getSenuid() {
        return senuid;
    }

    public void setSenuid(String senuid) {
        this.senuid = senuid;
    }

    public String getMsgtxt() {
        return msgtxt;
    }

    public void setMsgtxt(String msgtxt) {
        this.msgtxt = msgtxt;
    }

    public Groupchatmodel(String senname, String senuid, String msgtxt) {

        this.senname = senname;
        this.senuid = senuid;
        this.msgtxt = msgtxt;
    }
}
