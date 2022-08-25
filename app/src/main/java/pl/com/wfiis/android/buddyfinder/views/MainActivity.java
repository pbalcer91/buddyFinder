package pl.com.wfiis.android.buddyfinder.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //User user = null;
        User user = new User("John", "john@mail.com");

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable("user", user);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(fragmentBundle);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(fragmentBundle);

        OffersFragment offersFragment = new OffersFragment();
        offersFragment.setArguments(fragmentBundle);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationBar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, homeFragment).commit();
        bottomNavigation.setSelectedItemId(R.id.homeItem);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.homeItem:
                    selectedFragment = homeFragment;
                    break;
                case R.id.profileItem:
                    selectedFragment = profileFragment;
                    break;
                case R.id.offersItem:
                    selectedFragment = offersFragment;
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