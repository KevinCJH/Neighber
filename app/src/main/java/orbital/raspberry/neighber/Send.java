package orbital.raspberry.neighber;


public class Send {

    //1 is request, 2 is offer
    private int sendtype;
    private String recordid;
    private String postid;
    private String itemname;
    //Owner of this offer
    private String ownerid;
    private String ownername;
    //Target of this offer
    private String targetid;
    private String targetname;
    private String requestdesc;
    private String agreementdesc;
    private String returnagreementdesc;
    private String datetime;
    private long timestamp;
    private int status;
    private int rated;
    private String chatid;
    private String otherimg;
    private String offerdesc;
    private String lastmsg;


    public Send(){}

    public Send(int sendtype, String recordid, String postid, String itemname, String ownerid, String ownername, String targetid, String targetname){
        this.recordid = recordid;
        this.postid = postid;
        this.ownerid = ownerid;
        this.targetid = targetid;
        this.itemname = itemname;
        this.ownername = ownername;
        this.targetname = targetname;
        status = 1;
        rated = 0;
        this.sendtype = sendtype;
    }

    public String getRecordid(){
        return recordid;
    }

    public String getPostid(){
        return postid;
    }

    public String getOwnerid(){
        return ownerid;
    }

    public String getOwnername(){
        return ownername;
    }

    public String getTargetid(){
        return targetid;
    }

    public String getTargetname(){
        return targetname;
    }

    public String getItemname(){
        return itemname;
    }

    public String getAgreementdesc(){
        return agreementdesc;
    }

    public String getReturnagreementdesc(){
        return returnagreementdesc;
    }

    public void setRequestdesc(String requestdesc){
        this.requestdesc = requestdesc;
    }

    public String getRequestdesc(){
        return requestdesc;
    }

    public void setAgreementdesc(String agreementdesc){
        this.agreementdesc = agreementdesc;
    }

    public void setReturnagreementdesc(String returnagreementdesc){
        this.returnagreementdesc = returnagreementdesc;
    }

    public void setOfferdesc(String offerdesc){
        this.offerdesc = offerdesc;
    }

    public String getOfferdesc(){
        return offerdesc;
    }

    public String getDatetime(){ return datetime; }

    public void setDatetime(String datetime){
        this.datetime = datetime;
    }

    public long getTimestamp() { return timestamp; }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
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

    public int getSendtype(){
        return sendtype;
    }

    public String getLastmsg(){
        return lastmsg;
    }

}
