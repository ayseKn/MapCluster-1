package com.ak.cluster;

/** *
 * 
 * A GeoPoint is an instance in the data set. <p>
 * Data set is the set of instances to be clustered so that 
 * each GeoPoint will be exactly in one cluster. 
 */
public interface GeoPoint {
	double getLat();
	double getLon();
	
}
