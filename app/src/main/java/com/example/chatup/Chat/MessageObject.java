package com.example.chatup.Chat;

public class MessageObject {

    String username,messageId,message,receiverId,time,imageURL;

    public MessageObject() {
    }

    public MessageObject(String username,String messageId, String message, String receiverId, String time,String imageURL){
        this.username = username;
        this.messageId = messageId;
        this.message = message;
        this.receiverId = receiverId;
        this.time = time;
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
