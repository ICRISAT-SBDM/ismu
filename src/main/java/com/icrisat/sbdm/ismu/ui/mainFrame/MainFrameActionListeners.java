package com.icrisat.sbdm.ismu.ui.mainFrame;

import com.icrisat.sbdm.ismu.ui.analysis.Analysis;
import com.icrisat.sbdm.ismu.ui.components.ColumnSelection;
import com.icrisat.sbdm.ismu.ui.dataSummary.DataSummary;
import com.icrisat.sbdm.ismu.ui.openDialog.GenoPhenoPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.OpenDialog;
import com.icrisat.sbdm.ismu.ui.openDialog.components.GenotypeDB;
import com.icrisat.sbdm.ismu.ui.openDialog.components.PhenotypeDB;
import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class MainFrameActionListeners {

    private SharedInformation sharedInformation;
    private PathConstants pathConstants;
    private OpenDialog openDialog;
    private DynamicTree dynamicTree;
    private DataSummary dataSummary;
    private Analysis analysis;
    private ColumnSelection columnSelection;
    private Project project;
    private PdfConverter pdfConverter;
    private PhenotypeDB phenotypeDB;
    private GenotypeDB genotypeDB;

    @Autowired
    public void setGenotypeDB(GenotypeDB genotypeDB) {
        this.genotypeDB = genotypeDB;
    }

    @Autowired
    public void setPhenotypeDB(PhenotypeDB phenotypeDB) {
        this.phenotypeDB = phenotypeDB;
    }

    @Autowired
    public void setPdfConverter(PdfConverter pdfConverter) {
        this.pdfConverter = pdfConverter;
    }

    @Autowired
    public void setProject(Project project) {
        this.project = project;
    }

    @Autowired
    public void setColumnSelection(ColumnSelection columnSelection) {
        this.columnSelection = columnSelection;
    }

    @Autowired
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    public void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    @Autowired
    public void setOpenDialog(OpenDialog openDialog) {
        this.openDialog = openDialog;
    }

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    public void setPathConstants(PathConstants pathConstants) {
        this.pathConstants = pathConstants;
    }

    @Autowired
    public void setDataSummary(DataSummary dataSummary) {
        this.dataSummary = dataSummary;
    }

    /**
     * Open button action item.
     *
     * @param e Action event
     */
    void openActionItem(ActionEvent e) {
        openDialog.setVisible(true);
        if (pathConstants.resultDirectory != null) {
            if (pathConstants.recentGenotypeFile != null) {
                String sourceFilePath = pathConstants.recentGenotypeFile;
                addPanelTo(sourceFilePath, Constants.GENO, false);
            }
            if (pathConstants.recentPhenotypeFile != null) {
                String sourceFilePath = pathConstants.recentPhenotypeFile;
                addPanelTo(sourceFilePath, Constants.PHENO, pathConstants.isBrapiCallPheno);
            }
        }

        try {
            String cssPath = Util.getJarDirectory(this.getClass());
            cssPath = cssPath + "/doc/" + Constants.CSS;
            FileChannel inputFileChannel = new FileInputStream(new File(cssPath)).getChannel();
            FileChannel outputFileChannel = new FileOutputStream(new File(pathConstants.resultDirectory + Constants.CSS)).getChannel();
            outputFileChannel.transferFrom(inputFileChannel, 0, inputFileChannel.size());
            inputFileChannel.close();
            outputFileChannel.close();
        } catch (Exception ex) {
            sharedInformation.getLogger().error("ISSUE IN COPYING CSS.");
        }
    }

    /**
     * Action item to select a Genotype file.
     *
     * @param e Action Event
     */
    void selectGenotypeFileActionItem(ActionEvent e) {
        if (pathConstants.resultDirectory == null || pathConstants.isFirstGenoFile) {
            Util.showMessageDialog("For first time use, use Open button to choose files.");
        } else {
            JDialog dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
            dialogBox.setSize(new Dimension(500, 75));
            dialogBox.setLocation(Util.getLocation(500, 75));
            dialogBox.setTitle("Genotype File");
            GenoPhenoPanel genoPanel = new GenoPhenoPanel(sharedInformation.getFont(), "Genotype file    ");
            genoPanel.btnBrowse.setEnabled(true);
            genoPanel.btnConnect.setEnabled(true);
            genoPanel.btnBrowse.addActionListener(ae -> genoBrowseAction(ae, dialogBox));
            genoPanel.btnConnect.addActionListener(ae -> genoConnectAction(ae, dialogBox));
            genoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            dialogBox.add(genoPanel);
            dialogBox.setResizable(false);
            pathConstants.recentGenotypeFile = null;
            pathConstants.recentPhenotypeFile = null;
            dialogBox.setVisible(true);
        }
    }

    private void genoConnectAction(ActionEvent ae, JDialog dialogBox) {
        genotypeDB.setVisible(true);
        String genofile = sharedInformation.getOpenDialog().getTxtGenotype().getText();
        addPanelTo(genofile, Constants.GENO, false);
        genotypeDB.setVisible(false);
        dialogBox.setVisible(false);
    }

    private void genoBrowseAction(ActionEvent ae, JDialog dialogBox) {
        JTextField textField = new JTextField();
        Util.selectFile("Select a genotype file", textField, Constants.GENO, sharedInformation.getPathConstants(), ae);
        if (textField.getText().equals("")) {
            dialogBox.setVisible(false);
            return;
        }
        for (FileLocation file : pathConstants.genotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(textField.getText())) {
                Util.showMessageDialog("Genotype file selected is already open.");
                dialogBox.setVisible(false);
                return;
            }
        }
        pathConstants.recentGenotypeFile = textField.getText();
        addPanelTo(pathConstants.recentGenotypeFile, Constants.GENO, false);
        dialogBox.setVisible(false);

    }

    /**
     * Action item for Phenotype file.
     *
     * @param e Action Event
     */
    void selectPhenotypeFileActionItem(ActionEvent e) {
        if (pathConstants.resultDirectory == null || pathConstants.isFirstPhenoFile) {
            Util.showMessageDialog("For first time use, use Open button to choose files.");
        } else {
            JDialog dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
            dialogBox.setSize(new Dimension(500, 75));
            dialogBox.setLocation(Util.getLocation(500, 75));
            dialogBox.setTitle("Phenotype File");
            GenoPhenoPanel phenoPanel = new GenoPhenoPanel(sharedInformation.getFont(), "Phenotype file    ");
            phenoPanel.btnBrowse.setEnabled(true);
            phenoPanel.btnConnect.setEnabled(true);
            phenoPanel.btnBrowse.addActionListener(ae -> phenoBrowseAction(ae, dialogBox));
            phenoPanel.btnConnect.addActionListener(ae -> phenoConnectAction(ae, dialogBox));
            phenoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            dialogBox.add(phenoPanel);
            dialogBox.setResizable(false);
            pathConstants.recentGenotypeFile = null;
            pathConstants.recentPhenotypeFile = null;
            dialogBox.setVisible(true);
        }
    }

    private void phenoConnectAction(ActionEvent ae, JDialog dialogBox) {
        phenotypeDB.setVisible(true);
//        pathConstants.isBrapiCallPheno = true;
        OpenDialog openDialog = sharedInformation.getOpenDialog();
        String phenofile = openDialog.getTxtPhenotype().getText();
        if (phenofile.equals("")) return;
        addPanelTo(phenofile, Constants.PHENO, true);
        phenotypeDB.setVisible(false);
        dialogBox.setVisible(false);
    }


    private void phenoBrowseAction(ActionEvent ae, JDialog dialogBox) {
        JTextField textField = new JTextField();
        Util.selectFile("Select a phenotype file", textField, Constants.PHENO, sharedInformation.getPathConstants(), ae);
        if (textField.getText().equals("")) {
            dialogBox.setVisible(false);
            return;
        }
        for (FileLocation file : pathConstants.phenotypeFiles) {
            if (file.getFileLocationOnDisk().equalsIgnoreCase(textField.getText())) {
                Util.showMessageDialog("Phenotype file selected is already open.");
                dialogBox.setVisible(false);
                return;
            }
        }
        pathConstants.recentPhenotypeFile = textField.getText();
        addPanelTo(pathConstants.recentPhenotypeFile, Constants.PHENO, false);
        dialogBox.setVisible(false);
    }

    /**
     * Data summary action item.
     *
     * @param e Action Event
     */
    void dataSummaryActionItem(ActionEvent e) {
        if (pathConstants.genotypeFiles.size() > 0 || pathConstants.phenotypeFiles.size() > 0) {
            dataSummary.setVisible(true);
        } else {
            Util.showMessageDialog("Import at least one Genotype and Phenotype files");
        }
    }

    /**
     * Analysis action item.
     *
     * @param e Action Event
     */
    void analysisActionItem(ActionEvent e) {
        if (pathConstants.genotypeFiles.size() > 0 && pathConstants.phenotypeFiles.size() > 0) {
            analysis.setVisible();
        } else {
            Util.showMessageDialog("Import at least one Genotype and Phenotype files");
        }
    }

    /**
     * Saves current project
     *
     * @param e Action Event
     */
    void saveProjectActionItem(ActionEvent e) {
        String status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS))
            Util.showMessageDialog("Project saved successfully");
        else
            Util.showMessageDialog(status);
    }

    /**
     * Opens a saved project.
     * To handle switching the projects, user is prompted to save current project
     *
     * @param e Action Event
     */
    void openProjectActionItem(ActionEvent e) {
        String status;
        status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS)) status = project.openProject(e);
        else {
            int choice = JOptionPane.showConfirmDialog((java.awt.Component) e.getSource(), status + "\n" + "Do you want to continue without saving current project?",
                    "Open project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) status = project.openProject(e);
            else return;
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS))
            Util.showMessageDialog(status);
    }

    /**
     * Opens a new project.
     * To handle switching the projects, user is prompted to save current project
     *
     * @param e Action event
     */
    void newProjectActionItem(ActionEvent e) {
        String status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS)) project.newProject(e);
        else {
            int choice = JOptionPane.showConfirmDialog(sharedInformation.getMainFrame(), status + "\n" + "Do you want to continue without saving current project?",
                    "New project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) project.newProject(e);
        }
    }

    /**
     * Exit the application
     *
     * @param e Action Event
     */
    void exitApplicationActionItem(ActionEvent e) {
        if (Util.closeApplication(e, sharedInformation, project)) sharedInformation.getMainFrame().dispose();
    }

    /**
     * open save dialog box in which we can specify required file name and directory.
     * if file extension is  html then it convert to pdf
     * if file file extension is .csv then path to the file is displayed.
     * Example http://www.baeldung.com/pdf-conversions-java
     */
    void saveAsActionItem(ActionEvent e) {
        ClosableTabbedPane tabbedPane = sharedInformation.getTabbedPane();
        String selectedFile = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        selectedFile = selectedFile.trim();
        String filePath = getFilePath(selectedFile);
        if (filePath != null) {
            NativeJFileChooser pdfFileChooser = Util.getFolderChooser("Select a folder to save the file");
            if (pdfFileChooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
                String folder = pdfFileChooser.getSelectedFile().toString();
                String savedFileName = folder + "/" + Util.stripFileExtension(selectedFile) + ".pdf";
                pdfConverter.convertToPdf(e, filePath, savedFileName, sharedInformation);
            }
        } /*else if (ext.equalsIgnoreCase(Constants.CSV)) {
            NativeJFileChooser csvFileChooser = Util.getFolderChooser("Select a folder to save the file", "CSV");
            if (csvFileChooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
                String savedFileName = csvFileChooser.getSelectedFile().toString();
                if (!savedFileName.endsWith(Constants.CSV)) savedFileName = savedFileName + ".csv";
                List<FileLocation> genotypeFiles = sharedInformation.getPathConstants().genotypeFiles;
                List<FileLocation> phenotypeFiles = sharedInformation.getPathConstants().phenotypeFiles;
                List<FileLocation> resultFiles = sharedInformation.getPathConstants().resultFiles;
                String status = "";
                for (FileLocation file : genotypeFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }
                for (FileLocation file : phenotypeFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }

                for (FileLocation file : resultFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }
                if (status.equalsIgnoreCase(Constants.SUCCESS))
                    JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "File saved in location \n"
                            + savedFileName);
                else
                    JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), status);
            }
        } */ else {
            Util.showMessageDialog("Only HTML files can be saved to PDF.");
        }
    }

    private String getFilePath(String selectedFile) {
        if (sharedInformation.getPathConstants().resultDirectory == null) return null;
        try {
            DirectoryStream<Path> htm = Files.newDirectoryStream(Paths.get(sharedInformation.getPathConstants().resultDirectory), "*.htm");
            for (Path entry : htm) {
                if (entry.getFileName().toString().equalsIgnoreCase(selectedFile))
                    return entry.toString();
            }
        } catch (IOException e) {
            return null;

        }
        return null;
    }

    private void addPanelTo(String sourceFilePath, int type, boolean isBrapiCall) {
        String status = Constants.SUCCESS;
        String sourceFileName = new File(sourceFilePath).getName();
        String destFileName = Util.stripFileExtension(sourceFileName) + ".csv";
        String destFilePath = pathConstants.resultDirectory + destFileName;

        if (isBrapiCall) {
            if (pathConstants.qualitativeTraits.size() > 0) {
                Util.showMessageDialog("ISMU supports only quantitative traits at the moment." +
                        "\nFollowing traits are ignored\n" + pathConstants.qualitativeTraits);
            }
            status = columnSelection.selectColumns(sourceFilePath, pathConstants.noOfHeadersPheno);
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                if (status.equals(Constants.USER_CANCELLED)) return;
                Util.showMessageDialog(status);
                return;
            }
            sourceFilePath = columnSelection.getColumnSelectionPanel().getOutputFileName();
            sourceFileName = new File(sourceFilePath).getName();
            destFileName = sourceFileName;
            destFilePath = sourceFilePath;
        } else {
            if (sourceFileName.endsWith(".hmp.txt")) {
                status = Util.processHapMap(sourceFilePath, destFilePath);
            } else
                status = Util.copyFile(sourceFilePath, destFilePath, type);
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
            return;
        }

        JPanel csvPanel = new JPanel(new BorderLayout());
        csvPanel.setBounds(0, 0, 100, 100);
        Util.setJPanelName(csvPanel, destFileName);
        //TODO: Loading the file directly into the panel will crash system when file is large.
        if (type == Constants.GENO)
            status = UtilCSV.csvReader(destFilePath, csvPanel, true);
        else
            status = UtilCSV.csvReader(destFilePath, csvPanel, false);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        } else {
            sharedInformation.getTabbedPane().add(csvPanel);
            sharedInformation.getTabbedPane().setSelectedIndex(sharedInformation.getTabbedPane().getTabCount() - 1);
            FileLocation fileLocation = new FileLocation(destFileName, sourceFilePath);
            if (type == Constants.GENO) {
                pathConstants.genotypeFiles.add(fileLocation);
                dynamicTree.addObject(dynamicTree.getGenotypeNode(), fileLocation, Boolean.TRUE);
                FileLocation summaryFileLocation = new FileLocation(pathConstants.summaryFilesMap.get(destFileName), pathConstants.resultDirectory + pathConstants.summaryFilesMap.get(destFileName));
                dynamicTree.addObject(dynamicTree.getResultsNode(), summaryFileLocation, Boolean.TRUE);
                pathConstants.resultFiles.add(summaryFileLocation);
                pathConstants.isFirstGenoFile = false;
            }
            if (type == Constants.PHENO) {
                pathConstants.phenotypeFiles.add(fileLocation);
                dynamicTree.addObject(dynamicTree.getPhenotypeNode(), fileLocation, Boolean.TRUE);
                pathConstants.isFirstPhenoFile = false;
            }
        }
    }
}

