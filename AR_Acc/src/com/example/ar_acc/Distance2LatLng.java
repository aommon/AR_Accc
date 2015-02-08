package com.example.ar_acc;

import android.graphics.PointF;

public class Distance2LatLng {
	
	static double r = 6378.1;
	static double brng = 1.57;
	static int distance = 15;
	
	static double lat1 = Harversine.deg2rad(52.20472);
	static double lon1 = Harversine.deg2rad(0.14056);
	
	static double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance/r) + Math.cos(lat1)*Math.sin(distance/r)*Math.cos(brng) );
	static double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(distance/r)*Math.cos(lat1), Math.cos(distance/r)-Math.sin(lat1)*Math.sin(lat2));
	
	static double lat2n = Harversine.rad2deg(lat2);
	
	static double lon2n = Harversine.rad2deg(lon2);
	
	public static double returnLa(){
		return lat2n;		
	}
	public static double returnLon(){
		return lon2n;		
	}
}
