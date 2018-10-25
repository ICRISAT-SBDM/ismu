/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icrisat.sbdm.ismu.ui.mainFrame;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JTabbedPane;

/**
 *
 * @author Chaitanya
 * Implementing the close button on each tab opened in the application
 */
public class ClosableTabbedPane extends JTabbedPane {

    private TabCloseUI closeUI = new TabCloseUI(this);

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        closeUI.paint(g);
    }

    private class TabCloseUI extends MouseAdapter implements MouseMotionListener {

        private ClosableTabbedPane tabbedPane;
        private int closeX = 0, closeY = 0, meX = 0, meY = 0;
        private int selectedTab;
        private final int width = 8, height = 8;
        private Rectangle rectangle = new Rectangle(0, 0, width, height);

        TabCloseUI(ClosableTabbedPane pane) {
            tabbedPane = pane;
            tabbedPane.addMouseMotionListener(this);
            tabbedPane.addMouseListener(this);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (closeUnderMouse(me.getX(), me.getY())) {
                if (selectedTab > -1) {
                    tabbedPane.removeTabAt(selectedTab);
                }
                selectedTab = tabbedPane.getSelectedIndex();
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            meX = me.getX();
            meY = me.getY();
            if (mouseOverTab()) {
                controlCursor();
                tabbedPane.repaint();
            }
        }

        private void controlCursor() {
            if (tabbedPane.getTabCount() > 0) {
                if (closeUnderMouse(meX, meY)) {
                    tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (selectedTab > -1) {
                        tabbedPane.setToolTipTextAt(selectedTab, "Close " + tabbedPane.getTitleAt(selectedTab));
                    }
                } else {
                    tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }

        private boolean closeUnderMouse(int x, int y) {
            rectangle.x = closeX;
            rectangle.y = closeY;
            return rectangle.contains(x, y);
        }

        void paint(Graphics g) {

            int tabCount = tabbedPane.getTabCount();
            for (int j = 0; j < tabCount; j++) {
                if (tabbedPane.getComponent(j).isShowing()) {
                    int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width - width - 5;
                    int y = tabbedPane.getBoundsAt(j).y + 5;
                    drawClose(g, x, y);
                    break;
                }
            }
            if (mouseOverTab()) {
                drawClose(g, closeX, closeY);
            }
        }

        private void drawClose(Graphics g, int x, int y) {
            if (tabbedPane != null && tabbedPane.getTabCount() > 0) {
                Graphics2D g2 = (Graphics2D) g;
                drawColored(g2, isUnderMouse(x, y) ? Color.RED : Color.WHITE, x, y);
            }
        }

        private void drawColored(Graphics2D g2, Color color, int x, int y) {
            g2.setStroke(new BasicStroke(5, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, x + width, y + height);
            g2.drawLine(x + width, y, x, y + height);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
            g2.drawLine(x, y, x + width, y + height);
            g2.drawLine(x + width, y, x, y + height);

        }

        private boolean isUnderMouse(int x, int y) {
            return Math.abs(x - meX) < width && Math.abs(y - meY) < height;
        }

        private boolean mouseOverTab() {
            int tabCount = tabbedPane.getTabCount();
            for (int j = 0; j < tabCount; j++) {
                if (tabbedPane.getBoundsAt(j).contains(meX, meY)) {
                    selectedTab = j;
                    closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width - width - 5;
                    closeY = tabbedPane.getBoundsAt(j).y + 5;
                    return true;
                }
            }
            return false;
        }
    }
}
