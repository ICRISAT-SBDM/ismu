package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii;

import com.icrisat.sbdm.ismu.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GOBIIDataSetTable {
    public JTable table;
    public DefaultTableModel defaultTableModel;

    public GOBIIDataSetTable() {
        defaultTableModel = new DefaultTableModel(Constants.gobiiHeaders, 0);
        DefaultTableModel tableModel = new DefaultTableModel(Constants.gobiiHeaders, 0) {
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
