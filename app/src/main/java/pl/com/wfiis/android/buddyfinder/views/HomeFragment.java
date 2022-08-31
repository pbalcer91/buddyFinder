package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment {

    private User user;
    private ArrayList<Event> joinedEventsList = new ArrayList<>();
    private ArrayList<Event> createdEventsList = new ArrayList<>();

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

        TextView welcomeLabel = view.findViewById(R.id.welcome_label);
        welcomeLabel.setText("Hello, " + user.getUserName());

        //TODO: usunac po testach
//        EventDetailsDialog dialog = new EventDetailsDialog(requireContext(), new Event("Test", new User("Tommy", "email"),"Test description"), user, R.style.Dialog);
//        dialog.setContentView(R.layout.dialog_event_details);

        EventCreatorDialog eventCreatorDialog = new EventCreatorDialog(requireContext(), user, R.style.Dialog);
        eventCreatorDialog.setContentView(R.layout.dialog_event_creator);

        FloatingActionButton createEventButton = view.findViewById(R.id.btn_create_event);
        createEventButton.setOnClickListener(tempView -> eventCreatorDialog.show());

        RecyclerView joinedEventsListView = view.findViewById(R.id.rv_joined_events_list);
        RecyclerView createdEventsListView = view.findViewById(R.id.rv_created_events_list);

        setupEventsList();

        EventAdapter joinedEventAdapter = new EventAdapter(this.getContext(), joinedEventsList);
        EventAdapter createdEventAdapter = new EventAdapter(this.getContext(), createdEventsList);

        joinedEventsListView.setAdapter(joinedEventAdapter);
        joinedEventsListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        createdEventsListView.setAdapter(createdEventAdapter);
        createdEventsListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    private void setupEventsList() {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0)
                joinedEventsList.add(new Event("Event numero " + i, new User("Smith", "some_email")));
            else
                createdEventsList.add(new Event("Event numero " + i, user));
        }
        // TODO: implement setup eventList from database
    }
}