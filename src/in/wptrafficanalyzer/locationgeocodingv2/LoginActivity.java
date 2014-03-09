package in.wptrafficanalyzer.locationgeocodingv2;


import com.google.android.gms.common.SignInButton;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity implements OnClickListener,ConnectionCallbacks, OnConnectionFailedListener {
	
	Intent resultIntent;
	private String personPhotoUrl;
	private String personName;
	private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginActivity";
 
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
 
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    
    private boolean mIntentInProgress;
    
    private boolean mSignInClicked;
 
    private ConnectionResult mConnectionResult;
    
    private SignInButton btnGoogle;
    private Button btnRevokeAccess;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			

			setContentView(R.layout.activity_login);
			resultIntent = new Intent();
			
			btnGoogle = (SignInButton) findViewById(R.id.btn_google_sign_in);
			btnRevokeAccess = (Button) findViewById(R.id.revoke_button);
			
			btnGoogle.setOnClickListener(this);
			btnRevokeAccess.setOnClickListener(this);
			this.setFinishOnTouchOutside(true);
			
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			
			int height = size.y;
			
			getWindow().setLayout(LayoutParams.WRAP_CONTENT, (int)(height * 0.5f));
			
        	mGoogleApiClient = new GoogleApiClient.Builder(this)
	            .addConnectionCallbacks(this)
	            .addOnConnectionFailedListener(this)
	            .addApi(Plus.API, null)
	            .addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btn_google_sign_in:
            // Signin button clicked
            signInWithGplus();
            break;
        case R.id.revoke_button:
        	revokeGplusAccess();
        	break;
        }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
 
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
 
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }		
	}
	protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
 
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
      
 
        // Get user's information
        getProfileInformation();
        Toast.makeText(this, "User "+personName+" is connected!", Toast.LENGTH_LONG).show();
        
        if(mGoogleApiClient.isConnected()){
	        
	         if(personPhotoUrl != null){
	        	 resultIntent.putExtra("uri", personPhotoUrl);
	         }
		     setResult(Activity.RESULT_OK, resultIntent);
        }
	     
        		
	}
	
	
	@Override
	public void onConnectionSuspended(int arg0) {
		 mGoogleApiClient.connect();		
	}
	@Override
    protected void onActivityResult(int requestCode, int responseCode,
            Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
 
            mIntentInProgress = false;
 
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
	 private void getProfileInformation() {
	        try {
	            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	                Person currentPerson = Plus.PeopleApi
	                        .getCurrentPerson(mGoogleApiClient);
	                personName = currentPerson.getDisplayName();
	                personPhotoUrl = currentPerson.getImage().getUrl();
	                String personGooglePlusProfile = currentPerson.getUrl();
	                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
	 
	                Log.e(TAG, "Name: " + personName + ", plusProfile: "
	                        + personGooglePlusProfile + ", email: " + email
	                        + ", Image: " + personPhotoUrl);
	 
//	                txtName.setText(personName);
//	                txtEmail.setText(email);
	 
	                // by default the profile url gives 50x50 px image only
	                // we can replace the value with whatever dimension we want by
	                // replacing sz=X
	                personPhotoUrl = personPhotoUrl.substring(0,
	                        personPhotoUrl.length() - 2)
	                        + PROFILE_PIC_SIZE;
	 
	            } else {
	                Toast.makeText(getApplicationContext(),
	                        "Person information is null", Toast.LENGTH_LONG).show();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 private void signInWithGplus() {
	        if (!mGoogleApiClient.isConnecting()) {
	            mSignInClicked = true;
	            resolveSignInError();
	        }
	    }
	 /**
	     * Sign-out from google
	     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }
    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            resultIntent.putExtra("uri", "none");
                            setResult(Activity.RESULT_OK, resultIntent);
                            
                        }
 
                    });
        }
        Toast.makeText(getBaseContext(), "You are not connected", Toast.LENGTH_LONG).show();
    }
}
	 

