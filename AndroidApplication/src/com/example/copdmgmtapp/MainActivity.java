package com.example.copdmgmtapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

import com.example.copdmgmtapp.calculateBackgroundColor;
import com.example.copdmgmtapp.pollutionData;
import android.widget.*;

public class MainActivity extends ActionBarActivity {
	public TextView airPollution;
	public final pollutionData pollution = new pollutionData();
	
	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			onAirChange(airPollution, pollution);
			changeBackgroundColor(pollution);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		airPollution = (TextView) findViewById(R.id.airInfo);
		Thread metThread = new Thread()
		{
			@Override
		    public void run() 
		    {
				//get current location
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				String locationProvider = LocationManager.GPS_PROVIDER;
				Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
				double currentLatitude = lastKnownLocation.getLatitude();
				Log.d("current latitude", String.valueOf(currentLatitude));
				double currentLongitude = lastKnownLocation.getLongitude();
				Log.d("current longitude", String.valueOf(currentLongitude));
				
				
		    	//API calls example (http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android):
				HttpClient httpclient = new DefaultHttpClient();
			    // Prepare a request object against weather api with current Latitude/longitude
			    HttpGet httpget = new HttpGet("http://api.met.no/weatherapi/locationforecast/1.9/?lat=" +
			    currentLatitude +
			    ";lon=" +
			    currentLongitude +
			    ";msl=70"); 

			    // Execute the request
			    HttpResponse response;
			    try {
			        response = httpclient.execute(httpget);
			        // Examine the response status
			        Log.i("Praeda",response.getStatusLine().toString());

			        // Get hold of the response entity
			        HttpEntity entity = response.getEntity();
			        // If the response does not enclose an entity, there is no need
			        // to worry about connection release

			        if (entity != null) {

			            // A Simple JSON Response Read
			            InputStream instream = entity.getContent();
			            String result= convertStreamToString(instream);
			            Log.d("response", result);
			            // now you have the string representation of the HTML request
			            instream.close();
			        }


			    } catch (Exception e) {
			    	Log.d("request failed", e.toString());
			    }
		    }
		};
		metThread.start();
		startPollutionThread();
	}
	
	public void startPollutionThread(){
		Thread t = new Thread(){
			public void run(){
				pollution.getPollutionData();
				mHandler.post(mUpdateResults);
			}
		};
		t.start();
	}
	
	public void onAirChange(TextView airPollution, pollutionData pollution){
		airPollution.setText("PM10: "+pollution.pm10_value+" ("+pollution.pm10_avg+
							 ")\nPM2.5: "+pollution.pm2_5_value+" ("+pollution.pm2_5_avg+
							 ")\nNO2: "+pollution.no2_value+" ("+pollution.no2_avg+")");
	}

	private void changeBackgroundColor(pollutionData pollution){
		int color = calculateBackgroundColor.calculate(pollution);
		getWindow().getDecorView().setBackgroundColor(color);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
