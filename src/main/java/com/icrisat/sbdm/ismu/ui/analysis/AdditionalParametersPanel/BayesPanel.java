package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Bayes panel. Sub-panel for Analysis.
 */
@Component
public class BayesPanel extends JPanel {
    private JSpinner roundsSpinner, burninSpinner, thinningSpinner;

    public JSpinner getRoundsSpinner() {
        return roundsSpinner;
    }

    public JSpinner getBurninSpinner() {
        return burninSpinner;
    }

    public JSpinner getThinningSpinner() {
        return thinningSpinner;
    }

    @Autowired
    public BayesPanel(SharedInformation sharedInformation) {
        int width = 80, height = 30;
        setBorder(Util.getCompoundBorder("Bayes", sharedInformation));
        setLayout(null);

        JLabel rounds = Util.createJLabel("Rounds", sharedInformation);
        rounds.setBounds(20, 40, width, height);
        add(rounds);

        roundsSpinner = new JSpinner(new SpinnerNumberModel(1000, 1, 10000000, 1));
        roundsSpinner.setBounds(120, 40, width, height);
        add(roundsSpinner);

        JLabel burning = Util.createJLabel("Burning", sharedInformation);
        burning.setBounds(20, 80, width, height);
        add(burning);
        burninSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000000, 1));
        burninSpinner.setBounds(120, 80, width, height);
        add(burninSpinner);

        JLabel thinning = Util.createJLabel("Thinning", sharedInformation);
        thinning.setBounds(20, 120, width, height);
        add(thinning);
        thinningSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10000000, 1));
        thinningSpinner.setBounds(120, 120, width, height);
        add(thinningSpinner);
    }

    public void enableComponent(boolean value) {
        roundsSpinner.setEnabled(value);
        burninSpinner.setEnabled(value);
        thinningSpinner.setEnabled(value);
        roundsSpinner.getModel().setValue(1000);
        burninSpinner.getModel().setValue(100);
        thinningSpinner.getModel().setValue(5);

    }

}
