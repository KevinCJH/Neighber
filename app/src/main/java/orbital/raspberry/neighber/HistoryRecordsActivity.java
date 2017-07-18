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

public class HistoryRecordsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView borrowing, lending, history;
    private List<Post> posts;
    private ListView listViewRequests;
    private String userid;
    private Button requestbtn, offerbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyrecord);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        posts = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);
        requestbtn = (Button) findViewById(R.id.viewrequest);
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
                startActivity(new Intent(HistoryRecordsActivity.this, BorrowerRecordsActivity.class));
            }
        });

        lending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, LenderRecordsActivity.class));
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
                startActivity(new Intent(HistoryRecordsActivity.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, ProfileActivity.class));
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

                    //If post type is request aka 1
                        if(post.getPosttype() == 1 && post.getUserid().toString().equals(userid) && post.getStatus() == 6) {

                            //adding to the list
                            posts.add(post);

                        }else if(post.getPosttype() == 2 && post.getUserid().toString().equals(userid) && post.getStatus() == 5){

                            //adding to the list
                            posts.add(post);

                        }
                }

                //creating adapter
                HistoryList reqAdapter = new HistoryList(HistoryRecordsActivity.this, posts);
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

                final Post post = posts.get(position);

                //Status pending
                if(post.getPosttype() == 1) {

                    CharSequence options[] = new CharSequence[]{"View Lender Profile", "Rate this User", "Delete this Record"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(HistoryRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(HistoryRecordsActivity.this, ViewProfileActivity.class);
                                    i.putExtra("ruserid", post.getOtherid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    if(post.getRated() == 0) {
                                        Intent i2 = new Intent(HistoryRecordsActivity.this, RateUserActivity.class);
                                        i2.putExtra("ruserid", post.getOtherid());
                                        i2.putExtra("postid", post.getPostid());
                                        startActivity(i2);
                                    }else{
                                        Toast.makeText(HistoryRecordsActivity.this, "Already rated this user.", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    FirebaseDatabase.getInstance().getReference("posts").child(post.getPostid()).child("status").setValue(8);
                                    posts.remove(position);
                                    Toast.makeText(HistoryRecordsActivity.this, "Record has been deleted", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    builder.show();

                }else{

                    CharSequence options[] = new CharSequence[]{"View Borrower Profile", "Rate this User", "Delete this Record"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(HistoryRecordsActivity.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i = new Intent(HistoryRecordsActivity.this, ViewProfileActivity.class);
                                    i.putExtra("ruserid", post.getOtherid());
                                    startActivity(i);
                                    break;
                                case 1:
                                    if(post.getRated() == 0) {
                                        Intent i2 = new Intent(HistoryRecordsActivity.this, RateUserActivity.class);
                                        i2.putExtra("ruserid", post.getOtherid());
                                        i2.putExtra("postid", post.getPostid());
                                        startActivity(i2);
                                    }else{
                                        Toast.makeText(HistoryRecordsActivity.this, "Already rated this user.", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    FirebaseDatabase.getInstance().getReference("posts").child(post.getPostid()).child("status").setValue(8);
                                    posts.remove(position);
                                    Toast.makeText(HistoryRecordsActivity.this, "Record has been deleted", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    builder.show();

                }


            }
        });

        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, HistoryRequestActivity.class));
            }
        });

        offerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryRecordsActivity.this, HistoryOfferActivity.class));
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
                Intent i = new Intent(HistoryRecordsActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(HistoryRecordsActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}