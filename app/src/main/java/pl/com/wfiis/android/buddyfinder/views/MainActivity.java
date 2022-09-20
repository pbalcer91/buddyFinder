package pl.com.wfiis.android.buddyfinder.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_DATA_OK = 123;

    public static final String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };

    public static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static boolean isLocationPermissionGranted = false;

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static User currentUser = null;
    public static DBServices dbServices;

    @SuppressLint("StaticFieldLeak")
    public static BottomSheetDialog bottomSheetDialog;

    public static short prevFragmentIndex = 2;
    private static short nextFragmentIndex = 2;

    public static BottomNavigationView bottomNavigation;

    public static HomeFragment homeFragment;
    public static SettingsFragment settingsFragment;
    private ProfileFragment profileFragment;

    public static EventsFragment eventsFragment;
    private  CreatedEventsFragment createdEventsFragment;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onBackPressed() {
    }

    public static void showLoginDialog(Context context) {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_login);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        EditText emailField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_in_email);
        EditText passwordField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_in_password);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    String email = emailField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();

                    if(TextUtils.isEmpty(email)){
                        emailField.setError(context.getResources().getString(R.string.email_field_required));
                        return;
                    }

                    if(TextUtils.isEmpty(password)){
                        passwordField.setError("Password is Required.");
                        return;
                    }

                    dbServices.SignInUser(email, password, context);

                     //   showHomeViewSignIn();
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
        EditText emailField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_up_email);
        EditText passwordField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_up_password);
        EditText usernameField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_up_name);
        EditText rePasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_up_re_password);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    final String email = emailField.getText().toString().trim();
                    final String username = usernameField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();
                    String rePassword = rePasswordField.getText().toString().trim();

                    if(TextUtils.isEmpty(username)){
                        usernameField.setError(context.getResources().getString(R.string.name_field_required));
                        return;
                    }

                    if(TextUtils.isEmpty(email)){
                        emailField.setError(context.getResources().getString(R.string.email_field_required));
                        return;
                    }

                    if(TextUtils.isEmpty(password)){
                        passwordField.setError("Password is Required.");
                        return;
                    }

                    if(password.length() < 6){
                        passwordField.setError("Password must be >= 6 Characters");
                        return;
                    }

                    if(!password.equals(rePassword)){
                        rePasswordField.setError(context.getResources().getString(R.string.wrong_re_password));
                        return;
                    }

                    dbServices.registerUser(email,username,password,context);

                    MainActivity.bottomSheetDialog.dismiss();
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
        dbServices = new DBServices();

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
                                selectedFragment).setReorderingAllowed(false).commit();
            else
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.animation_from_right, R.anim.animation_to_left)
                        .replace(R.id.fragment_layout,
                                selectedFragment).setReorderingAllowed(false).commit();


            prevFragmentIndex = nextFragmentIndex;

            return true;
        });
    }

    public static void showHomeViewSignIn(){
        Bundle fragmentBundle = new Bundle();
        User usr = new User("asd","asd","asdd");
        fragmentBundle.putParcelable("user", usr);
        FragmentActivity manager =  MainActivity.homeFragment.getActivity();
        //TODO reload view
        manager.getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_layout, homeFragment).setReorderingAllowed(false).commit();

    }

    public static void successfulRegister(Context context){
        Toast.makeText(context, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            return;
        }

        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        isLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                        return;
                }

                isLocationPermissionGranted = true;
            }
        }
    }
}