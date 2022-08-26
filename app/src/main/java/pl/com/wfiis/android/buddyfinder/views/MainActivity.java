package pl.com.wfiis.android.buddyfinder.views;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class MainActivity extends AppCompatActivity {
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

        db.collection("Users").document("AddUserTest").set(user);
        db.collection("Users").document("qSOKYAYQTfqBl6KhCNP0").get(Source.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}