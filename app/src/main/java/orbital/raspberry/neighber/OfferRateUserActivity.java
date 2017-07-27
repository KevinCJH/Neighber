package orbital.raspberry.neighber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferRateUserActivity extends AppCompatActivity {

    private RatingBar ratingbar;
    private Button rateuser;
    private TextView browse, records, addnew, chat, profile;
    private String ruserid;
    private String rrecordid;
    private double userrating;
    private int totalrater;

    //creating reference to firebase storage
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rateuser);

        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rrecordid = i.getStringExtra("rrecordid");

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
           //     String imageUri = user.getImgUri();
             //   Picasso.with(getBaseContext()).load(imageUri).placeholder(R.mipmap.defaultprofile).into(imgView);

                //Fill up profile details
//                displayname.setText(user.getDisplayname());

                userrating = user.getRatings();

                totalrater = user.getTotalvote();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OfferRateUserActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        rateuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog pd = new ProgressDialog(OfferRateUserActivity.this);
                pd.setMessage("Submitting...");
                pd.show();

                double newRate = ratingbar.getRating();

                int newtotalrater = totalrater + 1;

                userrating = ((userrating * totalrater) + newRate) / newtotalrater;

                //Round off the value to two decimal place
                userrating = round(userrating, 2);

                FirebaseDatabase.getInstance().getReference("users").child(ruserid).child("ratings").setValue(userrating);
                FirebaseDatabase.getInstance().getReference("users").child(ruserid).child("totalvote").setValue(newtotalrater);
                FirebaseDatabase.getInstance().getReference("send").child(rrecordid).child("rated").setValue(1);

                pd.dismiss();

                Toast.makeText(OfferRateUserActivity.this, "Rating has been submitted", Toast.LENGTH_SHORT).show();

                finish();

            }
        });


    }

    //Round off value to two decimal place
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}