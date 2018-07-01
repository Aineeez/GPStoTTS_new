package stardust.aineeez.gpstotts_new;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView latitudeField;
    private TextView longitudeField;
    public Button talkBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //das ruft die Methode unten zum Verknüpfen der beiden Activities per Talk-Button


        latitudeField = (TextView) findViewById(R.id.latitudeResultTextView);
        longitudeField = (TextView) findViewById(R.id.longitudeResultTextView);


        Button showBtn = (Button) findViewById(R.id.showBtn);
        //R steht für Ressourcen, "i" tippen, dann aus der Liste wählen, wo meine im Layout-Fenster erstellten Buttons etc. stehen
        //findViewById ist genau dazu da, diese Ressource zu suchen

        showBtn.setOnClickListener(new View.OnClickListener() {  //hier definiere ich was passieren soll, wenn man draufklickt auf den Button

            @Override
            public void onClick(View v) { //das ist ein OnClick-Event, innerhalb der Klammern definieren, was passieren soll

                   TextView latitudeResultTextView = (TextView) findViewById(R.id.latitudeResultTextView);
                   latitudeResultTextView.setText("50.83592");

                   TextView longitudeResultTextView = (TextView) findViewById(R.id.longitudeResultTextView);
                    longitudeResultTextView.setText("12.92331");
            }
        });

    }


    public void init() {
        talkBtn = (Button) findViewById(R.id.talkBtn); //hier ist was doppelt gemoppelt - zwei listener für den einen Button
        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hearGPS = new Intent(MainActivity.this, ShowLocationActivity.class);
                startActivity(hearGPS);
            }
        });
    }

}
