package com.ttl.ritz7chat;

public class modeGroupChat {

    String sender,message, date, timestamp, time, type;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public modeGroupChat(String sender, String message, String date, String timestamp, String time, String type) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.timestamp = timestamp;
        this.time = time;
        this.type = type;
    }

    public modeGroupChat() {
    }
}
