package com.icrisat.sbdm.ismu.ui.mainFrame.ActionalListeners;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
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

    public GenoPhenoPanel(SharedInformation sharedInformation, DynamicTree dynamicTree, int type) {
        this.sharedInformation = sharedInformation;
        this.dynamicTree = dynamicTree;

        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(new Dimension(500, 75));
        dialogBox.setLocation(Util.getLocation(500, 75));
        dialogBox.setResizable(false);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.setLayout(new FlowLayout());
        // -----------------------------Geno----------------------------------------

        JLabel lbl = new JLabel();

        txtbox = new JTextField(20);
        txtbox.setEditable(false);
        txtbox.setFont(sharedInformation.getFont());

        btnBrowse = createNonEditableButton("Browse", sharedInformation.getFont());
        btnBrowse.setEnabled(true);

        if (type == Constants.PHENO) {
            dialogBox.setTitle("Phenotype file    ");
            lbl.setText("Phenotype file    ");
            lbl.setFont(sharedInformation.getFont());
            btnBrowse.addActionListener(this::phenoBrowseAction);
        } else {
            dialogBox.setTitle("Genotype file    ");
            lbl.setText("Genotype file    ");
            lbl.setFont(sharedInformation.getFont());
            btnBrowse.addActionListener(this::genoBrowseAction);
        }
        btnConnect = createNonEditableButton("Connect", sharedInformation.getFont());
        //      genoPanel.btnConnect.addActionListener(ae -> genoConnectAction(ae, genoPanel, dialogBox));

        panel.add(lbl);
        panel.add(txtbox);
        panel.add(btnBrowse);
        panel.add(btnConnect);
        dialogBox.add(panel);
        dialogBox.setVisible(true);
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

    private void genoBrowseAction(ActionEvent ae) {
        Util.selectFile("Select a genotype file", Constants.GENO, ae);
        for (FileLocation file : PathConstants.genotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                Util.showMessageDialog("Genotype file selected is already open.");
                return;
            }
        }
        copyFile(PathConstants.recentGenotypeFile, Constants.GENO, false);
        dialogBox.setVisible(false);
    }

    private void phenoBrowseAction(ActionEvent ae) {
        Util.selectFile("Select a phenotype file", Constants.PHENO, ae);
        for (FileLocation file : PathConstants.phenotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(PathConstants.recentPhenotypeFile)) {
                Util.showMessageDialog("Phenotype file selected is already open.");
                return;
            }
        }
        copyFile(PathConstants.recentPhenotypeFile, Constants.PHENO, false);
        dialogBox.setVisible(false);
    }

    /**
     * Copies file to destination directory and adds that file to display panel
     *
     * @param sourceFilePath source filepath
     * @param type           geno/pheno
     * @param isBrapiCall    is data received from a brapi call
     */
    private void copyFile(String sourceFilePath, int type, boolean isBrapiCall) {
        String status;
        String sourceFileName = new File(sourceFilePath).getName();
        String destFileName = Util.stripFileExtension(sourceFileName) + ".csv";
        String destFilePath = PathConstants.resultDirectory + destFileName;

   /*     if (isBrapiCall) {
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
        } else {*/
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
