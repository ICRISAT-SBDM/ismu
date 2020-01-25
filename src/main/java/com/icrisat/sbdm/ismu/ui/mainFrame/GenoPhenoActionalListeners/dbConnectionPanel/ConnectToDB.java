package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel;

import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.gobii.GOBIIDataSelectionPanel;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.bms.BMSDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Creates a panel with a url panel and a connect button.
 */
public class ConnectToDB {
    private JTextField urlField, usernameField;
    private JPasswordField passwordField;
    String url, userName, password, type;
    JButton connect;
    JDialog dialog;
    SharedInformation sharedInformation;


    /**
     * Creates login dialog for both bms and gobii
     *
     * @param sharedInformation shared information
     * @param type              BMS/GOBII
     */
    public ConnectToDB(SharedInformation sharedInformation, String type) {
        this.sharedInformation = sharedInformation;
        if (type.equalsIgnoreCase(Constants.GOBII))
            dialog = new JDialog(sharedInformation.getMainFrame(), "Connect to GOBii", Dialog.ModalityType.APPLICATION_MODAL);
        else
            dialog = new JDialog(sharedInformation.getMainFrame(), "Connect to BMS", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(new Dimension(400, 250));
        dialog.setLocation(Util.getLocation(400, 250));
        dialog.setLocationRelativeTo(sharedInformation.getMainFrame());
        dialog.setLayout(new BorderLayout());
        if (type.equalsIgnoreCase(Constants.GOBII)) {
            dialog.add(createConnectionPanel(Constants.GOBII), BorderLayout.CENTER);
            dialog.add(new LogoFooterPanel(sharedInformation, Constants.GOBII), BorderLayout.SOUTH);
        } else {
            dialog.add(createConnectionPanel(Constants.BMS), BorderLayout.CENTER);
            dialog.add(new LogoFooterPanel(sharedInformation, Constants.BMS), BorderLayout.SOUTH);
        }
        setVisible(true);
    }

    /**
     * Creates an URL panel.
     * When connect button is clicked the corresponding login panel is invoked.
     *
     * @param type BMS/GOBII.
     */
    public JPanel createConnectionPanel(String type) {
        this.type = type;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setSize(new Dimension(400, 180));
        url = null;
        userName = null;
        password = null;
        connect = null;

        JLabel urlNameLbl = new JLabel("URL");
        urlNameLbl.setFont(sharedInformation.getOkButtonFont());
        urlNameLbl.setHorizontalTextPosition(JLabel.LEFT);

        urlField = new JTextField(20);

        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setFont(sharedInformation.getOkButtonFont());
        usernameLbl.setHorizontalTextPosition(JLabel.LEFT);

        usernameField = new JTextField(20);

        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setFont(sharedInformation.getOkButtonFont());
        passwordLbl.setHorizontalTextPosition(JLabel.LEFT);

        passwordField = new JPasswordField(20);

        JButton connect = new JButton("Login");
        connect.setFont(sharedInformation.getBoldFont());

        Insets bottomInsets = new Insets(0, 0, 10, 0);
        Insets leftBottomInsets = new Insets(0, 10, 10, 0);
        int x = 0;
        int y = 0;
        panel.add(urlNameLbl, getGridBag(x, y, GridBagConstraints.FIRST_LINE_START, new Insets(25, 0, 10, 0)));

        x = x + 1;
        panel.add(urlField, getGridBag(x, y, GridBagConstraints.LINE_END, new Insets(25, 10, 10, 0)));

        x = 0;
        y = y + 1;
        panel.add(usernameLbl, getGridBag(x, y, GridBagConstraints.LINE_START, bottomInsets));

        x = x + 1;
        panel.add(usernameField, getGridBag(x, y, GridBagConstraints.LINE_END, leftBottomInsets));

        x = 0;
        y = y + 1;
        panel.add(passwordLbl, getGridBag(x, y, GridBagConstraints.LINE_START, bottomInsets));

        x = x + 1;
        panel.add(passwordField, getGridBag(x, y, GridBagConstraints.LINE_END, leftBottomInsets));

        y = y + 1;
        GridBagConstraints gridBag = getGridBag(x, y, GridBagConstraints.LINE_END, bottomInsets);
        gridBag.ipadx = 20;
        gridBag.ipady = 20;
        panel.add(connect, gridBag);
        connect.addActionListener(this::connect);
        return panel;
    }


    private GridBagConstraints getGridBag(int x, int y, int anchor, Insets insets) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.anchor = anchor;
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }

    /**
     * Connect panel action item
     *
     * @param e action event
     */
    private void connect(ActionEvent e) {
        String status = validateInput();
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            clearPasswordField();
            Util.showMessageDialog(status);
            return;
        }
        if (type.equalsIgnoreCase(Constants.BMS))
            loginBMS();
        if (type.equalsIgnoreCase(Constants.GOBII))
            loginGOBII();
    }

    /**
     * Logs into gobii with 1 retry.
     * If successful open data-selection panel
     * Else displays a message with issue
     */
    private void loginGOBII() {
        GOBIIRetrofitClient client = sharedInformation.getGobiiRetrofitClient();
        String status = client.authenticate(url, userName, password, sharedInformation.getLogger());
        // Retry logic.
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            status = client.authenticate(url, userName, password, sharedInformation.getLogger());
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        } else {
            setVisible(false);
            new GOBIIDataSelectionPanel(sharedInformation, new SelectionTable(Constants.GOBII));
        }
        clearPasswordField();
    }

    /**
     * Logs into gobii with 1 retry.
     * If successful open data-selection panel
     * Else displays a message with issue
     */
    private void loginBMS() {
        BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
        String status = client.authenticate(url, userName, password, sharedInformation.getLogger());
        // Retry logic.
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            status = client.authenticate(url, userName, password, sharedInformation.getLogger());
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        } else {
            setVisible(false);
            new BMSDataSelectionPanel(sharedInformation, new SelectionTable(Constants.BMS));
        }
        clearPasswordField();
    }

    public void setVisible(boolean visible) {
        dialog.setVisible(visible);
    }

    private String validateInput() {

        url = urlField.getText().trim();
        userName = usernameField.getText().trim();
        password = String.valueOf(passwordField.getPassword());
        password = password.trim();
        password = "abcd@1234";
        userName = "arathore";
        url = "bms.icrisat.ac.in:48080/bmsapi/";
        if (url.equalsIgnoreCase(""))
            return "Please enter a valid URL.";
        if (!(url.contains("http") || url.contains("HTTP") || url.contains("https") || url.contains("HTTPS")))
            url = "http://" + url;
        if (userName == null || password == null || userName.isEmpty() || password.isEmpty())
            return "Enter username and password.";
        return Constants.SUCCESS;
    }

    private void clearPasswordField() {
        passwordField.setText("");
    }
}
