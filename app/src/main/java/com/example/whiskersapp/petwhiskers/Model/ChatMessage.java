package com.example.whiskersapp.petwhiskers.Model;

import java.util.Date;

public class ChatMessage {
    private String id;
    private String messageText;
    private String messageUser; //User id
    private String messageName; //User Name
    private String userChat_id;
    private String messageTime;

    public ChatMessage(String id, String messageText, String messageUser, String messageName) {
        this.id = id;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageName = messageName;
    }

    public ChatMessage(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserChat_id() {
        return userChat_id;
    }

    public void setUserChat_id(String userChat_id) {
        this.userChat_id = userChat_id;
    }
}
