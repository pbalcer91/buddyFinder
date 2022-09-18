package pl.com.wfiis.android.buddyfinder.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import pl.com.wfiis.android.buddyfinder.DBServices.Callback;
import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_DATA_OK = 123;

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

    @Override
    public void onBackPressed() {
    }

    public static void showLoginDialog(Context context) {
      //  dbServices.logoutUser();
        MainActivity.bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_login);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        EditText emailField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_in_email);
        EditText passwordField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_in_password);

        //TODO: implement login


        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    String email = emailField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();
                    dbServices.SignInUser(email,password,context);

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
        EditText repasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.et_sing_up_re_password);

        //TODO: implement register

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    final String email = emailField.getText().toString().trim();
                    final String username = usernameField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();

                    if(TextUtils.isEmpty(email)){
                        emailField.setError("Email is Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(password)){
                        passwordField.setError("Password is Required.");
                        return;
                    }

                    if(password.length() < 6){
                        passwordField.setError("Password Must be >= 6 Characters");
                        return;
                    }
                    dbServices.registerUser(email,username,password,context);
                    //Toast.makeText(context, "Sign up attempt", Toast.LENGTH_SHORT).show();
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
        dbServices = new DBServices();
        dbServices.logoutUser();

        //TODO: for logged user tests
       // currentUser = new User("John", "john@mail.com");
        if(dbServices.isUserSignedIn()){
        //    currentUser = dbServices.getUser(dbServices.getUserId());
        }
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

    public static void showHomeViewSignIn(){
        Bundle fragmentBundle = new Bundle();
        User usr = new User("asd","asd","asdd");
        fragmentBundle.putParcelable("user", usr);
        FragmentActivity manager =  MainActivity.homeFragment.getActivity();
        //TODO reload view
        manager.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, homeFragment).commit();

    }

    public static void successfulRegister(Context context){
        Toast.makeText(context, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();

    }
}