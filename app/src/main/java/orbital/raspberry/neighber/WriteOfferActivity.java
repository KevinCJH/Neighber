package orbital.raspberry.neighber;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WriteOfferActivity extends AppCompatActivity {

    private String ruserid, rpostid, ritemname, ruserdisplayname;
    private TextView browse, records, addnew, chat, profile;
    private int rrecordcount;
    private Button submitBtn, takephoto;
    private EditText offerdescTxt;
    private TextView itemnameTxt;
    private static final int CAMERA_REQUEST = 1888;
    private Uri filePath;
    private ImageView photo;
    private int cat;
    private String olduri;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://neighber-b5ee0.appspot.com");
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_offer);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        ruserid = i.getStringExtra("ruserid");
        rpostid = i.getStringExtra("rpostid");
        ruserdisplayname = i.getStringExtra("ruserdisplayname");


        submitBtn = (Button)findViewById(R.id.submitRequest);
        takephoto = (Button)findViewById(R.id.takephoto);
        offerdescTxt = (EditText)findViewById(R.id.offerdesc);
        itemnameTxt = (TextView) findViewById(R.id.itemnametxt) ;
        photo = (ImageView) findViewById(R.id.imgView);

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
                Toast.makeText(WriteOfferActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        final DatabaseReference pDatabase = FirebaseDatabase.getInstance().getReference("posts");
        pDatabase.child(rpostid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                ritemname = post.getItemname();

                itemnameTxt.setText("You are offering: " + ritemname);

                rrecordcount = post.getRecordcount();

                cat = post.getCategory();

                olduri = post.getImgUri();

                //Toast.makeText(WriteOfferActivity.this, "Count: " + rrecordcount, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WriteOfferActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pd = new ProgressDialog(WriteOfferActivity.this);
                pd.setMessage("Sending...");
                pd.show();

                // get unique post id from firebase
                final String recordid = mDatabase.push().getKey();


                //UPLOAD IMAGE
                if (filePath != null) {

                    StorageReference childRef = storageRef.child(recordid + ".jpg");

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

                            //Create new offertoborrowpost object
                            Send newoffer = new Send(2, recordid, rpostid, ritemname, userid, userdisplayname[0],ruserid, ruserdisplayname);

                            //newoffer.setAgreementdesc(offerdescTxt.getText().toString().trim());
                            newoffer.setOfferdesc(offerdescTxt.getText().toString().trim());

                            //Add post to database
                            mDatabase.child(recordid).setValue(newoffer);

                            mDatabase.child(recordid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                            mDatabase.child(recordid).child("category").setValue(cat);
                            mDatabase.child(recordid).child("imguri").setValue(dlurl);

                            //Update number of offers made to the post

                            rrecordcount += 1;

                            pDatabase.child(rpostid).child("recordcount").setValue(rrecordcount);

                            Toast.makeText(WriteOfferActivity.this, "Offer Submitted! You may view/delete the offer in the Records tab", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(WriteOfferActivity.this, MainActivity2.class));
                            finish();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(WriteOfferActivity.this, "Fail to upload image, please try again." + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{

                    //Create new offertoborrowpost object
                    Send newoffer = new Send(2, recordid, rpostid, ritemname, userid, userdisplayname[0],ruserid, ruserdisplayname);

                    //newoffer.setAgreementdesc(offerdescTxt.getText().toString().trim());
                    newoffer.setOfferdesc(offerdescTxt.getText().toString().trim());

                    //Add post to database
                    mDatabase.child(recordid).setValue(newoffer);

                    mDatabase.child(recordid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    mDatabase.child(recordid).child("category").setValue(cat);
                    mDatabase.child(recordid).child("imguri").setValue(olduri);

                    //Update number of offers made to the post

                    rrecordcount += 1;

                    pDatabase.child(rpostid).child("recordcount").setValue(rrecordcount);

                    Toast.makeText(WriteOfferActivity.this, "Offer Submitted! You may view/delete the offer in the Records tab", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(WriteOfferActivity.this, MainActivity2.class));
                    finish();

                }

            }
        });

        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
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
        }
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
                Intent i = new Intent(WriteOfferActivity.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(WriteOfferActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////
}
