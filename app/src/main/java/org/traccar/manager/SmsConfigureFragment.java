package org.traccar.manager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SmsConfigureFragment extends Fragment {
    Context context;
    private String TAG = SmsConfigureFragment.class.getSimpleName();
    Button setTrackerPhnoBt, setAdminPhoneBt,  setSOSPhoneBt1, setSOSPhoneBt2, setSOSPhoneBt3, setSOSPhoneBt4, setSOSPhoneBt5, setSOSPhoneBt6, getTrackerPositionBt, setdeviceNumberBt;
    EditText trackerPhoneNumberET, adminPhoneNo , sosPhoneNo1ET , sosPhoneNo2ET , sosPhoneNo3ET , sosPhoneNo4ET , sosPhoneNo5ET , sosPhoneNo6ET ,deviceNumberET;

    private final String pwd = "123456";

    SharedPreferences settings ;
    SharedPreferences.Editor editor ;
    private static String PREFS_NAME  ;

    private static final String PREF_DEVICE_SIMNO = "trackerphno" ;
    private final String DefaultTrackerPhoneNumber = "";
    private String TrackerPhoneNumber ;


    private static final String PREF_ADMIN_SIMNO = "adminno";
    private final String DefaultAdminPhoneNumber = "";
    private String AdminPhoneNumber;

    private static final String PREF_SOS1_SIMNO = "SOS1no";
    private final String DefaultSOS1PhoneNumber = "";
    private String SOS1PhoneNumber;

    private static final String PREF_SOS2_SIMNO = "SOS2no";
    private final String DefaultSOS2PhoneNumber = "";
    private String SOS2PhoneNumber;

    private static final String PREF_SOS3_SIMNO = "SOS3no";
    private final String DefaultSOS3PhoneNumber = "";
    private String SOS3PhoneNumber;

    private static final String PREF_SOS4_SIMNO = "SOS4no";
    private final String DefaultSOS4PhoneNumber = "";
    private String SOS4PhoneNumber;

    private static final String PREF_SOS5_SIMNO = "SOS5no";
    private final String DefaultSOS5PhoneNumber = "";
    private String SOS5PhoneNumber;

    private static final String PREF_SOS6_SIMNO = "SOS6no";
    private final String DefaultSOS6PhoneNumber = "";
    private String SOS6PhoneNumber;

    String toPhoneNumber, smsMessage;

    private RadioGroup trackingRadioGroup;

    private RadioButton trackingRadioButton;

    public static final String EXTRA_DEVICE_ID = "deviceId";
    long deviceId ;


    //private BroadcastReceiver smsSent , smsDelivered ;
    private final BroadcastReceiver mybroadcast = new SmsBroadcastReceiver();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_smsconfigure, container, false);
        context = rootView.getContext();

        // Set preference based on device


         deviceId = getActivity().getIntent().getExtras().getLong(EXTRA_DEVICE_ID);

        //  Set tracker phone number
        trackerPhoneNumberET= (EditText) rootView.findViewById(R.id.trackerPhoneNumberET);
        setTrackerPhnoBt = (Button) rootView.findViewById(R.id.setTrackerPhnoBt);

        setTrackerPhnoBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (trackerPhoneNumberET.length() != 10 )
                    Toast.makeText(getContext(),
                            "Please enter Tracker phone number",Toast.LENGTH_SHORT).show();
                else {
                    //save tracker phno in preference
                    TrackerPhoneNumber = trackerPhoneNumberET.getText().toString();
                    //editor = settings.edit();  //settings are already set on device selection
                    //editor.putString(PREF_DEVICE_SIMNO, TrackerPhoneNumber);
                    Toast.makeText(getContext(),
                            "Tracker phone number set", Toast.LENGTH_SHORT).show();
                }
            }
        });   // Tracker phno


        // set Admin phno.

        adminPhoneNo = (EditText) rootView.findViewById(R.id.adminPhoneNo);
        setAdminPhoneBt = (Button) rootView.findViewById(R.id.setAdminPhoneBt);

        setAdminPhoneBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (trackerPhoneNumberET.length() != 10 )
                    Toast.makeText(getContext(),
                            "Please enter Tracker phone number",Toast.LENGTH_SHORT).show();
                else
                if (adminPhoneNo.length() == 10 ) {
                    //iti device   smsMessage = "admin" + pwd + " " + "0091" + adminPhoneNo.getText().toString();
                    smsMessage =  pwd + ",sos1," + "0" + adminPhoneNo.getText().toString() + "#";
                    Toast.makeText(getContext(), smsMessage, Toast.LENGTH_SHORT).show();
                    sendSMS(100);
                }
                else
                    Toast.makeText(getContext(),
                            "Please enter admin phone number.",Toast.LENGTH_SHORT).show();

            }
        });

        //Listener for adding SOS1 button

        setSOSPhoneBt1 = (Button) rootView.findViewById(R.id.setSOSPhoneBt1);
        sosPhoneNo1ET = (EditText) rootView.findViewById(R.id.sosPhoneNo1ET );

        setSOSPhoneBt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo1ET.length() != 10 )
                    Toast.makeText(getContext(), "Please enter first SOS number", Toast.LENGTH_SHORT).show();
                else
                    setSOS(1);

            }
        });    // SOS 1 listener

        //Listener for adding SOS2 button
        setSOSPhoneBt2 = (Button) rootView.findViewById(R.id.setSOSPhoneBt2);
        sosPhoneNo2ET  = (EditText) rootView.findViewById(R.id.sosPhoneNo2ET );
        setSOSPhoneBt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo2ET.length() != 10)
                    Toast.makeText(getContext(), "Please enter second SOS number", Toast.LENGTH_SHORT).show();

                else
                    setSOS(2) ;

            }
        });    // SOS 2 listener

        //* Comment for time being to get the layout working
        //Listener for adding SOS3 button
        setSOSPhoneBt3 = (Button) rootView.findViewById(R.id.setSOSPhoneBt3);
        sosPhoneNo3ET  = (EditText) rootView.findViewById(R.id.sosPhoneNo3ET );
        setSOSPhoneBt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo3ET.length() != 10)
                    Toast.makeText(getContext(), "Please enter third SOS number", Toast.LENGTH_SHORT).show();

                else
                    setSOS(3) ;

            }
        });    // SOS 3 listener

        //Listener for adding SOS4 button
        setSOSPhoneBt4 = (Button) rootView.findViewById(R.id.setSOSPhoneBt4);
        sosPhoneNo4ET  = (EditText) rootView.findViewById(R.id.sosPhoneNo4ET );
        setSOSPhoneBt4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo4ET.length() != 10)
                    Toast.makeText(getContext(), "Please enter fourth SOS number", Toast.LENGTH_SHORT).show();

                else
                    setSOS(4) ;

            }
        });    // SOS 4 listener

       //Comment SOS 5 and 6 code currently
        /*
        //Listener for adding SOS5 button
        setSOSPhoneBt5 = (Button) rootView.findViewById(R.id.setSOSPhoneBt5);
        sosPhoneNo5ET  = (EditText) rootView.findViewById(R.id.sosPhoneNo5ET );
        setSOSPhoneBt5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo5ET.length() != 10)
                    Toast.makeText(getContext(), "Please enter fifth SOS number", Toast.LENGTH_SHORT).show();

                else
                    setSOS(5) ;

            }
        });    // SOS 5 listener

        //Listener for adding SOS6 button
        setSOSPhoneBt6 = (Button) rootView.findViewById(R.id.setSOSPhoneBt6);
        sosPhoneNo6ET  = (EditText) rootView.findViewById(R.id.sosPhoneNo6ET );
        setSOSPhoneBt6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (sosPhoneNo6ET.length() != 10)
                    Toast.makeText(getContext(), "Please enter sixth SOS number", Toast.LENGTH_SHORT).show();

                else
                    setSOS(6) ;

            }
        });    // SOS 6 listener

        */

        //
        //Listener for getting tracker position


        getTrackerPositionBt = (Button) rootView.findViewById(R.id.getTrackerPositionBt);
        getTrackerPositionBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (adminPhoneNo.length()  != 10 || trackerPhoneNumberET.length() != 10 ) {
                    Toast.makeText(getContext(),
                            "Please enter admin phone number and tracker.",  Toast.LENGTH_SHORT).show();

                }
                else {
                    smsMessage = "dw#";  // Setting to India Timezone.
                    Toast.makeText(getContext(), smsMessage, Toast.LENGTH_SHORT).show();
                    sendSMS(200);
                }
            }
        });    //  Tracker position

        if ( deviceId > 1 ) {
            //set shared preference for device
            loadPreferences();
        }
        return rootView;
    }


    protected void setSOS(int sosbutno) {

        //Set SOS button based on the button number pressed.
        //Prior to setting ensure adminnumber , tracker number and device number is not null
        if (adminPhoneNo.length()  != 10 || trackerPhoneNumberET.length() != 10 ) {

            Toast.makeText(getContext(), "Please check 10 digit admin phone number, tracker number are entered.",  Toast.LENGTH_LONG).show();
            return ;
        }

        switch(sosbutno) {

            case 1:
                smsMessage = "sos1," + "0" + sosPhoneNo1ET.getText().toString() + "#";
                break;
            case 2:
                smsMessage = "sos2," + "0" + sosPhoneNo2ET.getText().toString() + "#";
                break;
            case 3:
                smsMessage = "sos3," + "0" + sosPhoneNo3ET.getText().toString() + "#";
                break;
            case 4:
                smsMessage = "sos4," + "0" + sosPhoneNo4ET.getText().toString() + "#";
                break;
            case 5:
                smsMessage = "sos5," + "0" + sosPhoneNo5ET.getText().toString() + "#";
                break;
            case 6:
                smsMessage = "sos6," + "0" + sosPhoneNo6ET.getText().toString() + "#";
                break;
            default:  Toast.makeText(getContext(), "No SOS button set", Toast.LENGTH_LONG).show();
                return;

        }  //End switch stmt
        // send message for SOS
        Toast.makeText(getContext(), smsMessage, Toast.LENGTH_LONG).show();
        sendSMS(1);

    }

    protected void sendSMS(Integer option) {
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_DELIVERED"), 0);

        //Need to add later what to do if tracker sim is blank, Should we return from function
        String toPhoneNumber = trackerPhoneNumberET.getText().toString();

        //BroadcastReceiver smsSent = new BroadcastReceiver(){   // will try later to remove intent leak
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            // }    #Commented leak handle
        }, new IntentFilter("SMS_SENT"));



        // For when the SMS has been delivered

        //BroadcastReceiver smsDelivered = new BroadcastReceiver(){
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED"));

        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, sentPendingIntent, deliveredPendingIntent);
            //Toast.makeText(getContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "sent sms");
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Sending SMS failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       //
       //Toast.makeText(getContext(), "in pause", Toast.LENGTH_LONG).show();
        savePreferences();
        getActivity().unregisterReceiver(mybroadcast);
        // getActivity().unregisterReceiver(smsSent);
        // getActivity().unregisterReceiver(smsDelivered);

    }

    @Override
    public void onResume () {
        super.onResume();
        //Toast.makeText(getContext(), "in resume" , Toast.LENGTH_LONG).show();
        loadPreferences();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(mybroadcast, filter);


      /* To investigate some more to avoid intent leaking

        IntentFilter smsSentFilter = new IntentFilter("SMS_SENT");
        IntentFilter smsDeliveredFilter = new IntentFilter("SMS_DELIVERED");

        getActivity().registerReceiver(smsSent, smsSentFilter) ;
        getActivity().registerReceiver(smsDelivered, smsDeliveredFilter) ;

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_DELIVERED"), 0);

      */
    }

    private void savePreferences() {
        if (deviceId > 1) {

            String sharedprefname = Long.toString(deviceId);
           // Toast.makeText(getContext(), "Inside save prference.", Toast.LENGTH_LONG).show();
            settings = getActivity().getSharedPreferences(sharedprefname, Context.MODE_PRIVATE);
            //settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();

            // Edit and commit
            TrackerPhoneNumber = trackerPhoneNumberET.getText().toString();
            editor.putString(PREF_DEVICE_SIMNO, TrackerPhoneNumber);

            AdminPhoneNumber = adminPhoneNo.getText().toString();
            editor.putString(PREF_ADMIN_SIMNO, AdminPhoneNumber);

            SOS1PhoneNumber = sosPhoneNo1ET.getText().toString();
            editor.putString(PREF_SOS1_SIMNO, SOS1PhoneNumber);

            SOS2PhoneNumber = sosPhoneNo2ET.getText().toString();
            editor.putString(PREF_SOS2_SIMNO, SOS2PhoneNumber);

            SOS3PhoneNumber = sosPhoneNo3ET.getText().toString();
            editor.putString(PREF_SOS3_SIMNO, SOS3PhoneNumber);

            SOS4PhoneNumber = sosPhoneNo4ET.getText().toString();
            editor.putString(PREF_SOS4_SIMNO, SOS4PhoneNumber);
/*Commented SOS 5 and 6
            SOS5PhoneNumber = sosPhoneNo5ET.getText().toString();
            editor.putString(PREF_SOS5_SIMNO, SOS5PhoneNumber);

            SOS6PhoneNumber = sosPhoneNo6ET.getText().toString();
            editor.putString(PREF_SOS6_SIMNO, SOS6PhoneNumber);
*/
            editor.apply();


        }
    }
    private void loadPreferences() {

            String sharedprefname = Long.toString(deviceId) ;
            settings = getActivity().getSharedPreferences(sharedprefname, Context.MODE_PRIVATE);
            boolean isdeviceSimDefined = settings.contains(PREF_DEVICE_SIMNO);

            if (isdeviceSimDefined ) {   // If it is first time then don't load.

           // Toast.makeText(getContext(), "in deviceSimDefined" , Toast.LENGTH_LONG).show();

            TrackerPhoneNumber = settings.getString(PREF_DEVICE_SIMNO, DefaultTrackerPhoneNumber);
            trackerPhoneNumberET.setText(TrackerPhoneNumber);

            AdminPhoneNumber = settings.getString(PREF_ADMIN_SIMNO, DefaultAdminPhoneNumber);
            adminPhoneNo.setText(AdminPhoneNumber);

            SOS1PhoneNumber = settings.getString(PREF_SOS1_SIMNO, DefaultSOS1PhoneNumber);
            sosPhoneNo1ET.setText(SOS1PhoneNumber);

            SOS2PhoneNumber = settings.getString(PREF_SOS2_SIMNO, DefaultSOS2PhoneNumber);
            sosPhoneNo2ET.setText(SOS2PhoneNumber);

            SOS3PhoneNumber = settings.getString(PREF_SOS3_SIMNO, DefaultSOS3PhoneNumber);
            sosPhoneNo3ET.setText(SOS3PhoneNumber);

            SOS4PhoneNumber = settings.getString(PREF_SOS4_SIMNO, DefaultSOS4PhoneNumber);
            sosPhoneNo4ET.setText(SOS4PhoneNumber);
/*Commented SOS 5 and 6 for timebeing
            SOS5PhoneNumber = settings.getString(PREF_SOS5_SIMNO, DefaultSOS5PhoneNumber);
            sosPhoneNo5ET.setText(SOS5PhoneNumber);

            SOS6PhoneNumber = settings.getString(PREF_SOS6_SIMNO, DefaultSOS6PhoneNumber);
            sosPhoneNo6ET.setText(SOS6PhoneNumber);
            */
       }
    }

}
