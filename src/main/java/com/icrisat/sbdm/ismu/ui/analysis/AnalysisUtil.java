package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.util.PathConstants;
import com.icrisat.sbdm.ismu.util.Util;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class AnalysisUtil {
    /**
     * Constructs data command to be executed.
     *
     * @param analysisDataObject Analysis object
     * @return Process builder.
     */
    private ProcessBuilder getProcessBuilderA(AnalysisDataObject analysisDataObject, Logger logger) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add("\"" + analysisDataObject.getEnginePath() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getaScriptPath() + "\"");
        scriptArgs.add("\"" + PathConstants.resultDirectory + "\"");
        scriptArgs.add("\"" + analysisDataObject.getGenoFile() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getPhenoFile() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getResultFileName() + "\"");
        scriptArgs.add(analysisDataObject.getPercentMissingMarkers());
        scriptArgs.add(analysisDataObject.getMAF());
        scriptArgs.add(analysisDataObject.getPICValue());
        scriptArgs.add("-1");
        scriptArgs.add(analysisDataObject.getCores());
        scriptArgs.add("?");
        scriptArgs.add(String.valueOf(analysisDataObject.getPhenoTraits().size()));
        //hardcoded calculated(marker type(snp/dart))
        scriptArgs.add("-1");
        //hardcoded calculated
        scriptArgs.add(":");
        //Engine
        scriptArgs.add("1");
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        logger.info("A command: " + scriptArgs.toString());
        pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "a_log.txt"));
        pb.redirectError(new java.io.File(PathConstants.resultDirectory + "a_log.txt"));
        return pb;
    }

    private ProcessBuilder getProcessBuilderAB(AnalysisDataObject analysisDataObject, Logger logger, int triatNo) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add("\"" + analysisDataObject.getEnginePath() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getabScriptPath() + "\"");
        scriptArgs.add("\"" + PathConstants.resultDirectory + "\"");
        scriptArgs.add("\"" + analysisDataObject.getResultFileName() + "\"");
        //HARDCODED
        scriptArgs.add("-1");
        scriptArgs.add(analysisDataObject.getCores());
        // Missing Char
        scriptArgs.add("-1");
        scriptArgs.add(String.valueOf(triatNo));
        //hardcoded calculated(marker type(snp/dart))
        scriptArgs.add("-1");
        //hardcoded calculated
        scriptArgs.add("-1");
        //Temp files
        scriptArgs.add("GSTemp.txt");
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        logger.info("AB command: " + scriptArgs.toString());
        pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "ab_log.txt"));
        pb.redirectError(new java.io.File(PathConstants.resultDirectory + "ab_log.txt"));
        return pb;
    }

    private ProcessBuilder getProcessBuilderB(AnalysisDataObject analysisDataObject, Logger logger, int triatNo, int methodNo) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add("\"" + analysisDataObject.getEnginePath() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getbScriptPath() + "\"");
        scriptArgs.add("\"" + PathConstants.resultDirectory + "\"");
        scriptArgs.add("\"" + analysisDataObject.getResultFileName() + "\"");
        //HARDCODED
        scriptArgs.add("-1");
        scriptArgs.add(analysisDataObject.getCores());
        // Missing Char
        scriptArgs.add("-1");
        for (int i = 0; i < 6; i++) {
            if (i == methodNo)
                scriptArgs.add("1");
            else
                scriptArgs.add("-1");
        }
        scriptArgs.add(String.valueOf(triatNo));
        //hardcoded calculated(marker type(snp/dart))
        scriptArgs.add("-1");
        //hardcoded separator
        scriptArgs.add("-1");
        //Temp files
        scriptArgs.add("GSTemp.txt");
        //Engine
        scriptArgs.add("1");
        //forton
        scriptArgs.add("-1");
        scriptArgs.add("-1");
        //covariate
        scriptArgs.add("Select");
        scriptArgs.add(analysisDataObject.getBayesRounds());
        scriptArgs.add(analysisDataObject.getBayesBurning());
        scriptArgs.add(analysisDataObject.getBayesThinning());
        scriptArgs.add(analysisDataObject.getReplication());
        scriptArgs.add(analysisDataObject.getFold());
        scriptArgs.add(analysisDataObject.getForests());
        scriptArgs.add(analysisDataObject.getScriptsPath());
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        logger.info("B command: " + scriptArgs.toString());
        pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "b_log.txt"));
        pb.redirectError(new java.io.File(PathConstants.resultDirectory + "b_log.txt"));
        return pb;
    }

    private ProcessBuilder getProcessBuilderC(AnalysisDataObject analysisDataObject, Logger logger) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add("\"" + analysisDataObject.getEnginePath() + "\"");
        scriptArgs.add("\"" + analysisDataObject.getcScriptPath() + "\"");
        scriptArgs.add("\"" + PathConstants.resultDirectory + "\"");
        scriptArgs.add("\"" + analysisDataObject.getResultFileName() + "\"");
        //Temp files
        scriptArgs.add("GSTemp.txt");
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        logger.info("C command: " + scriptArgs.toString());
        pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "c_log.txt"));
        pb.redirectError(new java.io.File(PathConstants.resultDirectory + "c_log.txt"));
        return pb;
    }

    boolean runAscript(ActionEvent e, AnalysisDataObject analysisDataObject, Logger logger) throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilderA(analysisDataObject, logger);
        long startTime = System.currentTimeMillis();
        Process processA = pb.start();
        boolean exitStatus = processA.waitFor(1, TimeUnit.HOURS);
        Util.resetStdout();
        logger.info("A script completed:\t" + String.valueOf(exitStatus) + ". Executed for " + (System.currentTimeMillis() - startTime));
        if (!exitStatus) {
            Util.showMessageDialog("Issue with R script execution.\n Please check a_log logfile for details");
            processA.destroyForcibly();
        }
        exitStatus = Util.checkLogStatus(PathConstants.resultDirectory + "a_log.txt");
        return exitStatus;
    }

    boolean runABscript(ActionEvent e, AnalysisDataObject analysisDataObject, Logger logger, Integer triatNo) throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilderAB(analysisDataObject, logger, triatNo);
        long startTime = System.currentTimeMillis();
        Process processAB = pb.start();
        boolean exitStatus = processAB.waitFor(1, TimeUnit.HOURS);
        Util.resetStdout();
        logger.info("AB script completed:\t" + String.valueOf(exitStatus) + ". Executed for " + (System.currentTimeMillis() - startTime));
        if (!exitStatus) {
            Util.showMessageDialog("Issue with R script execution.\n Please check logfile ab_log for details");
            processAB.destroyForcibly();
        }
        exitStatus = Util.checkLogStatus(PathConstants.resultDirectory + "ab_log.txt");
        return exitStatus;
    }

    boolean runBscript(ActionEvent e, AnalysisDataObject analysisDataObject, Logger logger, Integer triatNo, int methodNo) throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilderB(analysisDataObject, logger, triatNo, methodNo);
        long startTime = System.currentTimeMillis();
        Process processB = pb.start();
        boolean exitStatus = processB.waitFor(1, TimeUnit.HOURS);
        Util.resetStdout();
        logger.info("B script completed:\t" + String.valueOf(exitStatus) + ". Executed for " + (System.currentTimeMillis() - startTime));
        if (!exitStatus) {
            Util.showMessageDialog("Issue with R script execution.\n Please check logfile b_log for details");
            processB.destroyForcibly();
        }
        exitStatus = Util.checkLogStatus(PathConstants.resultDirectory + "b_log.txt");
        return exitStatus;
    }

    void runCscript(ActionEvent e, AnalysisDataObject analysisDataObject, Logger logger) throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilderC(analysisDataObject, logger);
        long startTime = System.currentTimeMillis();
        Process processC = pb.start();
        boolean exitStatus = processC.waitFor(1, TimeUnit.HOURS);
        Util.resetStdout();
        logger.info("C script completed:\t" + String.valueOf(exitStatus) + ". Executed for " + (System.currentTimeMillis() - startTime));
        if (!exitStatus) {
            Util.showMessageDialog("Issue with R script execution.\n Please check logfile c_log for details");
            processC.destroyForcibly();
        }
    }
}
