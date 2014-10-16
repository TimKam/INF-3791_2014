package com.example.copdmgmtapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.net.*;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Thread metThread = new Thread()
		{
			@Override
		    public void run() 
		    {
		    	//API calls example (http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android):
				HttpClient httpclient = new DefaultHttpClient();
			    // Prepare a request object
			    HttpGet httpget = new HttpGet("http://api.met.no/weatherapi/locationforecast/1.9/?lat=60.10;lon=9.58;msl=70"); 

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
		Thread pollutionThread = new Thread()
		{
			@Override
		    public void run() 
		    {
		    	//API calls example (http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android):
				HttpClient httpclient = new DefaultHttpClient();
			    // Prepare a request object
				//String station = "{49ba925d-7e72-4a1f-b4ec-f63086161e29}"; //Tverrforbindelsen
				String station = "{e3b8f62d-ae81-421a-94dc-76afdd9ee822}"; //Hansjordnesbukta
				try {
					station = URLEncoder.encode(station, "utf-8");
				} catch (Exception e) {

				}
			    // Execute the request
			    HttpResponse response;
			    try {
					HttpGet httpget = new HttpGet("http://www.luftkvalitet.info/home/overview.aspx?type=Station&id=" + station);

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
			            // now you have the string representation of the HTML request
			            instream.close();
						Document doc = Jsoup.parse(result);
						try{
							Log.d("result-air", scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl02")); // PM10
						}
						catch(Exception e){
						}
						try{
							Log.d("result-air", scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl03")); // PM2.5
						}
						catch(Exception e){
						}
						try{
							Log.d("result-air", scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl04")); // NO2
						}
						catch(Exception e){
						}

					}
			    } catch (Exception e) {
			    	Log.d("request failed", e.toString());
			    }
		    }
		};

		metThread.start();
		pollutionThread.start();


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

	private static String scrapeType(Document doc, String id){
        return doc.select(id+"_Label2").text();
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
