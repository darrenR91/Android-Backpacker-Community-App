package com.k00140908.darren.the88days.Model;

/**
 * Created by darre on 10/04/2016.
 */
public class Message {

    String msg;
    String backpackerReceiver;
    String backpackerSender;
    long sentAt;

    public Message(String msg, String backpackerReceiver, String backpackerSender, long sentAt) {
        this.msg = msg;
        this.backpackerReceiver = backpackerReceiver;
        this.backpackerSender = backpackerSender;
        this.sentAt = sentAt;
    }
    public Message()
    {
        // default constructor
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBackpackerReceiver() {
        return backpackerReceiver;
    }

    public void setBackpackerReceiver(String backpackerReceiver) {
        this.backpackerReceiver = backpackerReceiver;
    }

    public String getBackpackerSender() {
        return backpackerSender;
    }

    public void setBackpackerSender(String backpackerSender) {
        this.backpackerSender = backpackerSender;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }
}
