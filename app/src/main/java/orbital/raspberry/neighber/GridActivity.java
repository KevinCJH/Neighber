package orbital.raspberry.neighber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GridActivity extends AppCompatActivity {

    private TextView office, party, furniture;
    private TextView ladies, men, sports;
    private TextView electronic, food, others;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_grid);

        office = (TextView) findViewById(R.id.office);
        party = (TextView) findViewById(R.id.party);
        furniture = (TextView) findViewById(R.id.furniture);
        ladies = (TextView) findViewById(R.id.shirtf);
        men = (TextView) findViewById(R.id.shirtm);
        sports = (TextView) findViewById(R.id.sports);
        electronic = (TextView) findViewById(R.id.electronics);
        food = (TextView) findViewById(R.id.food);
        others = (TextView) findViewById(R.id.others);

        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 4);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 5);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 6);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        ladies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 7);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 8);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 9);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        electronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 10);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 11);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categorynum", 0);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });


/*
        Intent returnIntent = new Intent();
        returnIntent.putExtra("imagePath", ..);
        setResult(RESULT_OK,returnIntent);
        finish();
*/

    }
}
