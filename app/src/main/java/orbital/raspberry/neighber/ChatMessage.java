package orbital.raspberry.neighber;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String messageid;
    private String userid;

    public ChatMessage(String messageid, String messageText, String messageUser, String userid) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageid = messageid;
        this.userid = userid;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

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

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageid(){
        return messageid;
    }

    public String getUserid(){
        return userid;
    }

}
