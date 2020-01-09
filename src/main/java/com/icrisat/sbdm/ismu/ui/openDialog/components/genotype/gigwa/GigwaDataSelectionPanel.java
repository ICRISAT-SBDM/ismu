package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gigwa;

import com.icrisat.sbdm.ismu.retrofit.gigwa.GigwaRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.GenotypeDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GigwaDataSelectionPanel extends GenotypeDataSelectionPanel {

    public GigwaDataSelectionPanel(SharedInformation sharedInformation, GigwaDataSetTable gigwaDataSetTable) {
        super(sharedInformation, "Gigwa - Study ", gigwaDataSetTable);
    }

    protected void startDataExtract(ActionEvent actionEvent) {
        Util.showMessageDialog("Work in progress.");
    }

    @Override
    protected void filterTableData(ActionEvent e) {

    }

    @Override
    protected void resetTableData(ActionEvent e) {

    }

    protected void downloadData(String genotypeFile) {
    }

    @Override
    protected String getDataSets(DefaultTableModel model) {
        model.setRowCount(0);
        GigwaRetrofitClient client = sharedInformation.getGigwaRetrofitClient();
        List<String[]> studiesList = new ArrayList<>();
        String status = client.getStudies(studiesList, sharedInformation);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            status = client.getStudies(studiesList, sharedInformation);
        }
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (String[] study : studiesList) {
                model.addRow(study);
            }
        } else {
            Util.showMessageDialog("Error: " + status);
        }
        return status;
    }
}
