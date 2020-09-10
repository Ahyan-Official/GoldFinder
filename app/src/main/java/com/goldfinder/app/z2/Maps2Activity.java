package com.goldfinder.app.z2;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;


public class Maps2Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener  {



    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    private LatLng mCenterLatLong;


    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private Location mylocation;
    private GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    LocationListener locationListener;
    GoogleMap mMap;
    public  static final int PERMISSIONS_RECORD_REQUEST = 122;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    TextView mLocationMarkerText;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    EditText mLocationAddress;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 6;
    TextView mLocationText;
    Context mContext;
    Toolbar mToolbar;

    TextView tvSearch;



    Location loc;

    boolean click = true;

    TextView etLookingFor;
    String location_country ="Pakistan",location_city="Lahore";
    TextView text;
    double latitude,longitude, c_longitude, c_latitude;
    RadioGroup rdg;

    Button placeMarker;

    EditText title,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mContext = this;

        text = findViewById(R.id.tvLocation);


        description = findViewById(R.id.description);

        title = findViewById(R.id.title);

        rdg = findViewById(R.id.rdg);
        placeMarker = findViewById(R.id.placeMarker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);


//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));



        mapFragment.getMapAsync(this);

        mResultReceiver = new AddressResultReceiver(new Handler());



        setUpGClient();






        String apiKey = "AIzaSyDKuJvFsLa46aN3CIx6QASBvTNjjb8NDuU";
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.





        placeMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if(click){

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(Maps2Activity.this, Locale.getDefault());

                    try {

                        addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();

                            location_city = city;
                            location_country = country;
                            //tvSearch.setText(address+" "+state+" "+city+" "+country);
                        }




                        rdg = (RadioGroup) findViewById(R.id.rdg);


                        final MarkerOptions markerOptions = new MarkerOptions();

                        // Setting the position for the marker
                        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                        markerOptions.position(latLng);
                        markerOptions.snippet(description.getText().toString() + "Location: " + latLng.latitude + " : " + latLng.longitude);

                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerOptions.title(title.getText().toString());

                        int selectedId = rdg.getCheckedRadioButtonId();
                        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);





                        if( !title.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && rdg.getCheckedRadioButtonId() != -1){





                            if (radioSexButton.getText().equals("Gold")) {

                                Toast.makeText(getApplicationContext(), "asdas", Toast.LENGTH_LONG).show();
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gold_location2));

                            } else if (radioSexButton.getText().equals("Silver")) {

                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.silver_location2));
                            } else if (radioSexButton.getText().equals("Gem")) {

                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gem_location2));
                            }





                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.d13));
//                        rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//
//                            @Override
//                            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                // find which radio button is selected
//                                if(checkedId == R.id.gold) {
//
//                                    Toast.makeText(getApplicationContext(),"asdas",Toast.LENGTH_LONG).show();
//                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.d13));
//                                } else if(checkedId == R.id.silver) {
//
//                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.d13));
//                                } else if(checkedId == R.id.gem){
//
//                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.d13));
//                                }
//                            }
//
//                        });
                            // Clears the previously touched position
                            //mMap.clear();

                            // Animating to the touched position
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                            // Placing a marker on the touched position
                            mMap.addMarker(markerOptions);




                        }else {


                            Toast.makeText(getApplicationContext(),"Fill Marker details",Toast.LENGTH_LONG).show();
                        }






                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                }else{

                }


            }
        });



    }










    public void tt(String name){
        Toast.makeText(getApplicationContext(),name, Toast.LENGTH_SHORT).show();
    }
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.e("ChatStart==========","location====================");


                click = false;
                //btnFindStore.setText("Loading..");
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                //mMap.clear();
                click = true;

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    latitude = mCenterLatLong.latitude;
                    longitude = mCenterLatLong.longitude;

                    c_latitude = mCenterLatLong.latitude;
                    c_longitude = mCenterLatLong.longitude;
                    loc = mLocation;
                    startIntentService(mLocation);
                    //text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, Maps2Activity.this));

                    //mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);


                    Log.e("ChatEnd==========","location====================");
                    // btnFindStore.setText("Tap to Search things");



//                    Geocoder geocoder;
//                    List<Address> addresses;
//                    geocoder = new Geocoder(Maps2Activity.this, Locale.getDefault());
//
//                    try {
//
//                        addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String country = addresses.get(0).getCountryName();
//                        String postalCode = addresses.get(0).getPostalCode();
//                        String knownName = addresses.get(0).getFeatureName();
//                        location_country = country;
//                        location_city = city;
//
//                        //tvSearch.setText(address+" "+state+" "+city+" "+country);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });




//        placeMarker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int selectedId=rdg.getCheckedRadioButtonId();
//                 //RadioButton radioSexButton=(RadioButton)findViewById(selectedId);
//                //Toast.makeText(Maps2Activity.this,radioSexButton.getText(),Toast.LENGTH_SHORT).show();
//            }
//        });


//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//
//                placeMarker.setVisibility(View.VISIBLE);
//                // Creating a marker
//                MarkerOptions markerOptions = new MarkerOptions();
//
//                // Setting the position for the marker
//                markerOptions.position(latLng);
//                markerOptions.snippet("");
//
//                // Setting the title for the marker.
//                // This will be displayed on taping the marker
//                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
//
//                // Clears the previously touched position
//                //mMap.clear();
//
//                // Animating to the touched position
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                // Placing a marker on the touched position
//                mMap.addMarker(markerOptions);
//            }
//        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }


    private synchronized void setUpGClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Maps2Activity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }






    private void getMyLocation() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Maps2Activity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    mylocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.





                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(Maps2Activity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(mGoogleApiClient);
                                    }

                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But  could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(Maps2Activity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        //finish();
                        break;
                }
                break;
        }


        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                //Place place = PlaceAutocomplete.getPlace(mContext, data);
                com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

                tvSearch.setText(place.getName().toString());
                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                loc.setLatitude(latLong.latitude);
                loc.setLongitude(latLong.longitude);

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15f).tilt(0).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


//                Geocoder geocoder;
//                List<Address> addresses;
//                geocoder = new Geocoder(this, Locale.getDefault());
//
//                try {
//
//                    addresses = geocoder.getFromLocation(latLong.latitude, latLong.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
//
//                    tvSearch.setText(address+" "+state+" "+city+" "+country);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }


        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(Maps2Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        int permissionLocation = ContextCompat.checkSelfPermission(Maps2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

            getMyLocation();

        }
    }



    public static boolean isLocationEnabled(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        } else {

            return false;
        }
    }



    private void changeMap(Location location) {



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;



            Log.e("latlong",location.getLatitude()+" "+location.getLongitude());
            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            loc = location;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(15f).tilt(0).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));



            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            startIntentService(location);


//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(this, Locale.getDefault());
//
//            try {
//
//                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
//
//                tvSearch.setText(address+" "+state+" "+city+" "+country);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openAutocompleteActivity() {
        // The autocomplete activity requires Google Play Services to be available. The intent
        // builder checks this and throws an exception if it is not the case.


        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields) //NIGERIA
                .build(this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

//            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
//            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);


    }



    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }




    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //menu.add(0,2,0, R.string.ShowNHide).setIcon(R.drawable.newhidenshowdark).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(0,3,0,getString(R.string.Demo)).setIcon(R.drawable.mark).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==6){


            openAutocompleteActivity();


        }



        return super.onOptionsItemSelected(item);
    }



































    @Override
    protected void onResume() {
        super.onResume();

        //SharedPreferences sp = getSharedPreferences("RVPOSITIONVISIBLE", Context.MODE_PRIVATE);
        //lastFirstVisiblePosition = sp.getInt("rvPosition",0);

        //((LinearLayoutManager) recyclerViewVisible.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition,0);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();



    }




    class as extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
            return null;
        }
    }

    public void saveLocation(double latitude, double longitude) {
//        session.setData(Session.KEY_LATITUDE, String.valueOf(latitude));
//        session.setData(Session.KEY_LONGITUDE, String.valueOf(longitude));
    }
    @Override
    public void onBackPressed() {
        saveLocation(latitude, longitude);

        super.onBackPressed();
    }


    public void UpdateLocation(View view) {


        onBackPressed();
    }

}
