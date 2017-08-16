package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ViewOfferSentActivity extends AppCompatActivity {

    private String ruserid, rofferid, ruserdisplayname;
    private TextView rusernametxt, requestdesctxt ;
    private Button viewprofile, acceptoffer;
    private String postid;
    private String userid;
    private String otherimgurl;
    private String userimgurl;
    private String newimguri;
    private ImageView photo;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_offer_sent_view);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rofferid = i.getStringExtra("rofferid");

        rusernametxt = (TextView) findViewById(R.id.rusernameTxt);
        viewprofile = (Button) findViewById(R.id.viewprofile);
        acceptoffer = (Button) findViewById(R.id.acceptoffer);
        requestdesctxt = (TextView) findViewById(R.id.requestdesc);
        photo = (ImageView) findViewById(R.id.imgView);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        uDatabase.child(ruserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Display profile picture of user
                String imageUri = user.getImgUri();
                //Picasso.with(getBaseContext()).load(imageUri).placeholder(R.mipmap.defaultprofile).into(ruserimg);

                otherimgurl = imageUri;

                //Display user name
                rusernametxt.setText("Offer from: " + user.getDisplayname());

                ruserdisplayname = user.getDisplayname();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewOfferSentActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference u2Database = FirebaseDatabase.getInstance().getReference("users");
        u2Database.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Display profile picture of user
                userimgurl = user.getImgUri();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewOfferSentActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");
        mDatabase.child(rofferid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Send offer = dataSnapshot.getValue(Send.class);

                //Display description
                requestdesctxt.setText(offer.getOfferdesc());

                postid = offer.getPostid();

                newimguri = offer.getImguri();

                if(!newimguri.isEmpty()){
                    Picasso.with(ViewOfferSentActivity.this).load(newimguri).placeholder(R.mipmap.defaultitem).into(photo);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewOfferSentActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ViewOfferSentActivity.this, ViewProfileActivity.class);
                //Pass info to next activity
                i.putExtra("ruserid", ruserid);
                startActivity(i);

            }
        });

        acceptoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("agreementid").setValue(rofferid);
               // FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("agreementdesc").setValue(offerdesctxt.getText().toString().trim());
                FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("status").setValue(2);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("status").setValue(2);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("otherid").setValue(ruserid);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("othername").setValue(ruserdisplayname);

                //Create a chat room
                DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference("chatmessage");
                String chatroomid = cDatabase.push().getKey();

                FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("chatid").setValue(chatroomid);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("chatid").setValue(chatroomid);
                FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("lastmsg").setValue("Chat with this user!");
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("lastmsg").setValue("Chat with this user!");

                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("imguri").setValue(newimguri);

                FirebaseDatabase.getInstance().getReference("send").child(rofferid).child("otherimg").setValue(userimgurl);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("otherimg").setValue(otherimgurl);


                ////

                final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("send");

                //attaching value event listener
                oDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //iterating through all the nodes
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Send offer = postSnapshot.getValue(Send.class);

                            //If offer is under this post id then delete it
                            if(offer.getSendtype() == 2 && offer.getPostid().equals(postid)) {

                                if(!rofferid.equals(offer.getRecordid())){
                                    FirebaseDatabase.getInstance().getReference("send").child(offer.getRecordid()).child("status").setValue(0);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ////


                Toast.makeText(ViewOfferSentActivity.this, "You have accepted an offer from " + ruserdisplayname , Toast.LENGTH_SHORT).show();

                startActivity(new Intent(ViewOfferSentActivity.this, ProfileActivity2.class));
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
                Intent i = new Intent(ViewOfferSentActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(ViewOfferSentActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}
