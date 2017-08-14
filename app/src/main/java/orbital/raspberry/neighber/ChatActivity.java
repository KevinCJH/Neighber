package orbital.raspberry.neighber;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;
import com.squareup.picasso.Picasso;

import java.security.GeneralSecurityException;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String userid;
    private String username;
    private FirebaseListAdapter<ChatMessage> adapter;
    private String chatroomid;
    private String itemname;
    private String offerid, postid;
    private String cipherpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        chatroomid = i.getStringExtra("chatroomid");
        itemname = i.getStringExtra("itemname");
        offerid = i.getStringExtra("offerid");
        postid = i.getStringExtra("postid");

        getSupportActionBar().setTitle("" + itemname);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        cipherpassword = cipher(chatroomid);

      //  Toast.makeText(ChatActivity.this, "Cipher: " + cipherpw, Toast.LENGTH_LONG).show();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                username = user.getDisplayname();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });


        final DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference("chatmessage").child(chatroomid);

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                if(!input.getText().toString().trim().isEmpty()) {

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database

                    String chatmessage = input.getText().toString().trim();

                    try {

                        String msgid = cDatabase.push().getKey();

                        String encryptmessage = AESCrypt.encrypt(cipherpassword, chatmessage);

                        ChatMessage msg = new ChatMessage(msgid, encryptmessage, username, userid);

                        cDatabase.child(msgid).setValue(msg);

                        if(chatmessage.length() > 30) {

                            String newlastmsg = chatmessage.substring(0, 29) + "...";

                            FirebaseDatabase.getInstance().getReference("send").child(offerid).child("lastmsg").setValue(newlastmsg);
                            FirebaseDatabase.getInstance().getReference("posts").child(postid).child("lastmsg").setValue(newlastmsg);
                        }else{
                            FirebaseDatabase.getInstance().getReference("send").child(offerid).child("lastmsg").setValue(chatmessage);
                            FirebaseDatabase.getInstance().getReference("posts").child(postid).child("lastmsg").setValue(chatmessage);
                        }


                        // Clear the input
                        input.setText("");

                    } catch (GeneralSecurityException e) {
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }



                    //  cDatabase.child("latestmsg").setValue(input.getText().toString().trim());

                /*
                FirebaseDatabase.getInstance()
                        .getReference("chatmessage")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );
*/


                }
            }
        });


        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.newmessage, FirebaseDatabase.getInstance().getReference("chatmessage").child(chatroomid)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                LinearLayout bubble = (LinearLayout)v.findViewById(R.id.chatbubble);
                LinearLayout wholemsg = (LinearLayout)v.findViewById(R.id.msg);
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
             //   TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

               // messageText.setGravity();

                if(username.equals(model.getMessageUser())) {

                    wholemsg.setGravity(Gravity.RIGHT | Gravity.END);
                    bubble.setBackgroundResource(R.drawable.yourmsg);
                    messageText.setTextColor(Color.WHITE);
                    messageTime.setTextColor(Color.WHITE);

                }else{

                    wholemsg.setGravity(Gravity.LEFT | Gravity.START);
                    bubble.setBackgroundResource(R.drawable.othermsg);

                }

                try {
                    String decryptedmessage = AESCrypt.decrypt(cipherpassword, model.getMessageText());

                    // Set their text
                    messageText.setText(decryptedmessage);
                    //      messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));


                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Failed to retrieve message", Toast.LENGTH_SHORT).show();

                }


            }
        };

        listOfMessages.setAdapter(adapter);
        //listOfMessages.setSelection(adapter.getCount() - 1);


    }



    // Rotate a string k-positions
    public static String cipher(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(cipher(s.charAt(i), 3));
        }
        return sb.toString();
    }


    // Rotate a character k-positions
    public static char cipher(char c, int k) {
        // declare some helping constants
        final int alphaLength = 26;
        final char asciiShift = Character.isUpperCase(c) ? 'A' : 'a';
        final int cipherShift = k % alphaLength;

        // shift down to 0..25 for a..z
        char shifted = (char) (c - asciiShift);
        // rotate the letter and handle "wrap-around" for negatives and value >= 26
        shifted = (char) ((shifted + cipherShift + alphaLength) % alphaLength);
        // shift back up to english characters
        return (char) (shifted + asciiShift);
    }


}
