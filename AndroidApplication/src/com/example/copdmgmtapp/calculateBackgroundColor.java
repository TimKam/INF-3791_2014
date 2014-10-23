package com.example.copdmgmtapp;

import android.util.Log;

public class calculateBackgroundColor
{
	static final double R_Value = 500.0;
	static final double Y_Value = 350.0;
    static final int Green = 0xff78ff78;
    static final int Red = 0xfffa8072;
    static final int Yellow = 0xffffb500;

    public static int calculate(pollutionData pollution, WeatherInfo weatherInfo){
		int color;
		double pm10 = Double.parseDouble(pollution.pm10_value.replace(",", ".").replace("-", "0"));
		double pm2_5 = Double.parseDouble(pollution.pm2_5_value.replace(",", ".").replace("-", "0"));
		double no2 = Double.parseDouble(pollution.no2_value.replace(",", ".").replace("-", "0"));

        double wind = Double.parseDouble(weatherInfo.wi_windspeed);
        double wind_dangernumber;

        double temp = Double.parseDouble(weatherInfo.wi_temp);
        double temp_dangernumber;

        double air_dangernumber = pm10 + (pm2_5*20) + (no2*2.5);

        //TODO Fix traffic
        double traffic_dangernumber = 50;


        if(wind <= 5){
            wind_dangernumber = 0;
        } else if(wind >= 6 && wind <=10){
            wind_dangernumber = 10;
        } else if(wind >= 11 && wind <= 15){
            wind_dangernumber = 20;
        } else if(wind >= 16 && wind <= 20){
            wind_dangernumber = 30;
        } else {
            wind_dangernumber = 50;
        }


        if(temp > 10.0){
            temp_dangernumber = 0;
        } else if(temp >= 0 && temp <=10){
            temp_dangernumber = 50;
        }else if(temp < 0 && temp >= -10){
            temp_dangernumber = 100;
        }else{
            temp_dangernumber = 200;
        }

        double total_danger = air_dangernumber + wind_dangernumber + temp_dangernumber + traffic_dangernumber;

        Log.d("fetta: ", String.valueOf(total_danger));

		if (total_danger > R_Value){
			color = Red;
		}
		else if (total_danger > Y_Value){
			color = Yellow;
		}else{
            color = Green;
        }

		return color;
	}
}
