package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.germinate;

import com.icrisat.sbdm.ismu.retrofit.germinate.GerminateRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.GenotypeDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GerminateDataSelectionPanel extends GenotypeDataSelectionPanel {

    public GerminateDataSelectionPanel(SharedInformation sharedInformation, GerminateDataSetTable germinateDataSetTable) {
        super(sharedInformation, "Germinate - Study ", germinateDataSetTable);
    }

    /**
     * Gets the dataSet information using REST call.
     */
    @Override
    protected String getDataSets(DefaultTableModel model) {

        model.setRowCount(0);
        GerminateRetrofitClient client = sharedInformation.getGerminateRetrofitClient();
        List<String[]> studiesList = new ArrayList<>();
        String status = client.getStudies(studiesList,sharedInformation);
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

    @Override
    protected void startDataExtract(ActionEvent actionEvent) {
        List finalSelectedData = getSelectedData();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                if (finalSelectedData != null) {
                    GerminateRetrofitClient germinateRetrofitClient = sharedInformation.getGerminateRetrofitClient();
                    List<String> markerProfiles = germinateRetrofitClient.getMarkerProfiles(finalSelectedData);
                    // Last item is status
                    if (markerProfiles.get(markerProfiles.size() - 1).equalsIgnoreCase(Constants.SUCCESS)) {
                        // Remove last item as it is status.
                        markerProfiles.remove(markerProfiles.size() - 1);
                        // There will be only one jobId at this state as we can only select one dataset at a time.
                        List<String> response = germinateRetrofitClient.downloadData(markerProfiles);
                        String jobId = response.get(0);
                        // Wait for the extraction to complete. Then periodically query
                        checkJobStatusNDownload(Constants.GERMINATE_TYPE, jobId);
                    } else {
                        Util.showMessageDialog("Error: " + markerProfiles.get(markerProfiles.size()));
                    }
                }
                layerUI.stop();
                return null;
            }
        };
        worker.execute();
        submitPanel.submit.setEnabled(false);
        layerUI.start();
    }

    @Override
    protected void filterTableData(ActionEvent e) {

    }

    @Override
    protected void resetTableData(ActionEvent e) {

    }

    @Override
    protected void downloadData(String genotypeFile) {
        GerminateRetrofitClient germinateRetrofitClient = sharedInformation.getGerminateRetrofitClient();
        String outputFileName = getBrapiOutputFileName("Germinate");
        String status = germinateRetrofitClient.downloadData(genotypeFile, outputFileName);
        if (status.equals(Constants.SUCCESS)) {
            sharedInformation.getOpenDialog().getTxtGenotype().setText(outputFileName);
            dialogBox.setVisible(false);
        } else {
            Util.showMessageDialog(status);
        }
    }
}
