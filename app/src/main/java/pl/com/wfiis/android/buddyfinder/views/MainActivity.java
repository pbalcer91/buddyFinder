package pl.com.wfiis.android.buddyfinder.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_DATA_OK = 123;

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static User currentUser = null;

    @SuppressLint("StaticFieldLeak")
    public static BottomSheetDialog bottomSheetDialog;

    public static short prevFragmentIndex = 2;
    private short nextFragmentIndex = 2;

    public static BottomNavigationView bottomNavigation;

    private HomeFragment homeFragment;
    public static SettingsFragment settingsFragment;
    private ProfileFragment profileFragment;

    public static EventsFragment eventsFragment;
    private  CreatedEventsFragment createdEventsFragment;

    @Override
    public void onBackPressed() {
    }

    public static void showLoginDialog(Context context) {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_login);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        //TODO: implement login

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    Toast.makeText(context, "Sign in attempt", Toast.LENGTH_SHORT).show();
                    MainActivity.bottomSheetDialog.cancel();
                });
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    public static void showRegisterDialog(Context context) {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_register);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        //TODO: implement register

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    Toast.makeText(context, "Sign up attempt", Toast.LENGTH_SHORT).show();
                    MainActivity.bottomSheetDialog.cancel();
                });
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    FirebaseFirestore db;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();


        //TODO: for logged user tests
//        currentUser = new User("John", "john@mail.com", "xyz");

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable("user", currentUser);

        homeFragment = new HomeFragment();
        homeFragment.setArguments(fragmentBundle);

        profileFragment = new ProfileFragment();
        profileFragment.setArguments(fragmentBundle);

        settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(fragmentBundle);

        createdEventsFragment = new CreatedEventsFragment();
        createdEventsFragment.setArguments(fragmentBundle);

        eventsFragment = new EventsFragment();
        eventsFragment.setArguments(fragmentBundle);

        bottomNavigation = findViewById(R.id.bottomNavigationBar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, homeFragment).commit();
        bottomNavigation.setSelectedItemId(R.id.menu_item_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.menu_item_settings:
                    selectedFragment = settingsFragment;
                    nextFragmentIndex = 0;
                    break;
                case R.id.menu_item_profile:
                    selectedFragment = profileFragment;
                    nextFragmentIndex = 1;
                    break;
                case R.id.menu_item_home:
                    selectedFragment = homeFragment;
                    nextFragmentIndex = 2;
                    break;
                case R.id.menu_item_created_events:
                    selectedFragment = createdEventsFragment;
                    nextFragmentIndex = 3;
                    break;
                case R.id.menu_item_events:
                    selectedFragment = eventsFragment;
                    nextFragmentIndex = 4;
                    break;
            }

            if (selectedFragment == null)
                return false;

            if (prevFragmentIndex > nextFragmentIndex)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.animation_from_left, R.anim.animation_to_right)
                        .replace(R.id.fragment_layout,
                                selectedFragment).commit();
            else
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.animation_from_right, R.anim.animation_to_left)
                        .replace(R.id.fragment_layout,
                                selectedFragment).commit();


            prevFragmentIndex = nextFragmentIndex;

            return true;
        });
    }
}