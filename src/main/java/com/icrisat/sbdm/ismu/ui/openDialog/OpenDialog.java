package com.icrisat.sbdm.ismu.ui.openDialog;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.openDialog.components.GenotypeDB;
import com.icrisat.sbdm.ismu.ui.openDialog.components.PhenotypeDB;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SubmitPanel;
import com.icrisat.sbdm.ismu.util.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.icrisat.sbdm.ismu.util.Util.selectFile;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OpenDialog {

    private SharedInformation sharedInformation;
    private PathConstants pathConstants;
    private JDialog dialogBox;
    private DynamicTree dynamicTree;
    private PhenotypeDB phenotypeDB;
    private GenotypeDB genotypeDB;
    private GenoPhenoPanel genoPanel, phenoPanel;
    private ResultPanel resultPanel;

    @Autowired
    public void setGenotypeDB(GenotypeDB genotypeDB) {
        this.genotypeDB = genotypeDB;
    }

    @Autowired
    public void setPhenotypeDB(PhenotypeDB phenotypeDB) {
        this.phenotypeDB = phenotypeDB;
    }

    @Autowired
    public void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    public void setPathConstants(PathConstants pathConstants) {
        this.pathConstants = pathConstants;
    }

    public JTextField getTxtResultDir() {
        return resultPanel.txtResult;
    }

    public JButton getResultBrowseBtn() {
        return resultPanel.btnResult;
    }

    public JTextField getTxtPhenotype() {
        return phenoPanel.txtbox;
    }

    public JTextField getTxtGenotype() {
        return genoPanel.txtbox;
    }

    public ResultPanel getResultPanel() {
        return resultPanel;
    }

    public GenoPhenoPanel getGenoPanel() {
        return genoPanel;
    }

    public GenoPhenoPanel getPhenoPanel() {
        return phenoPanel;
    }

    /**
     * Set the visibility of Dialog
     *
     * @param value true/false.
     */
    public void setVisible(boolean value) {
        pathConstants.recentGenotypeFile = null;
        pathConstants.recentPhenotypeFile = null;
        dialogBox.setVisible(value);
    }

    /**
     * Create open dialog.
     */
    public void createOpenDialog() {
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setLocation(Util.getLocation(460, 170));
        dialogBox.setSize(new Dimension(460, 170));
        dialogBox.setTitle("Load files");

        createComponents();
        dialogBox.setResizable(false);
        pathConstants.recentGenotypeFile = null;
        pathConstants.recentPhenotypeFile = null;

        dialogBox.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                actionCancel();
            }

            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });
    }

    /**
     * Create the components to be added to dialog box.
     */
    private void createComponents() {
        resultPanel = new ResultPanel(sharedInformation.getFont());
        genoPanel = new GenoPhenoPanel(sharedInformation.getFont(), " Genotype file    ");
        phenoPanel = new GenoPhenoPanel(sharedInformation.getFont(), " Phenotype file  ");

        SubmitPanel submitPanel = new SubmitPanel(sharedInformation.getOkButtonFont());

        if (pathConstants.resultDirectory != null) {
            // As we already know the result directory, disable btnResultDir and enable btnPhenotype, btnPhenoDB, btnGenotype, btnGenoDB
            resultPanel.txtResult.setText(pathConstants.resultDirectory);
            resultPanel.btnResult.setEnabled(false);
            phenoPanel.btnConnect.setEnabled(true);
            phenoPanel.btnBrowse.setEnabled(true);
            genoPanel.btnConnect.setEnabled(true);
            genoPanel.btnBrowse.setEnabled(true);
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 5));
        mainPanel.add(resultPanel);
        mainPanel.add(genoPanel);
        mainPanel.add(phenoPanel);
        mainPanel.add(submitPanel);
        dialogBox.add(mainPanel);

        resultPanel.btnResult.addActionListener(this::actionResultDir);
        phenoPanel.btnBrowse.addActionListener(this::actionPhenoType);
        phenoPanel.btnConnect.addActionListener(this::actionPhenoDB);
        genoPanel.btnBrowse.addActionListener(this::actionGenoType);
        genoPanel.btnConnect.addActionListener(this::actionGenoDB);
        submitPanel.submit.addActionListener(this::actionOk);
        submitPanel.cancel.addActionListener(e -> actionCancel());
    }

    /**
     * Ok button action.
     *
     * @param e Action event.
     */
    private void actionOk(ActionEvent e) {
        String genoFileName = genoPanel.txtbox.getText();
        String phenoFileName = phenoPanel.txtbox.getText();

        // As these files if exist are valid(validated when added) we need to check for emptiness only.
        if (genoFileName.isEmpty() || phenoFileName.isEmpty()) {
            Util.showMessageDialog("Genotype and Phenotype files are mandatory");
            return;
        }

        pathConstants.resultDirectory = pathConstants.tempResultDirectory;
        resultPanel.btnResult.setEnabled(false);
        Logger logger = Util.createLogger(OpenDialog.class,getClass().getResource("/logback.xml"));
        logger.info("Logger Started");
        FileLocation logFile = new FileLocation(Constants.LOG_FILE_NAME, (pathConstants.resultDirectory + Constants.LOG_FILE_NAME));
        if (dynamicTree.getLogNode().getChildCount() == 0)
            dynamicTree.addObject(dynamicTree.getLogNode(), logFile, false);

        boolean genoFileExists = false;
        boolean phenoFileExists = false;
        for (FileLocation file : pathConstants.genotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(genoFileName))
                genoFileExists = true;
        }
        for (FileLocation file : pathConstants.phenotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(phenoFileName))
                phenoFileExists = true;
        }
        // Gets only name of file. Directory path is removed.
        if (genoFileExists && phenoFileExists) {
            Util.showMessageDialog("Genotype & Phenotype  file selected already opened. \nPlease select other files");
            return;
        }
        if (genoFileExists) {
            Util.showMessageDialog("Genotype file selected already opened.");
        } else
            pathConstants.recentGenotypeFile = genoFileName;
        if (phenoFileExists) {
            Util.showMessageDialog("Phenotype file selected already opened.");
        } else
            pathConstants.recentPhenotypeFile = phenoFileName;
        genoPanel.txtbox.setText("");
        phenoPanel.txtbox.setText("");
        dialogBox.setVisible(false);
    }

    /**
     * Genotype button action.
     *
     * @param e action event
     */
    private void actionGenoType(ActionEvent e) {
        selectFile("Select a genotype file", genoPanel.txtbox, Constants.GENO, sharedInformation.getPathConstants(), e);
    }

    /**
     * Phenotype button action.
     *
     * @param e action event
     */
    private void actionPhenoType(ActionEvent e) {
        selectFile("Select a phenotype  file", phenoPanel.txtbox, Constants.PHENO, sharedInformation.getPathConstants(), e);
        pathConstants.isBrapiCallPheno = false;
    }

    /**
     * Result directory button action.
     * Sets the logger.
     *
     * @param e action event
     */
    public void actionResultDir(ActionEvent e) {
        NativeJFileChooser fileChooser = Util.getFolderChooser("Folder to open and save project");
        if (fileChooser.showOpenDialog(sharedInformation.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            if (Files.isDirectory(Paths.get(fileChooser.getSelectedFile().toString()))) {
                Util.setTempResultDir(fileChooser);
            } else {
                // folder not exits
                int option = JOptionPane.showOptionDialog(sharedInformation.getMainFrame(),
                        "Folder does not exits.\nDo you want to create folder for the path",
                        "New Folder Creation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                if (option == JOptionPane.YES_OPTION) {
                    new File(fileChooser.getSelectedFile().toString()).mkdir();
                    Util.setTempResultDir(fileChooser);
                }
            }
        }
    }


    /**
     * PhenoDB directory button action.
     *
     * @param e action event
     */
    private void actionPhenoDB(ActionEvent e) {
        phenotypeDB.setVisible(true);
       // pathConstants.isBrapiCallPheno = true;
    }

    /**
     * GenoDB directory button action.
     *
     * @param e action event
     */

    private void actionGenoDB(ActionEvent e) {
        genotypeDB.setVisible(true);
    }

    /**
     * Cancel directory button action.
     */
    private void actionCancel() {
        pathConstants.recentGenotypeFile = null;
        pathConstants.recentPhenotypeFile = null;
        phenoPanel.txtbox.setText("");
        genoPanel.txtbox.setText("");
        dialogBox.setVisible(false);
    }
}
