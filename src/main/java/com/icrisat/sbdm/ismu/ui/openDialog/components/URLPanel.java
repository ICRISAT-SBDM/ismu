package com.icrisat.sbdm.ismu.ui.openDialog.components;

import com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel.LoginPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Creates a panel with a url panel and a connect button.
 */
class URLPanel extends JPanel {
    private CustomTextField urlField;
    private LoginPanel loginPanel;

    /**
     * Creates an URL panel.
     * When connect button is clicked the corresponding login panel is invoked.
     *
     * @param sharedInformation Information shared across the application.
     * @param title             Title of the panel.
     * @param loginPanel        Login panel corresponding to the type of data.
     */
    URLPanel(SharedInformation sharedInformation, String title, LoginPanel loginPanel) {

        this.loginPanel = loginPanel;
        urlField = new CustomTextField(30);
        urlField.setPlaceholder("URL");

        JButton connect = new JButton("Connect");
        connect.setFont(sharedInformation.getBoldFont());

        setBorder(Util.getCompoundBorder(title, sharedInformation));
        setLayout(new FlowLayout());

        add(urlField);
        add(connect);

        connect.addActionListener(this::connect);
    }

    private void connect(ActionEvent e) {
        String status = Constants.SUCCESS;
        String urlString = urlField.getText().trim();
        if (urlString.equalsIgnoreCase("URL"))
            status = "Please enter a valid URL.";
        if (!(urlString.contains("http") || urlString.contains("HTTP")))
            urlString = "http://" + urlString;
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        } else {
            loginPanel.setVisible(urlString, true);
        }
    }
}
