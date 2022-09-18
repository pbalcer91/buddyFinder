package pl.com.wfiis.android.buddyfinder.views;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
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

import java.util.Date;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.EventMemberAdapter;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventDetailsDialog extends AppCompatActivity {
    private Event event;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private boolean isMember;

    private TextView title;
    private TextView description;

    private TextView date;
    private TextView time;
    private TextView location;

    private Button actionButton;

    private FloatingActionButton chatButton;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS)
            return true;
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            Objects.requireNonNull(dialog).show();
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
        this.event.setLocation(getIntent().getParcelableExtra("location"));

        boolean isOrganizer = event.getAuthor().getId() == MainActivity.currentUser.getId();
        isMember = event.isMember(MainActivity.currentUser);

        ImageView backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        title = this.findViewById(R.id.tv_event_title);
        title.setText(event.getTitle());

        description = this.findViewById(R.id.tv_event_description);
        description.setText(event.getDescription());

        ImageView editButton = this.findViewById(R.id.btn_event_edit);
        editButton.setOnClickListener(event -> showEditEventActivity());
        editButton.setVisibility(isOrganizer ? View.VISIBLE : View.INVISIBLE);

        RelativeLayout locationButton = this.findViewById(R.id.btn_event_location);
        locationButton.setOnClickListener(event -> showMap());

        date = findViewById(R.id.tv_event_date);
        date.setText(MainActivity.dateFormat.format(event.getDate()));

        time = findViewById(R.id.tv_event_time);
        time.setText(MainActivity.timeFormat.format(event.getDate()));

        location = findViewById(R.id.tv_event_location);
        location.setText(event.getLocation().getAddressLine(0));

        RelativeLayout membersButton = this.findViewById(R.id.btn_event_members);
        membersButton.setOnClickListener(event -> showMembersDialog());

        TextView organizer = this.findViewById(R.id.tv_event_details_organizer);
        organizer.setText(event.getAuthor().getUserName());

        actionButton = this.findViewById(R.id.btn_event_action);
        actionButton.setOnClickListener(isMember ? event -> showLeaveDialog()
                                                    : event -> joinEvent());
        actionButton.setVisibility(isOrganizer ? View.INVISIBLE : View.VISIBLE);
        actionButton.setText(isMember ? getResources().getString(R.string.leave)
                : getResources().getString(R.string.join));

        Button deleteButton = this.findViewById(R.id.btn_event_delete);
        deleteButton.setOnClickListener(event -> showDeleteMessage());
        deleteButton.setVisibility(isOrganizer ? View.VISIBLE : View.INVISIBLE);

        chatButton = this.findViewById(R.id.btn_chat);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);
        chatButton.setOnClickListener(tempEvent -> {
            Intent intent = new Intent(EventDetailsDialog.this, ChatDialog.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == MainActivity.RESULT_DATA_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            event = data.getParcelableExtra("newEvent");
                            Address eventLocation = data.getParcelableExtra("newEventLocation");

                            Date eventDate = new Date();
                            eventDate.setTime(getIntent().getLongExtra("newEventDate", -1));

                            title.setText(event.getTitle());
                            description.setText(event.getDescription());

                            date.setText(MainActivity.dateFormat.format(eventDate));
                            time.setText(MainActivity.timeFormat.format(eventDate));
                            location.setText(eventLocation.getAddressLine(0));
                        }
                    }
                });
    }

    private void showEditEventActivity() {
        Intent intent = new Intent(this, EventCreatorDialog.class);
        intent.putExtra("newEvent", event);
        intent.putExtra("date", event.getDate().getTime());
        intent.putExtra("location", event.getLocation());
        activityResultLauncher.launch(intent);
    }

    private void showMap() {
        if (!isServicesOk())
            return;

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("location", event.getLocation());
        this.startActivity(intent);
    }

    private void showMembersDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_event_members);

        RecyclerView membersListView = MainActivity.bottomSheetDialog.findViewById(R.id.rv_event_members);
        Button closeButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_members_close);
        Objects.requireNonNull(closeButton).setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        Objects.requireNonNull(membersListView).getLayoutManager();

        EventMemberAdapter eventMemberAdapter = new EventMemberAdapter(this, event.getMembers());

        membersListView.setAdapter(eventMemberAdapter);
        membersListView.setLayoutManager(new LinearLayoutManager(this));

        MainActivity.bottomSheetDialog.show();
    }

    private void showDeleteMessage() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = MainActivity.bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        Objects.requireNonNull(message).setText(R.string.delete_event_info);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Objects.requireNonNull(acceptButton).setText(getResources().getString(R.string.delete));
        acceptButton.setOnClickListener(event -> {
            deleteEvent();
            MainActivity.bottomSheetDialog.dismiss();
        });

        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        Objects.requireNonNull(rejectButton).setText(R.string.reject);
        rejectButton.setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void showLeaveDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = MainActivity.bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        Objects.requireNonNull(message).setText(R.string.leave_event_info);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Objects.requireNonNull(acceptButton).setText(R.string.leave);
        acceptButton.setOnClickListener(event -> {
            leaveEvent();
            MainActivity.bottomSheetDialog.dismiss();
        });

        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        Objects.requireNonNull(rejectButton).setText(R.string.reject);
        rejectButton.setOnClickListener(event -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void joinEvent() {
        if (!event.addMember(MainActivity.currentUser)) {
            Toast.makeText(this, R.string.operation_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        actionButton.setOnClickListener(event -> showLeaveDialog());
        actionButton.setText(R.string.leave);
        isMember = event.getMembers().contains(MainActivity.currentUser);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);

        Toast.makeText(this, R.string.joined_event, Toast.LENGTH_SHORT).show();

    }

    private void leaveEvent() {
        if (!event.removeMemberById(MainActivity.currentUser.getId())) {
            Toast.makeText(this, R.string.operation_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        actionButton.setOnClickListener(event -> joinEvent());
        actionButton.setText(R.string.join);
        isMember = event.isMember(MainActivity.currentUser);
        chatButton.setVisibility(isMember ? View.VISIBLE : View.INVISIBLE);

        Toast.makeText(this, R.string.left_event, Toast.LENGTH_SHORT).show();

    }

    private void deleteEvent() {
        for (User member : event.getMembers()) {
            event.removeMemberById(member.getId());
            member.removeJoinedEventById(event.getId());

            if (member.getId() == event.getAuthor().getId())
                member.removeCreatedEventById(event.getId());
        }

        //TODO: delete event from database
        event = null;

        Toast.makeText(this, R.string.deleted_event, Toast.LENGTH_SHORT).show();

        if (MainActivity.bottomSheetDialog != null)
            MainActivity.bottomSheetDialog.cancel();

        this.finish();

    }
}
