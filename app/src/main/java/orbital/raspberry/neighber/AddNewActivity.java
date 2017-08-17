package orbital.raspberry.neighber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddNewActivity extends AppCompatActivity {

    private TextView browse, records, addnew, chat, profile;
    private TextView worktools, kitchen, cleaning, more;
    private Uri filePath;
    private ImageView photo;
    private Button submitBtn, lendtype, borrowtype, uploadphoto;
    private EditText itemnameTxt, postdescTxt, locationTxt, rentalTxt;
    private String userName;
    //1 for borrow, 2 for lending
    private int posttype;
    //1 for worktool, 2 for kitchen, 3 for furniture, 4 for others
    private int categorytype;
    public static final int GRID_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 8;
    private static final int PICK_IMAGE_REQUEST = 3;

    private String username;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://neighber-b5ee0.appspot.com");
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewActivity.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewActivity.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewActivity.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewActivity.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////

        submitBtn = (Button)findViewById(R.id.submitRequest);
        itemnameTxt = (EditText)findViewById(R.id.itemname);
        postdescTxt = (EditText)findViewById(R.id.postdesc);
        lendtype = (Button)findViewById(R.id.lendtype);
        borrowtype = (Button)findViewById(R.id.borrowtype);
        locationTxt = (EditText)findViewById(R.id.locationtxt);
        rentalTxt = (EditText)findViewById(R.id.rentaltxt);
        //Modify it into Currency edittext
        rentalTxt.addTextChangedListener(new MoneyTextWatcher(rentalTxt));

        worktools = (TextView) findViewById(R.id.worktools);
        kitchen = (TextView) findViewById(R.id.kitchen);
        cleaning = (TextView) findViewById(R.id.cleaning);
        more = (TextView) findViewById(R.id.more);

        photo = (ImageView) findViewById(R.id.imgView);
        uploadphoto = (Button)findViewById(R.id.takephoto);

        posttype = 1;

        categorytype = 0;

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final FirebaseUser currentFirebaseUser = auth.getCurrentUser() ;
        final String userid = currentFirebaseUser.getUid();

        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        uDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                username = user.getDisplayname();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddNewActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });


        worktools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorytype = 1;
                worktools.setBackground(ContextCompat.getDrawable(AddNewActivity.this,R.drawable.navborder));

                kitchen.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                kitchen.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                cleaning.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                cleaning.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                more.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                more.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));

            }
        });

        kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorytype = 2;
                kitchen.setBackground(ContextCompat.getDrawable(AddNewActivity.this,R.drawable.navborder));

                worktools.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                worktools.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                cleaning.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                cleaning.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                more.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                more.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));

            }
        });

        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorytype = 3;
                cleaning.setBackground(ContextCompat.getDrawable(AddNewActivity.this,R.drawable.navborder));

                worktools.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                worktools.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                kitchen.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                kitchen.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                more.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                more.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));

            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                categorytype = 4;*/

                worktools.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                worktools.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                kitchen.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                kitchen.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                cleaning.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                cleaning.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));

                Intent intent = new Intent(AddNewActivity.this, GridActivity.class);
                startActivityForResult(intent,GRID_REQUEST);


            }
        });

        borrowtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postdescTxt.setHint("Provide description for your request. Eg. Duration of borrow?");
                rentalTxt.setHint("Offer Fee");
                submitBtn.setText("Post Request");
                posttype = 1;
                lendtype.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                lendtype.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                borrowtype.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                borrowtype.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
            }
        });

        lendtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postdescTxt.setHint("Provide description for your offer. Eg. Size of item?");
                rentalTxt.setHint("Rental Fee");
                submitBtn.setText("Post Offer");
                posttype = 2;
                lendtype.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));
                lendtype.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                borrowtype.setBackgroundColor(ContextCompat.getColor(AddNewActivity.this,R.color.fadeorange));
                borrowtype.setTextColor(ContextCompat.getColor(AddNewActivity.this,R.color.colorPrimary));

            }
        });


        locationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity();
            }
        });



        /*
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               if(position == 0){
                   postdescTxt.setHint("Provide description for your request. Eg. Duration of borrow?");
                   submitBtn.setText("Post Request");
               }else{
                   postdescTxt.setHint("Provide description for your offer. Eg. Size of item?");
                   submitBtn.setText("Post Offer");
               }
            }
        }); */


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String location = locationTxt.getText().toString().trim();

                final String fee = rentalTxt.getText().toString().trim();

                //Toast.makeText(getApplicationContext(), "Result: " + fee, Toast.LENGTH_LONG).show();

                if (TextUtils.isEmpty(itemnameTxt.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Must have an item name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(postdescTxt.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Must have item description!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(location)){
                    Toast.makeText(getApplicationContext(), "Must have meetup location!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(fee)){
                    Toast.makeText(getApplicationContext(), "Must specify fee!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog pd = new ProgressDialog(AddNewActivity.this);
                pd.setMessage("Posting...");
                pd.show();

                // get unique post id from firebase
                final String postid = mDatabase.push().getKey();

                //Get the post type based on which item is selected in the spinner
            //    int postType = spinner.getSelectedIndex() + 1;


                //UPLOAD IMAGE
                if (filePath != null) {

                    StorageReference childRef = storageRef.child(postid + ".jpg");

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    byte[] idata = outputStream.toByteArray();

                    //uploading the image
                    UploadTask uploadTask = childRef.putBytes(idata);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            @SuppressWarnings("VisibleForTests") String dlurl = taskSnapshot.getDownloadUrl().toString();

                            //Create newpost object
                            Post newpost = new Post(postid, userid, itemnameTxt.getText().toString().trim(),
                                    username, postdescTxt.getText().toString().trim(), posttype, categorytype, location, fee);

                            //Add post to database
                            mDatabase.child(postid).setValue(newpost);

                            //Add timestamp to the post
                            mDatabase.child(postid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                            mDatabase.child(postid).child("imguri").setValue(dlurl);


                            Toast.makeText(AddNewActivity.this, "Post Submitted! You may edit/delete the post in My Profiles tab", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddNewActivity.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddNewActivity.this, "Fail to upload image, please try again." + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{

                    //Create newpost object
                    Post newpost = new Post(postid, userid, itemnameTxt.getText().toString().trim(),
                            username, postdescTxt.getText().toString().trim(), posttype, categorytype, location, fee);

                    //Add post to database
                    mDatabase.child(postid).setValue(newpost);

                    //Add timestamp to the post
                    mDatabase.child(postid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    mDatabase.child(postid).child("imguri").setValue("");

                     Toast.makeText(AddNewActivity.this, "Post Submitted! You may edit/delete the post in My Profiles tab", Toast.LENGTH_LONG).show();
                     pd.dismiss();
                     startActivity(new Intent(AddNewActivity.this, MainActivity.class));
                     finish();

                }


            }
        });


        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                CharSequence options[] = new CharSequence[]{"Browse Gallery", "Use Camera"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(AddNewActivity.this);
                builder.setTitle("Upload Item Image");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        switch (pos) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                                break;
                            case 1:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                Toast.makeText(AddNewActivity.this, "It is recommended for you to take a landscape photo for better quality", Toast.LENGTH_LONG).show();

                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                }
                        }
                    }
                });
                builder.show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GRID_REQUEST) {
            if(resultCode == RESULT_OK) {
                int num = data.getIntExtra("categorynum", 0);

                more.setBackground(ContextCompat.getDrawable(AddNewActivity.this,R.drawable.navborder));


                //Toast.makeText(AddNewActivity.this, "Category: " + num, Toast.LENGTH_LONG).show();

                switch(num) {
                    case 4:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.office, 0, 0);
                        more.setText("Office");
                        categorytype = 4;
                        break;
                    case 5:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.party, 0, 0);
                        more.setText("Party");
                        categorytype = 5;
                        break;
                    case 6:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.furniture, 0, 0);
                        more.setText("Furniture");
                        categorytype = 6;
                        break;
                    case 7:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.shirtf, 0, 0);
                        more.setText("Women's");
                        categorytype = 7;
                        break;
                    case 8:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.shirtm, 0, 0);
                        more.setText("Men's");
                        categorytype = 8;
                        break;
                    case 9:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.sports, 0, 0);
                        more.setText("Sports");
                        categorytype = 9;
                        break;
                    case 10:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.electrical, 0, 0);
                        more.setText("Electronics");
                        categorytype = 10;
                        break;
                    case 11:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.food, 0, 0);
                        more.setText("Food");
                        categorytype = 11;
                        break;
                    case 0:
                        more.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.others, 0, 0);
                        more.setText("Others");
                        categorytype = 0;
                        break;

                }

            }
            if (resultCode == RESULT_CANCELED) {
                //showError("The selection was cancelled");
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
           // Bitmap img = (Bitmap) data.getExtras().get("data");

            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                photo.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

           // photo.setImageBitmap(img);
        } else  if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                photo.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
               // Log.i(TAG, "Place Selected: " + place.getName());

                // Format the place's details and display them in the TextView.
              /*  locationTxt.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri())); */

              locationTxt.setText(place.getName());
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
                Intent i = new Intent(AddNewActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(AddNewActivity.this, SettingsActivity.class));
                break;
            case R.id.action_favourite:
                startActivity(new Intent(AddNewActivity.this, FavouriteActivity.class));
                break;
            case R.id.action_feedback:
                startActivity(new Intent(AddNewActivity.this, FeedbackActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////
}
