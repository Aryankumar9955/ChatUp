package com.example.chatup.Chat;

public class ChatObject {

    String chatID;
    String receiverId;
    String imageURL;
    String date;
    String lastMessage;
    String username;
    String mobile;

    public ChatObject() {
    }

    public ChatObject(String chatID, String receiverId, String imageURL, String date, String lastMessage,String username,String mobile) {
        this.chatID = chatID;
        this.receiverId = receiverId;
        this.imageURL = imageURL;
        this.date = date;
        this.lastMessage = lastMessage;
        this.username = username;
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}