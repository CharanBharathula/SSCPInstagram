package com.app.sscpinstagram.Model;

public class Comment
{
    String publisher,comment;

    public Comment(String comment, String publisher) {
        this.publisher = publisher;
        this.comment = comment;
    }
    Comment(){}

    public String getpublisher() {
        return publisher;
    }

    public void setpublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
