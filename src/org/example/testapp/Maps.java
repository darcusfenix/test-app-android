package org.example.testapp;

import java.text.DateFormat;
import java.util.Date;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends FragmentActivity implements OnMapReadyCallback,
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	protected static final String TAG = "basic-location-sample";

	protected GoogleApiClient mGoogleApiClient;

	protected Location mCurrentLocation;

	private GoogleMap map;

	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

	protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
	protected final static String LOCATION_KEY = "location-key";
	protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

	protected LocationRequest mLocationRequest;

	protected Boolean mRequestingLocationUpdates;

	protected String mLastUpdateTime;

	private Marker now;

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mGoogleApiClient.isConnected()) {
			stopLocationUpdates();
		}
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mRequestingLocationUpdates = false;
		mLastUpdateTime = "";

		updateValuesFromBundle(savedInstanceState);
		buildGoogleApiClient();

	}

	private void updateValuesFromBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
		
			if (savedInstanceState.keySet().contains(
					REQUESTING_LOCATION_UPDATES_KEY)) {
				mRequestingLocationUpdates = savedInstanceState
						.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
				setButtonsEnabledState();
			}

			if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
				
				mCurrentLocation = savedInstanceState
						.getParcelable(LOCATION_KEY);
			}

			if (savedInstanceState.keySet().contains(
					LAST_UPDATED_TIME_STRING_KEY)) {
				mLastUpdateTime = savedInstanceState
						.getString(LAST_UPDATED_TIME_STRING_KEY);

			}
			updateUI();
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {

		this.map = map;
		this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		this.map.setMyLocationEnabled(true);
		this.map.getUiSettings().setScrollGesturesEnabled(false);
		this.map.getUiSettings().setTiltGesturesEnabled(false);
		this.map.getUiSettings().setRotateGesturesEnabled(false);
		this.map.moveCamera(CameraUpdateFactory.zoomTo(19));
		mRequestingLocationUpdates = true;
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		createLocationRequest();
	}

	
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest
				.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	public void startUpdatesButtonHandler(View view) {
		if (!mRequestingLocationUpdates) {
			mRequestingLocationUpdates = true;
			setButtonsEnabledState();
			startLocationUpdates();
		}
	}

	public void stopUpdatesButtonHandler(View view) {
		if (mRequestingLocationUpdates) {
			mRequestingLocationUpdates = false;
			setButtonsEnabledState();
			stopLocationUpdates();
		}
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	private void setButtonsEnabledState() {
	
	}

	private void updateUI() {
		if (now != null) {
			now.remove();
		}
		if (mCurrentLocation != null) {
			
			LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude());
			now = map.addMarker(new MarkerOptions().position(myLocation).title(
					"My location"));
			map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
			//Toast.makeText(this, "ACTUALIZADO", Toast.LENGTH_SHORT).show();
		}
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
	}

	
	@Override
	public void onConnected(Bundle connectionHint) {
		
		if (mCurrentLocation == null) {
			mCurrentLocation = LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
			mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
			updateUI();
		}
		
		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		updateUI();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
				mRequestingLocationUpdates);
		savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
		savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY,
				mLastUpdateTime);
		super.onSaveInstanceState(savedInstanceState);
	}
}