package orbital.raspberry.neighber;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private String ruserid, rpostid, ruserdisplayname;
    private TextView rusernametxt, itemnametxt, postdesctxt, meetTxt;
    private CircleImageView ruserimg;
    private TextView browse, records, addnew, chat, profile;
   // private Button viewprofile;
    private int posttype;
    private TextView datetimeTxt, locationTxt, categoryTxt;
    private String datetime;
    private ImageView photo;
    private RatingBar ratingbar;

    //private FloatingActionButton writeoffer;
    private FloatingActionMenu menuFab;
    private com.github.clans.fab.FloatingActionButton fabW, fabP, fabF;


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
       // viewprofile = (Button) findViewById(R.id.viewprofile);
        //writeoffer = (FloatingActionButton) findViewById(R.id.sendoffer);

        //posttypeTxt = (TextView) findViewById(R.id.textView0);
        datetimeTxt = (TextView) findViewById(R.id.datetime);
        locationTxt = (TextView) findViewById(R.id.location);
        categoryTxt = (TextView) findViewById(R.id.categoryTxt);
        meetTxt = (TextView) findViewById(R.id.txt3);
        photo = (ImageView) findViewById(R.id.imgViewPhoto);

        ratingbar = (RatingBar) findViewById(R.id.rating);

        menuFab = (FloatingActionMenu) findViewById(R.id.menufab);
        fabW  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabW);
        fabP  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabP);
        fabF  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabF);

        menuFab.setClosedOnTouchOutside(true);

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
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser().getUid().toString().equals(ruserid)){
            //writeoffer.setVisibility(View.GONE);
            menuFab.setVisibility(View.GONE);

        }

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

                ruserdisplayname = user.getDisplayname();

                ratingbar.setRating(Double.valueOf(user.getRatings()).floatValue());


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

                if(!post.getImgUri().toString().trim().isEmpty()){
                    Picasso.with(PostActivity.this).load(post.getImgUri()).placeholder(R.mipmap.neighberlogo2).into(photo);
                }

                if(post.getPosttype() == 1) {
                    //posttypeTxt.setText("Requesting:");
                    itemnametxt.setText(post.getItemname());
                    meetTxt.setText("Meet The Requester");
                    fabW.setLabelText("Write Offer");
                    fabF.setVisibility(View.GONE);
                }else {
                    //posttypeTxt.setText("Offering:");
                    itemnametxt.setText(post.getItemname());
                    meetTxt.setText("Meet The Lender");
                    fabW.setLabelText("Write Request");
                }
                postdesctxt.setText(post.getPostdesc());

                posttype = post.getPosttype();

                datetime = getDate(post.getTimestamp());

                datetimeTxt.setText("Posted " + datetime);

                SpannableString content = new SpannableString(post.getLocation());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

                locationTxt.setText(content);

                switch(post.getCategory()){
                    case 0:
                        categoryTxt.setText("Others");
                        break;
                    case 1:
                        categoryTxt.setText("Worktools");
                        break;
                    case 2:
                        categoryTxt.setText("Kitchen");
                        break;
                    case 3:
                        categoryTxt.setText("Cleaning");
                        break;
                    case 4:
                        categoryTxt.setText("Office");
                        break;
                    case 5:
                        categoryTxt.setText("Party");
                        break;
                    case 6:
                        categoryTxt.setText("Furniture");
                        break;
                    case 7:
                        categoryTxt.setText("Men's");
                        break;
                    case 8:
                        categoryTxt.setText("Women's");
                        break;
                    case 9:
                        categoryTxt.setText("Sports");
                        break;
                    case 10:
                        categoryTxt.setText("Electronics");
                        break;
                    case 11:
                        categoryTxt.setText("Food");
                        break;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });

        locationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String query = URLEncoder.encode(locationTxt.getText().toString().trim(), "utf-8");

                    //Uri uri = Uri.parse("geo:0,0?q=my+street+address");
                    Uri uri = Uri.parse("geo:0,0?q=" + query);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri); //lat lng or address query
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        fabP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(auth.getCurrentUser().getUid().toString().equals(ruserid)){
                    startActivity(new Intent(PostActivity.this, ProfileActivity.class));
                }else {
                    Intent i = new Intent(PostActivity.this, ViewProfileActivity.class);
                    //Pass info to next activity
                    i.putExtra("ruserid", ruserid);
                    startActivity(i);
                }

            }
        });

        fabW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(posttype == 1) {
                    Intent i = new Intent(PostActivity.this, WriteOfferActivity.class);
                    //Pass info to next activity
                    i.putExtra("ruserid", ruserid);
                    i.putExtra("rpostid", rpostid);
                    i.putExtra("ruserdisplayname", ruserdisplayname);
                    startActivity(i);
                }else if(posttype == 2){
                    Intent i = new Intent(PostActivity.this, WriteOfferActivity2.class);
                    //Pass info to next activity
                    i.putExtra("ruserid", ruserid);
                    i.putExtra("rpostid", rpostid);
                    i.putExtra("ruserdisplayname", ruserdisplayname);
                    startActivity(i);
                }

            }
        });


        fabF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostActivity.this, "Post added to favourites!", Toast.LENGTH_SHORT).show();
            }
        });

        menuFab.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menuFab.isOpened())
                    menuFab.getMenuIconView().setImageResource(R.drawable.ic_close_24dp);
                else
                    menuFab.getMenuIconView().setImageResource(R.drawable.ic_menu_24dp);

                menuFab.toggle(true);
            }
        });

        fabW.setLabelColors(ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary));

        fabF.setLabelColors(ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary));

        fabP.setLabelColors(ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary),
                ContextCompat.getColor(PostActivity.this, R.color.colorPrimary));

    }


    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return date;
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
                Intent i = new Intent(PostActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(PostActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}
