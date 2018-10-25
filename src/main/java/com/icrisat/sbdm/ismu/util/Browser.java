package com.icrisat.sbdm.ismu.util;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;

/**
 * Class used to display html pages.
 */
public class Browser extends Region {

    private final WebView browser = new WebView();

    public Browser(FileLocation genoHtmlFileLocation) {
        // load the web page
        File file = new File(genoHtmlFileLocation.getFileLocationOnDisk());
        WebEngine webEngine = browser.getEngine();
        webEngine.load(file.toURI().toString());
        //add the web view to the scene
        getChildren().add(browser);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }
}
