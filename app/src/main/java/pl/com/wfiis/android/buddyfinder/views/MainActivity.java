package pl.com.wfiis.android.buddyfinder.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_DATA_OK = 123;

    FirebaseFirestore db;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        //User user = null;
        User user = new User("John", "john@mail.com");

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable("user", user);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(fragmentBundle);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(fragmentBundle);

        EventsFragment eventsFragment = new EventsFragment();
        eventsFragment.setArguments(fragmentBundle);

        RegisterActivity registerFragment = new RegisterActivity();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationBar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, homeFragment).commit();
        bottomNavigation.setSelectedItemId(R.id.menu_item_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.menu_item_home:
                    selectedFragment = homeFragment;
                    break;
                case R.id.menu_item_profile:
                    selectedFragment = profileFragment;
                    break;
                case R.id.menu_item_events:
                    selectedFragment = eventsFragment;
                    break;
            }

            if (selectedFragment == null)
                return false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout,
                            selectedFragment).commit();
            return true;
        });
    }
}