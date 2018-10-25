package com.icrisat.sbdm.ismu.ui.mainFrame;

import com.icrisat.sbdm.ismu.ui.AboutDialog;
import com.icrisat.sbdm.ismu.ui.analysis.Analysis;
import com.icrisat.sbdm.ismu.ui.components.ColumnSelection;
import com.icrisat.sbdm.ismu.ui.components.ColumnSelectionPanel;
import com.icrisat.sbdm.ismu.ui.dataSummary.DataSummary;
import com.icrisat.sbdm.ismu.ui.openDialog.OpenDialog;
import com.icrisat.sbdm.ismu.ui.openDialog.components.GenotypeDB;
import com.icrisat.sbdm.ismu.ui.openDialog.components.PhenotypeDB;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

@Component("createMainFrameComponents")
public class CreateMainFrameComponents {
    private SharedInformation sharedInformation;
    private HomeTab homeTab;
    private DynamicTree dynamicTree;
    private MainFrameActionListeners mainFrameActionListeners;
    /**
     * Below components are initialized here.
     */
    private OpenDialog openDialog;
    private PhenotypeDB phenotypeDB;
    private GenotypeDB genotypeDB;
    private DataSummary dataSummary;
    private Analysis analysis;
    private ColumnSelection columnSelection;
    private ColumnSelectionPanel columnSelectionPanel;
    private AboutDialog aboutDialog;

    @Autowired
    public void setAboutDialog(AboutDialog aboutDialog) {
        this.aboutDialog = aboutDialog;
    }

    @Autowired
    public void setColumnSelectionPanel(ColumnSelectionPanel columnSelectionPanel) {
        this.columnSelectionPanel = columnSelectionPanel;
    }

    @Autowired
    public void setColumnSelection(ColumnSelection columnSelection) {
        this.columnSelection = columnSelection;
    }

    @Autowired
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    public void setDataSummary(DataSummary dataSummary) {
        this.dataSummary = dataSummary;
    }

    @Autowired
    public void setGenotypeDB(GenotypeDB genotypeDB) {
        this.genotypeDB = genotypeDB;
    }

    @Autowired
    public void setPhenotypeDB(PhenotypeDB phenotypeDB) {
        this.phenotypeDB = phenotypeDB;
    }

    @Autowired
    private void setOpenDialog(OpenDialog openDialog) {
        this.openDialog = openDialog;
    }

    @Autowired
    private void setMainFrameActionListeners(MainFrameActionListeners mainFrameActionListeners) {
        this.mainFrameActionListeners = mainFrameActionListeners;
    }

    @Autowired
    private void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    private void setHomeTab(HomeTab homeTab) {
        this.homeTab = homeTab;
    }

    @Autowired
    private void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    /**
     * Sets frame properties for main-frame
     */
    public void setFrameProperties() {
        Frame frame = sharedInformation.getMainFrame();
        frame.setSize(1000, 1000);
        frame.setTitle("ISMU Pipeline");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/log_icrisat.png")));
        frame.setBackground(Color.WHITE);
        frame.setLayout(new BorderLayout());
    }

    /**
     * Creates menuBar for main-frame
     */
    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenu analysisMenu = new JMenu("Analysis");
        analysisMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(analysisMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        // Add menuItems to File

        JMenuItem newProjectMenuItem = new JMenuItem("New project");
        newProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(newProjectMenuItem);
        JMenuItem openProjectMenuItem = new JMenuItem("Open project");
        openProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(openProjectMenuItem);
        JMenuItem saveProjectMenuItem = new JMenuItem("Save project");
        saveProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(saveProjectMenuItem);
        fileMenu.addSeparator();


        JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(openMenuItem);
        JMenuItem importMenuItem = new JMenuItem("Import", KeyEvent.VK_I);
        importMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(importMenuItem);
        JMenuItem genotypeFileMenuItem = new JMenuItem("Genotype file", KeyEvent.VK_G);
        genotypeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        genotypeFileMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(genotypeFileMenuItem);
        JMenuItem phenotypeFileMenuItem = new JMenuItem("Phenotype file", KeyEvent.VK_P);
        phenotypeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        phenotypeFileMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(phenotypeFileMenuItem);
        JMenuItem saveFilMenuItem = new JMenuItem("Save As", KeyEvent.VK_S);
        saveFilMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveFilMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(saveFilMenuItem);
        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(exitMenuItem);

        //Add menuItems to Analysis
        JMenuItem dataSummaryMenuItem = new JMenuItem("Data summary", KeyEvent.VK_D);
        dataSummaryMenuItem.setFont(sharedInformation.getOkButtonFont());
        analysisMenu.add(dataSummaryMenuItem);
        analysisMenu.addSeparator();
        JMenuItem genomicSelectionMenuItem = new JMenuItem("Genomic selection", KeyEvent.VK_G);
        genomicSelectionMenuItem.setFont(sharedInformation.getOkButtonFont());
        analysisMenu.add(genomicSelectionMenuItem);

        //Add menuItems to Help
        JMenuItem manualMenuItem = new JMenuItem("User manual", KeyEvent.VK_U);
        manualMenuItem.setFont(sharedInformation.getOkButtonFont());
        helpMenu.add(manualMenuItem);
        helpMenu.addSeparator();
        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.setFont(sharedInformation.getOkButtonFont());
        helpMenu.add(aboutMenuItem);

        sharedInformation.getMainFrame().setJMenuBar(menuBar);
        openMenuItem.addActionListener(e -> mainFrameActionListeners.openActionItem(e));
        genomicSelectionMenuItem.addActionListener(e -> mainFrameActionListeners.analysisActionItem(e));
        genotypeFileMenuItem.addActionListener(e -> mainFrameActionListeners.selectGenotypeFileActionItem(e));
        phenotypeFileMenuItem.addActionListener(e -> mainFrameActionListeners.selectPhenotypeFileActionItem(e));
        saveFilMenuItem.addActionListener(e -> mainFrameActionListeners.saveAsActionItem(e));
        newProjectMenuItem.addActionListener(e -> mainFrameActionListeners.newProjectActionItem(e));
        openProjectMenuItem.addActionListener(e -> mainFrameActionListeners.openProjectActionItem(e));
        saveProjectMenuItem.addActionListener(e -> mainFrameActionListeners.saveProjectActionItem(e));
        importMenuItem.addActionListener(e -> mainFrameActionListeners.importActionItem(e));
        exitMenuItem.addActionListener(e -> mainFrameActionListeners.exitApplicationActionItem(e));
        dataSummaryMenuItem.addActionListener(e -> mainFrameActionListeners.dataSummaryActionItem(e));
        aboutMenuItem.addActionListener(e -> aboutDialog.setVisible(true));
    }

    /**
     * Creates toolBar for main-frame
     */
    public void createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton btnOpen = new JButton("Open");
        btnOpen.setToolTipText("Selecting the Genotype,Phenotype files and result directory");
        try {
            btnOpen.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/open-file-icon.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for openButton." + e.toString() + "\t" + e.getMessage());
        }
        toolBar.add(btnOpen);

        JButton btnImport = new JButton("Import");
        btnImport.setToolTipText("Selecting the population file");
        try {
            btnImport.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/import.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for importButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnImport);

        JButton btnDataSummary = new JButton("Data summary");
        btnDataSummary.setToolTipText("Cleaning of data");
        try {
            btnDataSummary.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/dataSummary.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for summaryButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnDataSummary);

        JButton btnAnalysis = new JButton("Genomic selection");
        btnAnalysis.setToolTipText("Genomic selection");
        try {
            btnAnalysis.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/analysis.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for analysisButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnAnalysis);

        sharedInformation.getMainFrame().add(toolBar, BorderLayout.NORTH);
        btnOpen.addActionListener(e -> mainFrameActionListeners.openActionItem(e));
        btnImport.addActionListener(e -> mainFrameActionListeners.importActionItem(e));
        btnDataSummary.addActionListener(e -> mainFrameActionListeners.dataSummaryActionItem(e));
        btnAnalysis.addActionListener(e -> mainFrameActionListeners.analysisActionItem(e));
    }

    /**
     * Creates the body of the main-frame.
     * Main frame is a splitPane with a tree on the left side and tabbedPane on right-side.
     */
    public void createBody() {
        // Creates a split pane with dynamicTree and tabbedPane. These two are autowired using Spring.
        ClosableTabbedPane tabbedPane = new ClosableTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.add(homeTab);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(dynamicTree.getTreePane()), tabbedPane);
        sharedInformation.setTabbedPane(tabbedPane);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        sharedInformation.getMainFrame().add(splitPane);
    }

    /**
     * Creates an empty footer.
     * Create openDialog
     * Create PhenotypeDB
     */
    public void createFooterStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(new CompoundBorder(new LineBorder(Color.BLACK),
                new EmptyBorder(10, 10, 10, 10)));
        final JLabel status = new JLabel();
        statusBar.add(status);
        sharedInformation.getMainFrame().add(statusBar, BorderLayout.SOUTH);
        columnSelection.createDialog(sharedInformation);
        openDialog.createOpenDialog();
        phenotypeDB.createPhenotypeDB();
        genotypeDB.createGenotypeDB();
        dataSummary.createDialog();
        analysis.createDialog(columnSelectionPanel);
        aboutDialog.createDialog(sharedInformation.getMainFrame());
    }
}
