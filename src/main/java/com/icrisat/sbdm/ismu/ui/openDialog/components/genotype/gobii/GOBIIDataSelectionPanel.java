package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.GenotypeDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GOBIIDataSelectionPanel extends GenotypeDataSelectionPanel {

    public GOBIIDataSelectionPanel(SharedInformation sharedInformation, GOBIIDataSetTable gobiiDataSetTable) {
        super(sharedInformation, "GOBII - Dataset ", gobiiDataSetTable);
    }

    /**
     * Gets the dataSet information using REST call.
     */
    @Override
    protected String getDataSets(DefaultTableModel model) {
        model.setRowCount(0);
        GOBIIRetrofitClient client = sharedInformation.getGobiiRetrofitClient();
        List<String[]> dataSetList = new ArrayList<>();
        String status = client.getVariantSets(dataSetList);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            // status = client.getDataSets(dataSetList);
            status = client.getVariantSets(dataSetList);
        }
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (String[] dataSet : dataSetList) {
                model.addRow(dataSet);
            }
        }
        return status;
    }

    /**
     * Download trial or study data and save in a file in result directory.
     *
     * @param e Action Event.
     */
    protected void startDataExtract(ActionEvent e) {
        List finalSelectedData = getSelectedData();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                if (finalSelectedData != null) {
                    GOBIIRetrofitClient gobiiRetrofitClient = sharedInformation.getGobiiRetrofitClient();
                    String outputFileName = getBrapiOutputFileName("GOBII");
                    List<String> response = gobiiRetrofitClient.downloadData(finalSelectedData,outputFileName);
                    // Last item is status
                    if (response.get(response.size() - 1).equalsIgnoreCase(Constants.SUCCESS)) {
                        // Remove last item as it is status.
                        response.remove(response.size() - 1);
                        //There will be only one jobId at this state as we can only select one dataset at a time.
                        String jobId = response.get(0);
                        // Wait for the extraction to complete. Then periodically query
                        checkJobStatusNDownload(Constants.GOBII_TYPE, jobId);
                    } else {
                        Util.showMessageDialog("Error: " + response.get(response.size()));
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
    protected void downloadData(String genotypeFile) {
        GOBIIRetrofitClient gobiiRetrofitClient = sharedInformation.getGobiiRetrofitClient();
        String outputFileName = getBrapiOutputFileName("GOBII");
        String status = gobiiRetrofitClient.downloadData(genotypeFile, outputFileName);
        if (status.equals(Constants.SUCCESS)) {
            sharedInformation.getOpenDialog().getTxtGenotype().setText(outputFileName);
            dialogBox.setVisible(false);
        } else {
            Util.showMessageDialog(status);
        }
    }
}
