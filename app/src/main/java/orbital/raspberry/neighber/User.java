package orbital.raspberry.neighber;

public class User {

    private String userid;
    private String displayname;
    private String email;
    private double ratings;
    private String imguri;
    private int totalvote;

    public User(){}

    public User(String userid, String email, String displayname){

        this.userid = userid;
        this.email = email;
        this.displayname = displayname;
        ratings = 0;
        imguri = "";
        totalvote = 0;

    }

    public String getUserid(){
        return userid;
    }

    public String getDisplayname(){
        return displayname;
    }

    public String getEmail(){
        return email;
    }

    public double getRatings(){
        return ratings;
    }

    public String getImgUri() { return imguri; }

    public int getTotalvote(){
        return totalvote;
    }
}
