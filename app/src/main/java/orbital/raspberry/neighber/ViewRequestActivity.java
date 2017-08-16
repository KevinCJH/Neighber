package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
    private List<Send> offers;
    private ListView listViewRequests;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_request);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        rpostid = i.getStringExtra("rpostid");

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        offers = new ArrayList<>();

        listViewRequests = (ListView) findViewById(R.id.listRequest);


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("send");

        //attaching value event listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                offers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Send offer = postSnapshot.getValue(Send.class);

                    //If offer is under this post id then display it
                        if(offer.getSendtype() == 1 && offer.getPostid().equals(rpostid)) {

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

                final Send offer = offers.get(position);

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
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return date;
    }



}