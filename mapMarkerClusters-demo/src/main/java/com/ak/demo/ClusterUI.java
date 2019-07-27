package com.ak.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Theme("demo")
@Title("MapCluster Add-on Demo")
@SuppressWarnings("serial")
public class ClusterUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ClusterUI.class)
    public static class Servlet extends VaadinServlet {}

    @Override
    protected void init(VaadinRequest request) {
		getCurrent().addWindow(new MapWindow());
    }
}


