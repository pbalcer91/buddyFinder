package pl.com.wfiis.android.buddyfinder.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventMemberAdapter;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventCreatorDialog extends Dialog {
    private Event newEvent;
    private User currentUser;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private ImageView backButton;

    private RelativeLayout dateButton;
    private TextView dateTextView;

    private RelativeLayout timeButton;
    private TextView timeTextView;

    private RelativeLayout locationButton;

    private EditText titleField;
    private EditText descriptionField;

    private Button createButton;

    private BottomSheetDialog bottomSheetDialog;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private boolean isDateSelected = false;
    private boolean isTimeSelected = false;

    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getContext());

        if (available == ConnectionResult.SUCCESS)
            return true;
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) this.getContext(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }

        return false;
    }

    public EventCreatorDialog(@NonNull Context context,
                              User currentUser,
                              int themeResId) {
        super(context, themeResId);
        this.currentUser = currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newEvent = new Event("New Event", currentUser);

        backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.cancel());

        locationButton = this.findViewById(R.id.btn_event_location);
        locationButton.setOnClickListener(event -> showMap());

        dateButton = this.findViewById(R.id.btn_event_create_date);
        dateButton.setOnClickListener(event -> showCalendarDialog(newEvent.getDate()));

        dateTextView = this.findViewById(R.id.tv_event_create_date);
        dateTextView.setText(dateFormat.format(newEvent.getDate()));

        timeButton = this.findViewById(R.id.btn_event_create_time);
        timeButton.setOnClickListener(event -> showTimeDialog(newEvent.getDate()));

        timeTextView = this.findViewById(R.id.tv_event_create_time);
        timeTextView.setText(timeFormat.format(newEvent.getDate()));

        titleField = this.findViewById(R.id.et_event_title);
        titleField.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                newEvent.setTitle(titleField.getText().toString());

                createButton.setEnabled(titleField.getText().length() > 0
                                && isDateSelected
                                && isTimeSelected);

                titleField.clearFocus();

                InputMethodManager inputManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                return true;
            }
            return false;
        });

        descriptionField = this.findViewById(R.id.et_event_description);
        descriptionField.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                newEvent.setDescription(descriptionField.getText().toString());

                createButton.setEnabled(titleField.getText().length() > 0
                                && isDateSelected
                                && isTimeSelected);

                descriptionField.clearFocus();

                InputMethodManager inputManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                return true;
            }
            return false;
        });

        createButton = this.findViewById(R.id.btn_event_creator_accept);
        createButton.setOnClickListener(event -> System.out.println("CREATED"));
        createButton.setEnabled(false);
    }

    private void showMap() {
        if (!isServicesOk())
            return;

        Intent intent = new Intent(this.getContext(), MapActivity.class);
        intent.putExtra("event", newEvent);
        this.getContext().startActivity(intent);
    }

    private void showCalendarDialog(Date selectedDate) {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_date_picker);

        bottomSheetDialog.setOnDismissListener(event -> createButton.setEnabled(
                    titleField.getText().length() > 0
                    && isDateSelected
                    && isTimeSelected
        ));

        final Date newDate = selectedDate;

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(selectedDate);

        Button closeButton = bottomSheetDialog.findViewById(R.id.btn_calendar_close);
        closeButton.setOnClickListener(event -> bottomSheetDialog.cancel());

        Button acceptButton = bottomSheetDialog.findViewById(R.id.btn_calendar_accept);
        acceptButton.setOnClickListener(event -> {
            newEvent.setDate(newDate);
            dateTextView.setText(dateFormat.format(newEvent.getDate()));

            if (!isDateSelected)
                isDateSelected = true;

            bottomSheetDialog.dismiss();
        });

        CalendarView calendar = bottomSheetDialog.findViewById(R.id.calendar_picker);
        calendar.setDate(selectedDate.getTime(),false,true);

        calendar.setOnDateChangeListener((calendarView, year, month, day) -> {
            calendarDate.set(year,
                    month,
                    day,
                    calendarDate.get(Calendar.HOUR_OF_DAY),
                    calendarDate.get(Calendar.MINUTE));

            newDate.setTime(calendarDate.getTimeInMillis());

            if (acceptButton.isEnabled() && newDate.getTime() < Calendar.getInstance().getTimeInMillis())
                acceptButton.setEnabled(false);

            if (!acceptButton.isEnabled() && newDate.getTime() >= Calendar.getInstance().getTimeInMillis())
                acceptButton.setEnabled(true);

        });

        bottomSheetDialog.show();
    }

    private void showTimeDialog(Date selectedDate) {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_time_picker);

        bottomSheetDialog.setOnDismissListener(event -> createButton.setEnabled(
                        titleField.getText().length() > 0
                        && isDateSelected
                        && isTimeSelected
        ));

        final Date newTime = selectedDate;

        TimePicker timePicker = bottomSheetDialog.findViewById(R.id.time_picker);
        Objects.requireNonNull(timePicker).setIs24HourView(true);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(selectedDate);

        timePicker.setHour(calendarTime.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendarTime.get(Calendar.MINUTE));

        Button closeButton = bottomSheetDialog.findViewById(R.id.btn_time_close);
        closeButton.setOnClickListener(event -> bottomSheetDialog.cancel());

        Button acceptButton = bottomSheetDialog.findViewById(R.id.btn_time_accept);
        acceptButton.setOnClickListener(event -> {
            newEvent.setDate(newTime);
            timeTextView.setText(timeFormat.format(newEvent.getDate()));

            if (!isTimeSelected)
                isTimeSelected = true;

            bottomSheetDialog.dismiss();
        });

        timePicker.setOnTimeChangedListener((timePicker1, hour, minute) -> {
            calendarTime.set(calendarTime.get(Calendar.YEAR),
                    calendarTime.get(Calendar.MONTH),
                    calendarTime.get(Calendar.DAY_OF_MONTH),
                    hour,
                    minute);

            newTime.setTime(calendarTime.getTimeInMillis());

            if (acceptButton.isEnabled() && newTime.getTime() < Calendar.getInstance().getTimeInMillis())
                acceptButton.setEnabled(false);

            if (!acceptButton.isEnabled() && newTime.getTime() >= Calendar.getInstance().getTimeInMillis())
                acceptButton.setEnabled(true);
        });

        bottomSheetDialog.show();
    }
}
