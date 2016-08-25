package org.traccar.manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cd4 on 23-08-2016.
 */

public class GetGeofenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_geofence);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new GetGeofenceFragment())
                    .commit();
        }
    }

}
