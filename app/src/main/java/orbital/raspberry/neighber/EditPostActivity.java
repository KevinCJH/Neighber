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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText itemnameTxt, postdescTxt;
    private FirebaseAuth auth;

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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditPostActivity.this, "Failed to retrieve post data", Toast.LENGTH_SHORT).show();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog pd = new ProgressDialog(EditPostActivity.this);
                pd.setMessage("Saving Changes...");
                pd.show();

                //Edit name of the post
                mDatabase.child(rpostid).child("itemname").setValue(itemnameTxt.getText().toString().trim());

                //Edit desc of the post
                mDatabase.child(rpostid).child("postdesc").setValue(postdescTxt.getText().toString().trim());

                pd.dismiss();

                Toast.makeText(EditPostActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();

                finish();

            }
        });

    }

}
