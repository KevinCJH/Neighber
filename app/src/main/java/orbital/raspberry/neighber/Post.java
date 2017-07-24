package orbital.raspberry.neighber;


import java.util.HashMap;
import java.util.Map;

public class Post {

    private String postid;
    private String userid;
    private String itemname;
    private String postdesc;
    private int recordcount;
    private long timestamp;
    //Status for records, always start from 1
    private int status;
    //Upon agreement, the other party id is recorded here
    private String otherid;
    private String agreementid;
    private String othername;
    //Post type -  1:request 2:offer
    private int posttype;
    //For list view
    private String datetime;
    private int rated;
    private String chatid;
    private String otherimg;
    private int category;

    public Post(){}

    public Post(String postid, String userid, String itemname, String postdesc, int posttype, int category){
        this.postid = postid;
        this.userid = userid;
        this.itemname = itemname;
        this.postdesc = postdesc;
        this.posttype = posttype;
        recordcount = 0;
        status = 1;
        rated = 0;
        this.category = category;
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

    //For Records

    public void setRecordcount(int recordcount){
        this.recordcount = recordcount;
    }

    public int getRecordcount(){
        return recordcount;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    public void setOtherid(String otherid){
        this.otherid = otherid;
    }

    public String getOtherid(){
        return otherid;
    }

    public String getOthername(){ return othername; }

    public void setAgreementid(String agreementid){
        this.agreementid = agreementid;
    }

    public String getAgreementid(){
        return agreementid;
    }

    public int getRated(){
        return rated;
    }

    public String getChatid(){
        return chatid;
    }

    public String getOtherimg(){
        return otherimg;
    }

    public int getCategory(){
        return category;
    }

}
