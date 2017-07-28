package orbital.raspberry.neighber;

public class ChatItem {

    private String othername;
    private String otherimguri;
    private String itemname;
    private String chatroomid;
    private int posttype;
    private String lastmsg;
    private String offerid, postid;

    public ChatItem(){}

    public ChatItem(String chatroomid, String itemname, String othername, String otherimguri, int posttype, String lastmsg, String postid, String offerid){

        this.chatroomid = chatroomid;
        this.itemname = itemname;
        this.otherimguri = otherimguri;
        this.othername = othername;
        this.posttype = posttype;
        this.lastmsg = lastmsg;
        this.offerid = offerid;
        this.postid = postid;

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

    public String getLastmsg(){
        return lastmsg;
    }

    public String getOfferid(){
        return offerid;
    }

    public String getPostid(){
        return postid;
    }

}
