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

public class BorrowerRecordsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView active, history;
    private List<Send> offers;
    private ListView listViewRequests2;
    private String userid;
    private int rrecordcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowerrecord);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        offers = new ArrayList<>();

        listViewRequests2 = (ListView) findViewById(R.id.listRequest2);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);
        active = (TextView) findViewById(R.id.action_active);
        history = (TextView) findViewById(R.id.action_history);

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BorrowerRecordsActivity.this, HistoryRecordsActivity.class));
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BorrowerRecordsActivity.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BorrowerRecordsActivity.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BorrowerRecordsActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BorrowerRecordsActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////



        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("send");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                offers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Send offer = postSnapshot.getValue(Send.class);

                    if(offer.getSendtype() == 1 && offer.getOwnerid().toString().equals(userid) && offer.getStatus() <= 4) {

                        //adding to the list
                        offers.add(offer);

                    }
                    else if(offer.getSendtype() == 2 && offer.getOwnerid().toString().equals(userid) && offer.getStatus() <= 5) {
                        //adding to the list
                        offers.add(offer);
                    }

                    }

                //creating adapter
                RecordsOfferList oAdapter = new RecordsOfferList(BorrowerRecordsActivity.this, offers);
                //attaching adapter to the listview
                listViewRequests2.setAdapter(oAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



////////////////////////////////////////////////////////////////////////////////////


        listViewRequests2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Send offer = offers.get(position);


                final DatabaseReference pDatabase = FirebaseDatabase.getInstance().getReference("posts");
                pDatabase.child(offer.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        rrecordcount = post.getRecordcount();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BorrowerRecordsActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
                    }
                });

                //If send type is request
                if(offer.getSendtype() == 1) {

                    //DELETE DIALOG
                    final DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    FirebaseDatabase.getInstance().getReference("send").child(offer.getRecordid()).removeValue();
                                    rrecordcount -= 1;
                                    FirebaseDatabase.getInstance().getReference("posts").child(offer.getPostid()).child("recordcount").setValue(rrecordcount);
                                    offers.remove(position);
                                    Toast.makeText(BorrowerRecordsActivity.this, "Request has been deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    /////

                    //Status pending
                    if (offer.getStatus() == 1) {

                        CharSequence options[] = new CharSequence[]{"View Post", "Delete this Request"};
                        final int newnumrecord;

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i = new Intent(BorrowerRecordsActivity.this, PostActivity.class);
                                        i.putExtra("rpostid", offer.getPostid());
                                        i.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i);
                                        break;
                                    case 1:
                                        AlertDialog.Builder builderdel = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                                        builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener2)
                                                .setNegativeButton("Cancel", dialogClickListener2).show();

                                        break;
                                }
                            }
                        });
                        builder.show();
                    }

                    //Status Agreement

                    else if (offer.getStatus() == 2) {

                        CharSequence options[] = new CharSequence[]{"View Agreement", "Chat with user", "View lender profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, AgreementActivity2.class);
                                        i1.putExtra("ruserid", offer.getTargetid());
                                        i1.putExtra("rofferid", offer.getRecordid());
                                        startActivity(i1);
                                        break;
                                    case 1:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i2.putExtra("chatroomid", offer.getChatid());
                                        i2.putExtra("itemname", offer.getItemname());
                                        i2.putExtra("offerid", offer.getRecordid());
                                        i2.putExtra("postid", offer.getPostid());
                                        startActivity(i2);
                                        break;
                                    case 2:
                                        Intent i3 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i3.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i3);
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }

                    //Status borrowing

                    else if (offer.getStatus() == 3) {

                        CharSequence options[] = new CharSequence[]{"Return Item", "Chat with user", "View lender profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, ReturnAgreementActivity2.class);
                                        i1.putExtra("agreementid", offer.getRecordid());
                                        i1.putExtra("postid", offer.getPostid());
                                        i1.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i1);
                                        break;
                                    case 1:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i2.putExtra("chatroomid", offer.getChatid());
                                        i2.putExtra("itemname", offer.getItemname());
                                        i2.putExtra("offerid", offer.getRecordid());
                                        i2.putExtra("postid", offer.getPostid());
                                        startActivity(i2);
                                        break;
                                    case 2:
                                        Intent i3 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i3.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i3);
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }


                    //Status returning

                    else if (offer.getStatus() == 4) {

                        CharSequence options[] = new CharSequence[]{"Chat with user", "View lender profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i2.putExtra("chatroomid", offer.getChatid());
                                        i2.putExtra("itemname", offer.getItemname());
                                        i2.putExtra("offerid", offer.getRecordid());
                                        i2.putExtra("postid", offer.getPostid());
                                        startActivity(i2);
                                        break;
                                    case 1:
                                        Intent i3 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i3.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i3);
                                        break;
                                }
                            }
                        });
                        builder.show();

                        //Rejected
                    }else if (offer.getStatus() == 0) {

                            CharSequence options[] = new CharSequence[]{"Delete this Request"};
                            final int newnumrecord;

                            final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                            builder.setTitle("Options");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pos) {
                                    switch (pos) {
                                        case 0:
                                            AlertDialog.Builder builderdel = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                                            builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener2)
                                                    .setNegativeButton("Cancel", dialogClickListener2).show();

                                            break;
                                    }
                                }
                            });
                            builder.show();
                        }

                }else{

                    //DELETE DIALOG
                    final DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    FirebaseDatabase.getInstance().getReference("send").child(offer.getRecordid()).removeValue();
                                    rrecordcount -= 1;
                                    FirebaseDatabase.getInstance().getReference("posts").child(offer.getPostid()).child("recordcount").setValue(rrecordcount);
                                    offers.remove(position);
                                    Toast.makeText(BorrowerRecordsActivity.this, "Offer has been deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    /////

                    //Status pending
                    if(offer.getStatus() == 1) {

                        CharSequence options[] = new CharSequence[]{"View Post", "Delete this Offer"};
                        final int newnumrecord;

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i = new Intent(BorrowerRecordsActivity.this, PostActivity.class);
                                        i.putExtra("rpostid", offer.getPostid());
                                        i.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i);
                                        break;
                                    case 1:
                                        AlertDialog.Builder builderdel = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                                        builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener2)
                                                .setNegativeButton("Cancel", dialogClickListener2).show();
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }

                    //Status send Agreement

                    else if(offer.getStatus() == 2) {

                        CharSequence options[] = new CharSequence[]{"Write agreement for item","Chat with user", "View borrower profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, SendAgreementActivity.class);
                                        i1.putExtra("ruserid", offer.getTargetid());
                                        i1.putExtra("rofferid", offer.getRecordid());
                                        startActivity(i1);
                                        break;
                                    case 1:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i2.putExtra("chatroomid", offer.getChatid());
                                        i2.putExtra("itemname", offer.getItemname());
                                        i2.putExtra("offerid", offer.getRecordid());
                                        i2.putExtra("postid", offer.getPostid());
                                        startActivity(i2);
                                        break;
                                    case 2:
                                        Intent i3 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i3.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i3);
                                        break;
                                }
                            }
                        });
                        builder.show();

                    }

                    //Status wait for accept agreement

                    else if(offer.getStatus() == 3) {
                        CharSequence options[] = new CharSequence[]{"Chat with user", "View borrower profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i1.putExtra("chatroomid", offer.getChatid());
                                        i1.putExtra("itemname", offer.getItemname());
                                        i1.putExtra("offerid", offer.getRecordid());
                                        i1.putExtra("postid", offer.getPostid());
                                        startActivity(i1);
                                        break;
                                    case 1:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i2.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i2);
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }


                    //Status lending

                    else if(offer.getStatus() == 4) {

                        CharSequence options[] = new CharSequence[]{"Chat with user", "View borrower profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i1.putExtra("chatroomid", offer.getChatid());
                                        i1.putExtra("itemname", offer.getItemname());
                                        i1.putExtra("offerid", offer.getRecordid());
                                        i1.putExtra("postid", offer.getPostid());
                                        startActivity(i1);
                                        break;
                                    case 1:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i2.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i2);
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }

                    //Status returning

                    else if(offer.getStatus() == 5) {

                        CharSequence options[] = new CharSequence[]{"View return agreement", "Chat with user", "View borrower profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent i = new Intent(BorrowerRecordsActivity.this, ReturnAgreementAcceptActivity.class);
                                        i.putExtra("rpostid", offer.getPostid());
                                        i.putExtra("rofferid", offer.getRecordid());
                                        startActivity(i);
                                        break;
                                    case 1:
                                        Intent i1 = new Intent(BorrowerRecordsActivity.this, ChatActivity.class);
                                        i1.putExtra("chatroomid", offer.getChatid());
                                        i1.putExtra("itemname", offer.getItemname());
                                        i1.putExtra("offerid", offer.getRecordid());
                                        i1.putExtra("postid", offer.getPostid());
                                        startActivity(i1);
                                        break;
                                    case 2:
                                        Intent i2 = new Intent(BorrowerRecordsActivity.this, ViewProfileActivity.class);
                                        i2.putExtra("ruserid", offer.getTargetid());
                                        startActivity(i2);
                                        break;
                                }
                            }
                        });
                        builder.show();

                        //Rejected
                    }else if(offer.getStatus() == 0) {

                        CharSequence options[] = new CharSequence[]{"Delete this Offer"};
                        final int newnumrecord;

                        final AlertDialog.Builder builder = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                switch (pos) {
                                    case 0:
                                        AlertDialog.Builder builderdel = new AlertDialog.Builder(BorrowerRecordsActivity.this);
                                        builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener2)
                                                .setNegativeButton("Cancel", dialogClickListener2).show();
                                        break;

                                }
                            }
                        });
                        builder.show();
                    }




                }


            }
        });



    }

    public void deleteRecords(final String postid){

        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("send");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Send offer = postSnapshot.getValue(Send.class);

                    String recordid = offer.getRecordid();

                    if(offer.getPostid().equals(postid)){
                        FirebaseDatabase.getInstance().getReference("send").child(recordid).removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                Intent i = new Intent(BorrowerRecordsActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(BorrowerRecordsActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}