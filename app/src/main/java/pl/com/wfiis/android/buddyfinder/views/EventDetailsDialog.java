package pl.com.wfiis.android.buddyfinder.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Date;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventMemberAdapter;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventDetailsDialog extends AppCompatActivity {
    private Event event;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private boolean isOrganizer;
    private boolean isMember;

    private ImageView backButton;
    private TextView title;
    private ImageView editButton;

    private TextView date;
    private TextView time;
    private TextView location;

    private RelativeLayout locationButton;

    private RelativeLayout membersButton;
    private TextView organizer;

    private Button actionButton;
    private Button deleteButton;

    private FloatingActionButton chatButton;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS)
            return true;
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_event_details);

        this.event = getIntent().getParcelableExtra("event");
        this.event.setDate(new Date());
        this.event.getDate().setTime(getIntent().getLongExtra("date", -1));

        isOrganizer = event.getAuthor().equals(MainActivity.currentUser);
        isMember = event.getMembers().contains(MainActivity.currentUser);

        backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        title = this.findViewById(R.id.tv_event_title);
        title.setText(event.getTitle());

        editButton = this.findViewById(R.id.btn_event_edit);
        editButton.setOnClickListener(event -> showEditEventActivity());
        editButton.setVisibility(isOrganizer ? View.VISIBLE : View.INVISIBLE);

        locationButton = this.findViewById(R.id.btn_event_location);
        locationButton.setOnClickListener(event -> showMap());


        date = findViewById(R.id.tv_event_date);
        date.setText(dateFormat.format(event.getDate()));

        time = findViewById(R.id.tv_event_time);
        time.setText(timeFormat.format(event.getDate()));

        location = findViewById(R.id.tv_event_location);
        location.setText("Some address");
        //location.setText(event.getLocation().getAddressLine(0));

        membersButton = this.findViewById(R.id.btn_event_members);
        membersButton.setOnClickListener(event -> showMembersDialog());

        organizer = this.findViewById(R.id.tv_event_details_organizer);
        organizer.setText(event.getAuthor().getUserName());

        actionButton = this.findViewById(R.id.btn_event_action);
        actionButton.setOnClickListener(isMember ? event -> showLeaveDialog()
                                                    : event -> joinEvent());
        actionButton.setVisibility(isOrganizer ? View.INVISIBLE : View.VISIBLE);
        actionButton.setText(isMember ? "Leave" : "Join");

        deleteButton = this.findViewById(R.id.btn_event_delete);
        deleteButton.setOnClickListener(event -> showDeleteMessage());
        deleteButton.setVisibility(isOrganizer ? View.VISIBLE : View.INVISIBLE);

        chatButton = this.findViewById(R.id.btn_chat);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);
        chatButton.setOnClickListener(tempEvent -> {
            Intent intent = new Intent(EventDetailsDialog.this, ChatDialog.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        for (int i = 0; i < 5; i++) {
            event.addMember(new User("Tommy", "someemail"));
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            event = data.getParcelableExtra("newEvent");

                            title.setText(event.getTitle());
                            date.setText(dateFormat.format(event.getDate()));
                            time.setText(timeFormat.format(event.getDate()));
                            location.setText(event.getLocation().getAddressLine(0));
                        }
                    }
                });
    }

    private void showEditEventActivity() {
        Intent intent = new Intent(this, EventDetailsDialog.class);
        intent.putExtra("newEvent", event);
        activityResultLauncher.launch(intent);
    }

    private void showMap() {
        if (!isServicesOk())
            return;

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("event", event);
        this.startActivity(intent);
    }

    private void showMembersDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_event_members);

        RecyclerView membersListView = MainActivity.bottomSheetDialog.findViewById(R.id.rv_event_members);
        Button closeButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_members_close);
        closeButton.setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        membersListView.getLayoutManager();

        EventMemberAdapter eventMemberAdapter = new EventMemberAdapter(this, event.getMembers());

        membersListView.setAdapter(eventMemberAdapter);
        membersListView.setLayoutManager(new LinearLayoutManager(this));

        MainActivity.bottomSheetDialog.show();
    }

    private void showDeleteMessage() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = MainActivity.bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        message.setText("Are you sure to delete this event?");

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        acceptButton.setText("Delete");
        acceptButton.setOnClickListener(event -> {
            deleteEvent();
            MainActivity.bottomSheetDialog.dismiss();
        });

        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        rejectButton.setText(R.string.reject);
        rejectButton.setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void showLeaveDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = MainActivity.bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        message.setText("Are you sure to leave this event?");

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        acceptButton.setText("Leave");
        acceptButton.setOnClickListener(event -> {
            leaveEvent();
            MainActivity.bottomSheetDialog.dismiss();
        });

        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        rejectButton.setText(R.string.reject);
        rejectButton.setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private boolean joinEvent() {
        if (!event.addMember(MainActivity.currentUser)) {
            Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
            return (false);
        }

        actionButton.setOnClickListener(event -> showLeaveDialog());
        actionButton.setText("Leave");
        isMember = event.getMembers().contains(MainActivity.currentUser);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);

        Toast.makeText(this, "Joined event", Toast.LENGTH_SHORT).show();

        return (true);
    }

    private boolean leaveEvent() {
        if (!event.removeMember(MainActivity.currentUser)) {
            Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
            return (false);
        }

        actionButton.setOnClickListener(event -> joinEvent());
        actionButton.setText("Join");
        isMember = event.getMembers().contains(MainActivity.currentUser);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);

        Toast.makeText(this, "Left event", Toast.LENGTH_SHORT).show();

        return (true);
    }

    private boolean deleteEvent() {
        event.removeAllMember();
        //TODO: delete event from database
        event = null;

        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();

        if (MainActivity.bottomSheetDialog != null)
            MainActivity.bottomSheetDialog.cancel();

        this.finish();

        return (true);
    }
}
