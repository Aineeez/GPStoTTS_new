package stardust.aineeez.gpstotts_new;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public Button talkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //das ruft die Methode unten zum Verknüpfen der beiden Activities per Talk-Button

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
