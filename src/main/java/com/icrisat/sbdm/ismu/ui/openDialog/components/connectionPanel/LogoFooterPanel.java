package com.icrisat.sbdm.ismu.ui.openDialog.components.connectionPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class LogoFooterPanel extends JPanel {
    public LogoFooterPanel(SharedInformation sharedInformation, String imageName) {
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        JLabel serviceLogo = new JLabel();
        try {
            if (imageName.equalsIgnoreCase("bms"))
                serviceLogo.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/bms.jpg"))));
            else
                serviceLogo.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/gobii.png"))));

        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for ." + imageName + e.toString() + "\t" + e.getMessage());
            serviceLogo.setText(imageName);
        }
        JLabel brapiLogo = new JLabel();
        try {
            brapiLogo.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/brapi.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for brapi." + e.toString() + "\t" + e.getMessage());
            brapiLogo.setText("BrAPI");
        }

        add(serviceLogo);
        add(new JLabel("   "));
        add(brapiLogo);
    }

}
