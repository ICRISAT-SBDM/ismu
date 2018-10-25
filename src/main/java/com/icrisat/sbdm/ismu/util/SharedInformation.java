package com.icrisat.sbdm.ismu.util;

import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.retrofit.germinate.GerminateRetrofitClient;
import com.icrisat.sbdm.ismu.retrofit.gigwa.GigwaRetrofitClient;
import com.icrisat.sbdm.ismu.retrofit.gigwa.GigwaRetrofitUtil;
import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.mainFrame.ClosableTabbedPane;
import com.icrisat.sbdm.ismu.ui.openDialog.OpenDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Shared information required across many classes
 */
@Component("sharedInformation")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SharedInformation {
    private JFrame mainFrame;
    private ClosableTabbedPane tabbedPane;
    private Font font, boldFont, okButtonFont, titleBoldFont;
    private PathConstants pathConstants;
    private JDialog phenotypeURLPanel, genotypeURLPanel;
    private OpenDialog openDialog;
    private BMSRetrofitClient bmsRetrofitClient;
    private GOBIIRetrofitClient gobiiRetrofitClient;
    private GerminateRetrofitClient germinateRetrofitClient;
    private GigwaRetrofitClient gigwaRetrofitClient;
    private String OS;
    private String workingDirectory = System.getProperty("user.dir");
    private Logger logger = LoggerFactory.getLogger(SharedInformation.class);

    public OpenDialog getOpenDialog() {
        return openDialog;
    }

    @Autowired
    public void setOpenDialog(OpenDialog openDialog) {
        this.openDialog = openDialog;
        this.openDialog.setSharedInformation(this);
    }

    public BMSRetrofitClient getBmsRetrofitClient() {
        return bmsRetrofitClient;
    }

    @Autowired
    public void setBmsRetrofitClient(BMSRetrofitClient bmsRetrofitClient) {
        this.bmsRetrofitClient = bmsRetrofitClient;
        this.bmsRetrofitClient.setSharedInformation(this);
    }

    public GOBIIRetrofitClient getGobiiRetrofitClient() {
        return gobiiRetrofitClient;
    }

    @Autowired
    public void setGobiiRetrofitClient(GOBIIRetrofitClient gobiiRetrofitClient) {
        this.gobiiRetrofitClient = gobiiRetrofitClient;
    }

    public GerminateRetrofitClient getGerminateRetrofitClient() {
        return germinateRetrofitClient;
    }

    @Autowired
    public void setGerminateRetrofitClient(GerminateRetrofitClient germinateRetrofitClient) {
        this.germinateRetrofitClient = germinateRetrofitClient;
    }

    public GigwaRetrofitClient getGigwaRetrofitClient() {
        return gigwaRetrofitClient;
    }

    @Autowired
    public void setGigwaRetrofitClient(GigwaRetrofitClient gigwaRetrofitClient) {
        this.gigwaRetrofitClient = gigwaRetrofitClient;
    }

    public PathConstants getPathConstants() {
        return pathConstants;
    }

    @Autowired
    public void setPathConstants(PathConstants pathConstants) {
        this.pathConstants = pathConstants;
    }

    public SharedInformation() {
        font = new Font("Arial", Font.PLAIN, 12);
        boldFont = new Font("Arial", Font.BOLD, 15);
        okButtonFont = new Font("Arial", Font.BOLD, 13);
        titleBoldFont = new Font("Arial", Font.BOLD, 15);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public ClosableTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setTabbedPane(ClosableTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    public Font getFont() {
        return font;
    }

    public Font getBoldFont() {
        return boldFont;
    }

    Font getTitleBoldFont() {
        return titleBoldFont;
    }

    public Font getOkButtonFont() {
        return okButtonFont;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public JDialog getPhenotypeURLPanel() {
        return phenotypeURLPanel;
    }

    public void setPhenotypeURLPanel(JDialog phenotypeURLDialog) {
        this.phenotypeURLPanel = phenotypeURLDialog;
    }

    public JDialog getGenotypeURLPanel() {
        return genotypeURLPanel;
    }

    public void setGenotypeURLPanel(JDialog genotypeURLPanel) {
        this.genotypeURLPanel = genotypeURLPanel;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}

