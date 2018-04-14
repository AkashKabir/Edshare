package com.education.edushare.edushare.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Akash Kabir on 21-11-2017.
 */

public class DashBoardModel implements Serializable{
    String pname,pdetails,userid,puid,username,notes,org,teamsize;

    public DashBoardModel(String pname, String pdetails, String userid, String puid, String username, String notes, String org, String teamsize, HashMap<String, String> nametag, HashMap<String, String> nameref, HashMap<String, String> members) {
        this.pname = pname;
        this.pdetails = pdetails;
        this.userid = userid;
        this.puid = puid;
        this.username = username;
        this.notes = notes;
        this.org = org;
        this.teamsize = teamsize;
        this.nametag = nametag;
        this.nameref = nameref;
        this.members=members;
    }

    public String getPname() {

        return pname;
    }

    public String getPdetails() {
        return pdetails;
    }

    public String getUserid() {
        return userid;
    }

    public String getPuid() {
        return puid;
    }

    public String getUsername() {
        return username;
    }

    public String getNotes() {
        return notes;
    }

    public String getOrg() {
        return org;
    }

    public String getTeamsize() {
        return teamsize;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public HashMap<String, String> getNametag() {
        return nametag;
    }

    public HashMap<String, String> getNameref() {
        return nameref;
    }

    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    HashMap<String,String> members= new HashMap<>();
    public String toS(){
        String string="pname="+pname+"\npdetails="+pdetails+"\nuserid="+userid+"\npuid="+puid+"\nusername="+username+"\nnotes="+notes+"\norg="+org+"\nteamsize="+teamsize+"\nnameref+"+nameref.toString()+"\nnametag="+nametag.toString();
        return  string;
    }

}
