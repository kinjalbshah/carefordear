package org.traccar.manager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import java.util.Calendar;

/**
 * Created by CF4  on 06-10-2016.
 */
public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener dateSetListener; // listener object to get calling fragment listener
    DatePickerDialog myDatePicker;

    public static final int FLAG_FROM_DATE = 0;
    public static final int FLAG_TO_DATE = 1;

    private int flag = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dateSetListener = (DatePickerDialog.OnDateSetListener)getTargetFragment(); // getting passed fragment
        myDatePicker = new DatePickerDialog(getActivity(), dateSetListener, year, month, day); // DatePickerDialog gets callBack listener as 2nd parameter
        // Create a new instance of DatePickerDialog and return it
        return myDatePicker;
    }

    public void setFlag(int i) {
        flag = i;
    }

    public int getFlag() {
        return flag;
    }
}