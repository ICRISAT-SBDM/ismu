package com.icrisat.sbdm.ismu.util;

import com.icrisat.sbdm.ismu.ui.components.VerticalTableHeaderCellRenderer;
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
     *
     * @param csvFileLocation CSV file location.
     */
    public static void addCSVToTabbedPanel(FileLocation csvFileLocation, boolean value) {
        String status;
        JPanel csvPanel = new JPanel(new BorderLayout());
        csvPanel.setBounds(0, 0, 100, 100);
        Util.setJPanelName(csvPanel, csvFileLocation.toString());
        status = csvReader(csvFileLocation.getFileLocationOnDisk(), csvPanel, value);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            showMessageDialog(status);
        } else {
            sharedInformation.getTabbedPane().add(csvPanel);
            sharedInformation.getTabbedPane().setSelectedIndex(sharedInformation.getTabbedPane().getTabCount() - 1);
        }
    }

    /**
     * Reads a csv file and adds content to the panel.
     *
     * @param fileName File Name
     * @param csvPanel Panel Name
     * @return Status of file reading.
     */
    public static String csvReader(String fileName, JPanel csvPanel, boolean rotateHeader) {
        String status = Constants.SUCCESS;
        try {
            //initialized a CsvReader object with file path. Assumes default separator as Comma.
            CSVReader reader = new CSVReader(new FileReader(fileName));
            // you have to always call readHeaders first before you do any other operation
            String[] headers = reader.readNext();
            int maxSizeOfHeader = 0;
            for (String header : headers) {
                if (header.length() > maxSizeOfHeader) maxSizeOfHeader = header.length();
            }
            headers[0] = "";
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
            status = "Error when reading file:" + fileName + "\n Pls check log file for details.";
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
     * Sorts the BMS data and then compute the average of the data
     *
     * @param outputLines
     * @param csvWriter
     */
    private static void processBMSDate(List<List<String>> outputLines, CSVWriter csvWriter) {
        List<String> firstOutputLine = outputLines.get(0);
        csvWriter.writeNext(firstOutputLine.toArray(new String[firstOutputLine.size()]));
        outputLines.remove(0);
        Collections.sort(outputLines, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(0).compareTo(o2.get(0));
            }
        });
        while (outputLines.size() > 0) {
            List<String> line = outputLines.get(0);
            outputLines.remove(0);
            int count = 0;
            while (outputLines.size() > 0 && outputLines.get(0).get(0).equalsIgnoreCase(line.get(0))) {
                count++;
                List<String> nextLine = outputLines.get(0);
                outputLines.remove(0);
                for (int i = 1; i < line.size(); i++) {
                    if (!line.get(i).equalsIgnoreCase("") && nextLine.get(i).equalsIgnoreCase(""))
                        line.set(i, String.valueOf(Integer.valueOf(line.get(i)) + Integer.valueOf(nextLine.get(i))));
                    else if (!nextLine.get(i).equalsIgnoreCase(""))
                        line.set(i, nextLine.get(i));
                }
            }
            for (int i = 1; i < line.size(); i++) {
                if (!line.get(i).equalsIgnoreCase(""))
                    line.set(i, String.valueOf(Integer.valueOf(line.get(i)) / count));
            }
            csvWriter.writeNext(line.toArray(new String[line.size()]));
        }
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

    static class MultiLineTableHeaderRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineTableHeaderRenderer() {
            setEditable(false);
            setLineWrap(true);
            setOpaque(false);
            setFocusable(false);
            setWrapStyleWord(true);
            LookAndFeel.installBorder(this, "TableHeader.cellBorder");
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int width = table.getColumnModel().getColumn(column).getWidth();
            setText((String) value);
            setSize(width, getPreferredSize().height);
            return this;
        }
    }

}