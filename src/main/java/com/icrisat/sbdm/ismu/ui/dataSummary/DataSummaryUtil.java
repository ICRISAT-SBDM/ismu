package com.icrisat.sbdm.ismu.ui.dataSummary;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.PathConstants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
class DataSummaryUtil {

    /**
     * Constructs data command to be executed.
     *
     * @param dataSummaryDataObject Data summary object
     * @param type                  GENO/PHENO
     * @param sharedInformation     sharedInformation.
     * @return Process builder.
     */
    ProcessBuilder getProcessBuilder(DataSummaryDataObject dataSummaryDataObject, int type, SharedInformation sharedInformation) {
        ArrayList<String> scriptArgs = new ArrayList<>();
        scriptArgs.add("\"" + dataSummaryDataObject.getEnginePath() + "\"");
        if (type == Constants.GENO)
            scriptArgs.add("\"" + dataSummaryDataObject.getGenoSummaryScript() + "\"");
        else
            scriptArgs.add("\"" + dataSummaryDataObject.getPhenoSummaryScript() + "\"");
        scriptArgs.add("\"" + PathConstants.resultDirectory + "\"");
        scriptArgs.add(dataSummaryDataObject.getPercentMissing());
        scriptArgs.add(dataSummaryDataObject.getPICValue());
        scriptArgs.add(dataSummaryDataObject.getMaf());
        scriptArgs.add("-1");
        scriptArgs.add("2");
        scriptArgs.add("?");
        //Engine
        scriptArgs.add("1");
        scriptArgs.add("\"" + dataSummaryDataObject.getGenoFile() + "\"");
        scriptArgs.add("\"" + dataSummaryDataObject.getGenoHtmlName() + "\"");
        scriptArgs.add("\"" + dataSummaryDataObject.getGenoSummaryCsvName() + "\"");
        scriptArgs.add("\"" + dataSummaryDataObject.getPhenoFile() + "\"");
        scriptArgs.add("\"" + dataSummaryDataObject.getPhenoHtmlName() + "\"");
        System.out.println(scriptArgs);
        ProcessBuilder pb = new ProcessBuilder(scriptArgs);
        if (type == Constants.GENO) {
            sharedInformation.getLogger().info("Geno summary command: " + scriptArgs.toString());
            pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "GenoSummaryLogFile.txt"));
            pb.redirectError(new java.io.File(PathConstants.resultDirectory + "GenoSummaryLogFile.txt"));
        } else {
            sharedInformation.getLogger().info("Pheno summary command: " + scriptArgs.toString());
            pb.redirectOutput(new java.io.File(PathConstants.resultDirectory + "PhenoSummaryLogFile.txt"));
            pb.redirectError(new java.io.File(PathConstants.resultDirectory + "PhenoSummaryLogFile.txt"));
        }
        return pb;
    }
}
