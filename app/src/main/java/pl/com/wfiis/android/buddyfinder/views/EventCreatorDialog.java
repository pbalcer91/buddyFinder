package pl.com.wfiis.android.buddyfinder.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class EventCreatorDialog extends AppCompatActivity {
    private Event newEvent;

    ActivityResultLauncher<Intent> activityResultLauncher;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private ImageView backButton;

    private RelativeLayout dateButton;
    private TextView dateTextView;

    private RelativeLayout timeButton;
    private TextView timeTextView;

    private RelativeLayout locationButton;
    private TextView locationTextView;

    private EditText titleField;
    private EditText descriptionField;

    private Button createButton;

    private BottomSheetDialog bottomSheetDialog;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private boolean isDateSelected = false;
    private boolean isTimeSelected = false;

    private boolean editMode = false;

    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS)
            return true;
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }

        return false;
    }

    private void validateCreator() {
        if (titleField.getText().length() > 0
                && newEvent.getDate().getTime() > Calendar.getInstance().getTimeInMillis()
                && newEvent.getLocation() != null) {
            createButton.setEnabled(true);
            return;
        }

        createButton.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_event_creator);

        this.newEvent = getIntent().getParcelableExtra("newEvent");

        editMode = (this.newEvent.getLocation() != null);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            Address address = data.getParcelableExtra("location");
                            newEvent.setLocation(address);

                            validateCreator();

                            locationTextView.setText(newEvent.getLocation() != null ?
                                    newEvent.getLocation().getAddressLine(0)
                                    : "Set location");
                        }
                    }
                });

        backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        locationButton = this.findViewById(R.id.btn_event_location);
        locationButton.setOnClickListener(event -> showMap());

        locationTextView = this.findViewById(R.id.tv_event_create_location);
        locationTextView.setText(newEvent.getLocation() != null ?
                newEvent.getLocation().getAddressLine(0)
                : "Set location");

        dateButton = this.findViewById(R.id.btn_event_create_date);
        dateButton.setOnClickListener(event -> showCalendarDialog(newEvent.getDate()));

        dateTextView = this.findViewById(R.id.tv_event_create_date);
        dateTextView.setText(dateFormat.format(newEvent.getDate()));

        timeButton = this.findViewById(R.id.btn_event_create_time);
        timeButton.setOnClickListener(event -> showTimeDialog(newEvent.getDate()));

        timeTextView = this.findViewById(R.id.tv_event_create_time);
        timeTextView.setText(timeFormat.format(newEvent.getDate()));

        titleField = this.findViewById(R.id.et_event_title);
        titleField.setText(newEvent.getTitle());
        titleField.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                newEvent.setTitle(titleField.getText().toString());

                validateCreator();

                titleField.clearFocus();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                return true;
            }
            return false;
        });

        descriptionField = this.findViewById(R.id.et_event_description);
        descriptionField.setText(newEvent.getDescription());
        descriptionField.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                newEvent.setDescription(descriptionField.getText().toString());

                validateCreator();

                descriptionField.clearFocus();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                return true;
            }
            return false;
        });

        createButton = this.findViewById(R.id.btn_event_creator_accept);
        if (editMode)
            createButton.setText(R.string.confirm);
        else
            createButton.setText("Create");

        createButton.setOnClickListener(event -> {
            Intent intent = new Intent();
            intent.putExtra("newEvent", newEvent);
            setResult(MainActivity.RESULT_DATA_OK, intent);
            //TODO: add event to database

            this.finish();
        });

        validateCreator();
    }

    private void showMap() {
        if (!isServicesOk())
            return;

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("event", newEvent);
        activityResultLauncher.launch(intent);
    }

    private void showCalendarDialog(Date selectedDate) {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_date_picker);

        bottomSheetDialog.setOnDismissListener(event -> validateCreator());

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
        });

        bottomSheetDialog.show();
    }

    private void showTimeDialog(Date selectedDate) {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_time_picker);

        bottomSheetDialog.setOnDismissListener(event -> validateCreator());

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
        });

        bottomSheetDialog.show();
    }
}
