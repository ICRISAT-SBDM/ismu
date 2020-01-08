package com.icrisat.sbdm.ismu.ui.openDialog.components.phenotype.bms;

import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SearchPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SubmitPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Trial data display panel.
 */
public class BMSDataSelectionPanel {
    private JDialog dialogBox;
    private SharedInformation sharedInformation;
    private JComboBox<String> cropsCombo;
    private SearchPanel searchPanel;
    private BMSTrialTable bmsTrialTable;
    private SubmitPanel submitPanel;
    private WaitLayerUI layerUI = new WaitLayerUI();

    public BMSDataSelectionPanel(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle("BMS - Trial ");
        dialogBox.setSize(new Dimension(900, 600));
        dialogBox.setLocation(Util.getLocation(900, 600));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        cropsCombo = new JComboBox<>();
        cropsCombo.setFont(sharedInformation.getFont());
        addCrops(cropsCombo);

        cropsCombo.addActionListener(this::getTrialInformation);
        searchPanel = new SearchPanel(sharedInformation);
        searchPanel.searchButton.addActionListener(this::filterData);
        JPanel cropPanel = new JPanel();
        cropPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        addCropsList(cropsCombo, searchPanel, cropPanel);
        mainPanel.add(cropPanel, BorderLayout.NORTH);

        bmsTrialTable = new BMSTrialTable();
        JScrollPane scrollPane = new JScrollPane(bmsTrialTable.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        bmsTrialTable.table.setFillsViewportHeight(true);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        submitPanel = new SubmitPanel(sharedInformation.getOkButtonFont());
        submitPanel.submit.addActionListener(this::getData);
        submitPanel.cancel.addActionListener(e -> dialogBox.setVisible(false));
        mainPanel.add(submitPanel, BorderLayout.SOUTH);

        dialogBox.add(mainPanel);
        dialogBox.add(new JLayer<>(mainPanel, layerUI));
    }

    private void addCropsList(JComboBox<String> cropsCombo, SearchPanel searchPanel, JPanel cropPanel) {
        cropPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        Util.addDummyLabels(cropPanel, 4);
        JLabel selectACrop = new JLabel("Select a crop");
        selectACrop.setFont(sharedInformation.getBoldFont());
        cropPanel.add(selectACrop);
        cropPanel.add(cropsCombo);
        Util.addDummyLabels(cropPanel, 4);
        cropPanel.add(searchPanel);
        Util.addDummyLabels(cropPanel, 4);
    }

    /**
     * Download trial or study data and save in a file in result directory.
     *
     * @param e Action Event.
     */
    private void getData(ActionEvent e) {
        int selectedRow = bmsTrialTable.table.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) bmsTrialTable.table.getModel();
            List selectedData = new ArrayList((Collection) model.getDataVector().elementAt(selectedRow));
            BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
            setEnableForComponents(false);
            SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    String status;
                    if (selectedData.get(7) == null || selectedData.get(7) == "")
                        status = client.getTrialData((String) selectedData.get(1), (String) selectedData.get(6));
                    else
                        status = client.getStudyData((String) selectedData.get(1), (String) selectedData.get(7));
                    if (status.equalsIgnoreCase(Constants.SUCCESS)) {
                        setVisible(false);
                    } else {
                        Util.showMessageDialog("Error: " + status);
                    }
                    layerUI.stop();
                    setEnableForComponents(true);
                    return null;
                }
            };
            worker.execute();
            layerUI.start();
        } else
            Util.showMessageDialog("Please select  a row.");
    }

    private void filterData(ActionEvent e) {
        setEnableForComponents(false);
        String programText = searchPanel.programInputField.getText();
        String trialText = searchPanel.trialInputField.getText();
        String locationText = searchPanel.locationInputField.getText();
        if (Objects.equals(programText, "") && Objects.equals(trialText, "") && Objects.equals(locationText, ""))
            bmsTrialTable.table.setModel(bmsTrialTable.defaultTableModel);
        DefaultTableModel newTableModel = new DefaultTableModel(Constants.bmsHeaders, 0);
        for (Object obj : bmsTrialTable.defaultTableModel.getDataVector()) {
            List<String> row = (List<String>) obj;
            boolean programMatch = false, trialMatch = false, locationMatch = false;
            if (Objects.equals(programText, "")) {
                programMatch = true;
            } else if (containsIgnoreCase(row.get(2), programText)) {
                programMatch = true;
            }
            if (Objects.equals(trialText, "")) {
                trialMatch = true;
            } else if (containsIgnoreCase(row.get(3), trialText)) {
                trialMatch = true;
            }
            if (Objects.equals(locationText, "")) {
                locationMatch = true;
            } else if (containsIgnoreCase(row.get(5), locationText)) {
                locationMatch = true;
            }
            if (programMatch && trialMatch && locationMatch)
                newTableModel.addRow(row.toArray());
        }
        bmsTrialTable.table.setModel(newTableModel);
        setEnableForComponents(true);
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }

    /**
     * Gets the trial information using REST call.
     *
     * @param e Action Event.
     */
    private void getTrialInformation(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String selectedCrop = (String) cb.getSelectedItem();
        setEnableForComponents(false);
        DefaultTableModel model = (DefaultTableModel) bmsTrialTable.defaultTableModel;
        model.setRowCount(0);
        BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
        List<String[]> trialList = new ArrayList<>();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                String triatstatus = client.getTrials(selectedCrop.trim(), trialList);
                sharedInformation.getLogger().info("Fetched List of Triats.");
                if (triatstatus.equalsIgnoreCase(Constants.SUCCESS)) {
                    long serialNo = 1;
                    for (String[] trial : trialList) {
                        String[] newTrial = new String[8];
                        newTrial[0] = String.valueOf(serialNo++);
                        System.arraycopy(trial, 0, newTrial, 1, trial.length);
                        model.addRow(newTrial);
                    }
                    bmsTrialTable.table.setModel(model);
                } else {
                    Util.showMessageDialog("Error: " + triatstatus);
                }
                layerUI.stop();
                setEnableForComponents(true);
                return null;
            }
        };
        worker.execute();
        layerUI.start();
    }

    private void setEnableForComponents(boolean b) {
        cropsCombo.setEnabled(b);
        searchPanel.programInputField.setEnabled(b);
        searchPanel.trialInputField.setEnabled(b);
        searchPanel.locationInputField.setEnabled(b);
        searchPanel.searchButton.setEnabled(b);
        submitPanel.submit.setEnabled(b);
    }

    /**
     * Makes a REST call and gets the list of Crops and add's to the combo box..
     *
     * @param cropsCombo Combo box.
     */
    private void addCrops(JComboBox<String> cropsCombo) {
        BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
        ArrayList<String> crops = new ArrayList<>();
        String status = client.getCrops(crops);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (String crop : crops) {
                cropsCombo.addItem("    " + crop + "    ");
            }
        } else {
            Util.showMessageDialog("Error: " + status);
        }
    }

    public void setVisible(boolean visible) {
        dialogBox.setVisible(visible);
        DefaultTableModel model = (DefaultTableModel) bmsTrialTable.table.getModel();
        model.setRowCount(0);
    }
}
