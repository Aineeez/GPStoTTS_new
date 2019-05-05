package stardust.aineeez.gpstotts_new;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
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

import java.util.Locale;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements LocationListener {
    public Button talkBtn;
    private TextView latitudeField;
    private TextView longitudeField;
    //locationRequest an das OS ist der eigentliche Kern der Sache,
    //dazu muss aber ein Client zur Kommunikation mit dem OS verwendet werden und
    //die Einstellungen zu den Permissions müssen beim OS mittels eines anderen Clients geprüft werden
    //und ggf. der User noch einmal um Erlaubnis gebeten werden
    private LocationRequest locationRequest;
    //mit minus initialisiert, damit man auf den ersten Blick erkennen kann, falls es noch keine Werte gibt
    private double longitude = -1;
    private double latitude = -1;
    long request_Interval = 1000; //nach 1 Sekunde werden neue GPS-Daten abgefragt

    private TextToSpeech myTTS;

    class TalkOnClickListener implements View.OnClickListener{
        //Override tut eigentlich nichts, außer zu prüfen, ob es die darunterstehende Methode auch wirklich
        //in der Elternklasse gibt, falls nicht (z. B. weil hier ein Tippfehler gemacht wurde), bekommt
        //der Programmierer einen Hinweis
        @Override
        public void onClick(View v) {
            //hier noch TTS-Spaß implementieren, der bei Click aktiv wird
        }
    }

    //diese Klasse ist dazu da, auf eine Fehlermeldung bei der Kommunikation mit dem OS zu horchen
    class LocationSettingsOnFailureListener implements OnFailureListener {
        int REQUEST_CHECK_SETTINGS = 1; //nicht mehr klar, weshalb das hier 1 sein muss
        //es wird die Activity, für die der Request gestellt/der Fehler geworfen wird, in der Klasse gespeichert
        //bei der Lösung des Fehlers muss man dann die Activity angeben
        Activity activity;

        //neuer Konstruktor, damit ich Activity übergeben kann, denn sonst kann ich nicht
        //auf die Klasse über der Klasse zugreifen
        LocationSettingsOnFailureListener(Activity activity){this.activity = activity;
        }
        @Override
        //hier wird definiert, was im Fehlerfall passiert
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) //e ist enthalten in der Menge ResolvableApiException
                try {
                    //handelt es sich um behebbare Fehler, dann behebe sie automatisich
                    //Cast, damit ich die Methoden der ResolvalbeApiExceptions nutzen kann
                    ResolvableApiException exception = (ResolvableApiException) e;
                    //löse das Problem
                    exception.startResolutionForResult(this.activity, REQUEST_CHECK_SETTINGS);
                } catch (Exception f) {
                    //do nothing
                    // --> die behebbaren Fehler wurden oben schon abgefangen
                    //nicht behebbare Fehler kann ich auch nicht behandeln
                }
            }
        }

    class LocationRequestResultCallback extends LocationCallback{
        MainActivity activity;
        //Konstruktor speichert ab, um welche Activity es geht
        LocationRequestResultCallback(MainActivity activity){
            this.activity = activity;
        }

        @Override
        public void onLocationResult(LocationResult result){
            //wenn ein neues Result kommt, nimm aus diesem heraus die letzte Location (könnten auch mehrere sein)
            updateLocation(result.getLastLocation());
        }
    }

    public void updateLocation(Location location){
        //mit getLongitude() hole ich den Longitude-Wert aus der gesamten Location
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        //this ist hier die MainActivity
        //hier wird nur das Merkmal gespeichert
        this.longitude = longitude;
        this.latitude = latitude;

        //hier wird der anzuzeigene Text als String in die Texbox geschrieben
        this.latitudeField.setText(Double.toString(latitude));
        this.longitudeField.setText(Double.toString(longitude));
    }

    /** Called when the activity is first created. Vgl. life-cycle-Diagramm für Activities */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Button, mit dem die TTS ausgelöst wird
        talkBtn = (Button) findViewById(R.id.talkBtn);

        //Boxen, wo die Koordinaten angezeigt werden
        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        //Neue Instanz eines TTS-Objekts anlegen, welches nachher das Sprechen ausführt
        //darin auch die Sprache der TTS auswählen und
        //Prüfen, ob die Daten für die TTS vorhanden sind
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if (status == TextToSpeech.SUCCESS){
                    int result = myTTS.setLanguage(Locale.GERMAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported");
                    }else {

                    }
                }else {
                    Log.e("TTS", "initialization failed");
                }
            }
        });

        //Der Listener wartet darauf, dass der Button geklickt wird
        //und führt dann aus, was in onClick() definiert ist
        talkBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        TextView TTStext = (TextView)findViewById(R.id.TTSdummy);
                        TTStext.setText("TTS spricht gerade");
                        String myDummyGPSData = "The longitude is thirteen point five nine two seven one seven";
                        myTTS.speak(myDummyGPSData, TextToSpeech.QUEUE_ADD, null);
                    }
        });

        //Instanziierung des LocationRequests an das OS
        this.locationRequest = LocationRequest.create();
        //setzen einiger mir sinnvoll erscheinender Parameter mittels zugehöriger Methoden
        //es gibt viele Methoden für Parameter, ich muss aber nicht alle setzen
        //einfach die Doku lesen und entscheiden, ob eine sinnvoll ist und mit welcher Ausprägung der Parameter
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationRequest.setInterval(request_Interval);

        //Einstellungen, wie die Arbeit des Requests dem User angezeigt werden, z. B. GPS-Symbol leuchtet (stimmt das?)
        //fragt User, ob GPS aktiviert werden soll
        //Konstruktor für LocationsSettingsRequest ist nicht frei verfügbar (weil man den als Programmierer falsch
        //verwenden und Schaden anrichten könnte
        // daher gibt es den Builder, der gleich meckert, wenn man ihn falsch ruft
        //ich instanziiere einen neuen Builder vom Typ LocationSettingsRequest.Builder
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();

        //der SettingsRequest muss vor dem eigentlich Request ans Betriebssystem prüfen, ob der Nutzer seine
        //Permission gegeben hat, falls nicht, wird ggf. Dialog zur Nachfrage beim Nutzer getriggert

        //add ist eine Methode des Builders, mit der zum Builder hinzugefügt wird, welcher Request
        //aus welcher Klasse gemeint ist, wenn die Settings abgefragt werden
        locationSettingsRequestBuilder.addLocationRequest(this.locationRequest);
        //auch setAlwaysShow() ist eine Methode, mit der ein Parameter für den Builder gesetzt wird
        //hier ist der Parameter true, denn GPS ist unbedingt nötig für die App
        locationSettingsRequestBuilder.setAlwaysShow(true);

        //der Builder baut jetzt einen echten LocationsSettingsRequest
        LocationSettingsRequest locationSettingsRequest = locationSettingsRequestBuilder.build();

        //Client führt den Request aus, Client ist Schnittstelle zu Service des OS
        //Client ist Vermittler zwischen App und Betriebssystem (einem seiner Services)
        //"this" ist die Instanz der Klasse in der ich mich befinde (Show Location Activity)
        //"this" muss übergeben werden, damit dieser Client in dieser Activity genutzt werden kann
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        //jetzt fragt settingsClient bei OS an, ob die Einstellungen so sind, dass der Request gestellt werden darf
        //Task = Aufgabe, führt etwas aus und antwortet mit dem, was in spitzen Klammern definiert ist
        //Task arbeitet sofort nach Initialisierung, ohne dass man ihn aufrufen muss
        //die returnte response (mit dem Typ in <> ist mir egal, ich will nur hören, ob bei der Anfrage ein Fehler auftritt
        Task<LocationSettingsResponse> checkLocationSettings = settingsClient.checkLocationSettings(locationSettingsRequest);

        //um den Parameter zu übergeben, musste ich oben die Klasse LocationSettingsOnFailureListener anlegen
        //ich füge ein Merkmal "OnFailureListener" mittels der add..()-Methode hinzu zur Instanz von checkLocationSettings
        checkLocationSettings.addOnFailureListener(new LocationSettingsOnFailureListener(this));
    }

    /* Request updates at startup */
    //GPS-Request muss in onResume rein, da es dann wieder ausgeführt wird, auch wenn die App
    //mal im Hintergrund lief oder eine andere Activity geöffnet wurde
    //würde sie bei onCreate() schon ausgeführt, wäre das nicht der Fall
    //vergleiche hierzu die Pfeile im life cycle von Activities

    @Override
    protected void onResume() {
        super.onResume();

        //könnte ich die ganze IF löschen??
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //diese Todo ist für mich nicht relevant, da ich für Android 4.2 programmiere und dort
            //die Definition der Permissions im Android-Manifest ausreichend ist. Es wird nicht bei
            //jeder einzelnen Nutzung von Services nach der Erlaubnis des Nutzers gefragt

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
        //hier initialisieren wir einen Client für das Abholen der GPS-Daten
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //immer wenn neue GPS-Daten kommen, wird ausgeführt, was in locationRequestResultCallback definiert ist
        //Looper unklar
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationRequestResultCallback(this), Looper.myLooper());

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();

    }

    //wenn das Gerät sich bewegt und die GPS-Daten sich ändern, werden die Texboxen neu gefüllt
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

        //Toast ist die kleine Nachricht mit weißer Schrift auf schwarzem Grund, die unten auf
        //dem Screen aufpoppt
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        //TTS-Ressourcen freigeben beim Schließen der App
        if(myTTS != null){
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }
}