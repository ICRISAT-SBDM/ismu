package com.icrisat.sbdm.ismu.ui.openDialog.components.connectionPanel;

import com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel.LoginPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Creates a panel with a url panel and a connect button.
 */
public class ConnectionPanel extends JPanel {
    private JTextField urlField;
    private JPasswordField passwordField;
    private JTextField usernameField;
    String url, urlName, userName, password;
    SharedInformation sharedInformation;
    private LoginPanel loginPanel;

    /**
     * Creates an URL panel.
     * When connect button is clicked the corresponding login panel is invoked.
     *
     * @param sharedInformation Information shared across the application.
     * @param loginPanel        Login panel corresponding to the type of data.
     */
    public ConnectionPanel(SharedInformation sharedInformation, LoginPanel loginPanel) {
        this.sharedInformation = sharedInformation;
        setLayout(new GridBagLayout());
        setSize(new Dimension(400, 180));
        initialize();

        this.loginPanel = loginPanel;
/*
        setLayout(new BorderLayout());
        setSize(new Dimension(500, 350));
        JButton connect = new JButton("Connect");
        connect.setFont(sharedInformation.getBoldFont());

        setBorder(Util.getCompoundBorder(title, sharedInformation));
        setLayout(new FlowLayout());

        add(urlField, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(connect, BorderLayout.SOUTH);

        connect.addActionListener(this::connect);
*/
    }

    private void initialize() {
        url = null;
        urlName = null;
        userName = null;
        password = null;

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

        JLabel logo = new JLabel();
        try {
            logo.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/bms.jpg"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for bms." + e.toString() + "\t" + e.getMessage());
            logo.setText("BMS");
        }
        JLabel logo1 = new JLabel();
        try {
            logo1.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/gobii.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for bms." + e.toString() + "\t" + e.getMessage());
            logo1.setText("gobii");
        }

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

        //  connect.addActionListener(this);
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
