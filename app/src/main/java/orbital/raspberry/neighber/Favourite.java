package orbital.raspberry.neighber;


public class Favourite {

    private String favid;
    private String userid;
    private String postid;

    public Favourite(){}

    public Favourite(String favid, String userid, String postid){

        this.favid = favid;
        this.userid = userid;
        this.postid = postid;

    }

    public String getFavid(){
        return favid;
    }

    public String getUserid(){
        return userid;
    }

    public String getPostid(){
        return postid;
    }

}
