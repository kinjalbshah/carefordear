package org.traccar.manager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.traccar.manager.model.Language;
import org.traccar.manager.model.Route;
import org.traccar.manager.model.RouteDetail;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by CF4 on 22-09-2016.
 */
public class GetRouteFragment extends Fragment
        implements  OnMapReadyCallback, AdapterView.OnItemSelectedListener {


    public static final String EXTRA_DEVICE_ID = "deviceId";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final int REPORT_DURATION = 6;
    private static final int NUMBEROFGEOCODE = 10;
    private DatePickerFragment mDatePickerDialogFragment;
    private Circle geofenceCircle;
    private Circle geofenceCircle2;

    private GoogleMap googleMap;

    private List<Route> routeList ;
    private ArrayList<RouteDetail> routeDetailList = new ArrayList<RouteDetail>();
    private ArrayList<Language> languageList = new ArrayList<Language>();
    private Polyline polyline;
    private String geocode_lang;


    Context context;
    Spinner geolngSpinner;
    ImageButton fromDate, toDate;
    TextView routeFromDate, routeToDate, langtextview;
    private Button routeButton, routeDetailsButton;




    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);


    public GetRouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_get_route, parent, false);
        context = rootView.getContext();

        // Get fromDate
        fromDate = (ImageButton) rootView.findViewById(R.id.fromDate); // getting the image button in fragment_blank.xml
        routeFromDate = (TextView) rootView.findViewById(R.id.routeFromDate); // getting the TextView in fragment_blank.xml
        // Get toDate
        toDate = (ImageButton) rootView.findViewById(R.id.toDate);
        routeToDate = (TextView) rootView.findViewById(R.id.routeToDate);

        //Get route button

        routeButton = (Button) rootView.findViewById(R.id.getRoute);

        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View vv = v;

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int Year, int Month, int Day) {
                        String formatMonth, formatDay;
                        Month++;   // As month is from 0-11
                        formatMonth =  (Month < 10) ? "0" + Month : "" + Month ;
                        formatDay =  (Day < 10) ? "0" + Day : "" + Day ;

                        StringBuilder sb = new StringBuilder().append(Year).append("-").append(formatMonth).append("-").append(formatDay);
                        String formattedDate = sb.toString();
                        if (vv.getId() == R.id.fromDate) { //From date was clicked {

                            routeFromDate.setText(formattedDate);
                        } else {//EndDate button was clicked {
                            routeToDate.setText(formattedDate);
                            //do the stuff
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        };
        fromDate.setOnClickListener(showDatePicker);
        toDate.setOnClickListener(showDatePicker);

        routeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Log.e(TAG, "inside onclick" + ". ");

                if (validateDate()) {
                    populateRoute();
                }

            }
        });

//Get language from the spinner
        populateLang() ;   // Populate Languages for Spinner
        geolngSpinner = (Spinner) rootView.findViewById(R.id.geocode_lang);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(getContext(),android.R.layout.simple_spinner_dropdown_item, languageList);
        // Apply the adapter to the spinner
        geolngSpinner.setAdapter(adapter);
        geolngSpinner.setOnItemSelectedListener(this);

//  Get route detail button
        routeDetailsButton = (Button) rootView.findViewById(R.id.getRouteDetails);
        routeDetailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (routeList != null && !routeList.isEmpty()) {   // if route is selected , populate the list and pass it to routedetail fragment for geocoding
                    populateforgeocoding();
                    Intent intent = new Intent(getContext(), RouteDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("routelist", routeDetailList);
                    bundle.putString("lang", geocode_lang);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Please select Route", Toast.LENGTH_LONG).show();
                }

            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Show route details option only when route is selected
         langtextview =  (TextView) rootView.findViewById(R.id.routedisplayLang);
        if (routeList == null || routeList.isEmpty()) {
            routeDetailsButton.setEnabled(false);
            geolngSpinner.setEnabled(false);
            langtextview.setEnabled(false);
            routeDetailsButton.setVisibility(View.GONE);
            geolngSpinner.setVisibility(View.GONE);
            langtextview.setVisibility(View.GONE);
        }
        else
           {  enablebuttons();
           }

        return rootView;
    }


    // on get route button click - do date validation - not null ; todate < fromdate
    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.

        googleMap = map;
        //  googleMap.setOnMarkerDragListener(this);
        googleMap.setContentDescription("Google Map with polylines.");

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater(null).inflate(R.layout.view_info, null);
                ((TextView) view.findViewById(R.id.title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.details)).setText(marker.getSnippet());
                return view;
            }
        });

        Log.i("inside map ready", "MAP");


        // Add a listener for polyline clicks that changes the clicked polyline's color.
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                // Flip the values of the r, g and b components of the polyline's color.
                int strokeColor = polyline.getColor() ^ 0x00ffffff;
                polyline.setColor(strokeColor);
            }
        });

    }
// Language Selected from Spinner
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Language templang = (Language) parent.getSelectedItem();
        geocode_lang = templang.getId();
        // geocode_lang = maplang();
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void populateRoute() {

        // Use deviceId to have rest call based on deviceID.
        final long deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);

        String todate = routeToDate.getText().toString() + "T23:59:59.000";
        String convertedToDate = convertDate(todate, TimeZone.getDefault().getID(), "UTC");
        // Log.i("todate: ", convertedToDate);

        String fromdate = routeFromDate.getText().toString() + "T00:00:00.000";
        String convertedFromDate = convertDate(fromdate, TimeZone.getDefault().getID(), "UTC");

        // Log.i("from date: ", convertedFromDate);
        final String from_date = convertedFromDate + "Z";
        final String to_date = convertedToDate + "Z";
        final String type = "%";
        final MainApplication application = (MainApplication) getActivity().getApplication();
        final WebService service = application.getService();

        service.getRouteEvents(deviceId, type, from_date, to_date).enqueue(new WebServiceCallback<List<Route>>(getContext()) {
            final ProgressDialog mProgress = ProgressDialog.show(getActivity(), "Generating Route", "Please wait", true, true);

            @Override
            public void onSuccess(Response<List<Route>> response) {
                routeList = response.body();

                Toast.makeText(getContext(), "Route retrieved succesfully", Toast.LENGTH_LONG).show();
                populateRouteonMap();

                if (isAdded()) {
                    mProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {

                Toast.makeText(getContext(), "Route retrieval failed. Please try again", Toast.LENGTH_LONG).show();
                String stacktrace = Log.getStackTraceString(t);
                Log.i("Route retrieval failed", stacktrace);
                // Call to delete geofences from the geofence table so that orphan geofence is not there.

                if (isAdded()) {
                    mProgress.dismiss();
                }
            }

        });
    }

    public void populateRouteonMap() {

        // Instantiating the class MarkerOptions to plot marker on the map
        IconGenerator iconFactory = new IconGenerator(getContext());

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
        int markerPositionIndex = 0;
        int numberofmarkers = 0;

        //Depending on number of points,show markers. For > 100000 show 1000; 50000- 1 lakh show 500 ; 10000-50000 show 50; <1000 show 10
        if (routeList.size() >= 100000) {
            numberofmarkers = 1000;
        } else {
            if ((routeList.size() >= 50000) && (routeList.size() < 100000)) {
                numberofmarkers = 500;
            } else {
                if ((routeList.size() >= 10000) && (routeList.size() < 50000)) {
                    numberofmarkers = 50;
                } else {
                    if ((routeList.size() >= 1000) && (routeList.size() < 10000)) {
                        numberofmarkers = 25;
                    } else {
                        numberofmarkers = 10;
                    }

                }
            }
        }
        Log.i("numberofmarker :", String.valueOf(numberofmarkers));
        //Depending on number of points,show markers. For > 100000 show 1000; 50000- 1 lakh show 500 ; 10000-50000 show 50; <1000 show 10
        int markerStep = (routeList.size() < 10) ? 1:(routeList.size() / numberofmarkers);

        Log.i("markerstep :", String.valueOf(markerStep));
        Log.i("routelist.size :", String.valueOf(routeList.size()));

        // Add point for polyline and also save the marker positions to add marker once the polyline is drawn
        // latLngList.clear();   // clear the list to avoid duplication
        // markerPosition.clear();
        googleMap.clear();
        for (int z = 0; z < routeList.size(); z++) {
            double routeLat = routeList.get(z).getLatitude();
            double routeLng = routeList.get(z).getLongitude();
            LatLng point = new LatLng(routeLat, routeLng);
            //  latLngList.add(z,point);
            options.add(point);

            /*
            if ((z % numberofmarkers) == 0 ) {
                markerPosition.add(z);
            }
            */
        }

        polyline = googleMap.addPolyline(options);

        // As there is a delay in adding markers ,move camera to starting point so that user can see some action
        // Add marker at first  position and showInfowindow
        Route firstroute = routeList.get(0);
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        Marker firstMarker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Start")))
                .position(new LatLng(firstroute.getLatitude(), firstroute.getLongitude()))
                        // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Start")   // change for first , last , position
                .snippet("speed :" + firstroute.getSpeed() + "\n distance: " + firstroute.getAttributes().getDistance() +
                        "total distance: " + firstroute.getAttributes().getTotalDistance() +
                        "\n devicetime: " + convertDate(firstroute.getDeviceTime(), "America/New_York", TimeZone.getDefault().getID())));

        firstMarker.showInfoWindow();

        // Move the map so that it is at the first point
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList.get(0)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(firstroute.getLatitude(), firstroute.getLongitude())));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        // add markers now
        //   MarkerOptions markerOptions = new MarkerOptions();
        // Point number to display
        int pointcnt = 2 ;
        for (int k = markerStep; k < routeList.size(); k += markerStep) { // Get the markers at the market step position
            Route routemarker = routeList.get(k);  // Get details of the route point identified for marker
            //String markerTitle = (k == 0 ) ? "Start" : k + "th Marker" ;
            String markerTitle = pointcnt + "th Location";
            iconFactory.setStyle(IconGenerator.STYLE_DEFAULT);
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(pointcnt))))
                    .position(new LatLng(routemarker.getLatitude(), routemarker.getLongitude()))
                    .title(markerTitle)
                    .snippet("speed :" + routemarker.getSpeed() + "distance: " + routemarker.getAttributes().getDistance() +
                            "total distance: " + routemarker.getAttributes().getTotalDistance() +
                            "device time: " + convertDate(routemarker.getDeviceTime(), "America/New_York", TimeZone.getDefault().getID()))

                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));

            Log.i("k:lat:long:st ", String.valueOf(k) + ":" + String.valueOf(routemarker.getLatitude()) +
                    " " + ":" + String.valueOf(routemarker.getLongitude()) + ":" +
                    routemarker.getServerTime());
            pointcnt++;

        }

        // Add marker at last position
        Route routemarker = routeList.get(routeList.size() - 1);
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Stop")))
                .position(new LatLng(routemarker.getLatitude(), routemarker.getLongitude()))
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("End")   // change for first , last , position
                .snippet("speed :" + routemarker.getSpeed() + "distance: " + routemarker.getAttributes().getDistance() +
                                "total distance: " + routemarker.getAttributes().getTotalDistance() +
                                "device time: " + convertDate(routemarker.getDeviceTime(), "America/New_York", TimeZone.getDefault().getID())
                ));


        Log.i("inside actcreate", "MAP");
       // enable buttons to get route details
        enablebuttons();

        /* FOR geofence showing TESTING
        //  Showgeofence circle 1

        double geolat1 = 13.06364754974461;
        double geolong1 =  77.63830348849297 ;
        double georadius1 = 283 ;

        if ( geofenceCircle != null )
            geofenceCircle.remove();

        geofenceCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(geolat1, geolong1)).radius(georadius1)
                .fillColor(Color.parseColor("#B2A9F6")));

        // FOR ROBERT TESTING
        //  Showgeofence circle 2

        double geolat2 = 13.048178355156535;
        double geolong2 =  77.62225583195686 ;
        double georadius2 = 259 ;

        if ( geofenceCircle2 != null )
            geofenceCircle2.remove();



        geofenceCircle2 = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(geolat2, geolong2)).radius(georadius2)
                .fillColor(Color.parseColor("#B2A9F6")));
*/
    }

    public static String convertDate(String inputdate, String fromtimezone, String totimezone)

    {
        String sDateinto = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

            String dateInString = inputdate.substring(0, 22);

            formatter.setTimeZone(TimeZone.getTimeZone(fromtimezone));
            Date date = formatter.parse(dateInString);
            Log.i("FromDate String : ", formatter.format(date));


            SimpleDateFormat totime = new SimpleDateFormat(DATE_FORMAT);
            //TimeZone tzlocaltime = TimeZone.getDefault();
            totime.setTimeZone(TimeZone.getTimeZone(totimezone));

            sDateinto = totime.format(date); // Convert to String first
            Date dateInTo = formatter.parse(sDateinto); // Create a new Date object

//            Log.i("ToDate String: ", sDateinto);
            //           Log.i("ToDate Object: ", formatter.format(dateInTo));
            return sDateinto;
        } catch (ParseException e) {
            Log.i("Date Conversion", "Error in parsing");
            return "Date parsing error";
        }

    }

    public Boolean validateDate() {
        boolean dateflag = true;
        String getFromDate = routeFromDate.getText().toString();
        String getToDate = routeToDate.getText().toString();

        try {
            if (getFromDate.isEmpty() || getToDate.isEmpty()) {
                Toast.makeText(context, "Please enter From and To date", Toast.LENGTH_SHORT).show();
                dateflag = false;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date validFromDate = sdf.parse(getFromDate);
                Date validToDate = sdf.parse(getToDate);

                if (validFromDate.after(validToDate)) {
                    Toast.makeText(context, "From date should be less than To date", Toast.LENGTH_SHORT).show();
                    dateflag = false;
                } else {
                    dateflag = true;
                }
            }
            return dateflag;
        } catch (ParseException e) {
            Log.i("Date Conversion", "Error in parsing");
            return false;
        }
    }   // end of function


    public void populateforgeocoding() {

        int markerStep = (routeList.size() < NUMBEROFGEOCODE) ? 1:(routeList.size() / NUMBEROFGEOCODE);

        Log.i("markerStep", String.valueOf(markerStep));

        routeDetailList.clear(); // Making sure array is not having any left over value.
        for (int k = 0; k < routeList.size(); k += markerStep) { // Get the markers at the market step position
            routeDetailList.add(new RouteDetail(routeList.get(k).getLatitude(), routeList.get(k).getLongitude(), routeList.get(k).getServerTime()));
            Log.i("forgeo", String.valueOf(k));

        }
        //print for debugging

        for (int k = 0; k < routeDetailList.size(); k++) {
            Log.i("route k:lat:lo:st ", String.valueOf(k) + ":" + routeDetailList.get(k).getLat() + " ** " + routeDetailList.get(k).getlon() + ":" +
                    routeDetailList.get(k).getservertime());
        }
    }  // end populatateforgeocoding

    private void populateLang() {

        languageList.clear();
        //Add Languages

        languageList.add(new Language("en", "English"));
        languageList.add(new Language("bn", "Bengali"));
        languageList.add(new Language("hi", "Hindi"));
        languageList.add(new Language("gu", "Gujrati"));
        languageList.add(new Language("ml", "Malayalam"));
        languageList.add(new Language("mr", "Marathi"));
        languageList.add(new Language("ta", "Tamil"));
        languageList.add(new Language("te", "Telgu"));

    }

    public void enablebuttons() {
        routeDetailsButton.setEnabled(true);
        geolngSpinner.setEnabled(true);
        langtextview.setEnabled(true);
        routeDetailsButton.setVisibility(View.VISIBLE);
        geolngSpinner.setVisibility(View.VISIBLE);
        langtextview.setVisibility(View.VISIBLE);
    }

}



