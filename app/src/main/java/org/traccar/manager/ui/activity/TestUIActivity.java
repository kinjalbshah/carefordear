package org.traccar.manager.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.traccar.manager.ui.fragments.SlidingButtons;

import org.traccar.manager.R;

public class TestUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new SlidingButtons())
                    .commit();
        }
    }

}
