package com.ak.cluster;

import org.vaadin.addon.leaflet.shared.Bounds;

public class ClusterUtils {
	public static double geoDistance(double lat1, double lon1, double lat2, double lon2) {
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
					+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
						 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	/**
	 * returns the geo-distance equivalent of percentageDistance% mapLimits, <BR>
	 * i.e., returns [percentageDistance*(window-hypotenuse-in-geoDistance)/100] 
	 * */
	public static double computeClusterDistanceLimit(Bounds mapLimits, float percentageDistance) {
		double lat2 = mapLimits.getSouthWestLat();
		double lon2 = mapLimits.getSouthWestLon();
		double deltaLat=Math.abs(mapLimits.getNorthEastLat()-lat2);
		double deltaLon=Math.abs(mapLimits.getNorthEastLon()-lon2);

		return geoDistance(lat2, lon2, lat2+deltaLat, lon2+deltaLon)*percentageDistance/100;	
	}

}
