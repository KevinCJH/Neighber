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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryRequestActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView borrowing, lending, history;
    private List<OfferToLendPost> offers;
    private ListView listViewRequests;
    private String userid;
    private Button recordbtn, offerbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyrequest);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        offers = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);
        recordbtn = (Button) findViewById(R.id.viewrecord);
        offerbtn = (Button) findViewById(R.id.viewoffer);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);
        borrowing = (TextView) findViewById(R.id.action_borrowing);
        lending = (TextView) findViewById(R.id.action_lending);
        history = (TextView) findViewById(R.id.action_history);

        borrowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, BorrowerRecordsActivity.class));
            }
        });

        lending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, LenderRecordsActivity.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("offertolend");

        //attaching value event listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                offers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    OfferToLendPost offer = postSnapshot.getValue(OfferToLendPost.class);

                    //If post type is request aka 1
                        if(offer.getOwnerid().toString().equals(userid) && offer.getStatus() == 5) {

                            //adding to the list
                            offers.add(offer);

                        }
                }

                //creating adapter
                HistoryRequestList reqAdapter = new HistoryRequestList(HistoryRequestActivity.this, offers);
                //attaching adapter to the listview
                listViewRequests.setAdapter(reqAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final OfferToLendPost offer = offers.get(position);

                //DELETE DIALOG
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                FirebaseDatabase.getInstance().getReference("offertolend").child(offer.getRecordid()).child("status").setValue(8);
                                offers.remove(position);
                                Toast.makeText(HistoryRequestActivity.this, "Record has been deleted", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                    CharSequence options[] = new CharSequence[]{"View Lender Profile", "Rate this User", "Delete this Record"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(HistoryRequestActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(HistoryRequestActivity.this, ViewProfileActivity.class);
                                    i.putExtra("ruserid", offer.getTargetid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    if(offer.getRated() == 0) {
                                        Intent i2 = new Intent(HistoryRequestActivity.this, RequestRateUserActivity.class);
                                        i2.putExtra("ruserid", offer.getTargetid());
                                        i2.putExtra("rrecordid", offer.getRecordid());
                                        startActivity(i2);
                                    }else{
                                        Toast.makeText(HistoryRequestActivity.this, "Already rated this user.", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    AlertDialog.Builder builderdel = new AlertDialog.Builder(HistoryRequestActivity.this);
                                    builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener)
                                            .setNegativeButton("Cancel", dialogClickListener).show();

                                    break;
                            }
                        }
                    });
                    builder.show();



            }
        });


        recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, HistoryRecordsActivity.class));
            }
        });

        offerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRequestActivity.this, HistoryOfferActivity.class));
            }
        });


    }


    ///////////////////Top Right Menu//////////////////////
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
                Intent i = new Intent(HistoryRequestActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(HistoryRequestActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}