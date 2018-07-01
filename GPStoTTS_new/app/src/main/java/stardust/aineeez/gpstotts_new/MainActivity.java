package stardust.aineeez.gpstotts_new;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private TextView latitudeField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeField = (TextView) findViewById(R.id.latitudeResultTextView);
        longitudeField = (TextView) findViewById(R.id.longitudeResultTextView);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

//      //Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latitudeField.setText("Location not available");
            longitudeField.setText("Location not available");
        }

        Button talkBtn = (Button) findViewById(R.id.talkBtn);
        //R steht für Ressourcen, "i" tippen, dann aus der Liste wählen, wo meine im Layout-Fenster erstellten Buttons etc. stehen
        //findViewById ist genau dazu da, diese Ressource zu suchen

        talkBtn.setOnClickListener(new View.OnClickListener() {  //hier definiere ich was passieren soll, wenn man draufklickt auf den Button

            @Override
            public void onClick(View v) { //das ist ein OnClick-Event, innerhalb der Klammern definieren, was passieren soll
                //GPS-Daten auslesen fehlt hier

                   TextView latitudeResultTextView = (TextView) findViewById(R.id.latitudeResultTextView);
                   latitudeResultTextView.setText("50.83592");

                   TextView longitudeResultTextView = (TextView) findViewById(R.id.longitudeResultTextView);
                    longitudeResultTextView.setText("12.92331");
            }
        });

    }

    @Override
    //Liest aus der Variable "location" die beiden Bestandteile longitude und latitude raus und zeigt sie in den Textfelder dafür an
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }
    /* Request updates at startup */

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }


    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }



}
