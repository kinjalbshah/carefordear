package org.traccar.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.traccar.manager.model.DeviceGeofence;
import org.traccar.manager.model.Geofence;

import retrofit2.Call;
import retrofit2.Response;

import java.util.Random;


/**
 * Created by cf4 on 23-08-2016.
 */

public class AddGeofenceFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener ,SeekBar.OnSeekBarChangeListener, OnMapReadyCallback {


private GoogleMap googleMap;
private GoogleApiClient mGoogleApiClient;
private Location mLastLocation;
        Marker centerMarker;
private int radius;
private Circle geofenceCircle;
protected static final String TAG = "AddGeofence";
private TextView locationTv;
private SeekBar mRadiusBar;
private int int_DEFAULT_RADIUS = 200;
private static final int RADIUS_MAX = 1000;

private Button savegeofence;
    private Button getLatLong;
    private EditText geofencename;
    private double geofencelat  ;
    private double geofencelong  ;

    private EditText getAddress;
private String getAddressValue ;

ConnectivityManager connMgr ;
NetworkInfo networkInfo ;

    Context context;
public static final String EXTRA_DEVICE_ID = "deviceId";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show error dialog if GoolglePlayServices not available
        //Commented for time being
        /*
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.fragment_add_geofence, parent, false);
        context = rootView.getContext();

        geofencename = (EditText) rootView.findViewById(R.id.getgeofencename);
        savegeofence = (Button) rootView.findViewById(R.id.savegeofence);
        mRadiusBar = (SeekBar) rootView.findViewById(R.id.radiusSeekBar);
        mRadiusBar.setMax(RADIUS_MAX);
        mRadiusBar.setProgress(int_DEFAULT_RADIUS);

        getLatLong = (Button) rootView.findViewById(R.id.getLatLong);
        getAddress = (EditText) rootView.findViewById(R.id.getAddress);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googleMap);

        if (savedInstanceState == null) {
            supportMapFragment.getMapAsync(this);

        }


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Toast.makeText(context, "inside map create : "  , Toast.LENGTH_LONG).show();

        // Check if network is available to make connection
        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        getLatLong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Log.e(TAG, "inside onclick" + ". ");
                // Toast.makeText(Simple_circle_marker_working.this, "inside onclick", Toast.LENGTH_SHORT).show();
                getAddressValue = getAddress.getText().toString();
                if (validateAddress(getAddressValue))
                    // Check if network is available and call the geocode URL with the address given to get Lat Long
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new getLatLongAsyncTask().execute(getAddressValue);

                    } else {
                        Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG).show();

                    }


                else
                    Toast.makeText(context, "Please enter a valid address", Toast.LENGTH_SHORT).show();


            }
        });
        //
        savegeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempgeofencename = geofencename.getText().toString();
                if ( tempgeofencename.isEmpty()) {
                    Toast.makeText(context, "Please enter a valid geofence name",Toast.LENGTH_SHORT).show();
                    geofencename.requestFocus();
                }

                else {
                    savegeofence();
                }

            }
        });
            return rootView;
        }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onConnected(Bundle connectionHint) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            // radius = mRadiusBar.getProgress();
            //Toast.makeText(this, "inside location connected radius: "  + radius , Toast.LENGTH_LONG).show();

            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            //Toast.makeText(context, "inside location connected lat : "  + latitude , Toast.LENGTH_LONG).show();

            drawCircle(latLng);

        }


    }



    public void onMapReady(GoogleMap map) {
      //  Toast.makeText(context, "on map ready : " , Toast.LENGTH_LONG).show();

        googleMap = map;
        googleMap.setOnMarkerDragListener(this);
        mRadiusBar.setOnSeekBarChangeListener(this);

    }
    // This method call when we start to drag marker

    @Override
    public void onMarkerDragStart(Marker marker) {
    }
    // This method call while we drag marker

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    /* This method call when we drop the marker at desired position. and this method we use for drag geofence so now where we drop
     marker we can get latitude and longitude of that position
     */

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;

        //locationTv.setText("New Latitude:" + dragLat + ", Longitude:" + dragLong);
        //geofencelat = dragLat ;
        //geofencelong = dragLong ;

        // Draw circle at the new position
        drawCircle(dragPosition);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Display how much the radius was dragged
        Toast.makeText(context, "radius changed to : "  + seekBar.getProgress(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar == mRadiusBar) {
         //   Toast.makeText(context, "Updated radius : "  + seekBar.getProgress(), Toast.LENGTH_LONG).show();

            // Change the circle radius to user selected radius
            if ( geofenceCircle != null )
                geofenceCircle.setRadius(progress);

        }

    }

    public void drawCircle(LatLng center)
    {
        double radius ;

        // Added to avoid marker being displayed twice
        //Remove the existing marker before drawing the geofence circle at the user given latlong . The existing cicrle is removed in the function itself.

        if ( centerMarker != null ) {
            Log.d(TAG, "Removing old marker");
            centerMarker.remove();
        }

        centerMarker = googleMap.addMarker(new MarkerOptions().draggable(true).position(center).title("click and drag"));
        centerMarker.showInfoWindow();

        radius = mRadiusBar.getProgress();

        if ( geofenceCircle != null )
            geofenceCircle.remove();

        geofenceCircle = googleMap.addCircle(new CircleOptions()
                .center(center).radius(radius)
                .fillColor(Color.parseColor("#B2A9F6")));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //locationTv.setText("Latitude:" + center.latitude + ", Longitude:" + center.longitude);

        geofencelat = center.latitude;
        geofencelong = center.longitude;

//        Toast.makeText(context, "inside drawCircle: "  , Toast.LENGTH_LONG).show();

    }
//  Added for gofencing using address

    private class getLatLongAsyncTask extends AsyncTask<String, Void, String[]> {

      //  ProgressDialog dialog = new ProgressDialog(AddGeofenceFragment.this);
      ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {

            Log.d(TAG, "inside onPreExecute. ");
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;

            String addressValue =params[0];

            try {
                Log.d(TAG, "inside doinbackground. " + addressValue );
                // response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=560077&sensor=false");

                response = getLatLongByURL(addressValue);
                Log.d(TAG, "after query response" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {

            if (result.equals("")|| result == null)
                locationTv.setText("No Matching address found. Please check address and reenter");
            else {
                try {

                    Log.d(TAG, "inside onPostExecute. ");
                    JSONObject jsonObject = new JSONObject(result[0]);

                    double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");

                    Log.d("latitude", "" + lat);
                    Log.d("longitude", "" + lng);

                    //displayLatLong = (TextView) activity.findViewById(R.id.displayLatLong);

                    //locationTv.setText("Latitude Longitude for the address is: " + lat + ", Longitude: " + lng);

                   // geofencelat = lat ;
                    //geofencelong = lng ;
                    LatLng latLng = new LatLng(lat, lng);

                    drawCircle(latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    public String getLatLongByURL(String addressValue) {
        URL url;
        String response = "";
        String addressUrl = "";
        try {
            // String addressURL = "http://maps.google.com/maps/api/geocode/json?address=" + addressValue + "&sensor=false" ;

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("maps.google.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("geocode")
                    .appendPath("json")
                    .appendQueryParameter("address",addressValue )
                    .appendQueryParameter("sensor", "false") ;

            addressUrl = builder.build().toString();

            Log.d(TAG, "inside getlatlong. " + addressUrl );
            url = new URL(addressUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            Log.d(TAG, "The response is: " + response);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Boolean validateAddress(String address) {
        boolean validAddress = false ;

        //  Toast.makeText(Simple_circle_marker_working.this,  "in validate address" + address ,Toast.LENGTH_SHORT).show();
        if (address != null && address.trim().length() != 0 )
            //Add check for 6 continous digit for zip code  TODO
            validAddress = true;
//        Toast.makeText(context,  "Valid Address" ,Toast.LENGTH_SHORT).show();
        return validAddress ;
    }

   //Function to post data to server
   public void savegeofence()  {
       long geofenceid ;
       radius = mRadiusBar.getProgress();
       Geofence geofence = new Geofence();
       final DeviceGeofence devicegeofence = new DeviceGeofence();

       geofence.setId(11111);     // will insert in geofence, device table first
       geofence.setName(geofencename.getText().toString());
       geofence.setDescription("testing geofence from app");   //Should change this to read from user entry

       String temparea = "CIRCLE (" + geofencelat + " " + geofencelong + "," + radius + ")" ;
       geofence.setArea(temparea);

       String tempgeofencedisplay = "Lat: " + geofencelat + " Long :" +  geofencelong + " radius: " + radius ;
       Toast.makeText (context, tempgeofencedisplay ,Toast.LENGTH_LONG).show();

       final long deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);
       final MainApplication application = (MainApplication) getActivity().getApplication();
       final WebService service = application.getService();
       service.saveGeofence(geofence).enqueue(new WebServiceCallback<org.traccar.manager.model.Geofence>(getContext()) {

                    @Override
                    public void onSuccess(Response<org.traccar.manager.model.Geofence> response) {
                        Toast.makeText(getContext(), R.string.command_sent, Toast.LENGTH_LONG).show();

                        devicegeofence.setGeofenceId(response.body().getId());
                        devicegeofence.setDeviceId(deviceId);

                        // Data inserted in geofences table . Get the geofence id and insert in geofence/device table to map geofence and device
                        // Send geofenceid + device id ;
                        service.saveDeviceGeofence(devicegeofence).enqueue(new WebServiceCallback<DeviceGeofence>(getContext()) {

                            @Override
                            public void onSuccess(Response<DeviceGeofence> response) {
                                Toast.makeText(getContext(), "Geofence created suuccesfully", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onFailure(Call<DeviceGeofence> call, Throwable t) {
                                CharSequence text = context.getResources().getText(R.string.error_connection);
                                Toast.makeText(context, "Geofence Device insertion failed", Toast.LENGTH_LONG).show();
                                String stacktrace = Log.getStackTraceString(t);
                                Log.d("Geofence add failed", stacktrace);
                                // Call to delete geofences from the geofence table so that orphan geofence is not there.
                            }

                        });
                    }

                });

               // getActivity().finish();
            }


    }






