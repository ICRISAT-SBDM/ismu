package com.icrisat.sbdm.ismu.util;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class PdfConverter {

    @Value("${pdfConverter.relativePath}")
    private String pdfConverterRelativePath;
    private Logger logger;

    /**
     * Creates the process builder for pdf conversion
     *
     * @param inputFile         input html file
     * @param pdfName           Pdf file name
     * @param sharedInformation sharedInformation
     * @return process builder
     */
    private ProcessBuilder getProcessBuilder(String inputFile, String pdfName, SharedInformation sharedInformation) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add(sharedInformation.getWorkingDirectory() + pdfConverterRelativePath);
        scriptArgs.add("file:///" + inputFile);
        scriptArgs.add(pdfName);
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        logger.info("Pdf conversion command: " + scriptArgs.toString());
        pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "PdfConversionLogFile.txt"));
        pb.redirectError(new java.io.File(PathConstants.resultDirectory + "PdfConversionLogFile.txt"));
        return pb;
    }

    /**
     * Converts the html file to pdf
     *
     * @param ae                Action event
     * @param inputFile         html file
     * @param pdfName           pdf file
     * @param sharedInformation Shared information
     */
    public void convertToPdf(ActionEvent ae, String inputFile, String pdfName, SharedInformation sharedInformation) {
        logger = sharedInformation.getLogger();
        ProcessBuilder pb = getProcessBuilder(inputFile, pdfName, sharedInformation);
        long startTime = System.currentTimeMillis();
        logger.info("Pdf conversion started for file " + inputFile);
        try {
            Process pdfProcess = pb.start();
            // Waiting for a minute for its completion.
            boolean exitStatus = pdfProcess.waitFor(1, TimeUnit.MINUTES);
            if (exitStatus) {
                Util.showMessageDialog("Pdf saved successfully at " + pdfName);
                logger.info("Pdf conversion completed:\t" + String.valueOf(exitStatus) + "  in " + (startTime - System.currentTimeMillis()));
            } else {
                Util.showMessageDialog("Issue with Pdf conversion.\n Please check logfile for details");
                pdfProcess.destroyForcibly();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            Util.showMessageDialog("Issue with Pdf conversion.\n Please check logfile for details");
        }
        Util.resetStdout();
    }
}
