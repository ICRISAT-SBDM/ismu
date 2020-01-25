package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.gobii;

import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.CustomTextField;
import com.icrisat.sbdm.ismu.util.SharedInformation;

import javax.swing.*;
import java.awt.*;

public class GOBIISearchPanel extends JPanel {

    public JTextField variantSetInputField, studyInputField;
    public JButton searchButton, resetButton;

    public GOBIISearchPanel(SharedInformation sharedInformation) {
        JPanel dataPanel = new JPanel();
        dataPanel.setSize(500, 300);
        dataPanel.setLayout(null);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        JLabel variantSetLabel = new JLabel("Variant Set");
        variantSetLabel.setFont(sharedInformation.getBoldFont());
        JLabel studyLabel = new JLabel("Study");
        studyLabel.setFont(sharedInformation.getBoldFont());
        variantSetInputField = new CustomTextField(15);
        studyInputField = new CustomTextField(15);
        searchButton = new JButton("  Search  ");
        searchButton.setFont(sharedInformation.getOkButtonFont());
        resetButton = new JButton("  Reset  ");
        resetButton.setFont(sharedInformation.getOkButtonFont());
        add(variantSetLabel);
        add(variantSetInputField);
        add(studyLabel);
        add(studyInputField);
        add(searchButton);
        add(resetButton);
    }
}
