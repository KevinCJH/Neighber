package orbital.raspberry.neighber;


import java.util.HashMap;
import java.util.Map;

public class Post {

    private String postid;
    private String userid;
    private String itemname;
    private String postdesc;
    private long timestamp;
    //Post type -  1:request 2:offer
    private int posttype;
    //For list view
    private String datetime;

    public Post(){}

    public Post(String postid, String userid, String itemname, String postdesc, int posttype){
        this.postid = postid;
        this.userid = userid;
        this.itemname = itemname;
        this.postdesc = postdesc;
        this.posttype = posttype;
    }

    public String getPostid() {
        return postid;
    }

    public String getUserid() {
        return userid;
    }

    public int getPosttype(){ return posttype; }

    public String getItemname() {
        return itemname;
    }

    public String getPostdesc() {
        return postdesc;
    }

    public long getTimestamp() { return timestamp; }

    //For listview

    public String getDatetime(){ return datetime; }

    public void setDatetime(String datetime){
        this.datetime = datetime;
    }

}
