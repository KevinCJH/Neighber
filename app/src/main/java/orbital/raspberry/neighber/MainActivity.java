package orbital.raspberry.neighber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView browsereq, browseoff;
    private List<Post> posts;
    private ListView listViewRequests;
    private FloatingActionButton searchfab;
    private static boolean calledAlready = false;
   // private EditText searchtxt;
   // private Button searchbtn;
   private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        doubleBackToExitPressedOnce = false;

        posts = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);
        browsereq = (TextView) findViewById(R.id.action_browse_request);
        browseoff = (TextView) findViewById(R.id.action_browse_offer);

        searchfab = (FloatingActionButton) findViewById(R.id.searchfab);

        browsereq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });

        browseoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

       // searchtxt = (EditText) findViewById(R.id.searchtxt);
      //  searchbtn = (Button) findViewById(R.id.searchbtn);


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
                    if(post.getPosttype() == 2 && post.getStatus() == 1) {

                        String datetime = getDate(post.getTimestamp());

                        post.setDatetime(datetime);

                        //adding to the list
                        posts.add(post);
                    }
                }

                //creating adapter
                RequestList reqAdapter = new RequestList(MainActivity.this, posts);
                //attaching adapter to the listview
                listViewRequests.setAdapter(reqAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = posts.get(position);
                //Toast.makeText(MainActivity.this, "Item: " + request.getItemname() + " selected.", Toast.LENGTH_SHORT).show();
                String requesterid = post.getUserid();
                String postid = post.getPostid();

                Intent i = new Intent(MainActivity.this, PostActivity.class);
                i.putExtra("ruserid", requesterid);
                i.putExtra("rpostid", postid);
                startActivity(i);

            }
        });


        searchfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_search);
                dialog.setTitle("");

                final Button searchbtn = (Button) dialog.findViewById(R.id.searchbtn);
                final EditText searchtxt = (EditText) dialog.findViewById(R.id.searchtxt);
                final MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner);
                spinner.setItems("Item Name", "User Name", "Meeting Point");

                searchbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(searchtxt.getText().toString().trim())) {
                            Toast.makeText(getApplicationContext(), "Enter your keyword!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int searchby = spinner.getSelectedIndex();

                        searchItem(searchtxt.getText().toString().trim(), searchby);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return date;
    }


    public void searchItem(String query, int searchby){

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Searching...");
        pd.show();

        //String query = searchtxt.getText().toString().trim();
        int found = 0;

        //If found at least one, repopulate the list
        for(Post p : posts) {

            String splitquery[] = query.split(" ");

            for (int j = 0; j < splitquery.length; j++) {

                if (searchby == 0) {
                    String splitname[] = p.getItemname().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainActivity.this, MainSearchedActivity.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);

                            break;
                        }
                    }
                } else if (searchby == 1) {
                    String splitname[] = p.getDisplayname().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainActivity.this, MainSearchedActivity2.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);

                            break;
                        }
                    }
                } else if (searchby == 2) {
                    String splitname[] = p.getLocation().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainActivity.this, MainSearchedActivity3.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);

                            break;
                        }
                    }
                }

                if (found == 1) {
                    break;
                }

            }

            if (found == 1) {
                break;
            }


        }

        if (found == 0) {
            Toast.makeText(MainActivity.this, "No such items found", Toast.LENGTH_SHORT).show();
        }

        pd.dismiss();

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
                Intent i = new Intent(MainActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_favourite:
                startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
                break;
            case R.id.action_feedback:
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}