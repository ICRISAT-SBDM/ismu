package com.icrisat.sbdm.ismu.ui.columnSelection;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ColumnSelectionPanel extends JPanel {
    private SharedInformation sharedInformation;
    private JList selectedColumns, allColumns;

    public JList getSelectedColumns() {
        return selectedColumns;
    }

    JList getAllColumns() {
        return allColumns;
    }

    @Autowired
    public ColumnSelectionPanel(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
        setBorder(Util.getCompoundBorder("Traits required", sharedInformation));
        setSize(new Dimension(600, 300));

        setLayout(new GridLayout(0, 3));
        allColumns = new JList(new DefaultListModel());
        allColumns.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        add(new JScrollPane(allColumns, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        add(getButtonGroup());
        selectedColumns = new JList(new DefaultListModel());
        selectedColumns.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        add(new JScrollPane(selectedColumns, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }

    /**
     * Gets the button group panel.
     *
     * @return Panel
     */
    private JPanel getButtonGroup() {
        JButton select = new JButton("  >  ");
        select.setFont(sharedInformation.getBoldFont());
        select.addActionListener(e -> moveAction(allColumns, selectedColumns));
        JButton selectAll = new JButton(" >> ");
        selectAll.setFont(sharedInformation.getBoldFont());
        selectAll.addActionListener(e -> moveAllAction(allColumns, selectedColumns));
        JButton clear = new JButton("  <  ");
        clear.setFont(sharedInformation.getBoldFont());
        clear.addActionListener(e -> moveAction(selectedColumns, allColumns));
        JButton clearAll = new JButton(" << ");
        clearAll.addActionListener(e -> moveAllAction(selectedColumns, allColumns));
        clearAll.setFont(sharedInformation.getBoldFont());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 3));

        Util.addDummyLabels(buttonPanel, 3);
        Util.addDummyLabels(buttonPanel, 3);
        addButton(selectAll, buttonPanel);
        addButton(select, buttonPanel);
        addButton(clear, buttonPanel);
        addButton(clearAll, buttonPanel);
        Util.addDummyLabels(buttonPanel, 3);
        Util.addDummyLabels(buttonPanel, 3);
        return buttonPanel;
    }

    /**
     * Move all the fields
     *
     * @param fromColumns from traits list
     * @param toColumns   to traits list
     */
    private void moveAllAction(JList fromColumns, JList toColumns) {
        DefaultListModel fromColumnsModel = (DefaultListModel) fromColumns.getModel();
        DefaultListModel toColumnsModel = (DefaultListModel) toColumns.getModel();
        int noOfElements = fromColumnsModel.getSize();
        for (int i = 0; i < noOfElements; i++) {
            toColumnsModel.addElement(fromColumnsModel.get(0));
            fromColumnsModel.removeElement(fromColumnsModel.get(0));
        }
        fromColumns.setModel(fromColumnsModel);
        toColumns.setModel(toColumnsModel);
    }

    /**
     * Move an item from one list to another.
     *
     * @param fromColumns Column list
     * @param toColumns   column list.
     */
    private void moveAction(JList fromColumns, JList toColumns) {
        if (fromColumns.getSelectedIndex() != -1) {
            DefaultListModel fromColumnsModel = (DefaultListModel) fromColumns.getModel();
            DefaultListModel toColumnsModel = (DefaultListModel) toColumns.getModel();
            java.util.List<String> selectedValuesList = fromColumns.getSelectedValuesList();
            for (String selectedValue : selectedValuesList) {
                toColumnsModel.addElement(selectedValue);
                fromColumnsModel.removeElement(selectedValue);
            }
            fromColumns.setModel(fromColumnsModel);
            toColumns.setModel(toColumnsModel);
        }
    }

    /**
     * Adds the button preceded and succeeded by empty space
     *
     * @param button      Button
     * @param buttonPanel Panel
     */
    private void addButton(JButton button, JPanel buttonPanel) {
        buttonPanel.add(new JLabel("  "));
        buttonPanel.add(button);
        buttonPanel.add(new JLabel("  "));
    }

    public String populateAllColumns(List<String> headers) {
        DefaultListModel allColumnsModel = (DefaultListModel) allColumns.getModel();
        allColumnsModel.clear();
        DefaultListModel selectedColumnsModel = (DefaultListModel) selectedColumns.getModel();
        selectedColumnsModel.clear();
        if (headers.size() != 0) {
            for (String field : headers) {
                allColumnsModel.addElement(field);
            }
            getAllColumns().setModel(allColumnsModel);
            getSelectedColumns().setModel(selectedColumnsModel);
            return Constants.SUCCESS;
        } else return "There are no triats in the selected file.";
    }
}
