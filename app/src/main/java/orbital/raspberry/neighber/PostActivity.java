package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private String ruserid, rpostid;
    private TextView rusernametxt, itemnametxt, postdesctxt;
    private CircleImageView ruserimg;
    private TextView browse, records, addnew, chat, profile;
    private Button viewprofile, writeoffer;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rpostid = i.getStringExtra("rpostid");

        rusernametxt = (TextView) findViewById(R.id.rusernameTxt);
        itemnametxt = (TextView) findViewById(R.id.itemnameTxt);
        postdesctxt = (TextView) findViewById(R.id.postdescTxt);
        ruserimg = (CircleImageView) findViewById(R.id.imgView);
        viewprofile = (Button) findViewById(R.id.viewprofile);
        writeoffer = (Button) findViewById(R.id.sendoffer);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
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
                startActivity(new Intent(PostActivity.this, AddNewActivity.class));
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
                startActivity(new Intent(PostActivity.this, ProfileActivity.class));
                finish();
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

                //Display profile picture of user
                String imageUri = user.getImgUri();
                Picasso.with(getBaseContext()).load(imageUri).placeholder(R.mipmap.defaultprofile).into(ruserimg);

                //Display user name
                rusernametxt.setText(user.getDisplayname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        mDatabase.child(rpostid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                //Display post item name and description

                if(post.getPosttype() == 1) {
                    itemnametxt.setText("I need an/a " + post.getItemname());
                }else {
                    itemnametxt.setText("I can lend an/a " + post.getItemname());
                }
                postdesctxt.setText(post.getPostdesc());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PostActivity.this, ViewProfileActivity.class);
                //Pass info to next activity
                i.putExtra("ruserid", ruserid);
                startActivity(i);

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
                startActivity(new Intent(PostActivity.this, LoginpageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}
