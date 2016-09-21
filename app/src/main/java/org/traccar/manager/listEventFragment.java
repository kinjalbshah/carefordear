package org.traccar.manager;

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

import org.traccar.manager.model.DeviceEvent;
import org.traccar.manager.model.DeviceGeofence;
import org.traccar.manager.model.Geofence;
import org.traccar.manager.model.DeviceEvent;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
/**
 * Created by cf4 on 23-08-2016.
 */

public class listEventFragment extends Fragment {

    public static final String EXTRA_DEVICE_ID = "deviceId";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    //UI References
    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private List<Geofence> geofencelist;
    private List<DeviceEvent> deviceeventlist;

    public HashMap<Long, String> DeviceGeofenceMap = new HashMap<Long,String>();

    class PopupAdapter extends ArrayAdapter<DeviceEvent> {

        PopupAdapter(List<DeviceEvent> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //View view = super.getView(position, convertView, container);
            //Get data item for this position
            DeviceEvent deviceevent = getItem(position);

            //Check if existing view is being used else inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_event_item, container, false);
            }

            // Lookup view for data population

            TextView eventtype = (TextView) convertView.findViewById(R.id.eventtype);
            TextView geofenceid = (TextView) convertView.findViewById(R.id.geofenceid);
            TextView eventtime = (TextView) convertView.findViewById(R.id.eventtime);

            //Populate data into template using the deviceevents

         // Data is stored in server time which is US currently. Convert from Server time to India time for displaying

            String convertedDate = convertDate(deviceevent.getServerTime(), "America/New_York", TimeZone.getDefault().getID());
            eventtime.setText(convertedDate.substring(2, 19));


            eventtype.setText(deviceevent.getType());

            // For "geofencExit" and "geofenceEnter" events get the geofence name from geofencedevice hashmap
             Log.i("deviceevent type: ", deviceevent.getType());

            if (deviceevent.getType().matches("geofenceExit|geofenceEnter" ))
            {
                String tempgeofencename =  DeviceGeofenceMap.get(deviceevent.getGeofenceId());
                // Display 15 chars onl of geofence due to space constraints
                tempgeofencename = (tempgeofencename.length() > 15) ? tempgeofencename.substring(0,14) : tempgeofencename ;
                geofenceid.setText(tempgeofencename);
            }
            else
           {
                geofenceid.setText("      N/A     ");
            }
            //return complete view to render on screen
            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_event, container, false);

        return view;
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
        String todate = year1 + "-" + month1 + "-" + day1 + "T00:00:00.000" ;
        String convertedToDate = convertDate(todate, TimeZone.getDefault().getID() ,"UTC" );

        Log.i("todate: " , convertedToDate );

// Generate from date - currently 7 days back - ie  a week

        cal.add(Calendar.DATE, -7);

        // get the value of all the calendar date fields.
        int year2, month2, day2;
        year2 = cal.get(Calendar.YEAR);
        month2 = cal.get(Calendar.MONTH) + 1;
        day2 = cal.get(Calendar.DATE);

        String fromdate = year2 + "-" + month2 + "-" + day2 + "T00:00:00.000" ;
        String convertedFromDate = convertDate(fromdate , TimeZone.getDefault().getID() ,"UTC" );


        Log.i("from date: " , convertedFromDate );
        final String from_date = convertedFromDate + "Z";
        final String to_date = convertedToDate + "Z";
        final String type = "%";

        /*
        fromDate = this.lookupReference('fromDateField').getValue();
        fromTime = this.lookupReference('fromTimeField').getValue();
        from = new Date(
                fromDate.getFullYear(), fromDate.getMonth(), fromDate.getDate(),
                fromTime.getHours(), fromTime.getMinutes(), fromTime.getSeconds(), fromTime.getMilliseconds());


        toDate = this.lookupReference('toDateField').getValue();
        toTime = this.lookupReference('toTimeField').getValue();

        to = new Date(
                toDate.getFullYear(), toDate.getMonth(), toDate.getDate(),
                toTime.getHours(), toTime.getMinutes(), toTime.getSeconds(), toTime.getMilliseconds());


        deviceId: deviceId,
                type: '%',
                from: from.toISOString(),
                to: to.toISOString()
*/
        final MainApplication application = (MainApplication) getActivity().getApplication();
        final WebService service = application.getService();
        service.getDeviceEvents(deviceId, type, from_date, to_date).enqueue(new WebServiceCallback<List<DeviceEvent>>(getContext()) {
            @Override
            public void onSuccess(Response<List<DeviceEvent>> response) {
                deviceeventlist = response.body();

                service.getGeofences(deviceId).enqueue(new WebServiceCallback<List<Geofence>>(getContext()) {

                    @Override

                    public void onSuccess(Response<List<Geofence>> response) {
                        geofencelist = response.body();

                        Toast.makeText(getContext(), "Events retrieved succesfully", Toast.LENGTH_LONG).show();
                        populateEventList();
                    }

                    @Override
                    public void onFailure(Call<List<Geofence>> call, Throwable t) {

                        Toast.makeText(getContext(), "Events retrieved failed. Please try again", Toast.LENGTH_LONG).show();
                        String stacktrace = Log.getStackTraceString(t);
                        Log.i("Events retrieval failed", stacktrace);
                        // Call to delete geofences from the geofence table so that orphan geofence is not there.
                    }

                });

            }
        });


}    // End of create Activity
    public void populateEventList() {

     // As events only have goefenceid , get the geofence name from geofence results. Map the geofenceid and name in hashmap
        // that will be used in getview to get the geofencename for each geofenceid

       for (ListIterator<Geofence> iter = geofencelist.listIterator(); iter.hasNext(); ) {
           Geofence hashgeofence = iter.next();
           Log.i("populating hashmap", hashgeofence.getId() + " ** " + hashgeofence.getName());
           //DeviceGeofenceMap.put( iter.next().getId(), iter.next().getName());
           DeviceGeofenceMap.put( hashgeofence.getId(), hashgeofence.getName());
       }

        PopupAdapter eventAdapter = new PopupAdapter(deviceeventlist);
        // Attach the adapter to a ListView
        ListView listView = (ListView) getActivity().findViewById(R.id.lvDeviceEvents);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.list_event_item_header, null);
        listView.addHeaderView(headerView);
        listView.setAdapter(eventAdapter);
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

            Log.i("ToDate String: " , sDateinto );
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




