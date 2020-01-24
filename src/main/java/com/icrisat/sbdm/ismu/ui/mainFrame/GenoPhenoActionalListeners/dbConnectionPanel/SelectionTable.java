package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel;

import com.icrisat.sbdm.ismu.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Generic selection table for both BMS and GOBII
 * Type is defined by type parameter of the constructor which in-turn defines the headers for the table
 */
public class SelectionTable {
    public JTable table;
    public DefaultTableModel defaultTableModel;

    public SelectionTable(String type) {
        if (type.equalsIgnoreCase(Constants.GOBII)) {
            defaultTableModel = new DefaultTableModel(Constants.gobiiHeaders, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        } else {
            defaultTableModel = new DefaultTableModel(Constants.bmsHeaders, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }
        table = new JTable(defaultTableModel);
        table.setAutoCreateRowSorter(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
