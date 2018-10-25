/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icrisat.sbdm.ismu.ui.mainFrame;

import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author Chaitanya
 */
@Component
public class HomeTab extends JPanel {

    public HomeTab(){
        JLabel background = new JLabel();
        setName("Home");
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/homeBackground.png")));
        add(background);
    }
}
