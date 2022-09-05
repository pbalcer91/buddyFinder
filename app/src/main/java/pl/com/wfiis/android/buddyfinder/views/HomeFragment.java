package pl.com.wfiis.android.buddyfinder.views;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private User user;
    private ArrayList<Event> joinedEventsList = new ArrayList<>();

    private TextView welcomeLabel;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeLabel = view.findViewById(R.id.welcome_label);
        welcomeLabel.setText("Hello, " + user.getUserName());

        FloatingActionButton createEventButton = view.findViewById(R.id.btn_create_event);
        createEventButton.setOnClickListener(tempView -> {
            Event emptyEvent = new Event("New Event", user);

            Intent intent = new Intent(this.getContext(), EventCreatorDialog.class);
            intent.putExtra("newEvent", emptyEvent);
            intent.putExtra("date", emptyEvent.getDate().getTime());
            activityResultLauncher.launch(intent);
        });

        RecyclerView joinedEventsListView = view.findViewById(R.id.rv_joined_events_list);
        RecyclerView createdEventsListView = view.findViewById(R.id.rv_created_events_list);

        setupEventsList();

        EventAdapter joinedEventAdapter = new EventAdapter(this.getContext(), joinedEventsList, this);
        EventAdapter createdEventAdapter = new EventAdapter(this.getContext(), user.getCreatedEvents(), this);

        joinedEventsListView.setAdapter(joinedEventAdapter);
        joinedEventsListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        createdEventsListView.setAdapter(createdEventAdapter);
        createdEventsListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            Event newEvent = data.getParcelableExtra("newEvent");
                            user.addCreatedEvent(newEvent);

                            EventAdapter newCreatedEventAdapter = new EventAdapter(this.getContext(), user.getCreatedEvents(), this);
                            createdEventsListView.setAdapter(newCreatedEventAdapter);
                            createdEventsListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                        }
                    }
                });

        return view;
    }

    private void setupEventsList() {
        for (int i = 0; i < 3; i++) {
                joinedEventsList.add(new Event("Event numero " + i, new User("Smith", "some_email")));
        }
        // TODO: implement setup eventList from database
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getContext(), EventDetailsDialog.class);
        intent.putExtra("currentUser", user);
        intent.putExtra("event", joinedEventsList.get(position));
        intent.putExtra("date", joinedEventsList.get(position).getDate().getTime());
        activityResultLauncher.launch(intent);
    }
}