package com.app.sscpinstagram.Model;

public class Story
{
    String imageurl,storyid,userid;
    long timestart,timeEnd;

    public Story(String imageurl, long timestart, long timeEnd, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeEnd = timeEnd;
        this.storyid = storyid;
        this.userid = userid;
    }
    public Story(){}
    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String usreid) {
        this.userid = usreid;
    }

}
