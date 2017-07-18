package orbital.raspberry.neighber;

public class ChatItem {

    private String othername;
    private String otherimguri;
    private String itemname;
    private String chatroomid;
    private int posttype;

    public ChatItem(){}

    public ChatItem(String chatroomid, String itemname, String othername, String otherimguri, int posttype){

        this.chatroomid = chatroomid;
        this.itemname = itemname;
        this.otherimguri = otherimguri;
        this.othername = othername;
        this.posttype = posttype;

    }

    public String getChatroomid(){
        return chatroomid;
    }

    public String getItemname(){
        return itemname;
    }

    public String getOthername(){
        return othername;
    }

    public String getOtherimguri(){
        return otherimguri;
    }

    public int getPosttype(){
        return posttype;
    }

}
