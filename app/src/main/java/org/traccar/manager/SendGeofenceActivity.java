

/**
 * Created by cd4 on 8/14/16.
 */
package org.traccar.manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SendGeofenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_geofence);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new SendCommandFragment())
                    .commit();
        }
    }

}