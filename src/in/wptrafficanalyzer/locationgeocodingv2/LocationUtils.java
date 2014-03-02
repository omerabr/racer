/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.wptrafficanalyzer.locationgeocodingv2;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;

import in.wptrafficanalyzer.locationgeocodingv2.R;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    // Debugging tag for the application
    public static final String APPTAG = "LocationSample";

    // Name of shared preferences repository that stores persistent state
    public static final String SHARED_PREFERENCES =
            "in.wptrafficanalyzer.locationgeocodingv2.SHARED_PREFERENCES";

    // Key for storing the "updates requested" flag in shared preferences
    public static final String KEY_UPDATES_REQUESTED =
            "in.wptrafficanalyzer.locationgeocodingv2.KEY_UPDATES_REQUESTED";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    // Create an empty string for initializing strings
    public static final String EMPTY_STRING = new String();

    /**
     * Get the latitude and longitude from the Location object returned by
     * Location Services.
     *
     * @param currentLocation A Location object containing the current location
     * @return The latitude and longitude of the current location, or null if no
     * location is available.
     */
    public static LatLng getLatLng(Location currentLocation) {
        // If the location is valid
        if (currentLocation != null) {

            // Return the latitude and longitude as strings
            return new LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        } else {

            // Otherwise, return the empty string
            return null;
        }
    }

    public static float[] getDistance(LatLng homeLocation,LatLng destLocation){
    	float[] results = new float[2];
    	
    	Location.distanceBetween(homeLocation.latitude, homeLocation.longitude, destLocation.latitude
    			, destLocation.longitude, results);
    	
    	return results;
    }

    public static double getRadius(LatLng homeLocation,LatLng destLocation){
    	float [] distance = getDistance(homeLocation, destLocation);
    	return distance[0]/6;
//    	
//    	if(distance[0] < 300){
//    		return 400;
//    	}
//    	else if (distance[0] > 300 && distance[0] < 1000) {
//			
//		}
//    	else if (distance[0] > 1000 && distance[0] < 5000) {
//			
//		}
//    	else if (distance[0] > 5000 && distance[0] < 10000) {
//			
//		}
//    	else if (distance[0] > 10000 && distance[0] < 50000) {
//			
//		}
//    	else{
//    		
//    	}
    }
}