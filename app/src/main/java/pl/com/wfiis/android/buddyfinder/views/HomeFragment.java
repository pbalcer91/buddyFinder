package pl.com.wfiis.android.buddyfinder.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private User user;

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

        ConstraintLayout homeViewLogged = view.findViewById(R.id.view_home_logged);
        RelativeLayout homeViewNotLogged = view.findViewById(R.id.view_home_not_logged);

        LinearLayout eventsListView = view.findViewById(R.id.layout_home_joined_events);
        LinearLayout emptyEventsListView = view.findViewById(R.id.layout_home_empty_joined_events);

        if (user == null) {
            homeViewNotLogged.setVisibility(View.VISIBLE);

            Button signIn = view.findViewById(R.id.btn_sign_in);
            signIn.setOnClickListener(event -> MainActivity.showLoginDialog(this.getContext()));

            Button signUp = view.findViewById(R.id.btn_sign_up);
            signUp.setOnClickListener(event -> MainActivity.showRegisterDialog(this.getContext()));

            return view;
        }

        homeViewLogged.setVisibility(View.VISIBLE);

        if (user.getJoinedEvents().size() == 0) {
            emptyEventsListView.setVisibility(View.VISIBLE);

            Button searchEventButton = view.findViewById(R.id.btn_home_search);
            Button createEventButton = view.findViewById(R.id.btn_home_create);

            searchEventButton.setOnClickListener(tempView -> {
                MainActivity.bottomNavigation.setSelectedItemId(R.id.menu_item_events);

                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.animation_from_right, R.anim.animation_to_left)
                        .replace(R.id.fragment_layout,
                                MainActivity.eventsFragment).commit();

                MainActivity.prevFragmentIndex = 3;
            });

            createEventButton.setOnClickListener(tempView -> {
                Event emptyEvent = new Event(getResources().getString(R.string.new_event), user);

                Intent intent = new Intent(this.getContext(), EventCreatorDialog.class);
                intent.putExtra("newEvent", emptyEvent);
                intent.putExtra("date", emptyEvent.getDate().getTime());
                activityResultLauncher.launch(intent);
            });

            activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                            Intent data = result.getData();

                            if (data != null) {
                                Event newEvent = data.getParcelableExtra("newEvent");
                                user.addCreatedEvent(newEvent);
                                user.addJoinedEvent(newEvent);
                            }
                        }
                    });

            return view;
        }

        eventsListView.setVisibility(View.VISIBLE);

        RecyclerView joinedEvents = view.findViewById(R.id.rv_joined_events_list);

        EventAdapter joinedEventAdapter = new EventAdapter(this.getContext(), user.getJoinedEvents(), this);

        joinedEvents.setAdapter(joinedEventAdapter);
        joinedEvents.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    private void setupEventsList() {
        // TODO: implement setup eventList from database
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getContext(), EventDetailsDialog.class);
        intent.putExtra("currentUser", user);
        intent.putExtra("event", user.getJoinedEvents().get(position));
        intent.putExtra("date", user.getJoinedEvents().get(position).getDate().getTime());
        activityResultLauncher.launch(intent);
    }
}