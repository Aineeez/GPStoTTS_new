package stardust.aineeez.gpstotts_new;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocationActivity extends Activity implements LocationListener {
    private TextView latitudeField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        //aus diesem Tutorial: https://youtu.be/scySXsk9yRc
        final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
        final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 102;
        boolean permissionIsGranted = false;


        String [] requestedPermissions = new String[2]; // das habe ich angelegt, weil beim Abfragen der Berechtigungen mit ActivityCompat.requestPermissions
        requestedPermissions[0] = "android.Manifest.permission.ACCESS_FINE_LOCATION"; //vom Nutzer ein String-Array übergeben werden muss
        requestedPermissions[1] = "android.Manifest.permission.ACCESS_COARSE_LOCATION";

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use default
        Criteria criteria = new Criteria(); //als Auswahlkriterium wird der Default genommen (unklar, was der Default ist)
        //Doku: https://developer.android.com/reference/android/location/Criteria

        provider = locationManager.getBestProvider(criteria, false); //wählt aus, welche Quelle die beste ist für die GPS-Daten

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //ActivityCompat.requestPermissions(this, requestedPermissions, MY_PERMISSIONS_REQUEST_FINE_LOCATION); //das ist aus der Doku, nicht aus dem YT-Tutorial

            return;
        }
        Location location = locationManager.getLastKnownLocation(provider); //lässt sich nur ausführen, wenn der permission check
        // in der IF direkt obendrüber ausgeführt wurde

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latitudeField.setText("Fucking Location not available");
            longitudeField.setText("Fucking Location not available");
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
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

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case 101: //das ist der Code, der für FINE_LOCATION verwendet wird

                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission was granted
               // permissionIsGranted = true;
                Toast.makeText(this, "Permission for GPS was granted",
                        Toast.LENGTH_SHORT).show();


                 }
                else{
                    //permission was denied
               //     permissionIsGranted = false;
                    Toast.makeText(this, "Permission for GPS was denied",
                            Toast.LENGTH_SHORT).show();
                    latitudeField.setText("permission not granted");
                    longitudeField.setText("permission not granted");
                }

                break;

            case 102: //nicht so klar: im Tutorial stand hier MY_PERMISSIONS_REQUEST_COARSE_LOCATION, aber das wird hier nicht erkannt
                //do something
                break;
        }

    }
*/
}