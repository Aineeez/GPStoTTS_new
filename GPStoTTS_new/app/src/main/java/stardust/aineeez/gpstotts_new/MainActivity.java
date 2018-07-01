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


}
