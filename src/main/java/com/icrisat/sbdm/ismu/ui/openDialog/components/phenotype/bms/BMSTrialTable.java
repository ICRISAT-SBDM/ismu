package com.icrisat.sbdm.ismu.ui.openDialog.components.phenotype.bms;

import com.icrisat.sbdm.ismu.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class BMSTrialTable {
    JTable table;
    DefaultTableModel defaultTableModel;

    BMSTrialTable() {
         defaultTableModel = new DefaultTableModel(Constants.bmsHeaders,0);
        DefaultTableModel tableModel = new DefaultTableModel(Constants.bmsHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}