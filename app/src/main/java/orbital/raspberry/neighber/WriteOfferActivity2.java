package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class WriteOfferActivity2 extends AppCompatActivity {

    private String ruserid, rpostid, ritemname, ruserdisplayname;
    private TextView browse, records, addnew, chat, profile;
    private int rrecordcount;
    private Button submitBtn;
    private EditText offerdescTxt;
    private TextView itemnameTxt;
    private int cat;
    private String imguri;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_offer2);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rpostid = i.getStringExtra("rpostid");
        ruserdisplayname = i.getStringExtra("ruserdisplayname");

   /*     //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteOfferActivity2.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteOfferActivity2.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteOfferActivity2.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteOfferActivity2.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteOfferActivity2.this, ProfileActivity.class));
            }
        });
*/
        //////////////////////End Navigation////////////////////////////

        submitBtn = (Button)findViewById(R.id.submitRequest);
        offerdescTxt = (EditText)findViewById(R.id.offerdesc);
        itemnameTxt = (TextView) findViewById(R.id.itemnametxt) ;

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final String userid = auth.getCurrentUser().getUid();

        final String[] userdisplayname = new String[1];

        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        uDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userdisplayname[0] = user.getDisplayname();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WriteOfferActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        final DatabaseReference pDatabase = FirebaseDatabase.getInstance().getReference("posts");
        pDatabase.child(rpostid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                ritemname = post.getItemname();

                itemnameTxt.setText("You are requesting: " + ritemname);

                rrecordcount = post.getRecordcount();

                cat = post.getCategory();

                imguri = post.getImgUri();

                //Toast.makeText(WriteOfferActivity.this, "Count: " + rrecordcount, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WriteOfferActivity2.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(offerdescTxt.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Enter your request!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get unique post id from firebase
                String recordid = mDatabase.push().getKey();

                //Create new offertoborrowpost object
                Send newreq = new Send(1, recordid, rpostid, ritemname, userid, userdisplayname[0],ruserid, ruserdisplayname);

                newreq.setRequestdesc(offerdescTxt.getText().toString().trim());

                //Add post to database
                mDatabase.child(recordid).setValue(newreq);

                mDatabase.child(recordid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                mDatabase.child(recordid).child("imguri").setValue(imguri);
                mDatabase.child(recordid).child("category").setValue(cat);
                //Update number of offers made to the post

                rrecordcount += 1;

                pDatabase.child(rpostid).child("recordcount").setValue(rrecordcount);

                uDatabase.child(ruserid).child("newsent").setValue(1);

                Toast.makeText(WriteOfferActivity2.this, "Request Submitted! You may view/delete the request in the Records tab", Toast.LENGTH_LONG).show();

                startActivity(new Intent(WriteOfferActivity2.this, MainActivity.class));
                finish();

            }
        });

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
                Intent i = new Intent(WriteOfferActivity2.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(WriteOfferActivity2.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////
}
