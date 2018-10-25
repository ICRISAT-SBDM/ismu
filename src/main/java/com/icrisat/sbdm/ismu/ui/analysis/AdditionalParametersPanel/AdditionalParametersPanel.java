package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Additional parameters panel. Sub-panel for Analysis.
 */
@Component
public class AdditionalParametersPanel extends JPanel {
    private SharedInformation sharedInformation;
    private BayesPanel bayesPanel;
    private SecondPanel secondPanel;
    private CrossValidationPanel crossValidationPanel;

    public SharedInformation getSharedInformation() {
        return sharedInformation;
    }

    public BayesPanel getBayesPanel() {
        return bayesPanel;
    }

    public SecondPanel getSecondPanel() {
        return secondPanel;
    }

    public CrossValidationPanel getCrossValidationPanel() {
        return crossValidationPanel;
    }

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    public void setBayesPanel(BayesPanel bayesPanel) {
        this.bayesPanel = bayesPanel;
    }

    @Autowired
    public void setSecondPanel(SecondPanel secondPanel) {
        this.secondPanel = secondPanel;
    }

    @Autowired
    public void setCrossValidationPanel(CrossValidationPanel crossValidationPanel) {
        this.crossValidationPanel = crossValidationPanel;
    }

    public void createAdditionalParametersPanel() {
        setSize(700, 180);
        setBorder(Util.getCompoundBorder("Additional parameters", sharedInformation));
        setLayout(new GridLayout(1, 2));
        add(bayesPanel);
        add(secondPanel);
        add(crossValidationPanel);
    }
}
