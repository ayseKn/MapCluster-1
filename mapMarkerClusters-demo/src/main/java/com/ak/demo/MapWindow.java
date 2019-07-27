package com.ak.demo;

import java.text.DecimalFormat;
import java.util.HashSet;

import org.vaadin.addon.leaflet.LMarker;

import com.ak.cluster.ClusterGeoPoints.Cluster;
import com.ak.cluster.ClustersLMap;
import com.ak.cluster.GeoPoint;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.NumberRenderer;

public class MapWindow extends Window {
	private static final long serialVersionUID = 7939702818520703119L;

	private HorizontalSplitPanel vsp = new HorizontalSplitPanel();
	private VerticalLayout layout = new VerticalLayout();
	private Label heading = new Label("<h2>Marker Content - Last clicked</h2>", ContentMode.HTML);
	private Label text = new Label();
	private Grid<GeoPoint> grid = new Grid<>("Points");
	private HashSet<GeoPoint> dp = new HashSet<>();
	private ListDataProvider<GeoPoint> dataProvider = new ListDataProvider<>(dp);
	
	private final static String lFormat = "####.#########";
	private static DecimalFormat df = new DecimalFormat(lFormat);
	
	{  // configure gadgets
		grid.setDataProvider(dataProvider);

		grid.addColumn(GeoPoint::getLat, new NumberRenderer(df)).setCaption("Lattitude").setResizable(false); 
		grid.addColumn(GeoPoint::getLon, new NumberRenderer(df)).setCaption("Longitude").setResizable(false);

		grid.setColumnReorderingAllowed(true); 
		grid.setSelectionMode(SelectionMode.NONE); 
		grid.setSizeFull();
		grid.setHeightMode(HeightMode.UNDEFINED);  
		
		layout.addComponents(heading, text, grid);
		layout.setSizeUndefined();
		layout.setSpacing(true);
		layout.setMargin(true);
	}

	public MapWindow() {
		ClustersLMap theMap = new ClustersLMap(GeoPointTest.testLat, GeoPointTest.testLon, GeoPointTest.populateTestGeoPoints(5));
//		ClustersLMap theMap = new ClustersLMap(GeoPointTest.testLat, GeoPointTest.testLon, GeoPointTest.populateTestGeoPointsFixed());
		theMap.setMarkerClickListener(e -> {
			LMarker m= (LMarker)e.getConnector();
			Cluster c = (Cluster)m.getData();
			dp.clear();
			dp.addAll(c.dataPoints);
			dataProvider.refreshAll();
			
			text.setValue("Cluster center: ("+df.format(c.centroidLat)+", "+df.format(c.centroidLon)+")");
		});

		vsp.setFirstComponent(theMap);
		vsp.setSecondComponent(layout);
		vsp.setSplitPosition(70, Sizeable.UNITS_PERCENTAGE);
		vsp.setLocked(true);
		vsp.setSizeFull();

		setClosable(false);
		setDraggable(false);
		setResizable(false);
		setSizeFull();
		setModal(true);
		setContent(vsp);
	}

}


