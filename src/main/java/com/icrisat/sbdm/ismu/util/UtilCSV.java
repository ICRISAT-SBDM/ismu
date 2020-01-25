package com.icrisat.sbdm.ismu.util;

import com.icrisat.sbdm.ismu.ui.columnSelection.VerticalTableHeaderCellRenderer;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.icrisat.sbdm.ismu.util.Util.showMessageDialog;

@Component
public class UtilCSV {
    private static SharedInformation sharedInformation;

    @Autowired
    public void setSharedConstants(SharedInformation sharedInformation) {
        UtilCSV.sharedInformation = sharedInformation;
    }

    /**
     * Add's the corresponding file to a panel.
     */
    public static String addCSVToTabbedPanel(String fileNameInApplication, String fileLocationOnDisk, boolean rotateHeader) {
        JPanel csvPanel = new JPanel(new BorderLayout());
        csvPanel.setBounds(0, 0, 100, 100);
        Util.setJPanelName(csvPanel, fileNameInApplication);
        String status = Constants.SUCCESS;
        try {
            //initialized a CsvReader object with file path. Assumes default separator as Comma.
            CSVReader reader = new CSVReader(new FileReader(fileLocationOnDisk));
            // you have to always call readHeaders first before you do any other operation
            String[] headers = reader.readNext();
            int maxSizeOfHeader = 0;
            for (String header : headers) {
                if (header.length() > maxSizeOfHeader) maxSizeOfHeader = header.length();
            }
            if (headers[1].length() < maxSizeOfHeader)
                headers[1] = headers[1] + String.format("%" + (maxSizeOfHeader - headers[1].length()) * 3 + "s", "");
            DefaultTableModel tableModel;
            JTable table;
            if (rotateHeader) {
                tableModel = new DefaultTableModel(headers, 0);
                table = new JTable(tableModel) {
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                    @Override
                    public java.awt.Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
                                                              int columnIndex) {
                        JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);
                        if (columnIndex > 0) {
                            String value = (String) getModel().getValueAt(rowIndex, columnIndex);
                            switch (value) {
                                case "N":
                                case "NN":
                                case "NA":
                                    component.setBackground(new Color(231, 232, 162));
                                    break;
                                case "A":
                                case "AA":
                                    component.setBackground(new Color(128, 144, 232));
                                    break;
                                case "0":
                                case "T":
                                case "TT":
                                    component.setBackground(new Color(149, 192, 236));
                                    break;
                                case "G":
                                case "GG":
                                    component.setBackground(new Color(86, 212, 102));
                                    break;
                                case "1":
                                case "C":
                                case "CC":
                                    component.setBackground(new Color(149, 220, 171));
                                    break;
                                default:
                                    component.setBackground(new Color(236, 112, 99));//245, 76, 71));
                                    break;
                            }
                        } else {
                            component.setBackground(new Color(236, 240, 241));
                            ((JComponent) component).setBorder(new MatteBorder(1, 1, 1, 2, Color.BLACK));
                        }
                        return component;
                    }
                };
            } else {
                tableModel = new DefaultTableModel(headers, 0);
                table = new JTable(tableModel);
            }

            // Cells center allignment.
            DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
            dtcr.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
                table.getColumnModel().getColumn(i).setCellRenderer(dtcr);


            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                tableModel.addRow(nextLine);
            }
            reader.close();
            table.setAutoCreateRowSorter(false);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            if (rotateHeader) {
                JTableHeader tableHeader = table.getTableHeader();
                tableHeader.setDefaultRenderer(new VerticalTableHeaderCellRenderer());
                for (int i = 1; i < table.getColumnCount(); i++) {
                    TableColumn col = table.getColumnModel().getColumn(i);
                    col.setPreferredWidth(25);
                    col.setMinWidth(25);
                    col.setMaxWidth(25);
                }
            }
            //Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setWheelScrollingEnabled(true);
            //Add the scroll pane to this panel.
            csvPanel.add(scrollPane);
            csvPanel.repaint();
        } catch (Exception e) {
            sharedInformation.getLogger().error(e.getMessage());
            status = "Error when reading file:" + fileLocationOnDisk + "\n Pls check log file for details.";
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            showMessageDialog(status);
        } else {
            sharedInformation.getTabbedPane().add(csvPanel);
            sharedInformation.getTabbedPane().setSelectedIndex(sharedInformation.getTabbedPane().getTabCount() - 1);
        }
        return status;
    }

    /**
     * Gets the header information from File.
     *
     * @param fileName file name
     * @return list of headers.
     */
    public static java.util.List<String> getHeaders(String fileName) {
        String[] headers = null;
        java.util.List<String> headerList = new ArrayList<>();
        try {
            //initialized a CsvReader object with file path. Assumes default separator as Comma.
            CSVReader reader = new CSVReader(new FileReader(fileName));
            // you have to always call readHeaders first before you do any other operation
            headers = reader.readNext();
            reader.close();
        } catch (Exception e) {
            //Log Message
            sharedInformation.getLogger().error("Error when reading file:" + fileName + "\n Pls check log file for details.");
        }
        if (headers != null) {
            headerList.addAll(Arrays.asList(headers));
        }
        return headerList;
    }

    /**
     * Copies the selected fields into a new file. First field is selected by default.
     *
     * @param fileName       File Name
     * @param requiredFields fields to be copied
     */
    public static String createCSVwithRequiredFields(String fileName, String outputFileName, java.util.List<String> requiredFields) {
        String status = Constants.SUCCESS;
        List<String> originalFileHeaders = getHeaders(fileName);
        List<Integer> indices = requiredIndices(originalFileHeaders, requiredFields);
        indices.add(0, 0);
        try {
            CSVReader reader = new CSVReader(new FileReader(fileName));
            CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));

            String[] nextLine;
            List<List<String>> outputLines = new ArrayList<>();
            while ((nextLine = reader.readNext()) != null) {
                List<String> outputLine = new ArrayList<>();
                for (Integer index : indices) {
                    outputLine.add(nextLine[index]);
                }
                outputLines.add(outputLine);
                csvWriter.writeNext(outputLine.toArray(new String[outputLine.size()]));
            }
            // This is the data from BMS unprocessed
          /*  if (fileName.contains("BMS") && !fileName.contains("selected"))
                processBMSDate(outputLines, csvWriter);
           */
            reader.close();
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            sharedInformation.getLogger().error(e.getMessage() + e.getStackTrace());
            status = e.getMessage();
        }
        return status;
    }


    /**
     * Gets the required header indices in sorted order.
     *
     * @param originalFileHeaders Header list
     * @param requiredFields      required columns.
     * @return Sorted list of columns indices.
     */
    private static List<Integer> requiredIndices(List<String> originalFileHeaders, List<String> requiredFields) {
        List<Integer> requiredIndices = new ArrayList<>();
        for (String field : requiredFields) {
            requiredIndices.add(originalFileHeaders.indexOf(field));
        }
        Collections.sort(requiredIndices);
        return requiredIndices;
    }
}
