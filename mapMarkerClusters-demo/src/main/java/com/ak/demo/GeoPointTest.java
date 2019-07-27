package com.ak.demo;

import java.util.HashSet;
import java.util.Set;

import com.ak.cluster.GeoPoint;

public class GeoPointTest implements GeoPoint{
	
	public static double testLat = 45.51;
	public static double testLon = -73.59;

	private double lat, lon;
	@Override public double getLat() { return lat; }
	@Override public double getLon() { return lon; }

	public GeoPointTest(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public static Set<GeoPoint> populateTestGeoPoints(int n) {
		Set<GeoPoint> testPoints = new HashSet<GeoPoint>();
		for (int i=0; i<n; i++) {
			testPoints.add(new GeoPointTest(testLat+Math.random()/2.0, testLon+Math.random()));
			testPoints.add(new GeoPointTest(testLat+Math.random()/2.0, testLon-Math.random()));
			testPoints.add(new GeoPointTest(testLat-Math.random()/2.0, testLon+Math.random()));
			testPoints.add(new GeoPointTest(testLat-Math.random()/2.0, testLon-Math.random()));
		}
		return testPoints;
	}
	
	public static Set<GeoPoint> populateTestGeoPointsFixed() {
		Set<GeoPoint> testPoints = new HashSet<GeoPoint>();
		testPoints.add(new GeoPointTest(45.1639, -73.7138));
		testPoints.add(new GeoPointTest(45.0180499, -74.3014));
		testPoints.add(new GeoPointTest(45.57705, -73.5518));
		testPoints.add(new GeoPointTest(45.8701, -73.1252));
		testPoints.add(new GeoPointTest(45.1172, -73.7499));
		testPoints.add(new GeoPointTest(45.66095, -72.7616));
		testPoints.add(new GeoPointTest(45.7634, -72.9734));
		testPoints.add(new GeoPointTest(45.05225, -74.0375));
		testPoints.add(new GeoPointTest(45.058099, -74.0342));
		testPoints.add(new GeoPointTest(45.59015, -73.3534));
		return testPoints;
	}
	
}
