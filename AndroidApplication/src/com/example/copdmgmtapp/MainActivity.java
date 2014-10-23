package com.example.copdmgmtapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.copdmgmtapp.calculateBackgroundColor;
import com.example.copdmgmtapp.pollutionData;
import com.example.copdmgmtapp.WeatherInfo;
import android.widget.*;


public class MainActivity extends ActionBarActivity {
	public TextView airPollution;
    public TextView weatherIcon;
    public TextView weatherTemp;
    public TextView weatherWind;
    //public TextView weatherHum;
	public final pollutionData pollution = new pollutionData();
    public final WeatherInfo weatherInfo = new WeatherInfo();
	
	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			onAirChange(airPollution, pollution);
			changeBackgroundColor(pollution);
		}
	};

    final Runnable mUpdateWeather = new Runnable() {
        public void run() {
            onWeatherChange(weatherInfo);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		airPollution = (TextView) findViewById(R.id.airInfo);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherTemp = (TextView) findViewById(R.id.textTemp);
        weatherWind = (TextView) findViewById(R.id.windInfo);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double currentLatitude = lastKnownLocation.getLatitude();
        Log.d("current latitude", String.valueOf(currentLatitude));
        double currentLongitude = lastKnownLocation.getLongitude();
        Log.d("current longitude", String.valueOf(currentLongitude));

        weatherInfo.setLatLon(currentLatitude, currentLongitude);

        String FontPath = "weather.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), FontPath);
        weatherIcon.setTypeface(tf);


		startPollutionThread();
        startWeatherThread();
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

    public void startWeatherThread(){
        Thread t = new Thread(){
            public void run(){
                weatherInfo.getWeatherInfo();
                mHandler.post(mUpdateWeather);
            }
        };
        t.start();
    }
	
	public void onAirChange(TextView airPollution, pollutionData pollution){
		airPollution.setText("PM10: "+pollution.pm10_value+" ("+pollution.pm10_avg+
							 ")\nPM2.5: "+pollution.pm2_5_value+" ("+pollution.pm2_5_avg+
							 ")\nNO2: "+pollution.no2_value+" ("+pollution.no2_avg+")");
	}

    public void onWeatherChange(WeatherInfo info){
        weatherTemp.setText(info.wi_temp+" \u2103"+
                            "\n"+info.wi_hum+" %");
        weatherWind.setText(info.wi_wind+
                            "\n "+info.wi_windspeed+" m/s");

        int intIcon = Integer.parseInt(info.wi_icon);

        if(intIcon == 1){
            weatherIcon.setText(R.string.weather_sunny);
        } else if(intIcon >= 2 && intIcon <= 4) {
            weatherIcon.setText(R.string.weather_cloudy);
        } else if(intIcon >= 5 && intIcon <= 8){
            weatherIcon.setText(R.string.weather_lightrainsun);
        }else if(intIcon >= 9 && intIcon <= 10){
            weatherIcon.setText(R.string.weather_rainy);
        }else if(intIcon == 11 || intIcon >= 20 && intIcon <= 34){
            weatherIcon.setText(R.string.weather_thunder);
        }else if(intIcon == 12 || intIcon == 13 || intIcon == 49 || intIcon == 50 ){
            weatherIcon.setText(R.string.weather_snowy);
        }else if(intIcon == 15){
            weatherIcon.setText(R.string.weather_foggy);
        }else if(intIcon == 46){
            weatherIcon.setText(R.string.weather_drizzle);
        }else {
            weatherIcon.setText("Unknow weather :)");
        }
        return;
    }

	private void changeBackgroundColor(pollutionData pollution){
		int color = calculateBackgroundColor.calculate(pollution, weatherInfo);
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
