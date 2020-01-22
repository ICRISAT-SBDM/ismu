package com.icrisat.sbdm.ismu.ui.openDialog.components.connectionPanel;

import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii.GOBIIDataSelectionPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii.GOBIIDataSetTable;
import com.icrisat.sbdm.ismu.ui.openDialog.components.phenotype.bms.BMSDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Creates a panel with a url panel and a connect button.
 */
public class ConnectionPanel extends JPanel {
    private JTextField urlField, usernameField;
    private JPasswordField passwordField;
    String url, userName, password, type;
    JButton connect;
    JDialog parentDialogBox;
    SharedInformation sharedInformation;

    /**
     * Creates an URL panel.
     * When connect button is clicked the corresponding login panel is invoked.
     *
     * @param sharedInformation Information shared across the application.
     * @param type              BMS/GOBII.
     * @param dialogBox
     */
    public ConnectionPanel(SharedInformation sharedInformation, String type, JDialog dialogBox) {
        this.sharedInformation = sharedInformation;
        this.type = type;
        this.parentDialogBox = dialogBox;
        setLayout(new GridBagLayout());
        setSize(new Dimension(400, 180));
        url = null;
        userName = null;
        password = null;
        connect = null;
        /*******************************URL************************************/
        JLabel urlNameLbl = new JLabel("URL");
        urlNameLbl.setFont(sharedInformation.getOkButtonFont());
        urlNameLbl.setHorizontalTextPosition(JLabel.LEFT);

        urlField = new JTextField(20);

        /*******************************Username************************************/
        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setFont(sharedInformation.getOkButtonFont());
        usernameLbl.setHorizontalTextPosition(JLabel.LEFT);

        usernameField = new JTextField(20);

        /*******************************Password************************************/
        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setFont(sharedInformation.getOkButtonFont());
        passwordLbl.setHorizontalTextPosition(JLabel.LEFT);

        passwordField = new JPasswordField(20);

        /*******************************Login************************************/
        JButton connect = new JButton("Login");
        connect.setFont(sharedInformation.getBoldFont());

        Insets bottomInsets = new Insets(0, 0, 10, 0);
        Insets leftBottomInsets = new Insets(0, 10, 10, 0);
        int x = 0;
        int y = 0;
        add(urlNameLbl, getGridBag(x, y, GridBagConstraints.FIRST_LINE_START, new Insets(25, 0, 10, 0)));

        x = x + 1;
        add(urlField, getGridBag(x, y, GridBagConstraints.LINE_END, new Insets(25, 10, 10, 0)));

        x = 0;
        y = y + 1;
        add(usernameLbl, getGridBag(x, y, GridBagConstraints.LINE_START, bottomInsets));

        x = x + 1;
        add(usernameField, getGridBag(x, y, GridBagConstraints.LINE_END, leftBottomInsets));

        x = 0;
        y = y + 1;
        add(passwordLbl, getGridBag(x, y, GridBagConstraints.LINE_START, bottomInsets));

        x = x + 1;
        add(passwordField, getGridBag(x, y, GridBagConstraints.LINE_END, leftBottomInsets));

        y = y + 1;
        GridBagConstraints gridBag = getGridBag(x, y, GridBagConstraints.LINE_END, bottomInsets);
        gridBag.ipadx = 20;
        gridBag.ipady = 20;
        add(connect, gridBag);
        connect.addActionListener(this::connect);
    }

    private GridBagConstraints getGridBag(int x, int y, int anchor, Insets insets) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.anchor = anchor;
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }

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

    private void loginGOBII() {
        GOBIIRetrofitClient client = sharedInformation.getGobiiRetrofitClient();
        String status = client.authenticate(url, userName, password,sharedInformation);
        // Retry logic.
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            status = client.authenticate(url, userName, password,sharedInformation);
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        } else {
            parentDialogBox.setVisible(false);
            new GOBIIDataSelectionPanel(sharedInformation, new GOBIIDataSetTable());
        }
        clearPasswordField();
    }

    private void loginBMS() {
        BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
        String status = client.authenticate(url, userName, password);
        // Retry logic.
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            status = client.authenticate(url, userName, password);
        }
        clearPasswordField();
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            if (status.equalsIgnoreCase(Constants.URL_ISSUE))
                urlField.setText("");
            Util.showMessageDialog(status);
        } else {
            parentDialogBox.setVisible(false);
            BMSDataSelectionPanel bmsDataSelectionPanel = new BMSDataSelectionPanel(sharedInformation);
            bmsDataSelectionPanel.setVisible(true);
        }
    }

    private String validateInput() {
        String status = Constants.SUCCESS;

        url = urlField.getText().trim();
        userName = usernameField.getText().trim();
        password = String.valueOf(passwordField.getPassword());
        password = password.trim();

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
