package com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class RMethodsPanel extends JPanel {

    private JCheckBox chkRidgeRegression, chkBayesCPI, chkBayesB, chkBayesLasso, chkRandomForest, chkKinshipGauss;

    public JCheckBox getChkRidgeRegression() {
        return chkRidgeRegression;
    }

    public JCheckBox getChkBayesCPI() {
        return chkBayesCPI;
    }

    public JCheckBox getChkBayesB() {
        return chkBayesB;
    }

    public JCheckBox getChkBayesLasso() {
        return chkBayesLasso;
    }

    public JCheckBox getChkRandomForest() {
        return chkRandomForest;
    }

    public JCheckBox getChkKinshipGauss() {
        return chkKinshipGauss;
    }

    @Autowired
    public RMethodsPanel(SharedInformation sharedInformation) {

        setSize(600, 150);
        setLayout(new GridLayout(6, 1));

        chkRidgeRegression = new JCheckBox("Ridge Regression BLUP");
        chkRidgeRegression.setFont(sharedInformation.getFont());
        add(chkRidgeRegression);

        chkKinshipGauss = new JCheckBox("Kinship Gauss");
        chkKinshipGauss.setFont(sharedInformation.getFont());
        add(chkKinshipGauss);

        chkBayesB = new JCheckBox("Bayes B");
        chkBayesB.setFont(sharedInformation.getFont());
        add(chkBayesB);

        chkBayesCPI = new JCheckBox("Bayes Cpi");
        chkBayesCPI.setFont(sharedInformation.getFont());
        add(chkBayesCPI);

        chkBayesLasso = new JCheckBox("Bayes LASSO");
        chkBayesLasso.setFont(sharedInformation.getFont());
        add(chkBayesLasso);

        chkRandomForest = new JCheckBox("Random Forest");
        chkRandomForest.setFont(sharedInformation.getFont());
        add(chkRandomForest);

    }

    public void setSelected(boolean value) {
        chkBayesB.setSelected(value);
        chkBayesCPI.setSelected(value);
        chkBayesLasso.setSelected(value);
        chkKinshipGauss.setSelected(value);
        chkRandomForest.setSelected(value);
        chkRidgeRegression.setSelected(value);
    }
}
