package orbital.raspberry.neighber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ReturnAgreementAcceptActivity2 extends AppCompatActivity {

    private String rofferid, rpostid;
    private TextView offerdesctxt;
    private Button acceptoffer;
    private String ruserid, userid;
    private CheckBox dispute;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_return_agreement_accept);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        rpostid = i.getStringExtra("rpostid");
        rofferid = i.getStringExtra("rofferid");

        offerdesctxt = (TextView) findViewById(R.id.offerdesc);
        acceptoffer = (Button) findViewById(R.id.acceptoffer);
        dispute = (CheckBox) findViewById(R.id.disputeCheck);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");
        mDatabase.child(rofferid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Send offer = dataSnapshot.getValue(Send.class);

                //Display description
                offerdesctxt.setText(offer.getReturnagreementdesc());

                ruserid = offer.getOwnerid();

                userid = offer.getTargetid();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReturnAgreementAcceptActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        dispute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReturnAgreementAcceptActivity2.this, "We will send you an email shortly regarding any dispute you have after you have accepted the return item.", Toast.LENGTH_LONG).show();
            }
        });

        acceptoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                confirmAgreement();
                                Toast.makeText(ReturnAgreementAcceptActivity2.this, "Item has been returned to you successfully", Toast.LENGTH_SHORT).show();
                                Intent i2 = new Intent(ReturnAgreementAcceptActivity2.this, RateUserActivity.class);
                                i2.putExtra("ruserid", ruserid);
                                i2.putExtra("postid", rpostid);
                                startActivity(i2);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ReturnAgreementAcceptActivity2.this);
                builder.setMessage("Please ensure that item is returned to you in good condition").setPositiveButton("Confirm", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();


            }
        });


    }

    public void confirmAgreement(){
        String agreement = offerdesctxt.getText().toString().trim();

        FirebaseDatabase.getInstance().getReference("posts").child(rpostid).child("status").setValue(5);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("status").setValue(5);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("returnagreementdesc").setValue(agreement);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("historypostid").setValue(rpostid);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("postid").setValue("");


        if(dispute.isChecked()) {

            DatabaseReference dDatabase = FirebaseDatabase.getInstance().getReference("dispute");

            String disputeid = dDatabase.push().getKey();

            Dispute newDispute = new Dispute(disputeid, rpostid, rofferid, userid, ruserid);

            dDatabase.child(disputeid).setValue(newDispute);
        }

        //Automatcally readd the item into post



        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");

        final String historypostid = mDatabase.push().getKey();

        mDatabase.child(rpostid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                post.setPostid(historypostid);

                /*Post newpost = new Post(historypostid, userid, post.getItemname(),
                        post.getDisplayname(), post.getPostdesc(), post.getPosttype(), post.getCategory(), post.getLocation());
*/
                //Add history post to database
                mDatabase.child(historypostid).setValue(post);

                mDatabase.child(historypostid).child("imguri").setValue(post.getImgUri());

                //Re configure the post to fresh post
                //Add timestamp to the post
                mDatabase.child(rpostid).child("otherimg").setValue("");
                mDatabase.child(rpostid).child("otherid").setValue("");
                mDatabase.child(rpostid).child("lastmsg").setValue("Chat with this User!");
                mDatabase.child(rpostid).child("otherimg").setValue("");
                mDatabase.child(rpostid).child("chatid").setValue("");
                mDatabase.child(rpostid).child("agreementid").setValue("");
                mDatabase.child(rpostid).child("othername").setValue("");

                mDatabase.child(rpostid).child("status").setValue(1);

                int newrecordcount = post.getRecordcount() - 1;

                mDatabase.child(rpostid).child("recordcount").setValue(newrecordcount);

                mDatabase.child(rpostid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                //mDatabase.child(historypostid).child("imguri").setValue(post.getImgUri());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReturnAgreementAcceptActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });



    }


}
