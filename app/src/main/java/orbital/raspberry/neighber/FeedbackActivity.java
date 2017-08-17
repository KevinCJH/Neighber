package orbital.raspberry.neighber;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);

        TextView emailtxt = (TextView) findViewById(R.id.emailtxt);

        SpannableString content = new SpannableString("raspberryorbital2017@gmail.com");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        emailtxt.setText(content);

        emailtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "raspberryorbital2017@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Neighber");

                startActivity(Intent.createChooser(intent, "Send Email"));
            */
                String mailto = "mailto:raspberryorbital2017@gmail.com" +
                       // "?cc=" + "alice@example.com" +
                        "?subject=" + Uri.encode("Feedback to Neighber");
                //+ "&body=" + Uri.encode(bodyText);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                //emailIntent.setType("message/rfc822");
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(FeedbackActivity.this, "No email app available", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
