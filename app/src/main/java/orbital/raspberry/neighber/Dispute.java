package orbital.raspberry.neighber;


public class Dispute {

    private String disputeid;
    private String postid;
    private String sendid;
    private String userid;
    private String targetid;


    public Dispute(){}

    public Dispute(String disputeid, String postid, String sendid, String userid, String targetid){

        this.disputeid = disputeid;
        this.postid = postid;
        this.sendid = sendid;
        this.userid = userid;
        this.targetid = targetid;

    }

    public String getDisputeid(){
        return disputeid;
    }

    public String getPostid(){
        return postid;
    }

    public String getSendid(){
        return sendid;
    }

    public String getUserid(){
        return userid;
    }

    public String getTargetid(){
        return targetid;
    }


}
