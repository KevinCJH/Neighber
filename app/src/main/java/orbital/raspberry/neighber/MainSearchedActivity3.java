package orbital.raspberry.neighber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class MainSearchedActivity3 extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private TextView browsereq, browseoff;
    private List<Post> posts;
    private List<Post> fulllist;
    private ListView listViewRequests;
    private FloatingActionButton searchfab;
    //private EditText searchtxt;
   // private Button searchbtn;
    private String searchkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Get search string
        Intent i = getIntent();
        searchkey = i.getStringExtra("searchkey");

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        posts = new ArrayList<>();
        fulllist = new ArrayList<>();

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
                startActivity(new Intent(MainSearchedActivity3.this, MainActivity2.class));
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
                startActivity(new Intent(MainSearchedActivity3.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSearchedActivity3.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSearchedActivity3.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSearchedActivity3.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

      //  searchtxt = (EditText) findViewById(R.id.searchtxt);
     //   searchbtn = (Button) findViewById(R.id.searchbtn);

      //  searchtxt.setText(searchkey);

        final FirebaseUser currentFirebaseUser = auth.getCurrentUser() ;
        final String userid = currentFirebaseUser.getUid();

        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        uDatabase.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                int notif = user.getNewsent();

                if(notif == 1){
                    ImageView newnotif = (ImageView) findViewById(R.id.notif);
                    newnotif.setVisibility(View.VISIBLE);
                }else{
                    ImageView newnotif = (ImageView) findViewById(R.id.notif);
                    newnotif.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // Toast.makeText(MainSearchedActivity3.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });


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

                    //Found reset for every search
                    int found = 0;

                    //Perform search
                    String splitname[] = post.getLocation().split(" ");

                    String splitquery[] = searchkey.split(" ");

                    //Search individual words in string to see if it matches keyword
                    for(int j = 0; j < splitquery.length; j++) {

                        for (int i = 0; i < splitname.length; i++) {

                            if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                                //Set found to 1 meaning item exist
                                found = 1;

                                break;
                            }
                        }

                        if(found == 1)
                            break;
                    }

                    //If post type is request aka 1 and valid search
                    if(post.getPosttype() == 2 && post.getStatus() == 1 && found == 1) {

                        String datetime = getDate(post.getTimestamp());

                        post.setDatetime(datetime);

                        //adding to the list
                        posts.add(post);

                    }

                    //All items in the list to be used if another search is performed
                    if(post.getPosttype() == 2 && post.getStatus() == 1){
                        fulllist.add(post);
                    }

                }

                //creating adapter
                RequestList reqAdapter = new RequestList(MainSearchedActivity3.this, posts);
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

                Intent i = new Intent(MainSearchedActivity3.this, PostActivity.class);
                i.putExtra("ruserid", requesterid);
                i.putExtra("rpostid", postid);
                startActivity(i);

            }
        });

        searchfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainSearchedActivity3.this);
                dialog.setContentView(R.layout.activity_search);
                dialog.setTitle("");

                final Button searchbtn = (Button) dialog.findViewById(R.id.searchbtn);
                final EditText searchtxt = (EditText) dialog.findViewById(R.id.searchtxt);
                final MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner);
                spinner.setItems("Item Name", "User Name", "Meeting Point");
                spinner.setSelectedIndex(2);

                searchtxt.setText(searchkey);

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

/*
        searchtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Always use a TextKeyListener when clearing a TextView to prevent android
                    // warnings in the log
                    TextKeyListener.clear((searchtxt).getText());

                }
            }
        });

        searchtxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextKeyListener.clear((searchtxt).getText());
            }

        });

        searchtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Always use a TextKeyListener when clearing a TextView to prevent android
                    // warnings in the log
                    TextKeyListener.clear((searchtxt).getText());

                }
            }
        });

        searchtxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextKeyListener.clear((searchtxt).getText());
            }

        });


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query = searchtxt.getText().toString().trim();
                int found = 0;

                for(Post p : fulllist){

                    String splitname[] = p.getItemname().split(" ");

                    for(int i=0; i<splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(query.toLowerCase())) {

                            String requesterid = p.getUserid();
                            String postid = p.getPostid();

                            found = 1;

                            Intent i2 = new Intent(MainSearchedActivity.this, MainSearchedActivity.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);
                            finish();

                            break;
                        }
                    }

                    if(found == 1){
                        break;
                    }

                }
                if(found == 0) {
                    Toast.makeText(MainSearchedActivity.this, "No such items found", Toast.LENGTH_SHORT).show();
                }

            }
        }); */

    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return date;
    }

    public void searchItem(String query, int searchby){

        final ProgressDialog pd = new ProgressDialog(MainSearchedActivity3.this);
        pd.setMessage("Searching...");
        pd.show();

        int found = 0;

        for(Post p : fulllist){

            String splitquery[] = query.split(" ");

            for (int j = 0; j < splitquery.length; j++) {

                if (searchby == 0) {

                    String splitname[] = p.getItemname().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainSearchedActivity3.this, MainSearchedActivity.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);
                            finish();

                            break;
                        }
                    }

                } else if (searchby == 1) {

                    String splitname[] = p.getDisplayname().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainSearchedActivity3.this, MainSearchedActivity2.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);
                            finish();

                            break;
                        }
                    }

                } else if (searchby == 2) {

                    String splitname[] = p.getLocation().split(" ");

                    for (int i = 0; i < splitname.length; i++) {

                        if (splitname[i].toLowerCase().equals(splitquery[j].toLowerCase())) {

                            found = 1;

                            Intent i2 = new Intent(MainSearchedActivity3.this, MainSearchedActivity3.class);
                            i2.putExtra("searchkey", query);
                            startActivity(i2);
                            finish();

                            break;
                        }
                    }

                }

                if(found == 1){
                    break;
                }

            }

            if(found == 1){
                break;
            }

        }
        if(found == 0) {
            Toast.makeText(MainSearchedActivity3.this, "No such items found", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(MainSearchedActivity3.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainSearchedActivity3.this, SettingsActivity.class));
                break;
            case R.id.action_favourite:
                startActivity(new Intent(MainSearchedActivity3.this, FavouriteActivity.class));
                break;
            case R.id.action_feedback:
                startActivity(new Intent(MainSearchedActivity3.this, FeedbackActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}