package com.ak.client;

import com.google.gwt.user.client.ui.Label;

// Extend any GWT Widget
public class MapClusterWidget extends Label {

    public MapClusterWidget() {

        // CSS class-name should not be v- prefixed
        setStyleName("mapMarkerClusters");

        // State is set to widget in MyComponentConnector
    }

}