package com.icrisat.sbdm.ismu.util;

import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class GenoFileFirstTImeProcessing {

    private static SharedInformation sharedInformation;

    @Autowired
    public void setSharedConstants(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }


    private static boolean shouldConvert = false;

    public static void genofileComputation(String destination, List<List<String>> matrix) throws IOException {
        shouldConvert = false;
        File dest_file = new File(destination);
        CSVWriter writer = new CSVWriter(new FileWriter(destination));
        // This is the first row.
        List<String> headerRow = matrix.remove(0);
        float headerSize = headerRow.size() - 1;
        writer.writeNext(headerRow.toArray(new String[0]));
        String summaryName = Util.stripFileExtension(dest_file.getName()) + "_summary_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        String genoSummary = dest_file.getParent() + "/" + summaryName;
        CSVWriter summaryWriter = new CSVWriter(new FileWriter(genoSummary));
        List<MarkerSummaryInfo> markerInfos = new LinkedList<>();

        for (List<String> row : matrix) {
            //Convert T/T to TT
            List<String> newRow = new ArrayList<>();
            for (String value : row) {
                String newValue = value;
                if (value.length() == 3 && value.charAt(1) == '/')
                    newValue = String.format("%s%s", value.charAt(0), value.charAt(2));
                newRow.add(newValue);
            }
            List<String> processedList = fixMissingValues(newRow, markerInfos);
            writer.writeNext(processedList.toArray(new String[0]));
        }
        writer.close();
        int count = 1;
        summaryWriter.writeNext(new String[]{"", "Marker", "PIC", "MissingPercent", "MAF"});
        for (MarkerSummaryInfo markerInfo : markerInfos) {
            calculateMAF(markerInfo);
            float missingPercent = markerInfo.getMissingPercent() / headerSize;
            DecimalFormat numberFormat = new DecimalFormat("#.000");
            DecimalFormat numberFormat5 = new DecimalFormat("#.00000");

            summaryWriter.writeNext(new String[]{String.valueOf(count++), markerInfo.getMarkerName(), String.valueOf(numberFormat.format(markerInfo.getPicValue())),
                    String.valueOf(numberFormat5.format(missingPercent)),
                    String.valueOf(numberFormat.format(markerInfo.getMafValue()))});
        }
        summaryWriter.close();
        HashMap<String, String> summaryFilesMap = PathConstants.summaryFilesMap;
        summaryFilesMap.put(dest_file.getName(), summaryName);
    }


    private static void calculateMAF(MarkerSummaryInfo markerSummaryInfo) {
        Map<String, Integer> mafValues = markerSummaryInfo.getMafValues();
        // Line has only one value
        if (mafValues.size() < 2) {
            markerSummaryInfo.setMafValue(0);
            markerSummaryInfo.setPicValue(0);
            return;
        }
        // THere is a 2 in the file
        if (shouldConvert) {
            float noOfZero = mafValues.get("0") != null ? mafValues.get("0") : 0;
            float noOfOne = mafValues.get("1") != null ? mafValues.get("1") : 0;
            float noOfTwo = mafValues.get("2") != null ? mafValues.get("2") : 0;
            noOfZero = noOfZero * 2 + noOfOne;
            noOfTwo = noOfTwo * 2 + noOfOne;
            actualComputeOfMAF(markerSummaryInfo, noOfZero / (noOfZero + noOfTwo), noOfTwo / (noOfZero + noOfTwo));
            return;
        }
        // Keys are 0,1
        String key = mafValues.keySet().iterator().next();
        if (key.equals("0") || key.equals("1")) {
            float noOfZero = mafValues.get("0");
            float noOfOne = mafValues.get("1");
            actualComputeOfMAF(markerSummaryInfo, noOfZero / (noOfZero + noOfOne), noOfOne / (noOfZero + noOfOne));
            return;
        }
        // Keys have 2 letters
        if (mafValues.keySet().iterator().next().length() > 1) {
            twoLetterMAFComputation(markerSummaryInfo);
        }
        // THere are A, T, G, C, Y, W twoLetterMAFComputation twoLetterMAFComputation case
        else {
            if (mafValues.keySet().contains("R") || mafValues.keySet().contains("Y") || mafValues.keySet().contains("S")
                    || mafValues.keySet().contains("W") || mafValues.keySet().contains("K") || mafValues.keySet().contains("M")) {
                Map<String, String> conversionMap = new HashMap<>();
                conversionMap.put("A", "AA");
                conversionMap.put("T", "TT");
                conversionMap.put("G", "GG");
                conversionMap.put("C", "CC");
                conversionMap.put("R", "AG");
                conversionMap.put("Y", "CT");
                conversionMap.put("S", "GC");
                conversionMap.put("W", "AT");
                conversionMap.put("K", "GT");
                conversionMap.put("M", "AC");

                Map<String, Integer> newMap = new HashMap<>();
                for (Map.Entry<String, Integer> entry : mafValues.entrySet()) {
                    newMap.put(conversionMap.get(entry.getKey()), mafValues.get(entry.getKey()));
                }
                markerSummaryInfo.setMafValues(newMap);
                twoLetterMAFComputation(markerSummaryInfo);
            } else {
                Set<String> keys = mafValues.keySet();
                List<String> keyStrings = new ArrayList<>(keys);
                float noOfEle1 = mafValues.get(keyStrings.get(0));
                float noOfEle2 = mafValues.get(keyStrings.get(1));
                actualComputeOfMAF(markerSummaryInfo, noOfEle1 / (noOfEle1 + noOfEle2), noOfEle2 / (noOfEle1 + noOfEle2));
            }
        }
    }

    private static void actualComputeOfMAF(MarkerSummaryInfo markerSummaryInfo, float a, float b) {
        double a2 = Math.pow(a, 2);
        double b2 = Math.pow(b, 2);
        markerSummaryInfo.setMafValue(Math.min(a, b));
        markerSummaryInfo.setPicValue((float) (1 - (a2 + b2) - 2 * a2 * b2));
    }

    private static void twoLetterMAFComputation(MarkerSummaryInfo markerSummaryInfo) {
        Map<String, Integer> mafValues = markerSummaryInfo.getMafValues();
        Iterator<String> iterator = mafValues.keySet().iterator();
        Set<String> keys = new HashSet<>();
        while (iterator.hasNext()) {
            String next = iterator.next();
            keys.add(next.substring(0, 1));
            keys.add(next.substring(1));
        }
        List<String> keyStrings = new ArrayList<>(keys);
        Map<String, Integer> map = new HashMap<>();
        map.put(keyStrings.get(0), 0);
        map.put(keyStrings.get(1), 0);
        for (Map.Entry<String, Integer> entry : mafValues.entrySet()) {
            String key1 = entry.getKey();
            map.put(key1.substring(0, 1), map.get(key1.substring(0, 1)) + entry.getValue());
            map.put(key1.substring(1), map.get(key1.substring(1)) + entry.getValue());
        }

        float total = map.get(keyStrings.get(0)) + map.get(keyStrings.get(1));
        float a = map.get(keyStrings.get(0)) / total;
        float b = map.get(keyStrings.get(1)) / total;
        actualComputeOfMAF(markerSummaryInfo, a / (a + b), b / (a + b));
    }

    private static List<String> fixMissingValues(List<String> inputRow, List<MarkerSummaryInfo> markerInfos) {
        List<String> processedRow = new ArrayList<>();
        MarkerSummaryInfo markerInfo = new MarkerSummaryInfo();
        Map<String, Integer> hashMap = new HashMap<>();

        // First cell is name
        markerInfo.setMarkerName(inputRow.get(0));
        processedRow.add(inputRow.get(0));
        int noOfValues = 0;
        for (int i = 1; i < inputRow.size(); i++) {
            String value = inputRow.get(i);
            if (value.equalsIgnoreCase("NA") || value.equalsIgnoreCase("NN") || value.equals("9") || value.equalsIgnoreCase("N")) {
                processedRow.add("NA");
                noOfValues++;
            } else {
                processedRow.add(value);
                Integer count = hashMap.get(value);
                if (count == null) hashMap.put(value, 1);
                else hashMap.put(value, hashMap.get(value) + 1);
            }
            if (value.equals("2")) shouldConvert = true;
        }
        markerInfo.setMissingPercent(noOfValues);
        markerInfo.setMafValues(hashMap);
        markerInfos.add(markerInfo);
        return processedRow;
    }
}