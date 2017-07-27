package orbital.raspberry.neighber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReturnAgreementActivity2 extends AppCompatActivity {

    private String agreementid, postid;
    private TextView browse, records, addnew, chat, profile;
    private Button submitBtn;
    private EditText returndesc;
    private String ruserid;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_return_agreement);

        //Get userid based on which item was click in the previous activity
        Intent i = getIntent();
        agreementid = i.getStringExtra("agreementid");
        postid = i.getStringExtra("postid");
        ruserid = i.getStringExtra("ruserid");


        submitBtn = (Button)findViewById(R.id.submitRequest);
        returndesc = (EditText)findViewById(R.id.returndesc);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String returnagreement = returndesc.getText().toString().trim();

                FirebaseDatabase.getInstance().getReference("send").child(agreementid).child("returnagreementdesc").setValue(returnagreement);
                FirebaseDatabase.getInstance().getReference("send").child(agreementid).child("status").setValue(4);
                FirebaseDatabase.getInstance().getReference("posts").child(postid).child("status").setValue(4);

                Toast.makeText(ReturnAgreementActivity2.this, "Please meet up with user to return the item", Toast.LENGTH_LONG).show();

                Intent i2 = new Intent(ReturnAgreementActivity2.this, OfferRateUserActivity.class);
                i2.putExtra("ruserid", ruserid);
                i2.putExtra("rrecordid", agreementid);
                startActivity(i2);

                finish();

            }
        });

    }

}
