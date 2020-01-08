package com.icrisat.sbdm.ismu.ui.openDialog.components;

import com.icrisat.sbdm.ismu.util.SharedInformation;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    private JLabel programLabel, trialLabel, locationLabel;
    public JTextField programInputField, trialInputField, locationInputField;
    public JButton program, trial, location;

    public SearchPanel(SharedInformation sharedInformation, String labelName) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        programLabel = new JLabel("Program");
        programLabel.setFont(sharedInformation.getBoldFont());
        trialLabel = new JLabel("Trial Name");
        trialLabel.setFont(sharedInformation.getBoldFont());
        locationLabel = new JLabel("Location");
        locationLabel.setFont(sharedInformation.getBoldFont());
        programInputField = new CustomTextField(10);
        trialInputField = new CustomTextField(10);
        locationInputField = new CustomTextField(10);
        program = new JButton("  Search  ");
        program.setFont(sharedInformation.getOkButtonFont());
        trial = new JButton("  Search  ");
        trial.setFont(sharedInformation.getOkButtonFont());
        location = new JButton("  Search  ");
        location.setFont(sharedInformation.getOkButtonFont());
        add(programLabel);
        add(programInputField);
        add(program);
        add(trialLabel);
        add(trialInputField);
        add(trial);
        add(locationLabel);
        add(locationInputField);
        add(location);
    }
}
