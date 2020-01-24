package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.ConnectToDB;
import com.icrisat.sbdm.ismu.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class GenoPhenoPanel extends JPanel {

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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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
            lbl.setText("Phenotype file    ");
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
        String status = Util.selectFile("Select a genotype file", Constants.GENO, ae);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (FileLocation file : PathConstants.genotypeFiles) {
                if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                    Util.showMessageDialog("Genotype file selected is already open.");
                    return;
                }
            }
            copyFile(PathConstants.recentGenotypeFile, Constants.GENO);
            setDialogBoxVisibility(false);
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
        }
        setDialogBoxVisibility(false);
    }

    /**
     * When browse button is clicked and the file type is genotype
     * Handles opening a phenotype file locally
     *
     * @param ae action event
     */
    private void phenoBrowseAction(ActionEvent ae) {
        String status = Util.selectFile("Select a phenotype file", Constants.PHENO, ae);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (FileLocation file : PathConstants.phenotypeFiles) {
                if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                    Util.showMessageDialog("Phenotype file selected is already open.");
                    return;
                }
            }
            copyFile(PathConstants.recentPhenotypeFile, Constants.PHENO);
            setDialogBoxVisibility(false);
        } else {
            Util.showMessageDialog(status);
        }
    }

    private void phenoConnectAction(ActionEvent ae) {
        ConnectToDB connectToDB = new ConnectToDB(sharedInformation, Constants.BMS);
        connectToDB.setVisible(true);
  /*      pathConstants.isBrapiCallPheno = true;
        String phenofile = phenoPanel.txtbox.getText();
        if (phenofile.equals("")) return;
        //  addPanelTo(phenofile, Constants.PHENO, true);
        phenotypeDB.setVisible(false);
        dialogBox.setVisible(false);
  */
  /*
   if (isBrapiCall) {
            if (PathConstants.qualitativeTraits.size() > 0) {
                Util.showMessageDialog("ISMU supports only quantitative traits at the moment." +
                        "\nFollowing traits are ignored\n" + PathConstants.qualitativeTraits);
            }
            status = columnSelection.selectColumns(sourceFilePath, PathConstants.noOfHeadersPheno);
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                if (status.equals(Constants.USER_CANCELLED)) return;
                Util.showMessageDialog(status);
                return;
            }
            sourceFilePath = columnSelection.getColumnSelectionPanel().getOutputFileName();
            sourceFileName = new File(sourceFilePath).getName();
            destFileName = sourceFileName;
            destFilePath = sourceFilePath;
        }
   */
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
}
