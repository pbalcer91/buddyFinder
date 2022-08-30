package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventsFragment extends Fragment {

    private ArrayList<Event> events = new ArrayList<>();
    private User user;

    public EventsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        RecyclerView eventsList = view.findViewById(R.id.rv_events_list);

        setupEventsList();

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), events);
        eventsList.setAdapter(eventAdapter);
        eventsList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    private void setupEventsList() {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0)
                events.add(new Event("Event numero " + i, new User("Smith", "some_email")));
            else
                events.add(new Event("Event numero " + i, user));
        }
        // TODO: implement setup eventsList from database
    }
}