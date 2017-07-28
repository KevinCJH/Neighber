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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendAgreementActivity extends AppCompatActivity {

    private String ruserid, rofferid, ruserdisplayname;
    private TextView offerdesctxt, offerwritten;
    private TextView browse, records, addnew, chat, profile;
    private Button sendagree;
    private String postid;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_agreementsend);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rofferid = i.getStringExtra("rofferid");

        offerdesctxt = (TextView) findViewById(R.id.offerdesc);
        sendagree = (Button) findViewById(R.id.sendagree);
        offerwritten = (TextView) findViewById(R.id.offerwritten);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        uDatabase.child(ruserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                ruserdisplayname = user.getDisplayname();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SendAgreementActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");
        mDatabase.child(rofferid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Send offer = dataSnapshot.getValue(Send.class);

                offerwritten.setText(offer.getOfferdesc());

                //Display description
                offerdesctxt.setText(offer.getAgreementdesc());

                postid = offer.getPostid();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SendAgreementActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        sendagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmAgreement();
                Toast.makeText(SendAgreementActivity.this, "Please meet up with " + ruserdisplayname + " to pass the item", Toast.LENGTH_SHORT).show();

                finish();

            }
        });


    }

    public void confirmAgreement(){
        String agreement = offerdesctxt.getText().toString().trim();
/*
        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("agreementid").setValue(rofferid);
        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("otherid").setValue(ruserid);
        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("othername").setValue(ruserdisplayname);
        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("status").setValue(2);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("status").setValue(2);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("agreementdesc").setValue(agreement);
    */
        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("status").setValue(3);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("status").setValue(3);
        FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("agreementdesc").setValue(agreement);


    }


}
