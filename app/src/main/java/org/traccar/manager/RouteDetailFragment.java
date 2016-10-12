package org.traccar.manager;

/**
 * Created by CF4 on 10-10-2016.
 */

        import android.util.Log;

        import android.app.Activity;
        import android.app.DatePickerDialog;
        import android.support.v4.app.Fragment;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.ListFragment;
        import android.support.v7.widget.PopupMenu;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.traccar.manager.model.DeviceEvent;
        import org.traccar.manager.model.DeviceGeofence;
        import org.traccar.manager.model.GeocodeResult;
        import org.traccar.manager.model.Geocodeobj;
        import org.traccar.manager.model.Geofence;
        import org.traccar.manager.model.DeviceEvent;
        import org.traccar.manager.model.Route;
        import org.traccar.manager.model.RouteDetail;


        import java.net.CookieManager;
        import java.net.CookiePolicy;
        import java.util.AbstractList;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.ListIterator;

        import okhttp3.JavaNetCookieJar;
        import okhttp3.OkHttpClient;
        import okhttp3.ResponseBody;
        import okhttp3.logging.HttpLoggingInterceptor;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.jackson.JacksonConverterFactory;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.TimeZone;
        import java.util.concurrent.TimeUnit;

/**
 * Created by cf4 on 23-08-2016.
 */

public class RouteDetailFragment extends Fragment {

    public static final String EXTRA_DEVICE_ID = "deviceId";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private SimpleDateFormat dateFormatter;

    private List<Geofence> geofencelist;
    private List<DeviceEvent> deviceeventlist;

    public HashMap<Long, String> DeviceGeofenceMap = new HashMap<Long,String>();
    public List<String> routeDisplayList = new ArrayList<String>();
    public ArrayList<temproute>  routeDetailList = new ArrayList<temproute>();

    private Retrofit retrofitgeo;

    private OkHttpClient httpclient;
    private int  globallistcounter = 0;


    class temproute {
        private String stime;
        private Double lat;
        private Double lon;
        private String rgeocodeaddres;

        public temproute(String pstime, Double plat, Double plon ) {
            stime = pstime ;
            lat = plat;
            lon = plon;

        }

        public Double getLatitude() {  return lat;  }

        public void setLatitude(Double latitude) {    this.lat = latitude;  }

        public Double getLongitude() { return lon;   }

        public void setLongitude(Double longitude) { this.lon = longitude;  }

        public String getFixTime() {  return stime ; }

        public void setFixTime(String fixTime) { this.stime = fixTime;  }

        public String getRGeocodeAddress() {  return rgeocodeaddres ; }

        public void setRGeocodeAddress(String address) { this.rgeocodeaddres = address;  }
    }

    class PopupAdapter extends ArrayAdapter<temproute> {

        PopupAdapter(List<temproute> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //View view = super.getView(position, convertView, container);
            //Get data item for this position
            temproute temproutedisplay = getItem(position);

            //Check if existing view is being used else inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_route_item, container, false);
            }

            // Lookup view for data population

            TextView routesno = (TextView) convertView.findViewById(R.id.routesno);
            TextView routetime = (TextView) convertView.findViewById(R.id.routetime);
            TextView routeaddress = (TextView) convertView.findViewById(R.id.routeaddress);

            int pos = position+1;
            routesno.setText("" + pos);
            // Data is stored in server time which is US currently. Convert from Server time to India time for displaying

            String convertedDate = convertDate(temproutedisplay.getFixTime(), "America/New_York", TimeZone.getDefault().getID());
            routetime.setText(convertedDate.substring(2, 19));
            routeaddress.setText(temproutedisplay.getRGeocodeAddress());

            Log.i("route address: ", temproutedisplay.getRGeocodeAddress());

            //return complete view to render on screen
            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_route, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use deviceId to have rest call based on deviceID.
        //final long deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);

        /*
        Bundle bundle = getIntent().getExtras();
        ArrayList<RouteDetail> arraylist = bundle.getParcelableArrayList("routeDetail");
        */

        //For logging rest calls
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        // Rest calls logging
       httpclient = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(logging)
                .build();

         retrofitgeo = new Retrofit.Builder()
                .client(httpclient)
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

         routeDetailList.add( new temproute("2016-10-07T08:14:06.000-04:00", 13.0629419, 77.6394281)) ;
         routeDetailList.add( new temproute("2016-10-07T10:08:26.000-04:00", -33.8696, 151.2094)) ;

        // For each point do the geocoding and populate the array

       for (int listcounter = 0; listcounter < routeDetailList.size(); listcounter++) {

            //Log.i("doing geocoding", routedetailgeocode.getLatitude() + " ** " + routedetailgeocode.getLongitude());
           doReverseGeocode(routeDetailList.get(listcounter).getLatitude(),
                   routeDetailList.get(listcounter).getLongitude(), listcounter);

        //     Toast.makeText(getContext(), "in for loop: " + listcounter, Toast.LENGTH_LONG).show();
        //     Toast.makeText(getContext(), routeDetailList.get(listcounter).getRGeocodeAddress(), Toast.LENGTH_LONG).show();

        }

            }

    public void doReverseGeocode (Double lat, Double lon, final int flistcounter) {

        String tmpLatLng = lat.toString() + "," + lon.toString() ;
        GeocodeService rgeocodeservice = retrofitgeo.create(GeocodeService.class);
        final Call<Geocodeobj> call = rgeocodeservice.getAddress(tmpLatLng);

        call.enqueue(new Callback<Geocodeobj>() {
            @Override
            public void onResponse(Call<Geocodeobj> call, Response<Geocodeobj> response) {

                Geocodeobj jsonobj1 = response.body();
                globallistcounter++ ;    // Increment counter for number of points being processed.

                if (jsonobj1.getStatus().equals("OK")) {   // Got results. Take the first result and populate array
                    List<GeocodeResult> geocodeResult = jsonobj1.getResults();

                    String formattedaddress = geocodeResult.get(0).getFormattedAddress();

                    routeDetailList.get(flistcounter).setRGeocodeAddress(formattedaddress);

                 //   Toast.makeText(getContext(), "Events retrieved succesfully", Toast.LENGTH_LONG).show();
                 //   Toast.makeText(getContext(), routeDetailList.get(flistcounter).getRGeocodeAddress(), Toast.LENGTH_LONG).show();
                }
                else {   // No results found.
                    routeDetailList.get(flistcounter).setRGeocodeAddress("No Address found");
                }

                // As the calls are in async, wait till all 10 points have been reversegeocoded till calling the display.
                if (globallistcounter >= 2) {
                    populateRouteDisplayList();
                }
            }

            @Override
            public void onFailure(Call<Geocodeobj> call, Throwable t) {

                Toast.makeText(getContext(), "Events retrieved failed. Please try again", Toast.LENGTH_LONG).show();
                String stacktrace = Log.getStackTraceString(t);
                Log.i("Events retrieval failed", stacktrace);

            }

        });

    }

    public void populateRouteDisplayList() {

       PopupAdapter routedetailAdapter = new PopupAdapter(routeDetailList);
        // Attach the adapter to a ListView
        ListView listView = (ListView) getActivity().findViewById(R.id.lvRouteDetails);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.list_route_header, null);
        listView.addHeaderView(headerView);
        listView.setAdapter(routedetailAdapter);

    }

    // Function to convert date from one timezone to another. It first sets string to fromtimezone and then converts to totimezone

    public static String convertDate(String inputdate, String fromtimezone, String totimezone)

    {
        String sDateinto = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

            String dateInString = inputdate.substring(0, 22) ;

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
            return sDateinto ;
        }
        catch (ParseException e)
        {
            Log.i("Date Conversion" ,"Error in parsing" );
            return "Date parsing error" ;
        }

    }

}




