package com.app.sscpinstagram.Model;

public class Notification
{
    private String userid,postid,text;
    private boolean isPost;

    public Notification(String userid, String postid, boolean isPost,String text) {
        this.userid = userid;
        this.postid = postid;
        this.isPost = isPost;
        this.text=text;
    }
    public Notification(){}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
