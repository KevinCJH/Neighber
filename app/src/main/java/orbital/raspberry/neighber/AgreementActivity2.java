package orbital.raspberry.neighber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgreementActivity2 extends AppCompatActivity {

    private String ruserid, rofferid, ruserdisplayname;
    private EditText offerdesctxt;
    private TextView browse, records, addnew, chat, profile;
    private Button acceptoffer;
    private String postid;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement2);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rofferid = i.getStringExtra("rofferid");

        offerdesctxt = (EditText) findViewById(R.id.offerdesc);
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
                startActivity(new Intent(AgreementActivity2.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgreementActivity2.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgreementActivity2.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgreementActivity2.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgreementActivity2.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

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
                Toast.makeText(AgreementActivity2.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("offertolend");
        mDatabase.child(rofferid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferToLendPost offer = dataSnapshot.getValue(OfferToLendPost.class);

                //Display description
                offerdesctxt.setText(offer.getAgreementdesc());

                postid = offer.getPostid();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AgreementActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AgreementActivity2.this, "You are now borrowing from: " + ruserdisplayname, Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(AgreementActivity2.this, BorrowerRecordsActivity.class));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AgreementActivity2.this);
                builder.setMessage("Please ensure that you have received the correct item and in good condition").setPositiveButton("Confirm", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();


            }
        });


    }

    public void confirmAgreement(){
        String agreement = offerdesctxt.getText().toString().trim();

        FirebaseDatabase.getInstance().getReference("posts").child(postid).child("status").setValue(3);
        FirebaseDatabase.getInstance().getReference("offertolend").child(rofferid).child("status").setValue(3);
        FirebaseDatabase.getInstance().getReference("offertolend").child(rofferid).child("agreementdesc").setValue(agreement);
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
                Intent i = new Intent(AgreementActivity2.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(AgreementActivity2.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}
