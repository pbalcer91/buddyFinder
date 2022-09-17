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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.BuildConfig;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;

    private ImageView saveLocationButton;

    private EditText searchText;

    private Event event;
    private Address newAddress = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!Places.isInitialized())
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        event = getIntent().getParcelableExtra("event");
        event.setLocation(getIntent().getParcelableExtra("location"));

        ImageView backButton = findViewById(R.id.btn_map_back);
        backButton.setOnClickListener(event -> this.finish());

        TextView title = findViewById(R.id.tv_map_title);
        title.setText(event.getTitle().equals("") ? getResources().getString(R.string.new_event) : event.getTitle());

        saveLocationButton = findViewById(R.id.btn_accept_location);
        saveLocationButton.setVisibility(View.INVISIBLE);
        saveLocationButton.setOnClickListener(task -> {
            Intent intent = new Intent();
            intent.putExtra("location", newAddress);
            setResult(MainActivity.RESULT_DATA_OK, intent);

            this.finish();
        });

        ImageView currentLocationButton = findViewById(R.id.btn_current_location);
        currentLocationButton.setOnClickListener(temp -> getCurrentLocation());

        LinearLayout eventLocation = findViewById(R.id.event_location);
        eventLocation.setVisibility(event.getLocation() == null ?
                View.INVISIBLE : View.VISIBLE);

        ImageView eventLocationButton = findViewById(R.id.btn_event_location);
        eventLocationButton.setOnClickListener(temp -> getEventLocation(event));

        //TODO: searchField with custom autocomplete
        searchText = findViewById(R.id.et_search_map);

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(event -> {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("PL")
                    .build(this);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        getLocationPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                geoLocate(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initSearchField() {
        searchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                geoLocate(searchText.getText().toString());
            }
            return false;
        });

        hideKeyboard();
    }

    private void geoLocate(String searchString) {
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
            if (MainActivity.isLocationPermissionGranted) {
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
            if (MainActivity.isLocationPermissionGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), getString(R.string.current_location));

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
        mMap.clear();

        if (!title.equals(getString(R.string.current_location))) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapActivity.DEFAULT_ZOOM));

        hideKeyboard();
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        if (!MainActivity.isLocationPermissionGranted)
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
                MainActivity.FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                MainActivity.COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            MainActivity.isLocationPermissionGranted = true;
            initMap();
            return;
        }

        ActivityCompat.requestPermissions(this, MainActivity.permissions, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MainActivity.isLocationPermissionGranted = false;

        if (requestCode == MainActivity.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                        return;
                }

                MainActivity.isLocationPermissionGranted = true;
                initMap();
            }
        }
    }
}
