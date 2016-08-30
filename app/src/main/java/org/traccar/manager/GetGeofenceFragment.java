package org.traccar.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.traccar.manager.model.Geofence;


import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by cf4 on 23-08-2016.
 */

public class GetGeofenceFragment extends ListFragment implements View.OnClickListener {

    public static final String EXTRA_DEVICE_ID = "deviceId";
/*  Comment for time being add later for delete/edit of geofence */
    class PopupAdapter extends ArrayAdapter<Geofence> {

        PopupAdapter(List<Geofence> items) {
            super(getActivity(), R.layout.list_item, android.R.id.text1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            View view = super.getView(position, convertView, container);
            View popupText = view.findViewById(android.R.id.text1);
            popupText.setTag(getItem(position));
            popupText.setOnClickListener(GetGeofenceFragment.this);
            return view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use deviceId to have rest call based on deviceID. we don't have currently
        final long deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);

        final MainApplication application = (MainApplication) getActivity().getApplication();
        application.getServiceAsync(new MainApplication.GetServiceCallback() {
            @Override
            public void onServiceReady(OkHttpClient client, Retrofit retrofit, WebService service) {
                service.getGeofences().enqueue(new WebServiceCallback<List<Geofence>>(getContext()) {
                    @Override
                    public void onSuccess(Response<List<Geofence>> response) {
                        setListAdapter(new PopupAdapter(response.body()));
                    }
                });
            }
        });
    }

    @Override
    public void onClick(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view) {
        final PopupAdapter adapter = (PopupAdapter) getListAdapter();
        final Geofence geofence = (Geofence) view.getTag();
        PopupMenu popup = new PopupMenu(getActivity(), view);

        popup.getMenuInflater().inflate(R.menu.geofence_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_geofence_edit:
                        //finishDevicesActivity(geofence.getId());
                        Toast.makeText(getContext(), "Add class to handle geofence edit and change finishDevicesActivity", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.action_geofence_delete:
                        //startSendCommandActivity(geofence.getId());
                        Toast.makeText(getContext(), "Add class to handle geofence delete", Toast.LENGTH_LONG).show();
                        return true;

                }
                return false;
            }
        });

        popup.show();
    }

    private void finishDevicesActivity(long deviceId) {
        Activity activity = getActivity();
        activity.setResult(MainFragment.RESULT_SUCCESS, new Intent().putExtra(EXTRA_DEVICE_ID, deviceId));
        activity.finish();
    }

    private void startSendCommandActivity(long deviceId) {
        startActivity(new Intent(getContext(), SendCommandActivity.class).putExtra(EXTRA_DEVICE_ID, deviceId));
    }
    private void startSendGeofenceActivity(long deviceId) {
        startActivity(new Intent(getContext(), SendGeofenceActivity.class).putExtra(EXTRA_DEVICE_ID, deviceId));
    }

    private void startgetGeofenceActivity(long deviceId) {
        startActivity(new Intent(getContext(), GetGeofenceActivity.class).putExtra(EXTRA_DEVICE_ID, deviceId));
    }

}
