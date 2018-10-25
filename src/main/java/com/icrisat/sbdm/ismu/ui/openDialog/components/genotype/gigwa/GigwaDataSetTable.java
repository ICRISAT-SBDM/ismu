package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gigwa;

import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.GenotypeDataSetTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GigwaDataSetTable extends GenotypeDataSetTable {
    public GigwaDataSetTable() {
        super(new String[]{"  Name  ", "  Study DB Id  ", "  Program Name  "});
    }
}
