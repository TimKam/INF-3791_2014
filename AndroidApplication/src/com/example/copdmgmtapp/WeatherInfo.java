package com.example.copdmgmtapp;

import java.io.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class WeatherInfo
{
    String wi_temp;
    String wi_wind;
    String wi_windspeed;
    String wi_icon;
    String wi_hum;
    Double lat = 0.0;
    Double lon = 0.0;

    public WeatherInfo(){
        wi_temp = "";
        wi_wind = "";
        wi_windspeed = "";
        wi_icon = "";
        wi_hum = "";
    }

    public void setLatLon(double inlat, double inlon){

        lat = inlat;
        lon = inlon;

    }

    public void getWeatherInfo() {

        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


        //API calls example (http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android):
        HttpClient httpclient = new DefaultHttpClient();
        // Execute the request
        HttpResponse response;


        HttpGet httpget = new HttpGet("http://api.met.no/weatherapi/locationforecast/1.9/?lat=" +
                lat +
                ";lon=" +
                lon +
                ";msl=70");

        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();

            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            if (entity != null) {
                // A Simple XML Response Read
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                //Log.d("response", result);

                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(result));

                doc = builder.parse(is);

                NodeList symbollist = doc.getElementsByTagName("symbol");
                NodeList templist = doc.getElementsByTagName("temperature");
                NodeList windlist = doc.getElementsByTagName("windSpeed");
                NodeList humlist = doc.getElementsByTagName("humidity");
                Element symbolele = (Element) symbollist.item(0);
                Element tempele = (Element) templist.item(0);
                Element windele = (Element) windlist.item(0);
                Element humele = (Element) humlist.item(0);

                wi_icon = symbolele.getAttribute("number");
                wi_temp = tempele.getAttribute("value");
                wi_wind = windele.getAttribute("name");
                wi_windspeed = windele.getAttribute("mps");
                wi_hum = humele.getAttribute("value");

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
