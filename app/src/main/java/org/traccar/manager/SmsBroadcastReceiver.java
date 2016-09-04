package org.traccar.manager;


        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;
        import java.text.SimpleDateFormat;
        import android.telephony.SmsMessage;
        import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    private String TAG = SmsBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle intentExtras = intent.getExtras();
        Log.d(TAG, "inside receiver");
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                long timeMillis = smsMessage.getTimestampMillis();

                Date date = new Date(timeMillis);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                String dateText = format.format(date);

                smsMessageStr += address +" at "+"\t"+ dateText + "\n";
                smsMessageStr += smsBody + "sms no " + i + "\n";


            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            // Display the entire SMS Message
            Log.d(TAG, smsMessageStr);

        }

    }
}
