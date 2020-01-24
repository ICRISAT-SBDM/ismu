package com.icrisat.sbdm.ismu.ui;

import javax.swing.*;
import java.awt.*;

public class SubmitPanel extends JPanel {

    public JButton submit, cancel;

    public SubmitPanel(Font font) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        submit = new JButton("  Ok  ");
        submit.setFont(font);
        cancel = new JButton("Cancel");
        cancel.setFont(font);
        add(submit);
        add(cancel);
    }
}
