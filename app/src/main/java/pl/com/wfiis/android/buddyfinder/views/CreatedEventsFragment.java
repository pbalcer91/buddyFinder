package pl.com.wfiis.android.buddyfinder.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class CreatedEventsFragment extends Fragment implements RecyclerViewInterface {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public CreatedEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_events, container, false);

        ConstraintLayout createdEventsViewLogged = view.findViewById(R.id.view_created_events_logged);
        RelativeLayout createdEventsViewNotLogged = view.findViewById(R.id.view_created_events_not_logged);

        LinearLayout createdEventsListView = view.findViewById(R.id.layout_created_events);
        LinearLayout emptyCreatedEventsListView = view.findViewById(R.id.layout_empty_created_events);

        RecyclerView createdEvents = view.findViewById(R.id.rv_created_events_list);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            Event newEvent = data.getParcelableExtra("newEvent");
                            MainActivity.currentUser.addCreatedEvent(newEvent);
                            MainActivity.currentUser.addJoinedEvent(newEvent);

                            EventAdapter newCreatedEventAdapter = new EventAdapter(this.getContext(), MainActivity.currentUser.getCreatedEvents(), this);
                            createdEvents.setAdapter(newCreatedEventAdapter);
                            createdEvents.setLayoutManager(new LinearLayoutManager(this.getContext()));
                        }
                    }
                });

        if (MainActivity.currentUser == null) {
            createdEventsViewNotLogged.setVisibility(View.VISIBLE);

            Button signIn = view.findViewById(R.id.btn_sign_in);
            signIn.setOnClickListener(event -> MainActivity.showLoginDialog(this.getContext()));

            Button signUp = view.findViewById(R.id.btn_sign_up);
            signUp.setOnClickListener(event -> MainActivity.showRegisterDialog(this.getContext()));

            return view;
        }

        createdEventsViewLogged.setVisibility(View.VISIBLE);

        if (MainActivity.currentUser.getCreatedEvents().size() == 0) {
            emptyCreatedEventsListView.setVisibility(View.VISIBLE);

            Button createFirstEventButton = view.findViewById(R.id.btn_empty_list_create);

            createFirstEventButton.setOnClickListener(tempView -> {
                Event emptyEvent = new Event(MainActivity.currentUser);

                Intent intent = new Intent(this.getContext(), EventCreatorDialog.class);
                intent.putExtra("newEvent", emptyEvent);
                intent.putExtra("date", emptyEvent.getDate().getTime());
                activityResultLauncher.launch(intent);
            });

            return view;
        }

        createdEventsListView.setVisibility(View.VISIBLE);

        Button createEventButton = view.findViewById(R.id.btn_create_event);
        createEventButton.setOnClickListener(tempView -> {
            Event emptyEvent = new Event(MainActivity.currentUser);

            Intent intent = new Intent(this.getContext(), EventCreatorDialog.class);
            intent.putExtra("newEvent", emptyEvent);
            intent.putExtra("date", emptyEvent.getDate().getTime());
            activityResultLauncher.launch(intent);
        });

        EventAdapter createdEventAdapter = new EventAdapter(this.getContext(), MainActivity.currentUser.getCreatedEvents(), this);

        createdEvents.setAdapter(createdEventAdapter);
        createdEvents.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getContext(), EventDetailsDialog.class);
        intent.putExtra("currentUser", MainActivity.currentUser);
        intent.putExtra("event", MainActivity.currentUser.getCreatedEvents().get(position));
        intent.putExtra("date", MainActivity.currentUser.getCreatedEvents().get(position).getDate().getTime());
        activityResultLauncher.launch(intent);
    }
}
