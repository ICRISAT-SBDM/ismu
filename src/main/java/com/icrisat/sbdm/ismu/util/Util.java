package com.icrisat.sbdm.ismu.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Util {
    private static SharedInformation sharedInformation;

    @Autowired
    public void setSharedConstants(SharedInformation sharedInformation) {
        Util.sharedInformation = sharedInformation;
    }

    /**
     * Strip the file extension.
     *
     * @param fileName Name of file
     * @return File name without extension
     */
    public static String stripFileExtension(String fileName) {
        int dotInd = fileName.lastIndexOf('.');
        // Dot in first position implies we are dealing with a hidden file rather than an extension
        return (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
    }

    /**
     * File extension.
     * Ex: a.html gives .html
     *
     * @param fileName Name of file
     * @return File extension
     */
    public static String getFileExtension(String fileName) {
        int dotInd = fileName.lastIndexOf('.');
        // Dot in first position implies we are dealing with a hidden file rather than an extension
        return (dotInd > 0) ? fileName.substring(dotInd + 1) : fileName;
    }

    /**
     * Copies a file from location source to location destination.
     *
     * @param source      File path
     * @param destination File Path
     * @param isGeno      0 for geno, 1 for pheno
     * @return Status of copy.
     */
    public static String copyFile(String source, String destination, int isGeno) {
        String returnStatus;
        File source_file = new File(source);
        File dest_file = new File(destination);
        try {
            if (source.equalsIgnoreCase(destination)) return Constants.SUCCESS;
            returnStatus = validate(source_file, dest_file, source, destination);
            if (!returnStatus.endsWith(Constants.SUCCESS)) return returnStatus;

            List<List<String>> valuesList = Files.lines(Paths.get(source))
                    .map(line -> line.replace("\"",""))
                    .map(line -> Arrays.asList(line.split(",")))
                    .collect(Collectors.toList());

            if (isGeno == Constants.GENO) {
                GenoFileFirstTImeProcessing.genofileComputation(destination, valuesList);
            } else {
                CSVWriter writer = new CSVWriter(new FileWriter(destination));
                for (List<String> values : valuesList)
                    writer.writeNext(values.toArray(new String[0]));
                writer.close();
            }
        } catch (IOException ie) {
            returnStatus = "IOException: Please refer to the log file. for details" + ie.toString() + ie.getMessage();
        } catch (Exception e) {
            returnStatus = "Exception: Please refer to the log file. for details" + e.toString() + e.getMessage();
        }
        return returnStatus;
    }


    /**
     * Copies a file from location source to location destination.
     *
     * @param source      File path
     * @param destination File Path
     * @return Status of copy.
     */
    public static String processHapMap(String source, String destination) {
        String returnStatus;
        File source_file = new File(source);
        File dest_file = new File(destination);
        returnStatus = validate(source_file, dest_file, source, destination);
        if (!returnStatus.endsWith(Constants.SUCCESS)) return returnStatus;
        try (BufferedReader br = new BufferedReader(new FileReader(source_file))) {
            String firstLine = br.readLine();
            if (firstLine == null) return "Not hapmap file";
            List<String> split = Arrays.asList(firstLine.split("\t"));
            if (split.size() < 12) return "Not hapmap file";
            if (!(split.get(0).equalsIgnoreCase("rs#") && split.get(1).equalsIgnoreCase("alleles") && split.get(2).equalsIgnoreCase("chrom") &&
                    split.get(3).equalsIgnoreCase("pos") && split.get(4).equalsIgnoreCase("strand") && split.get(5).equalsIgnoreCase("assembly#") &&
                    split.get(6).equalsIgnoreCase("center") && split.get(7).equalsIgnoreCase("protLSID") && split.get(8).equalsIgnoreCase("assayLSID") &&
                    split.get(9).equalsIgnoreCase("panelLSID") && split.get(10).equalsIgnoreCase("QCcode")
            )) return "Not valid hapmap file";
            int startPosition;
            if (split.get(11).equalsIgnoreCase("REFERENCE_GENOME")) startPosition = 12;
            else startPosition = 11;
            List<List<String>> outputLines = new ArrayList<>();
            List<String> outputLine = new ArrayList<>();
            outputLine.add(split.get(0));
            outputLine.addAll(split.subList(startPosition, split.size()));
            outputLines.add(outputLine);
            String line;
            while ((line = br.readLine()) != null) {
                outputLine = new ArrayList<>();
                split = Arrays.asList(line.split("\t"));
                outputLine.add(split.get(0));
                outputLine.addAll(split.subList(startPosition, split.size()));
                outputLines.add(outputLine);
            }
            GenoFileFirstTImeProcessing.genofileComputation(destination, outputLines);
        } catch (IOException ie) {
            returnStatus = "IOException: Please refer to the log file. for details" + ie.toString() + ie.getMessage();
        } catch (Exception e) {
            returnStatus = "Exception: Please refer to the log file. for details" + e.toString() + e.getMessage();
        }
        return returnStatus;
    }

    private static String validate(File source_file, File dest_file, String source, String destination) {
        String returnStatus = Constants.SUCCESS;
        if (!source_file.exists() || !source_file.isFile()) {
            returnStatus = "No source file found : " + source + "\n" + "Please select correct file";
            return returnStatus;
        }
        if (!source_file.canRead()) {
            returnStatus = "Source file is unreadable: " + source;
            return returnStatus;
        }
        if (dest_file.exists()) {
            returnStatus = "A file with same name already exists in destination directory\n" + destination;
            return returnStatus;
        } else {
             /* File.getParent() can return null when the file is specified without a directory or is in the root directory.
       This method handles those cases.*/
            File parentDir;
            if (dest_file.getParent() == null) {
                if (dest_file.isAbsolute()) {
                    parentDir = new File(File.separator);
                } else {
                    parentDir = new File(System.getProperty("user.dir"));
                }
            } else {
                parentDir = new File(dest_file.getParent());
            }
            if (!parentDir.exists()) {
                returnStatus = "No Destination directory exist: " + destination;
                return returnStatus;
            }
            if (!parentDir.canWrite()) {
                returnStatus = "Destination directory is not writable: " + destination;
                return returnStatus;
            }
        }
        return returnStatus;
    }

    /**
     * Reads no of lines in the file.
     *
     * @param filePath Path to the file.
     * @return No of lines. -1 when file not found.
     */
    static int noOfLinesInFile(String filePath) {
        Logger logger = sharedInformation.getLogger();
        int linenumber = 0;
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null)
                    linenumber++;
            } catch (IOException ex) {
                logger.warn(ex.getMessage());
            }
        } else {
            logger.warn("File does not exists!");
            linenumber = -1;
        }
        return linenumber;
    }

    /**
     * Computes how long it took for a function to run.
     *
     * @param startTime start time.
     * @return String representing the difference.
     */
    public static String resultComputedOn(long startTime) {
        String computedOn;
        long stopTime = System.currentTimeMillis();
        long difference = stopTime - startTime;
        long seconds = difference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = TimeUnit.MILLISECONDS.toDays(difference);
        long hours1 = TimeUnit.MILLISECONDS.toHours(difference) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(difference));
        long minutes1 = TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference));
        long seconds1 = TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference));
        if (days == 0) {
            computedOn = String.format(" (Total Time: %02d Hr : %02d Min : %02d Sec)", hours1, minutes1, seconds1);
        } else {
            computedOn = String.format(" (Total Time: %dd Days : %02d Hr : %02d Min : %02d Sec)", days, hours, minutes, seconds);
        }
        return computedOn;
    }

    /**
     * Creates a logger.
     *
     * @param className name of the class for the logger.
     * @param path      URI
     * @return Logger
     */
    public static Logger createLogger(Class className, URL path) {
        /* Logger Cases:
         *   There are three cases.
         *   Open, New project, Open project
         *   */
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        String resultDir = sharedInformation.getPathConstants().resultDirectory;
        System.setProperty("log.dir", resultDir);
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        try {
            jc.doConfigure(path);
        } catch (JoranException e) {
            e.printStackTrace();
        }
        Logger logger = LoggerFactory.getLogger(className);
        sharedInformation.setLogger(logger);
        return logger;
    }

    public static String getJarDirectory(Class className) {
        String jarDirectory = className.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarDirectory.contains("/")) {
            int dotInd = jarDirectory.lastIndexOf('/');
            if (dotInd > 0)
                jarDirectory = jarDirectory.substring(0, dotInd);
        }
        if (jarDirectory.contains("\\")) {
            int dotInd = jarDirectory.lastIndexOf('\\');
            if (dotInd > 0)
                jarDirectory = jarDirectory.substring(0, dotInd);
        }
        return jarDirectory;
    }

    /**
     * Resets Stdout and StdError.
     */
    public static void resetStdout() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
    }

    /**
     * Adds a specified no of empty label to panel.
     *
     * @param panel Panel
     */
    public static void addDummyLabels(JPanel panel, int count) {
        for (int i = 0; i < count; i++)
            panel.add(new JLabel(" "));
    }

    /**
     * Creates a compound border.
     *
     * @param title             Title
     * @param sharedInformation shared information
     * @return border
     */
    public static CompoundBorder getCompoundBorder(String title, SharedInformation sharedInformation) {
        TitledBorder inner = BorderFactory.createTitledBorder(title);
        inner.setTitleJustification(TitledBorder.CENTER);
        inner.setTitleFont(sharedInformation.getTitleBoldFont());
        Border outer = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        return BorderFactory.createCompoundBorder(outer, inner);
    }

    /**
     * Created a j-label with bold font.
     *
     * @param text              text to be displayed
     * @param sharedInformation shared information
     * @return jlabel.
     */
    public static JLabel createJLabel(String text, SharedInformation sharedInformation) {
        JLabel jLabel = new JLabel(text);
        jLabel.setFont(sharedInformation.getBoldFont());
        return jLabel;
    }

    public static void fillGenoAndPhenoFiles(JComboBox<String> genoCombo, JComboBox<String> phenoCombo) {
        genoCombo.removeAllItems();
        phenoCombo.removeAllItems();
        genoCombo.addItem(Constants.SELECT);
        for (FileLocation genoFile : sharedInformation.getPathConstants().genotypeFiles)
            genoCombo.addItem(genoFile.getFileNameInApplication());
        genoCombo.setSelectedIndex(0);
        phenoCombo.addItem(Constants.SELECT);
        for (FileLocation phenoFile : sharedInformation.getPathConstants().phenotypeFiles)
            phenoCombo.addItem(phenoFile.getFileNameInApplication());
        phenoCombo.setSelectedIndex(0);
    }

    /**
     * If result directory is not set then processing not happened so exit else ask for saving project.
     *
     * @param e                 Event
     * @param sharedInformation shared information
     * @param openSaveProject   open save project object
     */
    public static boolean closeApplication(EventObject e, SharedInformation sharedInformation, Project
            openSaveProject) {
        if (sharedInformation.getPathConstants().resultDirectory != null) {
            int exitStatus = JOptionPane.showConfirmDialog((java.awt.Component) e.getSource(), "Do you want to save project?");
            if (exitStatus == JOptionPane.YES_OPTION) {
                String status = openSaveProject.saveProject();
                if (status.equalsIgnoreCase(Constants.SUCCESS))
                    return true;
                else {
                    showMessageDialog(status);
                    return false;
                }
            } else if (exitStatus == JOptionPane.NO_OPTION) return true;
            else if (exitStatus == JOptionPane.CANCEL_OPTION) return false;
        }
        return true;
    }

    public static void setJPanelName(JPanel panel, String name) {
        panel.setName(name + "    ");
    }

    public static String getJPanelName(JPanel panel) {
        return panel.getName().trim();
    }

    /**
     * Returns a file chooser
     *
     * @param title Title
     * @return File chooser
     */
    public static NativeJFileChooser getFolderChooser(String title) {
        NativeJFileChooser fileChooser = new NativeJFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return fileChooser;
    }

    /**
     * Selects a csv file.
     *
     * @param fileChooserTitle Title for file chooser
     * @param field            Text field to set the text.
     * @param type             Either Geno/Pheno
     * @param e                Action event e;
     */
    public static void selectFile(String fileChooserTitle, JTextField field, int type, PathConstants
            pathConstants, ActionEvent e) {

        NativeJFileChooser chooser;// getting the browsePath if file already chosen
        if (pathConstants.lastChosenFilePath == null) {
            chooser = new NativeJFileChooser();
        } else {
            chooser = new NativeJFileChooser(pathConstants.lastChosenFilePath);
        }
        chooser.removeChoosableFileFilter(chooser.getFileFilter());
        chooser.setDialogTitle(fileChooserTitle);// setting the
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV (Comma delimited) (*.csv)", "csv");
        chooser.setFileFilter(filter);
        if (type == Constants.GENO) {
            FileNameExtensionFilter filter1 = new FileNameExtensionFilter("Hapmap (Tab delimited) (*.hmp.txt)", "txt");
            chooser.setFileFilter(filter1);
        }
        if (chooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getPath());
            pathConstants.lastChosenFilePath = chooser.getSelectedFile().getAbsolutePath();
            File file = new File(field.getText());
            if (!file.exists()) {
                showMessageDialog("Selected file doesn't exists. \nPlease select valid file");
                field.setText("");
            }
        }
    }

    public static boolean checkLogStatus(String file) {
        boolean exitStatus = true;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
            String line;
            String outputLine = "";
            while ((line = br.readLine()) != null) {
                outputLine = line;
            }
            if (outputLine.equalsIgnoreCase("Execution halted")) {
                exitStatus = false;
            }
        } catch (Exception e1) {
            exitStatus = false;
        }
        return exitStatus;
    }

    public static void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(sharedInformation.getMainFrame(), message);
    }

    /**
     * Sets temporary result dir.
     * Once ok is clicked this is moved to result directory.
     * Enables buttons to select genotype and phenotype files.
     *
     * @param fileChooser fileChooser
     */
    public static void setTempResultDir(NativeJFileChooser fileChooser) {
        String resDir = fileChooser.getSelectedFile().toString();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            resDir = resDir + "\\";
            sharedInformation.setOS(Constants.WINDOWS);
        } else {
            resDir = resDir + "/";
            sharedInformation.setOS(Constants.OTHEROS);
        }
        sharedInformation.getOpenDialog().getResultPanel().txtResult.setText(resDir);
        sharedInformation.getPathConstants().tempResultDirectory = resDir;
        sharedInformation.getOpenDialog().getPhenoPanel().btnBrowse.setEnabled(true);
        sharedInformation.getOpenDialog().getPhenoPanel().btnConnect.setEnabled(true);
        sharedInformation.getOpenDialog().getGenoPanel().btnConnect.setEnabled(true);
        sharedInformation.getOpenDialog().getGenoPanel().btnBrowse.setEnabled(true);
    }

    /**
     * Position of the dialog box
     *
     * @param x width of dialog box
     * @param y height of dialog box
     * @return Point
     */
    public static Point getLocation(int x, int y) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int xCoord = (int) ((screenSize.getWidth() - x) / 2);
        int yCoord = (int) (screenSize.getHeight() - y) / 2;
        return new Point(xCoord, yCoord);
    }

}