package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.ui.columnSelection.ColumnSelectionPanel;
import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Select files panel. Sub-panel for Analysis.
 * Has select files and Select Required Traits sub-panels.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SelectFilesPanel extends JPanel {
    private JComboBox<String> genoCombo, phenoCombo, covariateCombo;
    private ColumnSelectionPanel columnSelectionPanel;

    JComboBox<String> getGenoCombo() {
        return genoCombo;
    }

    JComboBox<String> getPhenoCombo() {
        return phenoCombo;
    }

    JComboBox<String> getCovariateCombo() {
        return covariateCombo;
    }

    @Autowired
    public SelectFilesPanel(SharedInformation sharedInformation) {
        int width = 200, height = 20;
        setBorder(Util.getCompoundBorder("Select files", sharedInformation));
        setSize(600, 150);
        setLayout(null);

        JLabel genotype_file = Util.createJLabel("Genotype file", sharedInformation);
        genotype_file.setBounds(50, 50, width, height);
        add(genotype_file);

        genoCombo = new JComboBox<>();
        genoCombo.setFont(sharedInformation.getFont());
        genoCombo.setBounds(250, 50, width, height);
        add(genoCombo);

        JLabel phenotype_file = Util.createJLabel("Phenotype file", sharedInformation);
        phenotype_file.setBounds(50, 80, width, height);
        add(phenotype_file);

        phenoCombo = new JComboBox<>();
        phenoCombo.setFont(sharedInformation.getFont());
        phenoCombo.setBounds(250, 80, width, height);
        add(phenoCombo);
        phenoCombo.addActionListener(this::actionPhenoCombo);

        JLabel covariate_file = Util.createJLabel("Covariate file", sharedInformation);
        covariate_file.setBounds(50, 110, width, height);
        add(covariate_file);

        covariateCombo = new JComboBox<>();
        covariateCombo.setFont(sharedInformation.getFont());
        covariateCombo.setBounds(250, 110, width, height);
        add(covariateCombo);
    }

    private void actionPhenoCombo(ActionEvent e) {
        if (phenoCombo.getSelectedItem() != null)
            if (!phenoCombo.getSelectedItem().toString().equalsIgnoreCase(Constants.SELECT)) {
                List<String> headers = UtilCSV.getHeaders(PathConstants.resultDirectory + phenoCombo.getSelectedItem());
                if (headers.size() > 0) {
                    //First header is genotype header not a trait
                    headers.remove(0);
                    columnSelectionPanel.populateAllColumns(headers);
                }
            }
    }

    /**
     * Sets column selection panel.
     *
     * @param columnSelectionPanel Column selection panel.
     */
    void setColumnSelectionPanel(ColumnSelectionPanel columnSelectionPanel) {
        this.columnSelectionPanel = columnSelectionPanel;
    }
}
