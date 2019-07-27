package com.ak.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ClusterGeoPoints {

	/** 
	 * dataSet & allDistances have parallel indices:
	 *  s.t. 
	 *          allDistances[i][j] = allDistances[j][i] for all data-points i,j
	 *          allDistances[i][j] is the distance between the GeoPoint-s dataSet.get(i) & dataSet.get(j).
	 */
	private final double [][] allDistances; // distances between each data-point pairs on the map
	private final ArrayList<GeoPoint> dataSet;  // data points, i.e., the vertices in the graph
	private Set<Cluster> clusters = new HashSet<>();  // dataSet.get(i) is in clusters.get(j) for some j iff clusters.get(j).contains(i)

	private final ArrayList<HashSet<Integer>>  clustersInternal = new ArrayList<>();  
	private final double distLimit; // max allowed distance% between pairs of data points in one cluster. Part of the user input. 
	private LinkedList<DistanceEdge> sortedDistances;  // sorted distances between pairwise dataSet members. 
	private Set<Integer> inSet = new HashSet<>();   // a dataPoint sitting at dataSet.get(i) is in inSet iff dataSet.get(i) is already in a cluster.

	//=======================================================================================================

	public ClusterGeoPoints(Collection<GeoPoint> dataSet, double dist){
		distLimit = dist; 
		this.dataSet=dataSet instanceof ArrayList?(ArrayList<GeoPoint>)dataSet:new ArrayList<GeoPoint>(dataSet); 
		allDistances = new double[dataSet.size()][dataSet.size()];
		computeAllDistances();
		runClusterer();
	}

	private void computeAllDistances() {
		int N=dataSet.size();
		DistanceEdge [] cd = new DistanceEdge [N*(N-1)/2];   
		int ind = 0; 
		for (int i=0; i<N; i++)
			for (int j=i+1; j<N; j++) {
				allDistances[i][j] = allDistances[j][i] 
						= ClusterUtils.geoDistance(dataSet.get(i).getLat(), dataSet.get(i).getLon(), dataSet.get(j).getLat(), dataSet.get(j).getLon());  
				cd[ind++] = new DistanceEdge(i, j, allDistances[i][j]);
			}
		Arrays.sort(cd);
		sortedDistances = new LinkedList<DistanceEdge>(Arrays.asList(cd));
	}

	@SuppressWarnings("serial")
	private void cluster() {
		while (inSet.size()<dataSet.size()) {
			final DistanceEdge anEdge = sortedDistances.removeLast();
			if ( !place(anEdge, true) )  // call for aPair.I
				clustersInternal.add(new HashSet<Integer>() {{add(anEdge.I);}});
			if ( !place(anEdge, false) ) // call for aPair.J
				clustersInternal.add(new HashSet<Integer>() {{add(anEdge.J);}});
		}      
	}

	/** 
	 * Places thePoint in an existing cluster among the clusters that it can get in.
	 *          updates inSet & clusters fields accly. <p>
	 * Returns false iff no such cluster exists and thus thePoint hasn't been placed in an existing cluster. 	 */    
	private boolean place(DistanceEdge aPair, boolean isI) {
		int thePoint = isI?aPair.I:aPair.J;

		if (inSet.contains(thePoint))
			return true; 

		double optDist;     // the avg dist of thePoint to the points in cluster K
		HashMap<Integer, Double> possibleClusters = new HashMap<Integer, Double>();  // <K,V>: K - cluster ID, V - avg.distance of thePoint to the points in cluster K

		outer: 
		for (int i=clustersInternal.size()-1; i>=0; i--) {  // so that the latest-added cluster is the likeliest to contain "this" point
			optDist = 0.0d;

			// calculate distance metric for this cluster. 
				// also consider min, max or some other ratther than average
			for (Integer point:clustersInternal.get(i))
				if (allDistances[point][thePoint]<=distLimit)
					optDist+=allDistances[thePoint][point];
				else continue outer;
			
			possibleClusters.put(i, optDist/clustersInternal.get(i).size());
		}

		if (possibleClusters.isEmpty()) 
			return false; 

		inSet.add(thePoint);
		clustersInternal.get(bestCluster(possibleClusters)).add(thePoint);
		return true; 
	}

	private int bestCluster(HashMap<Integer, Double> possibleClusters) {
		double currentOpt = Double.MAX_VALUE;
		double temp; 
		int currentBest=-1;
		for (Integer C:possibleClusters.keySet())
			if ((temp=possibleClusters.get(C))<currentOpt) {
				currentOpt = temp; 
				currentBest = C; 
			}
		return currentBest;  
	}

	public Set<Cluster> getClusterSet() { return clusters; }

	private void formResults() {
		clusters.clear();
		clustersInternal.forEach(C -> {
			Cluster cluster = new Cluster();
			C.forEach(dp -> cluster.dataPoints.add(dataSet.get(dp)));
			makeCentroid(cluster);
			clusters.add(cluster);
		});
	}

	private void runClusterer() {
		cluster();
		formResults();
	}

	private static Cluster makeCentroid(Cluster aCluster) {
		// Manhattan distances
		aCluster.dataPoints.forEach(C -> {
			aCluster.centroidLat+=C.getLat();
			aCluster.centroidLon+=C.getLon();
		});
		double N=aCluster.dataPoints.size();
		aCluster.centroidLat /=N;
		aCluster.centroidLon /=N;
		return aCluster; 
	}

	public static class Cluster{
		public HashSet<GeoPoint> dataPoints;
		public double centroidLat, centroidLon; 
		Cluster(){ dataPoints = new HashSet<>(); }
	}

	private static class DistanceEdge implements Comparable<DistanceEdge> {
		int I, J; 
		double distance; 
		DistanceEdge(int I, int J, double dist) {
			this.I = I; 
			this.J = J; 
			this.distance = dist;
		}           
		@Override public int compareTo(DistanceEdge o) {
			return (distance==o.distance?0:distance>o.distance? 1:-1);
		}
	}
}
