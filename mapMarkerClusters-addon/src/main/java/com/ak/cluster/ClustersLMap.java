package com.ak.cluster;

import java.util.HashSet;
import java.util.Set;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;

import com.ak.cluster.ClusterGeoPoints.Cluster;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class ClustersLMap extends LMap {

	public static final int DISTANCE_PERCTENTAGE = 10;
	
	//each marker contains a cluster. markers partition all the GeoPoints there are.
	private Set<LMarker> markers2BRendered = new HashSet<LMarker>();   

	private Set<GeoPoint> geoPts; 
	private Set<LMarker> currentMarkersOnMap = new HashSet<>();
	private ClusterGeoPoints theClusters;

	private LeafletClickListener markerClickListener = new LeafletClickListener(){
		@Override public void onClick(LeafletClickEvent e){
			LMarker z= (LMarker)e.getConnector();
			for (GeoPoint pt:((Cluster)z.getData()).dataPoints)
				/* TODO */;
		}
	};
	public LeafletClickListener getMarkerClickListener() { return markerClickListener; }
	public void setMarkerClickListener(LeafletClickListener markerClickListener) { this.markerClickListener = markerClickListener; }

	public ClustersLMap(double lat, double lon, Set<GeoPoint> markerPoints) {
		geoPts = markerPoints; 
		setCenter(lat, lon);
		setZoomLevel(10);
		addBaseLayer(new LOpenStreetMapLayer(), null);
		removeControl(getLayersControl());
		addMoveEndListener(e -> renderMarkers());
	}

	private void renderMarkers() {
		if (geoPts==null  || geoPts.isEmpty()) {
			Notification.show("No points in this range.").setPosition(Position.BOTTOM_LEFT);;
			clearCurrentMarkersOnMap();
			return; 
		}		
		theClusters = new ClusterGeoPoints(geoPts, ClusterUtils.computeClusterDistanceLimit(getBounds(), DISTANCE_PERCTENTAGE));
		makeMarkers( );
		setCurrentMarkersOnMap();
	}

	public void clearCurrentMarkersOnMap() {
		currentMarkersOnMap.forEach(this::removeComponent);
		currentMarkersOnMap.clear();
	}

	public void setCurrentMarkersOnMap() {
		clearCurrentMarkersOnMap();
		currentMarkersOnMap.addAll(markers2BRendered);
		currentMarkersOnMap.forEach(this::addComponent);
	}
	
	private void makeMarkers(){
		markers2BRendered.clear();
		theClusters.getClusterSet().stream()
			.filter(cluster->!cluster.dataPoints.isEmpty())
			.forEach(cluster -> {
				LMarker tut = new LMarker(cluster.centroidLat, cluster.centroidLon);
				tut.setData(cluster);
			    tut.addStyleName("v-leaflet-custom-svg");
				tut.setDivIcon(SVG_CODE_MARKER_1+(cluster.dataPoints.size()>999?"999+":cluster.dataPoints.size())+SVG_CODE_MARKER_2);
				tut.addClickListener(markerClickListener);
				markers2BRendered.add(tut);
		});
	}    

	private static final String SVG_CODE_MARKER_1 = "<svg width='47' height='22'> "
			+ "<defs> "
				+ "<radialGradient id='grad1' cx='50%' cy='50%' r='50%' fx='50%' fy='50%'> "
					+ "<stop offset='0%' style='stop-color:#ffeecc;stop-opacity:0' />      "
					+ "<stop offset='100%' style='stop-color:"+"#990033"+";stop-opacity:1' />    "
					+ "</radialGradient>  "
			+ "</defs>  "
			+ "<rect x='1' y='1' rx='15' ry='10' width='45' height='20'  style='fill:url(#grad1);stroke:"+"#990033"+";stroke-width:2;opacity:0.7' />"
			+ "<text x='24' y='17' text-anchor='middle' fill='black' font-size='18'>"
			;
	private static final String SVG_CODE_MARKER_2 = "</text></svg>";

}
