package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewRequestActivity extends AppCompatActivity {

    private String rpostid;
    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private List<OfferToLendPost> offers;
    private ListView listViewRequests;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        rpostid = i.getStringExtra("rpostid");

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        offers = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);


        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewRequestActivity.this, MainActivity.class));
                finish();
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewRequestActivity.this, BorrowerRecordsActivity.class));
                finish();
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewRequestActivity.this, AddNewActivity.class));
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
                startActivity(new Intent(ViewRequestActivity.this, ProfileActivity.class));
                finish();
            }
        });

        //////////////////////End Navigation////////////////////////////

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("offertolend");

        //attaching value event listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                offers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    OfferToLendPost offer = postSnapshot.getValue(OfferToLendPost.class);

                    //If offer is under this post id then display it
                        if(offer.getPostid().equals(rpostid)) {

                            String datetime = getDate(offer.getTimestamp());

                            offer.setDatetime(datetime);

                            //adding to the list
                            offers.add(offer);

                        }
                }

                //creating adapter
                OfferList2 reqAdapter = new OfferList2(ViewRequestActivity.this, offers);
                //attaching adapter to the listview
                listViewRequests.setAdapter(reqAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final OfferToLendPost offer = offers.get(position);

                Intent i = new Intent(ViewRequestActivity.this, ViewRequestSentActivity.class);
                i.putExtra("ruserid", offer.getOwnerid());
                i.putExtra("rofferid", offer.getRecordid());
                startActivity(i);

            }
        });

    }


    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        return date;
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
                startActivity(new Intent(ViewRequestActivity.this, LoginpageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}