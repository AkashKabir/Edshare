package com.education.edushare.edushare.model;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class CommunityModel {
    String name;
    String TagArray[];
    String date;
    String uid;
    int upvotes,stars;
    int upvote_Status, star_status;

    public CommunityModel(String name, String[] tagArray, String date, String uid, int upvotes, int stars, int upvote_Status, int star_status) {
        this.name = name;
        TagArray = tagArray;
        this.date = date;
        this.uid = uid;
        this.upvotes = upvotes;
        this.stars = stars;
        this.upvote_Status = upvote_Status;
        this.star_status = star_status;
    }

    public String getName() {
        return name;
    }

    public String[] getTagArray() {
        return TagArray;
    }

    public void setTagArray(String[] tagArray) {
        TagArray = tagArray;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getUpvote_Status() {
        return upvote_Status;
    }

    public void setUpvote_Status(int upvote_Status) {
        this.upvote_Status = upvote_Status;
    }

    public int getStar_status() {
        return star_status;
    }

    public void setStar_status(int star_status) {
        this.star_status = star_status;
    }

    public void setName(String name) {
        this.name = name;
    }
}
