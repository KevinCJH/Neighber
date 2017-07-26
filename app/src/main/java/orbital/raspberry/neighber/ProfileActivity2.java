package orbital.raspberry.neighber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity2 extends AppCompatActivity {
    private Button saveChange;
    private CircleImageView imgView;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private ProgressDialog pd;
    private EditText displayname;
    private TextView email, ratings;
    private RatingBar ratingbar;
    private TextView browse, records, addnew, chat, profile;
    private boolean imgclickflag = false;
    private TextView requests, offers;
    private List<Post> posts;
    private ListView listViewRequests;
    private int editmode;


    //creating reference to firebase storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://neighber-b5ee0.appspot.com");
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        editmode = 0;

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
                startActivity(new Intent(ProfileActivity2.this, ProfileActivity.class));
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
                startActivity(new Intent(ProfileActivity2.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity2.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity2.this, AddNewActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity2.this, ChatListActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //////////////////////End Navigation////////////////////////////


        saveChange = (Button)findViewById(R.id.saveChange);
        imgView = (CircleImageView)findViewById(R.id.imgView);
        displayname = (EditText)findViewById(R.id.displayname);
        email = (TextView) findViewById(R.id.email);
        ratings = (TextView) findViewById(R.id.ratingvalue);
        ratingbar = (RatingBar) findViewById(R.id.rating);

        listViewRequests = (ListView) findViewById(R.id.listRequest);

        posts = new ArrayList<>();

        displayname.setEnabled(false);

        //To store userdata
        final String[] userdata = new String[1];

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        final FirebaseUser currentFirebaseUser = auth.getCurrentUser() ;
        final String userid = currentFirebaseUser.getUid();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        //Set profile picture of user
                         String imageUri = user.getImgUri();
                         Picasso.with(getBaseContext()).load(imageUri).placeholder(R.mipmap.defaultprofile).into(imgView);

                        //Store user display name
                        userdata[0] = user.getDisplayname();

                        //Fill up profile details
                        displayname.setText(user.getDisplayname());
                        email.setText(user.getEmail());
                        ratings.setText("Ratings: " + user.getRatings());
                        ratingbar.setRating(Double.valueOf(user.getRatings()).floatValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity2.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
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

                    //If post type is request aka 1
                    if(post.getPosttype() == 1 && post.getUserid().toString().equals(userid) && post.getStatus() <= 5) {

                        //adding to the list
                        posts.add(post);

                    }
                }

                //creating adapter
                RecordsList reqAdapter = new RecordsList(ProfileActivity2.this, posts);
                //attaching adapter to the listview
                listViewRequests.setAdapter(reqAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //////ITEMS OWNED LIST
        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Post post = posts.get(position);

                //DELETE DIALOG
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                FirebaseDatabase.getInstance().getReference("posts").child(post.getPostid()).removeValue();
                                deleteRecords(post.getPostid());
                                posts.remove(position);
                                Toast.makeText(ProfileActivity2.this, "Post has been deleted", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };


                //Status pending
                if(post.getStatus() == 1) {

                    CharSequence options[] = new CharSequence[]{"View Offer", "Update this Post", "Delete this Post"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity2.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    if (post.getRecordcount() <= 0) {
                                        Toast.makeText(ProfileActivity2.this, "No offers were made to you", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(ProfileActivity2.this, ViewOfferActivity.class);
                                        i.putExtra("rpostid", post.getPostid());
                                        startActivity(i);
                                    }
                                    break;
                                case 1:
                                    Intent i = new Intent(ProfileActivity2.this, EditPostActivity.class);
                                    i.putExtra("rpostid", post.getPostid());
                                    startActivity(i);
                                    break;
                                case 2:
                                    AlertDialog.Builder builderdel = new AlertDialog.Builder(ProfileActivity2.this);
                                    builderdel.setMessage("Confirm Delete?").setPositiveButton("Confirm", dialogClickListener)
                                            .setNegativeButton("Cancel", dialogClickListener).show();

                                    break;
                            }
                        }
                    });
                    builder.show();
                }

                //wait for agreement
                else if(post.getStatus() == 2) {

                    CharSequence options[] = new CharSequence[]{"Chat with user", "View lender profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity2.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i1 = new Intent(ProfileActivity2.this, ChatActivity.class);
                                    i1.putExtra("chatroomid", post.getChatid());
                                    i1.putExtra("itemname", post.getItemname());
                                    startActivity(i1);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(ProfileActivity2.this, ViewProfileActivity.class);
                                    i2.putExtra("ruserid", post.getOtherid());
                                    startActivity(i2);
                                    break;
                            }
                        }
                    });
                    builder.show();

                }

                //accept agreement
                else if(post.getStatus() == 3) {

                    CharSequence options[] = new CharSequence[]{"View item agreement", "Chat with user", "View lender profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity2.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i1 = new Intent(ProfileActivity2.this, AgreementActivity.class);
                                    i1.putExtra("ruserid", post.getOtherid());
                                    i1.putExtra("rofferid", post.getAgreementid());
                                    startActivity(i1);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(ProfileActivity2.this, ChatActivity.class);
                                    i2.putExtra("chatroomid", post.getChatid());
                                    i2.putExtra("itemname", post.getItemname());
                                    startActivity(i2);
                                    break;
                                case 2:
                                    Intent i3 = new Intent(ProfileActivity2.this, ViewProfileActivity.class);
                                    i3.putExtra("ruserid", post.getOtherid());
                                    startActivity(i3);
                                    break;
                            }
                        }
                    });
                    builder.show();

                }

                //borrowing in progress
                else if(post.getStatus() == 4) {

                    CharSequence options[] = new CharSequence[]{"Return Item", "Chat with user", "View lender profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity2.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i1 = new Intent(ProfileActivity2.this, ReturnAgreementActivity.class);
                                    i1.putExtra("agreementid", post.getAgreementid());
                                    i1.putExtra("postid", post.getPostid());
                                    startActivity(i1);
                                    break;
                                case 1:
                                    Intent i2 = new Intent(ProfileActivity2.this, ChatActivity.class);
                                    i2.putExtra("chatroomid", post.getChatid());
                                    i2.putExtra("itemname", post.getItemname());
                                    startActivity(i2);
                                    break;
                                case 2:
                                    Intent i3 = new Intent(ProfileActivity2.this, ViewProfileActivity.class);
                                    i3.putExtra("ruserid", post.getOtherid());
                                    startActivity(i3);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

                //return in progress
                else if(post.getStatus() == 5) {

                    CharSequence options[] = new CharSequence[]{"Chat with user", "View lender profile"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity2.this);
                    builder.setTitle("Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0:
                                    Intent i2 = new Intent(ProfileActivity2.this, ChatActivity.class);
                                    i2.putExtra("chatroomid", post.getChatid());
                                    i2.putExtra("itemname", post.getItemname());
                                    startActivity(i2);
                                    break;
                                case 1:
                                    Intent i3 = new Intent(ProfileActivity2.this, ViewProfileActivity.class);
                                    i3.putExtra("ruserid", post.getOtherid());
                                    startActivity(i3);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }


            }
        });

///////////////////////////////END LIST ITEM//////////////////

        pd = new ProgressDialog(this);
        pd.setMessage("Saving Changes...");

        //Go to gallery when user click on image view
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editmode == 1) {

                    imgclickflag = true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                }

            }
        });


        //Save change of profile
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editmode == 0){
                    editmode = 1;
                    displayname.setEnabled(true);
                    saveChange.setText("Save Changes");

                }else {


                    pd.show();

                    //If display name is changed
                    if (!userdata[0].equals(displayname.getText().toString().trim())) {
                        mDatabase.child(userid).child("displayname").setValue(displayname.getText().toString().trim());
                    }

                    //If image has been modified
                    if (imgclickflag) {
                        if (filePath != null) {

                            StorageReference childRef = storageRef.child(userid + ".jpg");

                            //uploading the image
                            UploadTask uploadTask = childRef.putFile(filePath);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    pd.dismiss();
                                    @SuppressWarnings("VisibleForTests") String dlurl = taskSnapshot.getDownloadUrl().toString();
                                    mDatabase.child(userid).child("imguri").setValue(dlurl);
                                    Toast.makeText(ProfileActivity2.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(ProfileActivity2.this, "Fail to upload image" + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity2.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity2.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        editmode = 0;
                        displayname.setEnabled(false);
                        saveChange.setText("Edit");

                    }
                }
            }
        });

    }

    //Return the image from user gallery and set it into image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteRecords(final String postid){

        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("send");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Send offer = postSnapshot.getValue(Send.class);

                    String recordid = offer.getRecordid();

                    if(offer.getPostid().equals(postid)){
                        FirebaseDatabase.getInstance().getReference("send").child(recordid).removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                Intent i = new Intent(ProfileActivity2.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(ProfileActivity2.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////

}