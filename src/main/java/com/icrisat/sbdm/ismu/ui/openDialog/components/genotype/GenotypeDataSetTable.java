package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GenotypeDataSetTable {
    public JTable table;

    protected GenotypeDataSetTable(String[] headers) {
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
