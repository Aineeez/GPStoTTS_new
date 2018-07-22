package stardust.aineeez.gpstotts_new;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements LocationListener {
    public Button talkBtn;
    private TextView latitudeField;
    private TextView longitudeField;
    private LocationRequest locationRequest;
    private double longitude = -1; //mit minus initialisiert, damit man auf den ersten Blick erkennen kann, falls es noch keine Werte gibt
    private double latitude = -1;

    long request_Interval = 1000; //nach 1 Sekunde werden neue GPS-Daten abgefragt

    //
    class TalkOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            //hier noch TTS-Spaß implementieren, der bei Click aktiv wird


        }



    }

    class LocationSettingsOnFailureListener implements OnFailureListener {
        int REQUEST_CHECK_SETTINGS = 1;
        Activity activity;

        //neuer Konstruktor, damit ich Activity übergeben kann, denn sonst kann ich nicht
        //auf die Klasse über der Klasse zugreifen
        LocationSettingsOnFailureListener(Activity activity){
            this.activity = activity;
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) //
                try {
                    //Cast, damit ich die Methoden der ResolvalbeApiExceptions nutzen kann
                    ResolvableApiException exception = (ResolvableApiException) e;

                    exception.startResolutionForResult(this.activity, REQUEST_CHECK_SETTINGS);
                } catch (Exception f) {
                    //do nothing --> die behebbaren Fehler wurden oben schon abgefangen
                    //nicht behebbare Fehler kann ich auch nicht behandeln
                }
        }
    }

    class LocationRequestResultCallback extends LocationCallback{

        MainActivity activity;

        LocationRequestResultCallback(MainActivity activity){
            this.activity = activity;

        }

        @Override
        public void onLocationResult(LocationResult result){

            updateLocation(result.getLastLocation());

        }

    }


    public void updateLocation(Location location){

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        this.longitude = longitude;
        this.latitude = latitude;

        this.latitudeField.setText(Double.toString(latitude));
        this.longitudeField.setText(Double.toString(longitude));
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        talkBtn = (Button) findViewById(R.id.talkBtn);

        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        this.locationRequest = LocationRequest.create();
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationRequest.setInterval(request_Interval);

        //Einstellungen, wie die Arbeit des Requests dem User angezeigt werden, z. B. GPS-Symbol leuchtet
        //fragt User, ob GPS aktiviert werden soll
        //Konstruktor für LocationsSettingsRequest ist nicht frei verfügbar, daher Builder
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();

        //der Builder muss vor dem eigentlich Request ans Betriebssystem prüfen, ob der Nutzer seine
        //Permission gegeben hat, falls nicht, wird ggf. Dialog zur Nachfrage beim Nutzer getriggert
        locationSettingsRequestBuilder.addLocationRequest(this.locationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true); //true, denn GPS ist unbedingt nötig für die App

        //der Builder baut jetzt einen echten LocationsSettingsRequest
        LocationSettingsRequest locationSettingsRequest = locationSettingsRequestBuilder.build();

        //führt den Request aus, Client ist schnittstelle zu service
        //Client ist Vermittler zwischen App und Betriebssystem (einem Service)
        //mein Client wird angefragt bei den Location Services des Betriebssystems
        // this ist die Instanz der Klasse in der ich mich befinde (Show Location Activity)
        //this muss übergeben werden, damit dieser Client in dieser Activity genutzt werden kann
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        //jetzt fragt settingsClient bei OS an, ob die Einstellungen so sind, dass der Request gestellt werden kann
        //Task = Aufgabe, führt etwas aus und antwortet mit dem, was in spitzen Klammern definiert ist
        //Task arbeitet sofort nach Initialisierung, ohne dass man ihn aufrufen muss
        //die returnte response ist mir egal, ich will hören, wenn ein Fehler auftritt
        Task<LocationSettingsResponse> checkLocationSettings = settingsClient.checkLocationSettings(locationSettingsRequest);

        //um den Parameter zu übergeben, musste ich oben die entsprechende Klasse anlegen
        checkLocationSettings.addOnFailureListener(new LocationSettingsOnFailureListener(this));

    }

    /* Request updates at startup */

    //hierzu life cycle von activities berücksichtigen
    //GPS-Request muss in onResume rein, da es dann wieder ausgeführt wird, auch wenn die App
    //mal im Hintergrund lief oder eine andere Activity geöffnet wurde
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

        //Request wird nicht direkt ausgeführt sondern immer an einen Client übergeben
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //immer wenn neue GPS-Daten kommen, wird ausgeführt, was in locationRequestResultCallback definiert ist
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationRequestResultCallback(this), Looper.myLooper()); //Looper unklar



    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();

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

}