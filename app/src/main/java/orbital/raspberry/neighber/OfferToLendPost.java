package orbital.raspberry.neighber;

//IN BORROWING

public class OfferToLendPost {

    private String recordid;
    private String postid;
    private String borrowerid;
    private String lenderid;
    private String agreementdesc;
    private String returnagreementdesc;

    public OfferToLendPost(){}

    public OfferToLendPost(String recordid, String postid, String borrowerid, String lenderid){
        this.recordid = recordid;
        this.postid = postid;
        this.borrowerid = borrowerid;
        this.lenderid = lenderid;
    }

    public String getRecordid(){
        return recordid;
    }

    public String getPostid(){
        return postid;
    }

    public String getBorrowerid(){
        return borrowerid;
    }

    public String getLenderid(){
        return lenderid;
    }

    public String getAgreementdesc(){
        return agreementdesc;
    }

    public String getReturnagreementdesc(){
        return returnagreementdesc;
    }

    public void setAgreementdesc(String agreementdesc){
        this.agreementdesc = agreementdesc;
    }

    public void setReturnagreementdesc(String returnagreementdesc){
        this.returnagreementdesc = returnagreementdesc;
    }


}
