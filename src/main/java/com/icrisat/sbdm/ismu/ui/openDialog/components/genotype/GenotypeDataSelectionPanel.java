package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype;

import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SubmitPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public abstract class GenotypeDataSelectionPanel {
    protected JDialog dialogBox;
    protected SharedInformation sharedInformation;
    protected GenotypeDataSetTable genotypeDataSetTable;
    protected SubmitPanel submitPanel;
    protected WaitLayerUI layerUI = new WaitLayerUI();

    public GenotypeDataSelectionPanel(SharedInformation sharedInformation, String title, GenotypeDataSetTable dataSetTable) {
        this.sharedInformation = sharedInformation;
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle(title);
        dialogBox.setSize(new Dimension(900, 500));
        dialogBox.setLocation(Util.getLocation(900, 500));

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        genotypeDataSetTable = dataSetTable;
        String status = getDataSets((DefaultTableModel) genotypeDataSetTable.table.getModel());
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            sharedInformation.getGenotypeURLPanel().setVisible(false);
            JScrollPane scrollPane = new JScrollPane(genotypeDataSetTable.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            genotypeDataSetTable.table.setFillsViewportHeight(true);
            dataPanel.add(scrollPane, BorderLayout.CENTER);

            submitPanel = new SubmitPanel(sharedInformation.getBoldFont());
            submitPanel.submit.addActionListener(this::startDataExtract);
            submitPanel.cancel.addActionListener(e -> {
                layerUI.stop();
                dialogBox.setVisible(false);
            });
            submitPanel.submit.setEnabled(true);
            submitPanel.cancel.setEnabled(true);

            dataPanel.add(submitPanel, BorderLayout.SOUTH);
            dialogBox.add(new JLayer<>(dataPanel, layerUI));

            dialogBox.setVisible(true);
        } else {
            Util.showMessageDialog("Error: " + status);
        }
    }

    protected abstract void downloadData(String s);

    protected abstract String getDataSets(DefaultTableModel model);

    protected abstract void startDataExtract(ActionEvent actionEvent);

    /**
     * Takes jobId and queries until completion or failure
     *
     * @param clientType GOBII, Germinate etc.
     * @param jobId      jobId
     */
    protected void checkJobStatusNDownload(int clientType, String jobId) {
        try {
            boolean proceed = true;
            while (proceed) {
                Thread.sleep(5000);
                List<String> extractStatus = null;
                if (clientType == Constants.GOBII_TYPE)
                    extractStatus = sharedInformation.getGobiiRetrofitClient().getExtractStatus(jobId);
                else if (clientType == Constants.GERMINATE_TYPE)
                    extractStatus = sharedInformation.getGerminateRetrofitClient().getExtractStatus(jobId);
                else if (clientType == Constants.GIGWA_TYPE) {/*extractStatus = germinateRetrofitClient.getExtractStatus(jobId);*/}

                String status = extractStatus.get(extractStatus.size() - 1);
                if (extractStatus.size() > 1) {
                    downloadData(extractStatus.get(0));
                    proceed = false;
                } else if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                    Util.showMessageDialog("Error: could not download the data-set. " + status);
                    proceed = false;
                }
            }
        } catch (InterruptedException e1) {
            Util.showMessageDialog("Error: could not download the data-set.\n " + e1.getMessage());
        }
    }

    /**
     * Brapi output file name. Used for BMS, GOBII, Germinate etc.
     *
     * @param name Takes values like "BMS", "GERMINATE"
     * @return filename
     */
    protected String getBrapiOutputFileName(String name) {
        String outputFileName;
        if (sharedInformation.getPathConstants().resultDirectory == null)
            outputFileName = sharedInformation.getPathConstants().tempResultDirectory + name + "_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        else
            outputFileName = sharedInformation.getPathConstants().resultDirectory + name + "_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        return outputFileName;
    }

    protected List getSelectedData() {
        List selectedData = null;
        int selectedRow = genotypeDataSetTable.table.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) genotypeDataSetTable.table.getModel();
            selectedData = new ArrayList((Collection) model.getDataVector().elementAt(selectedRow));
        } else {
            Util.showMessageDialog("Please select  a row.");
        }
        return selectedData;
    }
}
