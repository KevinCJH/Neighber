package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity2 extends AppCompatActivity {

    private CircleImageView imgView;
    private TextView displayname, email, ratings;
    private RatingBar ratingbar;
    private TextView browse, records, addnew, chat, profile;
    private String ruserid;
    private TextView requests, offers;
    private List<Post> posts;
    private ListView listViewRequests;

    //creating reference to firebase storage
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view2);

        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);
        requests = (TextView) findViewById(R.id.action_browse_request);
        offers = (TextView) findViewById(R.id.action_browse_offer);

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewProfileActivity2.this, ViewProfileActivity.class);
                //Pass info to next activity
                i.putExtra("ruserid", ruserid);
                startActivity(i);
                finish();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity2.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity2.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity2.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity2.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity2.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////


        imgView = (CircleImageView) findViewById(R.id.imgView);
        displayname = (TextView) findViewById(R.id.displayname);
        email = (TextView) findViewById(R.id.email);
        ratings = (TextView) findViewById(R.id.ratingvalue);
        ratingbar = (RatingBar) findViewById(R.id.rating);

        listViewRequests = (ListView) findViewById(R.id.listRequest);

        posts = new ArrayList<>();


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(ruserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Set profile picture of user
                String imageUri = user.getImgUri();
                Picasso.with(getBaseContext()).load(imageUri).placeholder(R.mipmap.defaultprofile).into(imgView);

                //Fill up profile details
                displayname.setText(user.getDisplayname());
                email.setText(user.getEmail());
                ratings.setText("Ratings: " + user.getRatings());
                ratingbar.setRating(Double.valueOf(user.getRatings()).floatValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewProfileActivity2.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference postDatabase = FirebaseDatabase.getInstance().getReference("posts");

        //attaching value event listener
        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                posts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Post post = postSnapshot.getValue(Post.class);

                    //If post type is request aka 2
                    if(post.getPosttype() == 1 && post.getUserid().toString().equals(ruserid) && post.getStatus() == 1) {

                        String datetime = getDate(post.getTimestamp());

                        post.setDatetime(datetime);

                        //adding to the list
                        posts.add(post);

                    }
                }

                //creating adapter
                RequestList reqAdapter = new RequestList(ViewProfileActivity2.this, posts);
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

                Intent i = new Intent(ViewProfileActivity2.this, PostActivity.class);
                i.putExtra("ruserid", requesterid);
                i.putExtra("rpostid", postid);
                startActivity(i);

            }
        });


    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return date;
    }

    ////////////////////Top Right Menu//////////////////////
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
                Intent i = new Intent(ViewProfileActivity2.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(ViewProfileActivity2.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}