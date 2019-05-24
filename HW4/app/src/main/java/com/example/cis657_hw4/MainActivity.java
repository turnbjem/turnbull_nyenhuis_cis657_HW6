package com.example.cis657_hw4;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    public static final int DIST_UNIT = 1;
    Boolean begin = true;
    Location loc1 = new Location("GPS");
    Location loc2 = new Location("GPS");

    EditText lat1;
    EditText long1;
    EditText lat2;
    EditText long2;

    EditText[] inputarray = new EditText[4];
    String lat1str, lat2str, long1str, long2str;

    TextView distanceresult;
    TextView bearingresult;

    Double distance;
    Double bearing;

    String DistUnit = "kilometers";
    String BearUnit = "degrees";

    Button CalculateButton;
    Button ClearButton;

    int count =0;
//onCreate starts up the application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting view to a coordinatorlayout that includes the activity_main constraint layout
        setContentView(R.layout.activity_main_coordinatorlayout);

        lat1 = (EditText) findViewById(R.id.lat1);
        long1 = (EditText) findViewById(R.id.long1);
        lat2 = (EditText) findViewById(R.id.lat2);
        long2 = (EditText) findViewById(R.id.long2);


        distanceresult = (TextView) findViewById(R.id.distanceresult);
        bearingresult = (TextView) findViewById(R.id.bearingresult);

        CalculateButton = (Button) findViewById(R.id.CalculateButton);
        ClearButton = (Button) findViewById(R.id.ClearButton);

        //Sets an action listener for the button that gives instructions for post-button push
        CalculateButton.setOnClickListener(v -> {
            lat1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            lat2.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long2.onEditorAction(EditorInfo.IME_ACTION_DONE);

            inputToString();

            //Informs the user of incomplete data using a "snackbar" (pops up on bottom of screen)
            if ((lat1str.length() == 0) || (lat2str.length() == 0) ||
                    (long1str.length() == 0) || (long2str.length() == 0)) {
                Snackbar.make(CalculateButton, "One or more of the text fields is incomplete",
                        Snackbar.LENGTH_LONG).show();
            }

            else calcDistance();

        });

        ClearButton.setOnClickListener(y -> {
            lat1.setText("");
            lat2.setText("");
            long1.setText("");
            long2.setText("");
            distanceresult.setText("");
            bearingresult.setText("");
            lat1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            lat2.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long2.onEditorAction(EditorInfo.IME_ACTION_DONE);
        });

    }


    //Creates a menu in the coordinator layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //This function is triggered when a menu item is selected, and operates accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_settings) {

            inputToString();

            //New intent is made to move open a new screen (activity) for settings screen
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            //The ".putsExtra" command stores information that is transferred to the new activity
            intent.putExtra("currdistunit",DistUnit);
            intent.putExtra("currbearunit",BearUnit);
            intent.putExtra("lat1",lat1str);
            intent.putExtra("lat2",lat2str);
            intent.putExtra("long1",long1str);
            intent.putExtra("long2",long2str);

            //This function is used when the current activity anticipates a result from the newly
            //opened activity to be returned.
            startActivityForResult(intent,DIST_UNIT);
            return true;
        }
        return false;
    }

    //If a result is returned, this function is activated and stores the returned data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == DIST_UNIT) {
            DistUnit = data.getStringExtra("distunits");
            BearUnit = data.getStringExtra("bearunits");

            lat1.setText(data.getStringExtra("lat1"));
            lat2.setText(data.getStringExtra("lat2"));
            long1.setText(data.getStringExtra("long1"));
            long2.setText(data.getStringExtra("long2"));
        }

        inputToString();

        //recalculates the distance/bearing information with updated units, if applicable
        if ((lat1str.length() != 0) && (lat2str.length() != 0) &&
                (long1str.length() != 0) && (long2str.length() != 0))
                    calcDistance();
    }


    void calcDistance(){
        loc1.setLatitude(Double.parseDouble(lat1str));
        loc1.setLongitude(Double.parseDouble(long1str));
        loc2.setLatitude(Double.parseDouble(lat2str));
        loc2.setLongitude(Double.parseDouble(long2str));

        BigDecimal BDdist = new BigDecimal(Double.toString((loc1.distanceTo(loc2)/1000)));

        if(DistUnit.equals("miles"))
            BDdist = new BigDecimal(Double.toString(((loc1.distanceTo(loc2)/1000)*.621371)));

        BDdist = BDdist.setScale(2, RoundingMode.HALF_UP);
        distance = BDdist.doubleValue();

        BigDecimal BDbear = new BigDecimal(Double.toString(loc1.bearingTo(loc2)));

        if(BearUnit.equals("mils"))
            BDbear = new BigDecimal(Double.toString(loc1.bearingTo(loc2)*17.77777));

        BDbear = BDbear.setScale(2, RoundingMode.HALF_UP);
        bearing = BDbear.doubleValue();




       distanceresult.setText(""+distance+" "+DistUnit);
       bearingresult.setText(""+bearing+" "+BearUnit);
    }

    void inputToString(){

        lat1str = lat1.getText().toString();
        lat2str = lat2.getText().toString();
        long1str = long1.getText().toString();
        long2str = long2.getText().toString();
    }

}
