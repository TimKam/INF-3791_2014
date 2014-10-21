package com.example.copdmgmtapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import android.location.Location;
import android.location.LocationManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import android.util.Log;

public class WeatherInfo
{
    String wi_temp;
    String wi_wind;
    int wi_icon;

    public WeatherInfo(){
        String wi_temp = "";
        String wi_wind = "";
        int wi_icon = 0;
    }



    public void getWeatherInfo() {
        //API calls example (http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android):
        HttpClient httpclient = new DefaultHttpClient();

        // Execute the request
        HttpResponse response;

        HttpGet httpget = new HttpGet("http://api.met.no/weatherapi/locationforecast/1.9/?lat==60.10;lon=9.58;msl=70");

        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                Log.d("response", result);
                // now you have the string representation of the HTML request
                instream.close();
            }

        }catch (Exception e) {
            Log.d("request failed", e.toString());
        }
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