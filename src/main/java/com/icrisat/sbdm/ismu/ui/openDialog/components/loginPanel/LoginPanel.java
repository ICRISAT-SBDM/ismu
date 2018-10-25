package com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Abstract class which is the basic login panel for connecting to any database.
 */
public abstract class LoginPanel extends JDialog implements ActionListener {

    private JPasswordField passwordField;
    private JTextField usernameField;
    String url, userName, password;
    SharedInformation sharedInformation;

    LoginPanel(SharedInformation sharedInformation, String title) {
        super(sharedInformation.getMainFrame(), title, ModalityType.APPLICATION_MODAL);
        this.sharedInformation = sharedInformation;
        setSize(new Dimension(300, 150));
        setLocation(Util.getLocation(300, 150));
        setLocationRelativeTo(sharedInformation.getMainFrame());

        setLayout(new GridBagLayout());
        initialize();
    }

    private void setURL(String url) {
        this.url = url;
    }

    void clearPasswordField() {
        passwordField.setText("");
    }

    /**
     * Add components to login panel.
     */
    private void initialize() {
        url = null;
        userName = null;
        password = null;

        JLabel usernameLbl = new JLabel("USERNAME");
        usernameLbl.setFont(sharedInformation.getOkButtonFont());
        usernameLbl.setHorizontalTextPosition(JLabel.LEFT);

        usernameField = new JTextField(20);

        JLabel passwordLbl = new JLabel("PASSWORD");
        passwordLbl.setFont(sharedInformation.getOkButtonFont());
        passwordLbl.setHorizontalTextPosition(JLabel.LEFT);

        passwordField = new JPasswordField(20);

        JButton connect = new JButton("Login");
        connect.setFont(sharedInformation.getBoldFont());

        int x = 0;
        int y = 0;
        GridBagConstraints gc_userNameLbl = new GridBagConstraints();
        gc_userNameLbl.gridx = x;
        gc_userNameLbl.gridy = y;
        gc_userNameLbl.anchor = GridBagConstraints.FIRST_LINE_START;
        gc_userNameLbl.insets = new Insets(5, 0, 0, 0);
        add(usernameLbl, gc_userNameLbl);

        y = y + 1;
        Insets insets = new Insets(0, 0, 5, 0);
        GridBagConstraints gc_userNameTxt = new GridBagConstraints();
        gc_userNameTxt.gridx = x;
        gc_userNameTxt.gridy = y;
        gc_userNameTxt.anchor = GridBagConstraints.LINE_START;
        gc_userNameTxt.insets = insets;
        add(usernameField, gc_userNameTxt);

        y = y + 1;
        GridBagConstraints gc_passwordLbl = new GridBagConstraints();
        gc_passwordLbl.gridx = x;
        gc_passwordLbl.gridy = y;
        gc_passwordLbl.anchor = GridBagConstraints.LINE_START;
        gc_passwordLbl.insets = insets;
        add(passwordLbl, gc_passwordLbl);

        y = y + 1;
        GridBagConstraints gc_passwordTxt = new GridBagConstraints();
        gc_passwordTxt.gridx = x;
        gc_passwordTxt.gridy = y;
        gc_passwordTxt.anchor = GridBagConstraints.LINE_START;
        gc_passwordTxt.insets = insets;
        add(passwordField, gc_passwordTxt);

        y = y + 1;
        GridBagConstraints gc_connect = new GridBagConstraints();
        gc_connect.gridx = x;
        gc_connect.gridy = y;
        gc_connect.insets = insets;
        gc_connect.anchor = GridBagConstraints.LAST_LINE_END;
        add(connect, gc_connect);

        connect.addActionListener(this);
    }

    /**
     * Validates username and password.
     */
    boolean validateUserFields() {
        userName = usernameField.getText().trim();
        password = String.valueOf(passwordField.getPassword());
        password = password.trim();
        if (userName == null || password == null || userName.isEmpty() || password.isEmpty()) {
            Util.showMessageDialog("Enter username and password.");
            return false;
        }
        return true;
    }

    public void setVisible(String urlText, boolean value) {
        setURL(urlText);
        setVisible(value);
    }
}
