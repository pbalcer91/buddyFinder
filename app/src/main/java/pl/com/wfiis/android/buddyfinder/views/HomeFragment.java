package pl.com.wfiis.android.buddyfinder.views;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventAdapter;
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private DBServices dbServices;

    public HomeFragment() {
        dbServices = new DBServices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ConstraintLayout homeViewLogged = view.findViewById(R.id.view_home_logged);
        RelativeLayout homeViewNotLogged = view.findViewById(R.id.view_home_not_logged);

        LinearLayout eventsListView = view.findViewById(R.id.layout_home_joined_events);
        LinearLayout emptyEventsListView = view.findViewById(R.id.layout_home_empty_joined_events);

        if (MainActivity.currentUser == null) {
            homeViewNotLogged.setVisibility(View.VISIBLE);

            Button signIn = view.findViewById(R.id.btn_sign_in);
            signIn.setOnClickListener(event -> MainActivity.showLoginDialog(this.getContext()));

            Button signUp = view.findViewById(R.id.btn_sign_up);
            signUp.setOnClickListener(event -> MainActivity.showRegisterDialog(this.getContext()));

            return view;
        }

        homeViewLogged.setVisibility(View.VISIBLE);

        RecyclerView joinedEvents = view.findViewById(R.id.rv_joined_events_list);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            EventAdapter joinedEventAdapter = new EventAdapter(this.getContext(), MainActivity.currentUser.getJoinedEvents(), this);
                            joinedEvents.setAdapter(joinedEventAdapter);
                            joinedEvents.setLayoutManager(new LinearLayoutManager(this.getContext()));
                        }
                    }
                });

        dbServices.getEventsJoinedByUser(list -> {
            if (!list.isEmpty() && MainActivity.currentUser != null)
                MainActivity.currentUser.setJoinedEvents(list);
        });

        if (MainActivity.currentUser.getJoinedEvents().size() == 0) {
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
                Event emptyEvent = new Event(MainActivity.currentUser);

                Intent intent = new Intent(this.getContext(), EventCreatorDialog.class);
                intent.putExtra("newEvent", emptyEvent);
                intent.putExtra("date", emptyEvent.getDate().getTime());
                activityResultLauncher.launch(intent);
            });

            return view;
        }

        eventsListView.setVisibility(View.VISIBLE);

        if (joinedEvents.getAdapter() != null) {
            joinedEvents.getAdapter().notifyDataSetChanged();
            return view;
        }

        EventAdapter joinedEventAdapter = new EventAdapter(this.getContext(), MainActivity.currentUser.getJoinedEvents(), this);


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
        intent.putExtra("currentUser", MainActivity.currentUser);
        intent.putExtra("event", MainActivity.currentUser.getJoinedEvents().get(position));
        intent.putExtra("date", MainActivity.currentUser.getJoinedEvents().get(position).getDate().getTime());
        intent.putExtra("location", MainActivity.currentUser.getJoinedEvents().get(position).getLocation());
        activityResultLauncher.launch(intent);
    }
}