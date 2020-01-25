package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.bms;

import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.CustomTextField;
import com.icrisat.sbdm.ismu.util.SharedInformation;

import javax.swing.*;
import java.awt.*;

public class BMSSearchPanel extends JPanel {

    public JTextField programInputField, trialInputField, locationInputField;
    public JButton searchButton, resetButton;

    public BMSSearchPanel(SharedInformation sharedInformation) {
        JPanel dataPanel = new JPanel();
        dataPanel.setSize(500, 300);
        dataPanel.setLayout(null);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        JLabel programLabel = new JLabel("Program");
        programLabel.setFont(sharedInformation.getBoldFont());
        JLabel trialLabel = new JLabel("Trial Name");
        trialLabel.setFont(sharedInformation.getBoldFont());
        JLabel locationLabel = new JLabel("Location");
        locationLabel.setFont(sharedInformation.getBoldFont());
        programInputField = new CustomTextField(10);
        trialInputField = new CustomTextField(10);
        locationInputField = new CustomTextField(10);
        searchButton = new JButton("  Search  ");
        searchButton.setFont(sharedInformation.getOkButtonFont());
        resetButton = new JButton("  Reset  ");
        resetButton.setFont(sharedInformation.getOkButtonFont());
        add(programLabel);
        add(programInputField);
        add(trialLabel);
        add(trialInputField);
        add(locationLabel);
        add(locationInputField);
        add(searchButton);
        add(resetButton);
    }
}
