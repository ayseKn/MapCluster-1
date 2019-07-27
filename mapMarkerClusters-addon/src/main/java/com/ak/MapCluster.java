package com.ak;

import com.ak.client.MapClusterClientRpc;
import com.ak.client.MapClusterServerRpc;
import com.ak.client.MapClusterState;

import com.vaadin.shared.MouseEventDetails;

// This is the server-side UI component that provides public API 
// for MyComponent
public class MapCluster extends com.vaadin.ui.AbstractComponent {

    private int clickCount = 0;

    public MapCluster() {

        // To receive events from the client, we register ServerRpc
        MapClusterServerRpc rpc = this::handleClick;
        registerRpc(rpc);
    }

    // We must override getState() to cast the state to MyComponentState
    @Override
    protected MapClusterState getState() {
        return (MapClusterState) super.getState();
    }
    
    private void handleClick(MouseEventDetails mouseDetails){
        // Send nag message every 5:th click with ClientRpc
        if (++clickCount % 5 == 0) {
            getRpcProxy(MapClusterClientRpc.class)
                    .alert("Ok, that's enough!");
        }
        
        // Update shared state. This state update is automatically 
        // sent to the client. 
        getState().text = "You have clicked " + clickCount + " times";
    }
}
