package in.wptrafficanalyzer.locationgeocodingv2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.CameraPosition; 
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;



public class MainActivity extends FragmentActivity implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	final private static String TAG = "MainFragment"  ;
	final private static int LOGIN_ACTIVITY_CODE = 2013  ;
	
	List<LatLng> points;
	GoogleMap googleMap;
	MarkerOptions markerOptions;
	CircleOptions circleOptions;
	LatLng latLng;
	List<Marker> markers = new ArrayList<Marker>();
	List<Circle> homeRadiuses = new ArrayList<Circle>();
	List<Circle> destRadiuses = new ArrayList<Circle>();
	PolylineOptions rectLine;
	Polyline newPolyline;
	RelativeLayout topPanel;
	RelativeLayout statsPanel;
	ProgressBar spinner;
	String[] mCount = new String[]{
			 "", "", "", ""};
	int[] mDrawerImages = new int[]{
			 R.drawable.ic_settings,
			 R.drawable.ic_stats,
			 R.drawable.ic_medal,
			 R.drawable.ic_podium
			 };
	private List<HashMap<String,String>> mList;
	EditText etLocation;
	Button btn_find;
	Button btn_start;
	Button btnREady;
	Button btnStop;
	Button btnConnect;
	EditText mph;
	EditText etStopper;
	Animation footerAnimate;
	DrawerLayout mDrawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	ImageView imgProfilePic;
	
	private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    private LatLng mLatLng;
    private LatLng destLatLng;
    private String mAddress;
    private String mConnectionState;
    private String mConnectionStatus;
    private Handler mHandler = new Handler();
    private Long mStartTime;
    private SimpleAdapter mAdapter;
    private CharSequence mTitle; 
    private String[] mDrawerItems;
    private ListView mDrawerList;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    boolean mUpdatesRequested = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			
			setContentView(R.layout.activity_main);
			
			SupportMapFragment supportMapFragment = (SupportMapFragment) 
					getSupportFragmentManager().findFragmentById(R.id.map);
			
			
			
			
			googleMap = supportMapFragment.getMap();
			googleMap.getUiSettings().setCompassEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.setMyLocationEnabled(true);
			
			imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		    btn_find = (Button) findViewById(R.id.btn_find);
			btn_start = (Button) findViewById(R.id.btn_start);
			btnREady = (Button) findViewById(R.id.btn_ready);
			etLocation = (EditText) findViewById(R.id.et_location);
			etStopper = (EditText) findViewById(R.id.stopper_float);
			topPanel = (RelativeLayout) findViewById(R.id.top_panel);
			spinner = (ProgressBar) findViewById(R.id.spinner);
			statsPanel = (RelativeLayout) findViewById(R.id.stats_pnl);
			mph = (EditText) findViewById(R.id.mph);
			btnStop = (Button) findViewById(R.id.btn_stop);
			btnConnect = (Button) findViewById(R.id.btn_connect);
			
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = (ListView) findViewById(R.id.left_drawer);
			mDrawerItems = getResources().getStringArray(R.array.nav_drawer_items);
			
			mList = new ArrayList<HashMap<String,String>>();
			 for(int i=0;i<mDrawerItems.length;i++){
				 HashMap<String, String> hm = new HashMap<String,String>();
				 hm.put("item", mDrawerItems[i]);
				 hm.put("icon", Integer.toString(mDrawerImages[i]) );
				 mList.add(hm);
			 }
			 String[] from = { "icon","item"};
			 
			 int[] to = { R.id.flag , R.id.text1};
			 
			mAdapter = new SimpleAdapter(this, mList, R.layout.drawer_list_item, from, to);
			mDrawerList.setAdapter(mAdapter);
			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			
			btn_find.setOnClickListener(findClickListener);
			btn_start.setOnClickListener(startClickListener);
			btnREady.setOnClickListener(readyClickListener);
			btnStop.setOnClickListener(stopClickListener);
			btnConnect.setOnClickListener(connectClickListener);
			
			setUpDrawerToggle();
			
			etStopper.setEnabled(false);
			mph.setEnabled(false);
			
			
			
			mLocationRequest = LocationRequest.create();
			
			/*
	         * Set the update interval
	         */
	        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

	        // Use high accuracy
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	        // Set the interval ceiling to one minute
	        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

	        // Note that location updates are off until the user turns them on
	        mUpdatesRequested = false;

	        // Open Shared Preferences
	        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

	        // Get an editor
	        mEditor = mPrefs.edit();

	        /*
	         * Create a new location client, using the enclosing class to
	         * handle callbacks.
	         */
	        mLocationClient = new LocationClient(this, this, this);	
			
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        String ss = prefs.getString("speed_preference", "none");
	        Toast.makeText(getApplicationContext(), ss, Toast.LENGTH_LONG).show();
	        }
	        
        	
			
		    catch (Exception name){
		    	String err = (name.getMessage()==null)?"":name.getMessage();
				Log.e("MainActivity onCreate: ",err);
			}
		    
	}

	@Override
	public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();
        

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		
		// Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, "resolved");

                        // Display the result
                        mConnectionState = "connected";
                        mConnectionStatus = "resolved";
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, "no reolution");

                        // Display the result
                        mConnectionState = "disconnected";
                        mConnectionStatus = "no resolution";

                    break;
                }
            case LOGIN_ACTIVITY_CODE:
            	switch (resultCode){
            	 	case Activity.RESULT_OK:
            	 		String picURL;
            	 		if(!intent.getExtras().get("uri").equals("none")){
            	 			picURL = (String)intent.getExtras().get("uri");
            	 			new LoadProfileImage(imgProfilePic).execute(picURL);
            	 		}
            	 		else{
            	 			imgProfilePic.setImageResource(android.R.color.transparent);
            	 		}
            	}

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       "unknown_activity_request_code,"+requestCode);

               break;
        }
    }
	
	private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, "google play services available");

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }
	
	public LatLng getLocation() {
		try{
	        // If Google Play Services is available
	        if (servicesConnected()) {
	        	if(mLocationClient==null){
	        		mLocationClient = new LocationClient(this, this, this);
	        	}
	            // Get the current location
	            Location currentLocation = mLocationClient.getLastLocation();
	
	            if (currentLocation != null) {
	            	return LocationUtils.getLatLng(currentLocation);
				}
	            else {
					Toast.makeText(getApplicationContext(), "nolocation", Toast.LENGTH_LONG).show();
					return null;
				}
	        }
	        else{
	        	return null;
	        }
		}
		catch(Exception e){
			String err = (e.getMessage()==null)?"":e.getMessage();
			Log.d("MainActivity getlocation:",err);
			return null;
		}
    }
	
	public void startUpdates() {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
            mStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    }
	
	@Override
	public void onConnected(Bundle bundle) {
        mConnectionStatus="connected";
//        
//       Location loc = mLocationClient.getLastLocation();
        mLatLng = getLocation();
        
        if(mLatLng != null){
        	setCameraOnMap(mLatLng,10);
        }
        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        mConnectionStatus = "disconnected";
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {
    	
    	try{
	        mConnectionStatus = "location_updated";
	        LatLng LatLng = new LatLng(location.getLatitude(), location.getLongitude());
	        points = newPolyline.getPoints();
	        points.add(LatLng);
	        newPolyline.setPoints(points);
	        
	        setCameraOnMap(LatLng,18);
	        Toast.makeText(getApplicationContext(), Float.toString(location.getAccuracy()), Toast.LENGTH_SHORT).show();
	        
	        float[] distance = new float[2];
	        
	        distance = LocationUtils.getDistance(new LatLng(location.getLatitude(), location.getLongitude()),
	        		new LatLng(destRadiuses.get(0).getCenter().latitude, destRadiuses.get(0).getCenter().longitude));
	
	        if (location.hasSpeed()) {
	        	mph.setText(Integer.toString((int) (location.getSpeed()*2.2369))+"/Mph");
			}
	        else{
	        	mph.setText("0/Mph");
	        }
	        if( distance[0] > destRadiuses.get(0).getRadius()  ){
	            //Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
	        } else {
	            stopPeriodicUpdates();
	            btnStop.setText("Reset");
	        }
    	}
    	catch (Exception e){
    		String err = (e.getMessage()==null)?"":e.getMessage();
    		Log.e("MainActivity - onLocationChanged", err);
    	}
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {
    	try{
	    	if(mLocationClient==null){
	    		mLocationClient = new LocationClient(this, this, this);
	    	}
	        mLocationClient.requestLocationUpdates(mLocationRequest, this);
	        mConnectionState = "location_requested";
    	}
    	catch(Exception e){
    		String err = (e.getMessage()==null)?"":e.getMessage();
    		Log.e("MainActivity - startPeriodicUpdates", err);
    	}
        
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        
        //googleMap.setMyLocationEnabled(false);
        mConnectionState = "location_updates_stopped";
        mHandler.removeCallbacks(mUpdateTimeTask);
   
    }
    
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }
	public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
			
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
		
			@Override
			protected List<Address> doInBackground(String... locationName) {
				// Creating an instance of Geocoder class
				Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
				List<Address> addresses = null;
				
				try {
					int tryTimes = 3;
					addresses = geocoder.getFromLocationName(locationName[0], 3);
					
					while((addresses==null || addresses.size()==0) && tryTimes != 0){
						Thread.sleep(3000);
						tryTimes--;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				return addresses;
			}
			
			
			@Override
			protected void onPostExecute(List<Address> addresses) {			
		        
		        if(addresses==null || addresses.size()==0){
					Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
					return;
				}
		        
		        try{
					for(int i=0;i<addresses.size();i++){				
						if(markers != null){
							for(int ix=0;ix<markers.size();ix++){
			    	 			if(markers.get(ix).getTitle().equals("End")){
			    	 				markers.get(ix).remove();
			    	 				markers.remove(ix);
			    	 				
			    	 			}
			    	 		}
						}
						if(destRadiuses != null){
	    	    	 		for(int ix=0;ix<destRadiuses.size();ix++){
	    	    	 			destRadiuses.get(ix).remove();
	    	    	 			destRadiuses.remove(ix);
	    	    	 		}
		    	 		}
						
						Address address = (Address) addresses.get(i);
						
				        // Creating an instance of GeoPoint, to display in Google Map
				        latLng = new LatLng(address.getLatitude(), address.getLongitude());
				        
	//			        String addressText = String.format("%s, %s",
	//	                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	//	                        address.getCountryName());
	
				        markerOptions = new MarkerOptions();
				        markerOptions.position(latLng);
				        markerOptions.title("End");
				   		
				        mLatLng = getLocation();
				        
				        //check distance to determine radius length
 				        double radiusSize = LocationUtils.getRadius(mLatLng,latLng);
				     
				      //  Toast.makeText(getApplicationContext(), Double.toString(radiusSize), Toast.LENGTH_LONG).show();
				        
				        circleOptions = new CircleOptions();
				        circleOptions.radius(radiusSize);
				        circleOptions.fillColor(0x10000000);
				        circleOptions.center(latLng);
				        
				        Marker marker = googleMap.addMarker(markerOptions);
				        Circle circle = googleMap.addCircle(circleOptions);
				        
				        markers.add(marker);
				        destRadiuses.add(circle);
				        
				        // Locate the first location
				        if(i==0){
				        	if(latLng != null){
					        	CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
					        			.zoom(10)
			    		                .bearing(0)
			    		                .tilt(70)
			    		                .build();
					        	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					        	
					        	marker.showInfoWindow();
					        	spinner.setVisibility(ProgressBar.GONE);
					        	btnREady.setVisibility(View.VISIBLE);
					        	
				        	}
				        }
				        	
					}		
		        }
		        catch(NullPointerException ex){
		        	String err = (ex.getMessage()==null)?"":ex.getMessage();
		        	Log.e("postExec",err);
		        	return;
		        }
		        catch(Exception exc){
		        	String err = (exc.getMessage()==null)?"":exc.getMessage();
		        	Log.e("postExec",err);
		        	return;
		        }
			}		
		}
    
	OnClickListener startClickListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {

			etLocation.setVisibility(View.VISIBLE);
			btn_start.setVisibility(View.INVISIBLE);
			topPanel.setVisibility(RelativeLayout.VISIBLE);
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(etLocation, 0);
			
		}
	};
	
	OnClickListener findClickListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			// Getting user input location
			spinner.setVisibility(ProgressBar.VISIBLE);
			String location = etLocation.getText().toString();
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etLocation.getWindowToken(), 0);
			if(location!=null && !location.equals("")){
				new GeocoderTask().execute(location);
			}
		}
	};
	
	OnClickListener	readyClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
//			moveTaskToBack(true);
//			NotificationManager notificationManager = (NotificationManager) 
//            getSystemService(NOTIFICATION_SERVICE);
//
//		    Intent intent = new Intent(getBaseContext(), MainActivity.class);
//		    
//		    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		    PendingIntent activity = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
//		    
//			Notification notification = new Notification.Builder(getBaseContext())
//	         .setContentTitle("Racer is running")
//	         .setContentText("click to go back")
//	         .setSmallIcon(R.drawable.ic_launcher)
//	         .setContentIntent(activity)
//	         .setOngoing(true)
//	         .build(); 
//					
//		    // Hide the notification after its selected
//		    notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		    
//		    notificationManager.notify(0, notification);
//		    
			
			etLocation.setVisibility(View.INVISIBLE);
			btnREady.setVisibility(View.INVISIBLE);
			topPanel.setVisibility(RelativeLayout.GONE);
			
			Animation bottomUp = AnimationUtils.loadAnimation(getBaseContext(),
		            R.anim.bottom_up);
			statsPanel.setVisibility(RelativeLayout.VISIBLE);
			statsPanel.startAnimation(bottomUp);
		
			initializeDraw();
			startUpdates();
		}
	};
	
	
	private void initializeDraw(){
		rectLine = new PolylineOptions().width(10).color(Color.RED);
		newPolyline = googleMap.addPolyline(rectLine);
	}
	
	OnClickListener stopClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (btnStop.getText().equals("Stop")) {
				stopPeriodicUpdates();
				googleMap.clear();
				btnStop.setText("Reset");
			}
			else{
				statsPanel.setVisibility(View.GONE);
				btnStop.setText("Stop");
				etStopper.setText("00:00:00");
				btn_start.setVisibility(View.VISIBLE);
			}
			
		}
	};
	OnClickListener connectClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getBaseContext(), LoginActivity.class);
			startActivityForResult(intent, 2013);
		}
	};
	private void setCameraOnMap(LatLng param, int zoom){
		 CameraPosition cameraPosition = new CameraPosition.Builder().target(param)
	                .zoom(zoom)
	                .bearing(0)
	                .tilt(70)
	                .build();
	        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private Runnable mUpdateTimeTask = new Runnable() 
	{
	   public void run() 
	   {
		   long millis = System.currentTimeMillis() - mStartTime;
           int seconds = (int) (millis / 1000);
           int minutes = (seconds%3600)/60;
           int hours = seconds / 3600;
           seconds     = seconds % 60;
           
           
           etStopper.setText(String.format("%02d:%02d:%02d",hours, minutes, seconds));
           mHandler.postDelayed(this, 500);
	   }
	}; 
	
	private void setUpDrawerToggle(){
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayHomeAsUpEnabled(true);
		    actionBar.setHomeButtonEnabled(true);
		    
		    // ActionBarDrawerToggle ties together the the proper interactions
		    // between the navigation drawer and the action bar app icon.
		    mDrawerToggle = new ActionBarDrawerToggle(
		            this,                             /* host Activity */
		            mDrawerLayout,                    /* DrawerLayout object */
		            R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
		            R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
		            R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		    ) {
		        @Override
		        public void onDrawerClosed(View drawerView) {
		            invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
		        }
	
		        @Override
		        public void onDrawerOpened(View drawerView) {
		            invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
		        }
		    };
	
		    // Defer code dependent on restoration of previous instance state.
		    // NB: required for the drawer indicator to show up!
		    mDrawerLayout.post(new Runnable() {
		        @Override
		        public void run() {
		            mDrawerToggle.syncState();
		        }
		    });
		    
		    mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Pass the event to ActionBarDrawerToggle, if it returns
	    // true, then it has handled the app icon touch event
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	      return true;
	    }
	    
	    switch (item.getItemId()) {
        // Handle home button in non-drawer mode
        case android.R.id.home:
        	 goBack();
             return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	    // Handle your other action bar items...

	}
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
	    // Create a new fragment and specify the planet to show based on position
		PrefsFragment fragment;
		
		if (position == 0) {
	    	fragment = new PrefsFragment();
		}
		else{
			return;
		}

	    // Insert the fragment by replacing any existing fragment
	    android.app.FragmentManager fragmentManager = getFragmentManager();
 	    fragmentManager.beginTransaction()
 	    			   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
 	                   .replace(R.id.content_frame, fragment)
 	                   .addToBackStack(null)
 	                   .commit();
	    // Highlight the selected item, update the title, and close the drawer
 	   
 	    mDrawerToggle.setDrawerIndicatorEnabled(false);
 	    mDrawerList.setItemChecked(position, true);
	    setTitle(mDrawerItems[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		    
		if (keyCode == KeyEvent.KEYCODE_MENU) {
	        
	        if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
	            mDrawerLayout.openDrawer(mDrawerList);
	        }
	        else{
	        	mDrawerLayout.closeDrawer(mDrawerList);
	        }
	        return true;
	    }
	   
	    return super.onKeyDown(keyCode, e);
	}

	@Override
	public void onBackPressed()
	{
	  	if(mDrawerLayout.isDrawerOpen(mDrawerList)){
	  		mDrawerLayout.closeDrawer(mDrawerList);
    		return;
    	}
	  	if(getActionBar().getTitle().toString().equals("Settings")){
	  		goBack();
	  		return;
	  	}
	  	super.onBackPressed();  // optional depending on your needs
	}
	private void goBack(){
		getFragmentManager().popBackStack();
        //make sure transactions are finished before reading backstack count
        getFragmentManager().executePendingTransactions();
        if (getFragmentManager().getBackStackEntryCount() < 1){
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerList.clearChoices();

            for (int i = 0; i < mDrawerList.getCount(); i++) {
            	mDrawerList.setItemChecked(i, false);
            }
            getActionBar().setTitle(R.string.app_name);
        }
	}
	public void updateUserImage()
	{
		
	}
	
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
 
        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }
 
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
 
        protected void onPostExecute(Bitmap result) {
            
        	bmImage.setImageBitmap(result);
        }
    }
	
}

