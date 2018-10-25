package com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class FortranMethodsPanel extends JPanel {

    private JCheckBox chkRidgeRegression, chkBayesA;

    public JCheckBox getChkRidgeRegression() {
        return chkRidgeRegression;
    }

    public JCheckBox getChkBayesA() {
        return chkBayesA;
    }

    @Autowired
    public FortranMethodsPanel(SharedInformation sharedInformation) {

        setLayout(null);

        chkRidgeRegression = new JCheckBox("Ridge Regression BLUP");
        chkRidgeRegression.setFont(sharedInformation.getFont());
        chkRidgeRegression.setBounds(10, 10, 600, 25);
        add(chkRidgeRegression);

        chkBayesA = new JCheckBox("Bayes A");
        chkBayesA.setFont(sharedInformation.getFont());
        chkBayesA.setBounds(10, 40, 600, 25);
        add(chkBayesA);
    }

    public void setSelected(boolean value) {
        chkBayesA.setSelected(value);
        chkRidgeRegression.setSelected(value);
    }

}
