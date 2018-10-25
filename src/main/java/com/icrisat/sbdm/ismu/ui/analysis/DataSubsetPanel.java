package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class DataSubsetPanel extends JPanel {

    private JSpinner percentSpinner, picSpinner, mafSpinner;

    public JSpinner getPercentSpinner() {
        return percentSpinner;
    }

    public JSpinner getPicSpinner() {
        return picSpinner;
    }

    public JSpinner getMafSpinner() {
        return mafSpinner;
    }

    @Autowired
    public DataSubsetPanel(SharedInformation sharedInformation) {
        int lblWidth = 270, width = 70, height = 30;
        setBorder(Util.getCompoundBorder("Data subset", sharedInformation));
        setLayout(null);

        JLabel percent = Util.createJLabel("Percentage(%) of missing markers", sharedInformation);
        percent.setBounds(20, 30, lblWidth, height);
        add(percent);

        percentSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
        percentSpinner.setBounds(270, 30, width, height);
        add(percentSpinner);

        JLabel picValue = Util.createJLabel("PIC value", sharedInformation);
        picValue.setBounds(20, 70, lblWidth, height);
        add(picValue);
        picSpinner = new JSpinner(new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 0.1d));
        picSpinner.setBounds(270, 70, width, height);
        add(picSpinner);

        JLabel maf = Util.createJLabel("Minor Allele Frequency(MAF)", sharedInformation);
        maf.setBounds(20, 110, lblWidth, height);
        add(maf);
        mafSpinner = new JSpinner(new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 0.01d));
        mafSpinner.setBounds(270, 110, width, height);
        add(mafSpinner);
    }

    public void resetValues() {
        mafSpinner.getModel().setValue(0.0d);
        picSpinner.getModel().setValue(0.0d);
        percentSpinner.getModel().setValue(10);
    }
}
