package org.traccar.manager;

/**
 * Created by CF4 on 10-10-2016.
 */

        import android.util.Log;
        import android.support.v4.app.Fragment;

        import android.os.Bundle;
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
        import org.traccar.manager.model.GeocodeResult;
        import org.traccar.manager.model.Geocodeobj;
        import org.traccar.manager.model.RouteDetail;
        import org.traccar.manager.model.TranslateAddress;
        import org.traccar.manager.model.Translation;

        import java.io.IOException;
        import java.lang.reflect.Array;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
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
    private static final int NUMBEROFGEOCODE = 10;

    public ArrayList<RouteDetail> routeDetailList = new ArrayList<RouteDetail>();
    public TranslateAddress translateAddress;
    public ArrayList<String> formattedAddressTranslate = new ArrayList<String>();


    private Retrofit retrofitgeo, retrofittrans;
    private OkHttpClient httpclient, httpclienttrans;
    //private Translate translate ;
    private int globallistcounter = 0;

    private String geolang;


    class PopupAdapter extends ArrayAdapter<RouteDetail> {

        PopupAdapter(List<RouteDetail> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //View view = super.getView(position, convertView, container);
            //Get data item for this position
            RouteDetail RouteDetaildisplay = getItem(position);

            //Check if existing view is being used else inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_route_item, container, false);
            }

            // Lookup view for data population

            TextView routesno = (TextView) convertView.findViewById(R.id.routesno);
            TextView routetime = (TextView) convertView.findViewById(R.id.routetime);
            TextView routeaddress = (TextView) convertView.findViewById(R.id.routeaddress);
            TextView routeaddresstranslated = (TextView) convertView.findViewById(R.id.routeaddresstranslated);

            int pos = position + 1;
            routesno.setText("" + pos);
            // Data is stored in server time which is US currently. Convert from Server time to India time for displaying

            String convertedDate = convertDate(RouteDetaildisplay.getservertime(), "America/New_York", TimeZone.getDefault().getID());
            routetime.setText(convertedDate.substring(2, 19));
            routeaddress.setText(RouteDetaildisplay.getRGeocodeAddress());

        if (RouteDetaildisplay.getTranslatedaddress() != null) {
                routeaddresstranslated.setText(RouteDetaildisplay.getTranslatedaddress());
         }

           // Toast.makeText(getContext(), "add " + RouteDetaildisplay.getTranslatedaddress(), Toast.LENGTH_LONG).show();
           // routeaddress.setText(RouteDetaildisplay.getTranslatedaddress() );

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

        Bundle bundle = getActivity().getIntent().getExtras();

        routeDetailList.clear();
        routeDetailList = bundle.getParcelableArrayList("routelist");
        geolang = bundle.getString("lang");


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

        //Keeping httpclient and retrofit seprate for translate as that will be called from geocode response

        httpclienttrans = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(logging)
                .build();

        retrofittrans = new Retrofit.Builder()
                .client(httpclienttrans)
                .baseUrl("https://www.googleapis.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // For each point do the geocoding and populate the array

        globallistcounter = 0;
        Log.i("routedetlang  " , geolang);
       for (int listcounter = 0; listcounter < routeDetailList.size(); listcounter++) {

           Log.i("rdetail i lat lon ", listcounter + " :" + routeDetailList.get(listcounter).getLat() + " ** " + routeDetailList.get(listcounter).getlon()
                + "**" + routeDetailList.get(listcounter).getservertime() );
           doReverseGeocode(routeDetailList.get(listcounter).getLat(),
                            routeDetailList.get(listcounter).getlon(), listcounter);

          }

    }

    public void doReverseGeocode(final Double lat, final Double lon, final int flistcounter) {

        String tmpLatLng = lat.toString() + "," + lon.toString();
        GeocodeService rgeocodeservice = retrofitgeo.create(GeocodeService.class);
      //  final Call<Geocodeobj> call = rgeocodeservice.getAddress(tmpLatLng, geolang);
        final Call<Geocodeobj> call = rgeocodeservice.getAddress(tmpLatLng, "en"); //always calling in en as using translate

        call.enqueue(new Callback<Geocodeobj>() {
            @Override
            public void onResponse(Call<Geocodeobj> call, Response<Geocodeobj> response) {

                Geocodeobj jsonobj1 = response.body();
                globallistcounter++;    // Increment counter for number of points being processed.

                if (jsonobj1.getStatus().equals("OK")) {   // Got results. Take the first result and populate array
                    List<GeocodeResult> geocodeResult = jsonobj1.getResults();

                    String formattedaddress = geocodeResult.get(0).getFormattedAddress();

                   //Populate routedetaillist
                    routeDetailList.get(flistcounter).setRGeocodeAddress(formattedaddress);

                    Log.i("OK", flistcounter + "" + lat + "" + lon + "" + routeDetailList.get(flistcounter).getRGeocodeAddress());

                } else {   // No results found.
                    routeDetailList.get(flistcounter).setRGeocodeAddress("No Address found");

                }

                // As the calls are in async, wait till all 10 points have been reversegeocoded till calling the display.
                Log.i("glc : lc", "" + globallistcounter + "" + flistcounter);

                if (globallistcounter >= NUMBEROFGEOCODE + 1) {
                    gettranslateaddressfunc();   // Get translated address using translate api

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
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.list_route_header, null);
        listView.addHeaderView(headerView);
        listView.setAdapter(routedetailAdapter);

    }

    public void gettranslateaddressfunc() {
    GeocodeService translateservice = retrofittrans.create(GeocodeService.class);

        formattedAddressTranslate.clear();

        for (int i = 0 ; i < routeDetailList.size() ; i++) {
            formattedAddressTranslate.add(i,routeDetailList.get(i).getRGeocodeAddress());
        }

    final Call<TranslateAddress> call = translateservice.getTranslatedAddress(geolang, "en", formattedAddressTranslate);

        call.enqueue(new Callback<TranslateAddress>()

                     {
                         @Override
                         public void onResponse
                                 (Call<TranslateAddress> call, Response<TranslateAddress> response) {

                             translateAddress = response.body();

                             if (response.code() != 200) {
                                 Log.i("Translate err ret code ", String.valueOf(response.code()));
                                 Toast.makeText(getContext(), "Error in translation " + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                             } else {  // proceeed only if translate API gave good results - status code 200
                                 for (int i = 0; i < translateAddress.getData().getTranslations().size(); i++) {
                                     String transadd = translateAddress.getData().getTranslations().get(i).getTranslatedText();
                                     routeDetailList.get(i).setTranslatedaddress(transadd);

                                   //  Log.i("Translated i ", i + routeDetailList.get(i).getTranslatedaddress());
                                   //  Toast.makeText(getContext(), "i add: " + i + routeDetailList.get(i).getTranslatedaddress(), Toast.LENGTH_LONG).show();

                                 }
                                 //Display once the address is translated.


                             }
                             populateRouteDisplayList();  // Display the list

                         }

                         @Override
                         public void onFailure(Call<TranslateAddress> call, Throwable t) {

                             Toast.makeText(getContext(), "Translate retrieval failed. Please try again", Toast.LENGTH_LONG).show();
                             String stacktrace = Log.getStackTraceString(t);
                             Log.i("Translate call failed", stacktrace);

                     }

                 }

    );
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

           // Log.i("ToDate String: ", sDateinto);
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




