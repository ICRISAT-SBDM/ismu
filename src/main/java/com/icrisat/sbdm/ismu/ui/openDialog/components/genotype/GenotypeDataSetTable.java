package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GenotypeDataSetTable {
    public JTable table;
    public DefaultTableModel defaultTableModel;

    protected GenotypeDataSetTable(String[] headers) {
        defaultTableModel = new DefaultTableModel(headers,0);
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
