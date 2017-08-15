package orbital.raspberry.neighber;

import android.app.ProgressDialog;
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

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class EditPostActivity extends AppCompatActivity {

    private String rpostid;
    private Button submitBtn;
    private EditText itemnameTxt, postdescTxt, location;
    private FirebaseAuth auth;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_post);

        Intent i = getIntent();
        rpostid = i.getStringExtra("rpostid");


        submitBtn = (Button)findViewById(R.id.submitRequest);
        itemnameTxt = (EditText)findViewById(R.id.itemname);
        postdescTxt = (EditText)findViewById(R.id.postdesc);
        location = (EditText)findViewById(R.id.location);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        mDatabase.child(rpostid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                //Display post item name and description

                itemnameTxt.setText(post.getItemname());

                postdescTxt.setText(post.getPostdesc());

                location.setText(post.getLocation());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditPostActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAutocompleteActivity();

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(itemnameTxt.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Must have an item name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(postdescTxt.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Must have item description!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(location.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Must have meetup location!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog pd = new ProgressDialog(EditPostActivity.this);
                pd.setMessage("Saving Changes...");
                pd.show();

                //Edit name of the post
                mDatabase.child(rpostid).child("itemname").setValue(itemnameTxt.getText().toString().trim());

                //Edit desc of the post
                mDatabase.child(rpostid).child("postdesc").setValue(postdescTxt.getText().toString().trim());

                //Edit desc of the post
                mDatabase.child(rpostid).child("location").setValue(location.getText().toString().trim());

                pd.dismiss();

                Toast.makeText(EditPostActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();

                finish();

            }
        });



        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                // Log.i(TAG, "Place Selected: " + place.getName());

                // Format the place's details and display them in the TextView.
              /*  locationTxt.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri())); */

                location.setText(place.getAddress());
/*
                // Display attributions if required.
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
                } else {
                    mPlaceAttribution.setText("");
                }
                */
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                //Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }

    }


    public void openAutocompleteActivity(){
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setBoundsBias(new LatLngBounds(
                    new LatLng(1.218504, 103.652802),
                    new LatLng(1.414832, 103.939819))).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            // Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

}
