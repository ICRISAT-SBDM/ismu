package com.icrisat.sbdm.ismu.ui.dataSummary;

import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SubmitPanel;
import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataSummary {
    private final String SELECTED = "1";
    private JDialog dialogBox;
    private SharedInformation sharedInformation;
    private JComboBox<String> genoCombo, phenoCombo;
    private JCheckBox chkMissingPercent, chkPICValue, chkMaf, chkDataSummary;
    private JButton btnStart;
    private DataSummaryDataObject dataSummaryDataObject;
    private DataSummaryUtil dataSummaryUtil;
    private DynamicTree dynamicTree;
    private Process processGeno, processPheno;
    private String genoSummaryStatus, phenoSummaryStatus;
    private WaitLayerUI layerUI = new WaitLayerUI();

    @Autowired
    public void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    @Autowired
    public void setDataSummaryUtil(DataSummaryUtil dataSummaryUtil) {
        this.dataSummaryUtil = dataSummaryUtil;
    }

    @Autowired
    public void setDataSummaryDataObject(DataSummaryDataObject dataSummaryDataObject) {
        this.dataSummaryDataObject = dataSummaryDataObject;
    }

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    /**
     * Set the visibility of Dialog
     *
     * @param value true/false.
     */
    public void setVisible(boolean value) {
        Util.fillGenoAndPhenoFiles(genoCombo, phenoCombo);
        setEnableForAllComponents(true);
        dialogBox.setVisible(value);
    }

    /**
     * Creates dialog box.
     */
    public void createDialog() {
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(500, 200);
        dialogBox.setLocation(Util.getLocation(500, 220));
        dialogBox.setTitle("Data Summary");
        createAndAddComponents();
        dialogBox.setResizable(false);

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
     * When cancel is clicked.
     */
    private void actionCancel() {
        layerUI.stop();
        setEnableForAllComponents(false);
        dialogBox.setVisible(false);

    /*
    // THis code will be used later. Probably with Java 9 :)
        if (processGeno.isAlive()) {
            try {
                String line;
                Process p = Runtime.getRuntime().exec
                        (System.getenv("windir") + "\\system32\\" + "tasklist.exe /v");
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    if (line.contains("Rscript.exe"))
                        System.out.println(line); //<-- Parse data here.

                }
                input.close();
               List<ProcessInfo> processInfoList = JProcesses.getProcessList();
                for (final ProcessInfo processInfo : processInfoList) {
                    System.out.println("Process PID: " + processInfo.getPid());
                    System.out.println("Process Name: " + processInfo.getName());
                }
                // Runtime.getRuntime().exec("taskkill /F /Rscript.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/
    }

    /**
     * Creates the components and adds them to panel which intern is added to dialog.
     */
    private void createAndAddComponents() {
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(null);

        JLabel lblGenotype = new JLabel("Genotype file");
        lblGenotype.setFont(sharedInformation.getFont());
        lblGenotype.setBounds(10, 10, 90, 20);

        genoCombo = new JComboBox<>();
        genoCombo.setFont(sharedInformation.getFont());
        genoCombo.setBounds(100, 10, 130, 20);
        genoCombo.addActionListener(e -> actionGenoCombo());

        JLabel lblPhenotype = new JLabel("Phenotype file");
        lblPhenotype.setFont(sharedInformation.getFont());
        lblPhenotype.setBounds(250, 10, 90, 20);

        phenoCombo = new JComboBox<>();
        phenoCombo.setFont(sharedInformation.getFont());
        phenoCombo.setBounds(340, 10, 130, 20);
        phenoCombo.addActionListener(e -> actionPhenoCombo());

        chkMissingPercent = new JCheckBox("Percentage(%)  Missing");
        chkMissingPercent.setFont(sharedInformation.getFont());
        chkMissingPercent.setBounds(10, 40, 200, 20);
        chkMissingPercent.setSelected(true);

        chkPICValue = new JCheckBox("PIC");
        chkPICValue.setFont(sharedInformation.getFont());
        chkPICValue.setBounds(10, 70, 180, 20);
        chkPICValue.setSelected(true);

        chkMaf = new JCheckBox("MAF");
        chkMaf.setFont(sharedInformation.getFont());
        chkMaf.setBounds(10, 100, 180, 20);
        chkMaf.setSelected(true);

        chkDataSummary = new JCheckBox("Data Summary");
        chkDataSummary.setFont(sharedInformation.getFont());
        chkDataSummary.setBounds(250, 40, 200, 20);
        chkDataSummary.setSelected(true);

        SubmitPanel submitPanel = new SubmitPanel(sharedInformation.getOkButtonFont());
        btnStart = submitPanel.submit;
        submitPanel.setBounds(100, 130, 300, 30);
        submitPanel.submit.addActionListener(e -> actionStart());
        submitPanel.cancel.addActionListener(e -> actionCancel());

        dataPanel.add(lblGenotype);
        dataPanel.add(lblPhenotype);
        dataPanel.add(genoCombo);
        dataPanel.add(phenoCombo);
        dataPanel.add(chkPICValue);
        dataPanel.add(chkMissingPercent);
        dataPanel.add(chkMaf);
        dataPanel.add(chkDataSummary);
        dataPanel.add(submitPanel);
        dialogBox.add(new JLayer<>(dataPanel, layerUI));
    }

    /**
     * Action when Start button is clicked.
     */
    private void actionStart() {
        String genoFile = Objects.requireNonNull(genoCombo.getSelectedItem()).toString();
        String phenoFile = Objects.requireNonNull(phenoCombo.getSelectedItem()).toString();
        // Process if at-least one of phenotype or genotype selected. Else don't do anything.
        if (!genoFile.equalsIgnoreCase(Constants.SELECT) || !phenoFile.equalsIgnoreCase(Constants.SELECT)) {
            // setEnableForAllComponents(false);
            storeUserSelectedValues();
            SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    addSummaryLogFiles();
                    // define what thread will do here
                    runGenoSummary();
                    runPhenoSummary();
                    sharedInformation.getLogger().info("Geno and Pheno summary completed.");
                    actionCancel();
                    return null;
                }
            };
            worker.execute();
            setEnableForAllComponents(false);
            layerUI.start();            // Disable all the components
        }
    }

    /**
     * Runs Genotype summary analysis.
     *
     * @throws IOException          I/O exception
     * @throws InterruptedException Break after an hour of execution.
     */
    private void runGenoSummary() throws IOException, InterruptedException {
        if (dataSummaryDataObject.getMaf().equalsIgnoreCase(SELECTED) || dataSummaryDataObject.getPICValue().equalsIgnoreCase(SELECTED) || dataSummaryDataObject.getPercentMissing().equalsIgnoreCase(SELECTED)) {
            ProcessBuilder pb = dataSummaryUtil.getProcessBuilder(dataSummaryDataObject, Constants.GENO, sharedInformation);
            long startTime = System.currentTimeMillis();
            processGeno = pb.start();
            // Waiting for 10 minutes for its completion.
            boolean exitStatus = processGeno.waitFor(10, TimeUnit.MINUTES);
            Util.resetStdout();
            sharedInformation.getLogger().info("Geno Summary completed:\t" + String.valueOf(exitStatus));
            if (exitStatus) {
                String logFile = sharedInformation.getPathConstants().resultDirectory + "GenoSummaryLogFile.txt";
                final String[] lastLine = new String[1];
                try (Stream<String> stream = Files.lines(Paths.get(logFile))) {
                    stream.forEach(line -> lastLine[0] = line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (lastLine[0].equalsIgnoreCase("Execution halted")) {
                    Util.showMessageDialog("Issue with Phenotype summary.\n Please check logfile for details");
                    processPheno.destroyForcibly();
                } else
                saveGenoSummaryFiles(Util.resultComputedOn(startTime));
            } else {
                Util.showMessageDialog("Issue with Genotype summary.\n Please check logfile for details");
                processGeno.destroyForcibly();
            }
        }
    }

    /**
     * Runs Pheno summary analysis.
     *
     * @throws IOException          I/O exception
     * @throws InterruptedException Break after an hour of execution.
     */
    private void runPhenoSummary() throws IOException, InterruptedException {
        if (dataSummaryDataObject.getPhenoDataSummary().equalsIgnoreCase(SELECTED)) {
            ProcessBuilder pb = dataSummaryUtil.getProcessBuilder(dataSummaryDataObject, Constants.PHENO, sharedInformation);
            long startTime = System.currentTimeMillis();
            processPheno = pb.start();
            // Waiting for 10 min for its completion.
            boolean exitStatus = processPheno.waitFor(10, TimeUnit.MINUTES);
            Util.resetStdout();
            sharedInformation.getLogger().info("Pheno Summary completed\t" + String.valueOf(exitStatus));
            if (exitStatus) {
                String logFile = sharedInformation.getPathConstants().resultDirectory + "PhenoSummaryLogFile.txt";
                final String[] lastLine = new String[1];
                try (Stream<String> stream = Files.lines(Paths.get(logFile))) {
                    stream.forEach(line -> lastLine[0] = line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (lastLine[0].equalsIgnoreCase("Execution halted")) {
                    Util.showMessageDialog("Issue with Phenotype summary.\n Please check logfile for details");
                    processPheno.destroyForcibly();
                } else
                    savePhenoSummaryFiles(Util.resultComputedOn(startTime));
            } else {
                Util.showMessageDialog("Issue with Phenotype summary.\n Please check logfile for details");
                processPheno.destroyForcibly();
            }
        }
    }

    /**
     * Saves pheno results in tree and displays them.
     *
     * @param timeTaken Time taken to compute the result.
     */
    private void savePhenoSummaryFiles(String timeTaken) {
        if (Files.exists(Paths.get(sharedInformation.getPathConstants().resultDirectory + dataSummaryDataObject.getPhenoHtmlName()))) {
            FileLocation phenoHtmlFileLocation = new FileLocation(dataSummaryDataObject.getPhenoHtmlName(), sharedInformation.getPathConstants().resultDirectory + dataSummaryDataObject.getPhenoHtmlName());
            dynamicTree.addObject(dynamicTree.getResultsNode(), phenoHtmlFileLocation, Boolean.TRUE);
            sharedInformation.getPathConstants().resultFiles.add(phenoHtmlFileLocation);
            processHTMLFile(timeTaken, phenoHtmlFileLocation);
        } else {
            Util.showMessageDialog("Error in computing phenotype summary. Check log file.");
        }
    }

    /**
     * Saves geno results in tree and displays them.
     *
     * @param timeTaken Time taken to compute the result.
     */
    private void saveGenoSummaryFiles(String timeTaken) {
        if (Files.exists(Paths.get(sharedInformation.getPathConstants().resultDirectory + dataSummaryDataObject.getGenoHtmlName()))) {
            FileLocation genoHtmlFileLocation = new FileLocation(dataSummaryDataObject.getGenoHtmlName(), sharedInformation.getPathConstants().resultDirectory + dataSummaryDataObject.getGenoHtmlName());
            dynamicTree.addObject(dynamicTree.getResultsNode(), genoHtmlFileLocation, Boolean.TRUE);
            sharedInformation.getPathConstants().resultFiles.add(genoHtmlFileLocation);
            if (!processHTMLFile(timeTaken, genoHtmlFileLocation)) return;

            FileLocation genoCSVFileLocation = new FileLocation(dataSummaryDataObject.getGenoSummaryCsvName(), sharedInformation.getPathConstants().resultDirectory + dataSummaryDataObject.getGenoSummaryCsvName());
            dynamicTree.addObject(dynamicTree.getResultsNode(), genoCSVFileLocation, Boolean.TRUE);
            sharedInformation.getPathConstants().resultFiles.add(genoCSVFileLocation);
            UtilCSV.addCSVToTabbedPanel(genoCSVFileLocation, false);
        } else {
            Util.showMessageDialog("Error in computing genotype summary. Check log file.");
        }
    }

    /**
     * Adds processing time to the file.
     * Edits the file to display images.
     * Displays the images
     *
     * @param timeTaken        Time taken
     * @param htmlFileLocation HTML file Location object
     * @return status.
     */
    private boolean processHTMLFile(String timeTaken, FileLocation htmlFileLocation) {
        String status = UtilHTML.addingProcessTime2HTMlFileSummary(htmlFileLocation.getFileLocationOnDisk(), timeTaken);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
            return false;
        }
        status = UtilHTML.editHTML2DisplayImages(htmlFileLocation, "src='", "src='file:///");
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
            return false;
        }
        UtilHTML.displayHTMLFile(htmlFileLocation);
        return true;
    }

    /**
     * Reads user selected values and stores in an object;
     */
    private void storeUserSelectedValues() {
        dataSummaryDataObject.clearValues();
        if (chkMissingPercent.isSelected()) {
            dataSummaryDataObject.setPercentMissing(SELECTED);
        }
        if (chkPICValue.isSelected()) {
            dataSummaryDataObject.setPICValue(SELECTED);
        }
        if (chkMaf.isSelected()) {
            dataSummaryDataObject.setMaf(SELECTED);
        }
        if (chkDataSummary.isSelected()) {
            dataSummaryDataObject.setPhenoDataSummary(SELECTED);
        }
        dataSummaryDataObject.setInputFile(Objects.requireNonNull(genoCombo.getSelectedItem()).toString(), Objects.requireNonNull(phenoCombo.getSelectedItem()).toString());
    }

    /**
     * Enables or disables all the components of data-summary panel.
     *
     * @param value Enable(true)/Disable(false)
     */
    private void setEnableForAllComponents(boolean value) {
        genoCombo.setEnabled(value);
        phenoCombo.setEnabled(value);
        chkMissingPercent.setEnabled(false);
        chkPICValue.setEnabled(false);
        chkMaf.setEnabled(false);
        chkDataSummary.setEnabled(false);
        btnStart.setEnabled(value);
    }

    /**
     * Action when an item from pheno-combo is selected.
     */
    private void actionPhenoCombo() {
        if (phenoCombo.getSelectedItem() != null) {
            if (phenoCombo.getSelectedItem().toString().equalsIgnoreCase(Constants.SELECT)) {
                chkDataSummary.setEnabled(false);
                chkDataSummary.setSelected(false);
            } else {
                chkDataSummary.setEnabled(true);
                chkDataSummary.setSelected(true);
            }
        }
    }

    /**
     * Action when an item from geno-combo is selected.
     */
    private void actionGenoCombo() {
        if (genoCombo.getSelectedItem() != null) {
            if ((genoCombo.getSelectedItem().toString()).equalsIgnoreCase(Constants.SELECT)) {
                chkMaf.setEnabled(false);
                chkMissingPercent.setEnabled(false);
                chkPICValue.setEnabled(false);
                chkMaf.setSelected(false);
                chkMissingPercent.setSelected(false);
                chkPICValue.setSelected(false);
            } else {
                chkMaf.setEnabled(true);
                chkMissingPercent.setEnabled(true);
                chkPICValue.setEnabled(true);
                chkMaf.setSelected(true);
                chkMissingPercent.setSelected(true);
                chkPICValue.setSelected(true);
            }
        }
    }

    private void addSummaryLogFiles() {
        for (int i = 0; i < dynamicTree.getLogNode().getChildCount(); i++) {
            DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) dynamicTree.getLogNode().getChildAt(i);
            FileLocation userObject = (FileLocation) childAt.getUserObject();
            if (userObject.getFileNameInApplication().equalsIgnoreCase("GenoSummaryLogFile.txt"))
                return;
        }
        FileLocation genoFileLocation = new FileLocation("GenoSummaryLogFile.txt", sharedInformation.getPathConstants().resultDirectory + "GenoSummaryLogFile.txt");
        dynamicTree.addObject(dynamicTree.getLogNode(), genoFileLocation, Boolean.TRUE);
        FileLocation phenoFileLocation = new FileLocation("PhenoSummaryLogFile.txt", sharedInformation.getPathConstants().resultDirectory + "PhenoSummaryLogFile.txt");
        dynamicTree.addObject(dynamicTree.getLogNode(), phenoFileLocation, Boolean.TRUE);
    }
}
