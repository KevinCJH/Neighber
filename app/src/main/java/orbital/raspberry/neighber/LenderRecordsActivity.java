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

public class LenderRecordsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView borrowing, lending, history;
    private List<Post> posts;
    private List<OfferToBorrowPost> offers;
    private ListView listViewRequests, listViewRequests2;
    private String userid;
    private int rrecordcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenderrecord);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        posts = new ArrayList<>();
        offers = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);
        listViewRequests2 = (ListView) findViewById(R.id.listRequest2);

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
                startActivity(new Intent(LenderRecordsActivity.this, BorrowerRecordsActivity.class));
            }
        });

        lending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LenderRecordsActivity.this, HistoryRecordsActivity.class));
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LenderRecordsActivity.this, MainActivity.class));
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
                startActivity(new Intent(LenderRecordsActivity.this, AddNewActivity.class));
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
                startActivity(new Intent(LenderRecordsActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");

        //attaching value event listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                posts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Post post = postSnapshot.getValue(Post.class);

                    //If post type is offers aka 2
                        if(post.getPosttype() == 2 && post.getUserid().toString().equals(userid) && post.getStatus() <= 4) {

                            //adding to the list
                            posts.add(post);

                        }
                }

                //creating adapter
                RecordsList2 reqAdapter = new RecordsList2(LenderRecordsActivity.this, posts);
                //attaching adapter to the listview
                listViewRequests.setAdapter(reqAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("offertoborrow");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                offers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    OfferToBorrowPost offer = postSnapshot.getValue(OfferToBorrowPost.class);

                    if(offer.getOwnerid().toString().equals(userid) && offer.getStatus() <= 5) {

                        //adding to the list
                        offers.add(offer);

                    }
                }

                //creating adapter
                RecordsOfferList2 oAdapter = new RecordsOfferList2(LenderRecordsActivity.this, offers);
                //attaching adapter to the listview
                listViewRequests2.setAdapter(oAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Post post = posts.get(position);

                //Status pending
                if(post.getStatus() == 1) {

                    CharSequence options[] = new CharSequence[]{"View Requests", "Update this Post", "Delete this Post"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    if (post.getRecordcount() <= 0) {
                                        Toast.makeText(LenderRecordsActivity.this, "No requests were made to you", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(LenderRecordsActivity.this, ViewRequestActivity.class);
                                        i.putExtra("rpostid", post.getPostid());
                                        startActivity(i);
                                    }
                                    break;
                                case 1:
                                    Intent i = new Intent(LenderRecordsActivity.this, EditPostActivity.class);
                                    i.putExtra("rpostid", post.getPostid());
                                    startActivity(i);
                                    break;
                                case 2:
                                    FirebaseDatabase.getInstance().getReference("posts").child(post.getPostid()).removeValue();
                                    deleteRecords(post.getPostid());
                                    posts.remove(position);
                                    Toast.makeText(LenderRecordsActivity.this, "Post has been deleted", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

                //Status borrowing

                else if(post.getStatus() == 2) {

                    CharSequence options[] = new CharSequence[]{"View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
                                    i2.putExtra("ruserid", post.getOtherid());
                                    startActivity(i2);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

                //Status returning

                else if(post.getStatus() == 4) {

                    CharSequence options[] = new CharSequence[]{"View return agreement", "View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(LenderRecordsActivity.this, ReturnAgreementAcceptActivity2.class);
                                    i.putExtra("rpostid", post.getPostid());
                                    i.putExtra("rofferid", post.getAgreementid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
                                    i2.putExtra("ruserid", post.getOtherid());
                                    startActivity(i2);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }


            }
        });


        listViewRequests2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final OfferToBorrowPost offer = offers.get(position);

                final DatabaseReference pDatabase = FirebaseDatabase.getInstance().getReference("posts");
                pDatabase.child(offer.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        rrecordcount = post.getRecordcount();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LenderRecordsActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
                    }
                });

                //Status pending
                if(offer.getStatus() == 1) {

                    CharSequence options[] = new CharSequence[]{"View Post", "Delete this Offer"};
                    final int newnumrecord;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(LenderRecordsActivity.this, PostActivity.class);
                                    i.putExtra("rpostid", offer.getPostid());
                                    i.putExtra("ruserid", offer.getTargetid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    FirebaseDatabase.getInstance().getReference("offertoborrow").child(offer.getRecordid()).removeValue();
                                    rrecordcount -= 1;
                                    FirebaseDatabase.getInstance().getReference("posts").child(offer.getPostid()).child("recordcount").setValue(rrecordcount);
                                    offers.remove(position);
                                    Toast.makeText(LenderRecordsActivity.this, "Offer has been deleted", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

                //Status send Agreement

                else if(offer.getStatus() == 2) {

                    CharSequence options[] = new CharSequence[]{"Write agreement for item", "View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i1 = new Intent(LenderRecordsActivity.this, SendAgreementActivity.class);
                                    i1.putExtra("ruserid", offer.getTargetid());
                                    i1.putExtra("rofferid", offer.getRecordid());
                                    startActivity(i1);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
                                    i2.putExtra("ruserid", offer.getTargetid());
                                    startActivity(i2);
                                    break;
                            }
                        }
                    });
                    builder.show();

                }

                //Status wait for accept agreement

                else if(offer.getStatus() == 3) {
                    CharSequence options[] = new CharSequence[]{"View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
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

                    CharSequence options[] = new CharSequence[]{"View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
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

                    CharSequence options[] = new CharSequence[]{"View return agreement", "View borrower profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LenderRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(LenderRecordsActivity.this, ReturnAgreementAcceptActivity.class);
                                    i.putExtra("rpostid", offer.getPostid());
                                    i.putExtra("rofferid", offer.getRecordid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(LenderRecordsActivity.this, ViewProfileActivity.class);
                                    i2.putExtra("ruserid", offer.getTargetid());
                                    startActivity(i2);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

            }
        });



    }

    public void deleteRecords(final String postid){

        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("offertolend");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    OfferToLendPost offer = postSnapshot.getValue(OfferToLendPost.class);

                    String recordid = offer.getRecordid();

                    if(offer.getPostid().equals(postid)){
                        FirebaseDatabase.getInstance().getReference("offertolend").child(recordid).removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                Intent i = new Intent(LenderRecordsActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(LenderRecordsActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}