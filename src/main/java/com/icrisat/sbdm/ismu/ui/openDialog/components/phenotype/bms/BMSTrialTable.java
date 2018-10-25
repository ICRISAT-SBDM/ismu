package com.icrisat.sbdm.ismu.ui.openDialog.components.phenotype.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class BMSTrialTable {
    JTable table;

    BMSTrialTable() {
        String[] headers = {"  Crop  ", "  Program  ", "  Trial  ", "  Study  ", "  Location  ", "Trial DB Id", "Study DB Id"};
        DefaultTableModel tableModel = new DefaultTableModel(headers, 0) {
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