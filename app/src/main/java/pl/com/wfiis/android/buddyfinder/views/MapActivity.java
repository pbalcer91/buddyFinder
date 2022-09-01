package pl.com.wfiis.android.buddyfinder.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";

    private static final String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };
    private static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final float DEFAULT_ZOOM = 15f;

    private boolean isLocationPermissionGranted = false;
    private GoogleMap mMap;

    private ImageView backButton;
    private TextView title;
    private ImageView saveLocationButton;
    private ImageView currentLocationButton;

    private RelativeLayout searchRow;
    private EditText searchText;

    private Event event;
    private Address newAddress = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        event = getIntent().getParcelableExtra("event");

        backButton = findViewById(R.id.btn_map_back);
        backButton.setOnClickListener(event -> this.finish());

        title = findViewById(R.id.tv_map_title);
        title.setText(event.getTitle());

        saveLocationButton = findViewById(R.id.btn_accept_location);
        saveLocationButton.setVisibility(View.INVISIBLE);
        saveLocationButton.setOnClickListener(task -> {
            Intent intent = new Intent();
            intent.putExtra("location", newAddress);
            setResult(MainActivity.RESULT_DATA_OK, intent);

            this.finish();
        });

        currentLocationButton = findViewById(R.id.btn_current_location);
        currentLocationButton.setOnClickListener(event -> getCurrentLocation());

        searchText = findViewById(R.id.et_search_map);

        searchRow = findViewById(R.id.btn_map_search_field);

//        searchRow.setOnClickListener(event -> {
//            SearchMapAnimation animation = new SearchMapAnimation(searchRow, 400);
//            animation.setDuration(500);
//            searchRow.startAnimation(animation);
//        });

        getLocationPermission();
    }

    private void initSearchField() {
        if (event.getLocation() != null) {
            searchRow.setVisibility(View.INVISIBLE);
            return;
        }

        searchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                geoLocate();
            }
            return false;
        });

        hideKeyboard();
    }

    private void geoLocate() {
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            newAddress = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0));

            if (newAddress != null)
                saveLocationButton.setVisibility(View.VISIBLE);

            searchText.setText(address.getAddressLine(0));

            hideKeyboard();
        }
    }

    public void getEventLocation(Event event) {
        try {
            if (isLocationPermissionGranted) {
                moveCamera(new LatLng(event.getLocation().getLatitude(),
                        event.getLocation().getLongitude()), event.getLocation().getAddressLine(0));
            }
        } catch(SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void getCurrentLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (isLocationPermissionGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), "Current location");

                    } else {
                        Log.d(TAG, "getCurrentLocation: null");
                    }
                });
            }
        } catch(SecurityException e) {
                Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapActivity.DEFAULT_ZOOM));

        if (title.equals("Current location"))
            return;

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);

        mMap.addMarker(options);

        hideKeyboard();
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        if (!isLocationPermissionGranted)
            return;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            Objects.requireNonNull(mapFragment).getMapAsync(googleMap -> {
                mMap = googleMap;

                if (event.getLocation() != null)
                    getEventLocation(event);
                else
                    getCurrentLocation();

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);

                initSearchField();
            });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            initMap();
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
                initMap();
            }
        }
    }
}
