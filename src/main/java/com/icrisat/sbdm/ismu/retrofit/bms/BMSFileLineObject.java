package com.icrisat.sbdm.ismu.retrofit.bms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BMSFileLineObject {
    private String plotId, germplasmName, germplasmDbId, plotNumber, blockNumber, replicate;
    private List<String> data;
    private int count;

    public BMSFileLineObject() {
        data = new ArrayList<>();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHeaderSize() {
        return 6;
    }

    public List<String> getHeaderList() {
        return new ArrayList<String>(Arrays.asList(plotId, germplasmName, germplasmDbId, plotNumber, blockNumber, replicate));
    }

    public List<String> getHeadersAndData() {
        List<String> headerAndData = getHeaderList();
        headerAndData.addAll(getData());
        return headerAndData;
    }

    public String getPlotId() {
        return plotId;
    }

    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    public String getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    public String getGermplasmDbId() {
        return germplasmDbId;
    }

    public void setGermplasmDbId(String germplasmDbId) {
        this.germplasmDbId = germplasmDbId;
    }

    public String getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(String plotNumber) {
        this.plotNumber = plotNumber;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getReplicate() {
        return replicate;
    }

    public void setReplicate(String replicate) {
        this.replicate = replicate;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
