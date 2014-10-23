package com.example.copdmgmtapp;

public class calculateBackgroundColor
{
	static final double R_Value = 50.0;
	static final double Y_Value = 35.0;
	
	public static int calculate(pollutionData pollution){
		int color = 0xff78ff78; // GREEN
		double pm10 = Double.parseDouble(pollution.pm10_value.replace(",", ".").replace("-", "0"));
		double pm2_5 = Double.parseDouble(pollution.pm2_5_value.replace(",", ".").replace("-", "0"));
		double no2 = Double.parseDouble(pollution.no2_value.replace(",", ".").replace("-", "0"));
		
		if (pm10 > R_Value || pm2_5 > R_Value || no2 > R_Value){
			color = 0xfffa8072; // RED
		}
		else if (pm10 > Y_Value || pm2_5 > Y_Value || no2 > Y_Value){
			color = 0xffffb500; // ORANGE
		}
		return color;
	}
}
