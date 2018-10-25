package com.icrisat.sbdm.ismu.ui.openDialog;

import javax.swing.*;
import java.awt.*;

public class GenoPhenoPanel extends JPanel {

    public JButton btnBrowse, btnConnect;
    JTextField txtbox;
    private Font font;

    public GenoPhenoPanel(Font font, String label) {
        this.font = font;
        setLayout(new FlowLayout());
        // -----------------------------Geno----------------------------------------
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);

        txtbox = new JTextField(20);
        txtbox.setEditable(false);
        txtbox.setFont(font);

        btnBrowse = createNonEditableButton("Browse");
        btnConnect = createNonEditableButton("Connect");

        add(lbl);
        add(txtbox);
        add(btnBrowse);
        add(btnConnect);
        }

    /**
     * Create a non editable button with label.
     *
     * @param btnLabel Label.
     * @return button.
     */
    private JButton createNonEditableButton(String btnLabel) {
        JButton button = new JButton(btnLabel);
        button.setFont(font);
        button.setEnabled(false);
        return button;
    }
}
