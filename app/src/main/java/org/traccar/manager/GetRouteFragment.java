package org.traccar.manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import org.traccar.manager.model.Route;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
    implements  OnMapReadyCallback {


    public static final String EXTRA_DEVICE_ID = "deviceId";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private SimpleDateFormat dateFormatter;
    private GoogleMap googleMap;

    private List<Route> routeList;
    private List<LatLng> latLngList = new ArrayList<LatLng>() ;
    private Polyline polyline ;

    private Polyline mMutablePolyline;

    private Polyline mClickablePolyline;

    private CheckBox mClickabilityCheckbox;


    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_get_route, parent, false);
        context = rootView.getContext();


        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.

        googleMap = map;
      //  googleMap.setOnMarkerDragListener(this);
        googleMap.setContentDescription("Google Map with polylines.");

        Log.i("inside map ready", "MAP");

        // A simple polyline with the default options from Melbourne-Adelaide-Perth.

        /*

        // A geodesic polyline that goes around the world.
        mClickablePolyline = map.addPolyline((new PolylineOptions())
                .add(LHR, AKL, LAX, JFK, LHR)
                .width(5)
                .color(Color.BLUE)
                .geodesic(true)
                .clickable(mClickabilityCheckbox.isChecked()));

                */


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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use deviceId to have rest call based on deviceID.
        final long deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);
        //Generate today and today -7 to get events for  a week
        // create a calendar
        Calendar cal = Calendar.getInstance();

        // get the value of all the calendar date fields.
        int year1, month1, day1;
        year1 = cal.get(Calendar.YEAR);
        month1 = cal.get(Calendar.MONTH) + 1;
        day1 = cal.get(Calendar.DATE);
        String todate = year1 + "-" + month1 + "-" + day1 + "T23:59:59.000";   //to cover all events including today
        String convertedToDate = convertDate(todate, TimeZone.getDefault().getID(), "UTC");


        Log.i("todate: ", convertedToDate);

// Generate from date - currently 7 days back - ie  a week

        cal.add(Calendar.DATE, -6);

        // get the value of all the calendar date fields.
        int year2, month2, day2;
        year2 = cal.get(Calendar.YEAR);
        month2 = cal.get(Calendar.MONTH) + 1;
        day2 = cal.get(Calendar.DATE);

        String fromdate = year2 + "-" + month2 + "-" + day2 + "T00:00:00.000";
        String convertedFromDate = convertDate(fromdate, TimeZone.getDefault().getID(), "UTC");


        Log.i("from date: ", convertedFromDate);
        final String from_date = convertedFromDate + "Z";
        final String to_date = convertedToDate + "Z";
        final String type = "%";

        final MainApplication application = (MainApplication) getActivity().getApplication();
        final WebService service = application.getService();
        service.getRouteEvents(deviceId, type, from_date, to_date).enqueue(new WebServiceCallback<List<Route>>(getContext()) {
            @Override
            public void onSuccess(Response<List<Route>> response) {
                routeList = response.body();

                Toast.makeText(getContext(), "Events retrieved succesfully", Toast.LENGTH_LONG).show();
                populateRouteonMap();
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {

                Toast.makeText(getContext(), "Route retrieval failed. Please try again", Toast.LENGTH_LONG).show();
                String stacktrace = Log.getStackTraceString(t);
                Log.i("Route retrieval failed", stacktrace);
                // Call to delete geofences from the geofence table so that orphan geofence is not there.
            }

        });
    }

    public void populateRouteonMap() {

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
        for (int z = 0; z < routeList.size(); z++) {
            double routeLat = routeList.get(z).getLatitude();
            double routeLng = routeList.get(z).getLongitude();
            LatLng point = new LatLng(routeLat, routeLng);
            latLngList.add(z,point);
            options.add(point);
        }
        polyline = googleMap.addPolyline(options);

        // Move the map so that it is at the first point
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList.get(0)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Log.i("inside actcreate", "MAP");
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

            Log.i("ToDate String: ", sDateinto);
            Log.i("ToDate Object: ", formatter.format(dateInTo));
            return sDateinto;
        } catch (ParseException e) {
            Log.i("Date Conversion", "Error in parsing");
            return "Date parsing error";
        }

    }

}
