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
            long serialNo = 1;
            for (String[] dataSet : dataSetList) {
                String[] newDataset = new String[5];
                newDataset[0] = String.valueOf(serialNo++);
                System.arraycopy(dataSet, 0, newDataset, 1, dataSet.length);
                model.addRow(newDataset);
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
                    String outputFileName = getBrapiOutputFileName("GOBII");
                    String status = sharedInformation.getGobiiRetrofitClient().downloadData((String) finalSelectedData.get(1), (String) finalSelectedData.get(4), outputFileName);
                    if (status.equals(Constants.SUCCESS)) {
                        sharedInformation.getOpenDialog().getTxtGenotype().setText(outputFileName);
                        dialogBox.setVisible(false);
                    } else {
                        Util.showMessageDialog(status);
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
        //Never used
    }
}
