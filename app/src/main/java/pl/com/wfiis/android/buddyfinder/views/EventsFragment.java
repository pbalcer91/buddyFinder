package pl.com.wfiis.android.buddyfinder.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.DBServices.Callback;
import pl.com.wfiis.android.buddyfinder.DBServices.CallbackEvents;
import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventsFragment extends Fragment implements RecyclerViewInterface {

    private ArrayList<Event> events = new ArrayList<>();

    private float filteredDistance = 0;
    private String filteredTitle = "";

    private EditText searchText;

    private RecyclerView eventsList;

    RelativeLayout emptyListLabel;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private DBServices dbServices;
    private static ArrayList<Event> list = new ArrayList<>();

    public EventsFragment() {
        dbServices = new DBServices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsList = view.findViewById(R.id.rv_events_list);
        emptyListLabel = view.findViewById(R.id.empty_label);

        setupEventsList();

        searchText = view.findViewById(R.id.et_search);
        searchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                filteredTitle = searchText.getText().toString();
                filterEvents();
            }
            return false;
        });

        ImageView searchButton = view.findViewById(R.id.search_icon);
        searchButton.setOnClickListener(event -> filterEvents());

        ImageView filterIcon = view.findViewById(R.id.filter_icon);
        filterIcon.setOnClickListener(event -> showFilterDialog());

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), events, this);

        if (eventAdapter.getItemCount() == 0) {
            eventsList.setVisibility(View.INVISIBLE);
            emptyListLabel.setVisibility(View.VISIBLE);
        } else {
            eventsList.setVisibility(View.VISIBLE);
            emptyListLabel.setVisibility(View.INVISIBLE);
        }

        eventsList.setAdapter(eventAdapter);
        eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
//                            Event newEvent = data.getParcelableExtra("newEvent");
//                            user.addCreatedEvent(newEvent);
//
//                            EventAdapter newJoinedEventAdapter = new EventAdapter(this.getContext(), MainActivity.currentUser.getCreatedEvents(), this);
//                            eventsList.setAdapter(newJoinedEventAdapter);
//                            eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
                        }
                    }
                });

        return view;
    }

    private void setupEventsList() {
        // TODO: implement setup eventsList from database

                dbServices.getAllEvents(new CallbackEvents() {
                    @Override
                    public void onCallbackGetAllEvents(ArrayList<Event> dbList) {
                            events = dbList;
                    }


        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getContext(), EventDetailsDialog.class);
        intent.putExtra("event", events.get(position));
        intent.putExtra("date", events.get(position).getDate().getTime());
        intent.putExtra("latitude", events.get(position).getLatitude());
        intent.putExtra("longitude", events.get(position).getLongitude());
        activityResultLauncher.launch(intent);
    }

    public void showFilterDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_filter);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        Button resetButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reset);

        TextView distance = MainActivity.bottomSheetDialog.findViewById(R.id.tv_distance);

        SeekBar distanceBar = MainActivity.bottomSheetDialog.findViewById(R.id.seekBar);
        Objects.requireNonNull(distanceBar).setProgress(filteredDistance == 0 ? 10 : (int)filteredDistance);
        Objects.requireNonNull(distanceBar).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Objects.requireNonNull(distance).setText(distanceToString(distanceBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Objects.requireNonNull(distance).setText(distanceToString(distanceBar.getProgress()));

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    filteredDistance = distanceBar.getProgress() * 100;
                    filterEvents();

                    MainActivity.bottomSheetDialog.dismiss();
                });
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        Objects.requireNonNull(resetButton).setOnClickListener(
                tempView -> {
                    resetFilters();
                    MainActivity.bottomSheetDialog.dismiss();
                });

        MainActivity.bottomSheetDialog.show();
    }

    public void filterEvents() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);

        ArrayList<Event> searchedEvents = (ArrayList<Event>) this.events.clone();

        if (!filteredTitle.equals(""))
            searchedEvents.removeIf(event -> !event.getTitle().contains(filteredTitle));

        if (filteredDistance == 0) {
            EventAdapter eventAdapter = new EventAdapter(this.getContext(), searchedEvents, this);

            if (eventAdapter.getItemCount() == 0) {
                eventsList.setVisibility(View.INVISIBLE);
                emptyListLabel.setVisibility(View.VISIBLE);
            } else {
                eventsList.setVisibility(View.VISIBLE);
                emptyListLabel.setVisibility(View.INVISIBLE);
            }

            this.eventsList.setAdapter(eventAdapter);
            this.eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));

            return;
        }

        final Location[] currentLocation = new Location[1];
        Location eventLocation = new Location("");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        try {
            if (MainActivity.isLocationPermissionGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentLocation[0] = task.getResult();
                    }
                });
            }
        } catch(SecurityException e) {
            System.out.println("getDeviceLocation: SecurityException: " + e.getMessage());
        }

        for (Event event : searchedEvents) {
            eventLocation.setLatitude(event.getLocation().getLatitude());
            eventLocation.setLongitude(event.getLocation().getLongitude());

            float distance = currentLocation[0].distanceTo(eventLocation);

            if (distance > filteredDistance)
                searchedEvents.remove(event);
        }

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), searchedEvents, this);

        if (eventAdapter.getItemCount() == 0) {
            eventsList.setVisibility(View.INVISIBLE);
            emptyListLabel.setVisibility(View.VISIBLE);
        } else {
            eventsList.setVisibility(View.VISIBLE);
            emptyListLabel.setVisibility(View.INVISIBLE);
        }

        this.eventsList.setAdapter(eventAdapter);
        this.eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    public String distanceToString(int distance) {
        if (distance < 10) {
            String meters = String.valueOf(distance * 100);
            return (meters + " m");
        }

        String kilometers = String.valueOf((distance * 100) / 1000);
        String meters = String.valueOf(distance % 10);

        return (kilometers + "," + meters + " km");
    }

    public void resetFilters() {
        filteredDistance = 0;
        filteredTitle = "";
        searchText.setText(filteredTitle);

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), events, this);
        this.eventsList.setAdapter(eventAdapter);
        this.eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}