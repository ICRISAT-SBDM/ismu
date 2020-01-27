package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners;

import com.icrisat.sbdm.ismu.ui.columnSelection.ColumnSelection;
import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.ConnectToDB;
import com.icrisat.sbdm.ismu.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GenoPhenoPanel {

    private JDialog dialogBox;
    SharedInformation sharedInformation;
    DynamicTree dynamicTree;
    public JButton btnBrowse, btnConnect;
    public JTextField txtbox;
    public JPanel panel;
    boolean useDialogbox;

    //There are cases(New folder) where only the panel is used instead of complete dialog box so e use useDialogbox
    public GenoPhenoPanel(SharedInformation sharedInformation, DynamicTree dynamicTree, int type, boolean useDialogbox) {
        this.sharedInformation = sharedInformation;
        this.dynamicTree = dynamicTree;
        this.useDialogbox = useDialogbox;
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(new Dimension(500, 75));
        dialogBox.setLocation(Util.getLocation(500, 75));
        dialogBox.setResizable(false);

        createJPanel(sharedInformation, type);
        dialogBox.add(panel);
        setDialogBoxVisibility(true);
    }

    private void setDialogBoxVisibility(boolean visibility) {
        /*Use dialogbox is set when the object is created i.e. in the constructor call
         * */
        if (useDialogbox) {
            dialogBox.setVisible(visibility);
        }
    }

    private void createJPanel(SharedInformation sharedInformation, int type) {
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panel.setLayout(new FlowLayout());
        // -----------------------------Geno----------------------------------------

        JLabel lbl = new JLabel();

        txtbox = new JTextField(20);
        txtbox.setEditable(false);
        txtbox.setFont(sharedInformation.getFont());

        btnBrowse = createNonEditableButton("Browse", sharedInformation.getFont());
        btnBrowse.setEnabled(true);
        btnConnect = createNonEditableButton("Connect", sharedInformation.getFont());
        btnConnect.setEnabled(true);

        if (type == Constants.PHENO) {
            dialogBox.setTitle("Phenotype file    ");
            lbl.setText("Phenotype file  ");
            lbl.setFont(sharedInformation.getFont());
            btnBrowse.addActionListener(this::phenoBrowseAction);
            btnConnect.addActionListener(this::phenoConnectAction);
        } else {
            dialogBox.setTitle("Genotype file    ");
            lbl.setText("Genotype file    ");
            lbl.setFont(sharedInformation.getFont());
            btnBrowse.addActionListener(this::genoBrowseAction);
            btnConnect.addActionListener(this::genoConnectAction);
        }

        panel.add(lbl);
        panel.add(txtbox);
        panel.add(btnBrowse);
        panel.add(btnConnect);
    }

    /**
     * Create a non editable button with label.
     *
     * @param btnLabel Label.
     * @return button.
     */
    private JButton createNonEditableButton(String btnLabel, Font font) {
        JButton button = new JButton(btnLabel);
        button.setFont(font);
        button.setEnabled(false);
        return button;
    }

    /**
     * When browse button is clicked and the file type is genotype
     * Handles opening a genotype file locally
     *
     * @param ae action event
     */
    private void genoBrowseAction(ActionEvent ae) {
        PathConstants.recentGenotypeFile = null;
        String status = Util.selectFile("Select a genotype file", Constants.GENO, ae);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            if (PathConstants.recentGenotypeFile != null) {
                for (FileLocation file : PathConstants.genotypeFiles) {
                    if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                        Util.showMessageDialog("Genotype file selected is already open.");
                        return;
                    }
                }
                copyFile(PathConstants.recentGenotypeFile, Constants.GENO);
                setDialogBoxVisibility(false);
            }
        } else {
            Util.showMessageDialog(status);
        }
    }

    /**
     * When connect button is clicked and the file type is genotype
     * Handles connecting to GOBII DB
     *
     * @param ae action event
     */
    private void genoConnectAction(ActionEvent ae) {
        PathConstants.recentGenotypeFile = null;
        new ConnectToDB(sharedInformation, Constants.GOBII);
        // File already in result directory so add it to panel
        if (PathConstants.recentGenotypeFile != null) {
            String sourceFilePath = PathConstants.recentGenotypeFile;
            String sourceFileName = new File(sourceFilePath).getName();
            addToPaneAndTree(Constants.GENO, sourceFileName, sourceFilePath);
            setDialogBoxVisibility(false);
        }
    }

    /**
     * When browse button is clicked and the file type is genotype
     * Handles opening a phenotype file locally
     *
     * @param ae action event
     */
    private void phenoBrowseAction(ActionEvent ae) {
        PathConstants.recentPhenotypeFile = null;
        String status = Util.selectFile("Select a phenotype file", Constants.PHENO, ae);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            if (PathConstants.recentPhenotypeFile != null) {
                for (FileLocation file : PathConstants.phenotypeFiles) {
                    if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                        Util.showMessageDialog("Phenotype file selected is already open.");
                        return;
                    }
                }
                copyFile(PathConstants.recentPhenotypeFile, Constants.PHENO);
                setDialogBoxVisibility(false);
            }
        } else {
            Util.showMessageDialog(status);
        }

    }

    private void phenoConnectAction(ActionEvent ae) {
        PathConstants.recentPhenotypeFile = null;
        new ConnectToDB(sharedInformation, Constants.BMS);
        if (PathConstants.recentPhenotypeFile != null) {
            String sourceFilePath = PathConstants.recentPhenotypeFile;
            List<String> quantitativeHeaders = computeQualitativeTraits(sourceFilePath);
            List<String> selectTraits = selectTraits(quantitativeHeaders);
            if (selectTraits != null) {
                String destFileName = Util.stripFileExtension(new File(sourceFilePath).getName()) + "_selected" + ".csv";
                String destFilePath = PathConstants.resultDirectory + destFileName;
                UtilCSV.createCSVwithRequiredFields(sourceFilePath, destFilePath, selectTraits);
                addToPaneAndTree(Constants.PHENO, destFileName, destFilePath);
            }
            setDialogBoxVisibility(false);
        }
    }

    private List<String> selectTraits(List<String> quantitativeHeaders) {
        ColumnSelection columnSelection = new ColumnSelection();
        columnSelection.createDialog(sharedInformation);
        String status = columnSelection.selectColumns(quantitativeHeaders);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
            return null;
        } else {
            return columnSelection.getSelectedFields();
        }
    }

    /**
     * Find which traits are quantitative and store in path constants
     *
     * @param sourceFilePath source file
     */
    private List<String> computeQualitativeTraits(String sourceFilePath) {
        List<String> quantitativeHeaders = new ArrayList<>();
        ArrayList<String[]> fileMatrix = new ArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath))) {
            String line = reader.readLine();
            String[] headerSplit = line.split(",");
            int noOfColumns = headerSplit.length;
            Boolean[] isQuanlitative = new Boolean[noOfColumns];
            for (int i = 0; i < isQuanlitative.length; i++) isQuanlitative[i] = false;
            while ((line = reader.readLine()) != null)
                fileMatrix.add(line.split(","));
            // First is genotype name
            for (int i = 1; i < noOfColumns; i++) {
                for (String[] row : fileMatrix) {
                    /*
                     * row[i] = ""50.3809694723533""
                     * val = "50.3809694723533"
                     */
                    String val = row[i].substring(1, row[i].length() - 1);
                    if (!val.equalsIgnoreCase("NA")) {
                        try {
                            double d = Double.parseDouble(val);
                        } catch (NumberFormatException nfe) {
                            isQuanlitative[i] = true;
                            break;
                        }
                    }
                }
            }
            List<String> qualitativeTraits = new ArrayList<>();
            for (int i = 1; i < noOfColumns; i++) {
                if (isQuanlitative[i]) {
                    qualitativeTraits.add(headerSplit[i]);
                } else
                    quantitativeHeaders.add(headerSplit[i]);
            }
            if (qualitativeTraits.size() > 0) {
                Util.showMessageDialog("ISMU supports only quantitative traits at the moment." +
                        "\nFollowing traits are ignored\n" + qualitativeTraits);
            }
        } catch (Exception e) {
            System.out.println("Could not open file" + sourceFilePath);
        }
        return quantitativeHeaders;
    }

    /**
     * Copies file to destination directory and adds that file to display panel
     *
     * @param sourceFilePath source filepath
     * @param type           geno/pheno
     */
    private void copyFile(String sourceFilePath, int type) {
        String status;
        String sourceFileName = new File(sourceFilePath).getName();
        String destFileName = Util.stripFileExtension(sourceFileName) + ".csv";
        String destFilePath = PathConstants.resultDirectory + destFileName;
        if (sourceFileName.endsWith(".hmp.txt")) {
            status = Util.processHapMap(sourceFilePath, destFilePath);
        } else
            status = Util.copyFile(sourceFilePath, destFilePath, type);
        //     }
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            addToPaneAndTree(type, destFileName, destFilePath);
        } else {
            Util.showMessageDialog(status);

        }
    }

    /**
     * Adds data to panel and tree
     */
    private void addToPaneAndTree(int type, String destFileName, String destFilePath) {
        String status;//TODO: Loading the file directly into the panel will crash system when file is large.
        if (type == Constants.GENO)
            status = UtilCSV.addCSVToTabbedPanel(destFileName, destFilePath, true);
        else
            status = UtilCSV.addCSVToTabbedPanel(destFileName, destFilePath, false);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            txtbox.setText(destFilePath);
            FileLocation fileLocation = new FileLocation(destFileName, destFilePath);
            if (type == Constants.GENO) {
                PathConstants.genotypeFiles.add(fileLocation);
                dynamicTree.addObject(dynamicTree.getGenotypeNode(), fileLocation, Boolean.TRUE);
                FileLocation summaryFileLocation = new FileLocation(PathConstants.summaryFilesMap.get(destFileName), PathConstants.resultDirectory + PathConstants.summaryFilesMap.get(destFileName));
                dynamicTree.addObject(dynamicTree.getResultsNode(), summaryFileLocation, Boolean.TRUE);
                PathConstants.resultFiles.add(summaryFileLocation);
            }
            if (type == Constants.PHENO) {
                PathConstants.phenotypeFiles.add(fileLocation);
                dynamicTree.addObject(dynamicTree.getPhenotypeNode(), fileLocation, Boolean.TRUE);
            }
        }
    }

    public void setEnabled(boolean value) {
        btnBrowse.setEnabled(value);
        btnConnect.setEnabled(value);
    }
}
