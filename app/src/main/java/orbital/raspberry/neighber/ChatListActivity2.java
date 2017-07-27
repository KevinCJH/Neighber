package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

public class ChatListActivity2 extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView browse, records, addnew, chat, profile;
    private List<ChatItem> chats;
    private ListView chatList;
    private String userid;
    private TextView post, active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist2);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();

        chats = new ArrayList<>();

        chatList = (ListView) findViewById(R.id.listchat);

        //////////////Navigations/////////////
        records = (TextView) findViewById(R.id.action_records);
        addnew = (TextView) findViewById(R.id.action_addnew);
        chat = (TextView) findViewById(R.id.action_chat);
        profile = (TextView) findViewById(R.id.action_profile);
        browse = (TextView) findViewById(R.id.action_browse);
        post = (TextView) findViewById(R.id.action_post);
        active = (TextView) findViewById(R.id.action_active);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatListActivity2.this, ChatListActivity.class));
            }
        });


        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatListActivity2.this, MainActivity.class));
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatListActivity2.this, BorrowerRecordsActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatListActivity2.this, AddNewActivity.class));
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
                startActivity(new Intent(ChatListActivity2.this, ProfileActivity.class));
            }
        });

        //////////////////////End Navigation////////////////////////////


        final DatabaseReference oDatabase = FirebaseDatabase.getInstance().getReference("send");

        //attaching value event listener
        oDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous list
                chats.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Send offer = postSnapshot.getValue(Send.class);

                    if(offer.getSendtype() == 1 && offer.getStatus() >= 2 && offer.getOwnerid().equals(userid)) {

                        ChatItem newchat = new ChatItem(offer.getChatid(), offer.getItemname(), offer.getTargetname(), offer.getOtherimg(), offer.getSendtype());
                        chats.add(newchat);
                    }
                    else if(offer.getSendtype() == 2 && offer.getStatus() >= 2 && offer.getOwnerid().equals(userid)){
                        ChatItem newchat = new ChatItem(offer.getChatid(), offer.getItemname(), offer.getTargetname(), offer.getOtherimg(), offer.getSendtype());
                        chats.add(newchat);
                    }
                }

                 //creating adapter
                ChatList reqAdapter = new ChatList(ChatListActivity2.this, chats);
                //attaching adapter to the listview
                chatList.setAdapter(reqAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatItem chat = chats.get(position);

               Intent i = new Intent(ChatListActivity2.this, ChatActivity.class);
                i.putExtra("chatroomid", chat.getChatroomid());
                i.putExtra("itemname", chat.getItemname());
                startActivity(i);

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
                Intent i = new Intent(ChatListActivity2.this, LoginpageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(ChatListActivity2.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////End top menu////////////////////////


}