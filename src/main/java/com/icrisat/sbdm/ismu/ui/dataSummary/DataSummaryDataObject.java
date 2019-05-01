package com.icrisat.sbdm.ismu.ui.dataSummary;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@PropertySource("classpath:configuration.properties")
@Scope("singleton")
public class DataSummaryDataObject {

    @Value("${rEngine.relativePath}")
    private String rEngineRelativePath;
    @Value("${scripts.relativeLocation}")
    private String scriptsRelativePath;
    @Value("${rScript.genoSummary}")
    private String dataSummaryScriptName;
    @Value("${rScript.phenoSummary}")
    private String phenoSummaryScriptName;

    private String percentMissing;
    private String PICValue;
    private String maf;
    private String phenoDataSummary;
    private String genoSummaryScript, phenoSummaryScript, enginePath;
    private String genoFile, phenoFile, genoHtmlName, genoSummaryCsvName, phenoHtmlName;
    private SharedInformation sharedInformation;

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    String getPercentMissing() {
        return percentMissing;
    }

    void setPercentMissing(String percentMissing) {
        this.percentMissing = percentMissing;
    }

    String getPICValue() {
        return PICValue;
    }

    void setPICValue(String PICValue) {
        this.PICValue = PICValue;
    }

    String getMaf() {
        return maf;
    }

    void setMaf(String maf) {
        this.maf = maf;
    }

    String getPhenoDataSummary() {
        return phenoDataSummary;
    }

    void setPhenoDataSummary(String phenoDataSummary) {
        this.phenoDataSummary = phenoDataSummary;
    }

    String getEnginePath() {
        return enginePath;
    }

    String getGenoSummaryScript() {
        return genoSummaryScript;
    }

    String getGenoHtmlName() {
        return genoHtmlName;
    }

    String getGenoSummaryCsvName() {
        return genoSummaryCsvName;
    }

    String getPhenoHtmlName() {
        return phenoHtmlName;
    }

    String getPhenoSummaryScript() {
        return phenoSummaryScript;
    }

    String getGenoFile() {
        return genoFile;
    }

    String getPhenoFile() {
        return phenoFile;
    }

    /**
     * Clears all the values.
     */
    void clearValues() {
        percentMissing = "-1";
        PICValue = "-1";
        maf = "-1";
        phenoDataSummary = "-1";
        genoSummaryScript = sharedInformation.getWorkingDirectory() + scriptsRelativePath + dataSummaryScriptName;
        phenoSummaryScript = sharedInformation.getWorkingDirectory() + scriptsRelativePath + phenoSummaryScriptName;
        enginePath = sharedInformation.getWorkingDirectory() + rEngineRelativePath;
    }

    /**
     * Sets phenotype file, html file.
     * Sets genotype file, html and csv file.
     *
     * @param genoFile  Genotype file name.(Relative to result directory.)
     * @param phenoFile Phenotype file name.(Relative to result directory.)
     */

    void setInputFile(String genoFile, String phenoFile) {
        this.genoFile = genoFile;
        this.phenoFile = phenoFile;
        this.genoHtmlName = Util.stripFileExtension(genoFile) + "_sum_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".htm";
        this.genoSummaryCsvName = sharedInformation.getPathConstants().summaryFilesMap.get(genoFile);
        this.phenoHtmlName = Util.stripFileExtension(phenoFile) + "_sum_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".htm";
    }
}

