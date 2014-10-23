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
import android.util.Log;

public class pollutionData
{
	String pm10_value;
	String pm2_5_value;
	String no2_value;
	String pm10_avg;
	String pm2_5_avg;
	String no2_avg;
	
	public pollutionData(){
		pm10_value = "-";
		pm2_5_value = "-";
		no2_value = "-";
		pm10_avg = "-";
		pm2_5_avg = "-";
		no2_avg = "-";
	}
	
	public void getPollutionData() {
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
				String data= convertStreamToString(instream);
				// now you have the string representation of the HTML request
				instream.close();
				Document doc = Jsoup.parse(data);
				try{
					String[] separated = scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl02").split(" ");
					pm10_value = separated[1];
					pm10_avg = separated[3];
				}
				catch(Exception e){
				}
				try{
					String[] separated = scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl03").split(" ");
					pm2_5_value = separated[1];
					pm2_5_avg = separated[3];
				}
				catch(Exception e){
				}
				try{
					String[] separated = scrapeType(doc, "#ctl00_cph_Map_ctl00_gwStation_ctl04").split(" ");
					no2_value = separated[1];
					no2_avg = separated[3];
				}
				catch(Exception e){
				}

			}
		} catch (Exception e) {
			Log.d("request failed", e.toString());
		}
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
