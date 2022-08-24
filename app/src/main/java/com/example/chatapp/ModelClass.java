package com.example.chatapp;

public class ModelClass {
    String Message;
    String From;

    public ModelClass() {

    }

    public ModelClass(String message, String from) {
        this.Message = message;
        this.From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        this.From = from;
    }
}
