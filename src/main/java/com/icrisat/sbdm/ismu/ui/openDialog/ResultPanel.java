package com.icrisat.sbdm.ismu.ui.openDialog;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    JButton btnResult;
    public JTextField txtResult;

    ResultPanel(Font font) {

        setLayout(new FlowLayout());
        // -----------------------------Geno----------------------------------------
        JLabel lblResult = new JLabel(" Result directory");
        lblResult.setFont(font);

        txtResult = new JTextField(20);
        txtResult.setEditable(false);
        txtResult.setFont(font);

        btnResult = new JButton("           Browse          ");
        btnResult.setFont(font);

        add(lblResult);
        add(txtResult);
        add(btnResult);
    }
}
