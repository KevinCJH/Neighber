package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class RateUserActivity extends AppCompatActivity {

    private CircleImageView imgView;
    private TextView displayname;
    private RatingBar ratingbar;
    private Button rateuser;
    private TextView browse, records, addnew, chat, profile;
    private String ruserid;
    private String postid;
    private double userrating;
    private int totalrater;

    //creating reference to firebase storage
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateuser);

        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        postid = i.getStringExtra("postid");

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateUserActivity.this, MainActivity.class));
                finish();
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateUserActivity.this, BorrowerRecordsActivity.class));
                finish();
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateUserActivity.this, AddNewActivity.class));
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
                startActivity(new Intent(RateUserActivity.this, ProfileActivity.class));
                finish();
            }
        });

        //////////////////////End Navigation////////////////////////////


        imgView = (CircleImageView) findViewById(R.id.imgView);
        displayname = (TextView) findViewById(R.id.displayname);
        ratingbar = (RatingBar) findViewById(R.id.rating);
        rateuser = (Button) findViewById(R.id.submitRating);


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

                userrating = user.getRatings();

                totalrater = user.getTotalvote();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RateUserActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        rateuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double newRate = ratingbar.getRating();

                int newtotalrater = totalrater + 1;

                userrating = ((userrating * totalrater) + newRate) / newtotalrater;

                FirebaseDatabase.getInstance().getReference("users").child(ruserid).child("ratings").setValue(userrating);
                FirebaseDatabase.getInstance().getReference("users").child(ruserid).child("totalvote").setValue(newtotalrater);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("rated").setValue(1);

                Toast.makeText(RateUserActivity.this, "Rating has been submitted", Toast.LENGTH_SHORT).show();

                finish();

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
                startActivity(new Intent(RateUserActivity.this, LoginpageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}