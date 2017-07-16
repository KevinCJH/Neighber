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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReturnAgreementAcceptActivity2 extends AppCompatActivity {

    private String rofferid, rpostid;
    private TextView offerdesctxt;
    private TextView browse, records, addnew, chat, profile;
    private Button acceptoffer;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_agreement_accept);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        rpostid = i.getStringExtra("rpostid");
        rofferid = i.getStringExtra("rofferid");

        offerdesctxt = (TextView) findViewById(R.id.offerdesc);
        acceptoffer = (Button) findViewById(R.id.acceptoffer);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, MainActivity.class));
                finish();
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, BorrowerRecordsActivity.class));
                finish();
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, AddNewActivity.class));
                finish();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, ProfileActivity.class));
                finish();
            }
        });

        //////////////////////End Navigation////////////////////////////

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("offertolend");
        mDatabase.child(rofferid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferToLendPost offer = dataSnapshot.getValue(OfferToLendPost.class);

                //Display description
                offerdesctxt.setText(offer.getReturnagreementdesc());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReturnAgreementAcceptActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
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
                                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, LenderRecordsActivity.class));
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
        FirebaseDatabase.getInstance().getReference("offertolend").child(rofferid).child("status").setValue(5);
        FirebaseDatabase.getInstance().getReference("offertolend").child(rofferid).child("returnagreementdesc").setValue(agreement);
    }

    //////////////////Top Right Menu//////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // to do logout action
                auth.signOut();
                startActivity(new Intent(ReturnAgreementAcceptActivity2.this, LoginpageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}
