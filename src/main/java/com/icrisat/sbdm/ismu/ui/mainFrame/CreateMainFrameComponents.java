package com.icrisat.sbdm.ismu.ui.mainFrame;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.icrisat.sbdm.ismu.ui.AboutDialog;
import com.icrisat.sbdm.ismu.ui.analysis.Analysis;
import com.icrisat.sbdm.ismu.ui.columnSelection.ColumnSelection;
import com.icrisat.sbdm.ismu.ui.columnSelection.ColumnSelectionPanel;
import com.icrisat.sbdm.ismu.ui.dataSummary.DataSummary;
import com.icrisat.sbdm.ismu.util.FileLocation;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("createMainFrameComponents")
public class CreateMainFrameComponents {
    private SharedInformation sharedInformation;
    private HomeTab homeTab;
    private DynamicTree dynamicTree;
    private MainFrameActionListeners mainFrameActionListeners;
    /**
     * Below components are initialized here.
     */
    private static JButton btnPhenotype, btnGenotype;
    private static JMenuItem phenotypeFileMenuItem, genotypeFileMenuItem;
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
    void setFrameProperties() {
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
    void createMenuBar() {
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

        JMenuItem newProjectMenuItem = new JMenuItem("New Project", KeyEvent.VK_N);
        newProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(newProjectMenuItem);
        JMenuItem openProjectMenuItem = new JMenuItem("Open Project", KeyEvent.VK_O);
        openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(openProjectMenuItem);
        JMenuItem saveProjectMenuItem = new JMenuItem("Save Project", KeyEvent.VK_S);
        saveProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveProjectMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(saveProjectMenuItem);
        fileMenu.addSeparator();


        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(openMenuItem);
        genotypeFileMenuItem = new JMenuItem("Genotype file", KeyEvent.VK_G);
        genotypeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        genotypeFileMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(genotypeFileMenuItem);

        phenotypeFileMenuItem = new JMenuItem("Phenotype file", KeyEvent.VK_P);
        phenotypeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        phenotypeFileMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(phenotypeFileMenuItem);
        JMenuItem saveFilMenuItem = new JMenuItem("Save As", KeyEvent.VK_S);
        saveFilMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));
        saveFilMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(saveFilMenuItem);
        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.setFont(sharedInformation.getOkButtonFont());
        fileMenu.add(exitMenuItem);

        //Add menuItems to Analysis
        JMenuItem dataSummaryMenuItem = new JMenuItem("Data Summary", KeyEvent.VK_D);
        dataSummaryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK));
        dataSummaryMenuItem.setFont(sharedInformation.getOkButtonFont());
        analysisMenu.add(dataSummaryMenuItem);
        analysisMenu.addSeparator();
        JMenuItem genomicSelectionMenuItem = new JMenuItem("Genomic Selection", KeyEvent.VK_G);
        genomicSelectionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.ALT_DOWN_MASK));
        genomicSelectionMenuItem.setFont(sharedInformation.getOkButtonFont());
        analysisMenu.add(genomicSelectionMenuItem);

        //Add menuItems to Help
        JMenuItem manualMenuItem = new JMenuItem("User Manual", KeyEvent.VK_U);
        manualMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK));
        manualMenuItem.setFont(sharedInformation.getOkButtonFont());
        helpMenu.add(manualMenuItem);
        helpMenu.addSeparator();
        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.setFont(sharedInformation.getOkButtonFont());
        helpMenu.add(aboutMenuItem);

        sharedInformation.getMainFrame().setJMenuBar(menuBar);
        genomicSelectionMenuItem.addActionListener(e -> mainFrameActionListeners.analysisActionItem(e));
        genotypeFileMenuItem.addActionListener(e -> mainFrameActionListeners.selectGenotypeFileActionItem(e));
        phenotypeFileMenuItem.addActionListener(e -> mainFrameActionListeners.selectPhenotypeFileActionItem(e));
        saveFilMenuItem.addActionListener(e -> mainFrameActionListeners.saveAsActionItem(e));
        newProjectMenuItem.addActionListener(e -> mainFrameActionListeners.newProjectActionItem(e));
        openProjectMenuItem.addActionListener(e -> mainFrameActionListeners.openProjectActionItem(e));
        saveProjectMenuItem.addActionListener(e -> mainFrameActionListeners.saveProjectActionItem(e));
        exitMenuItem.addActionListener(e -> mainFrameActionListeners.exitApplicationActionItem(e));
        dataSummaryMenuItem.addActionListener(e -> mainFrameActionListeners.dataSummaryActionItem(e));
        aboutMenuItem.addActionListener(e -> aboutDialog.setVisible(true));
    }

    /**
     * Creates toolBar for main-frame
     */
    void createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton btnNewProject = new JButton("New");
        btnNewProject.setToolTipText("Create a new project");
        try {
            btnNewProject.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/new.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for newProjectButton." + e.toString() + "\t" + e.getMessage());
        }
        toolBar.add(btnNewProject);

        JButton btnOpenProject = new JButton("Open");
        btnOpenProject.setToolTipText("Open an existing project");
        try {
            btnOpenProject.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/open.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for openProjectButton." + e.toString() + "\t" + e.getMessage());
        }
        toolBar.add(btnOpenProject);

        btnGenotype = new JButton("Genotype File");
        btnGenotype.setToolTipText("Selecting the Genotype File");
        try {
            btnGenotype.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/import.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for genotypeButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnGenotype);

        btnPhenotype = new JButton("Phenotype File");
        btnPhenotype.setToolTipText("Selecting the Phenotype File");
        try {
            btnPhenotype.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/import.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for phenotypeButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnPhenotype);

        JButton btnDataSummary = new JButton("Data Summary");
        btnDataSummary.setToolTipText("Cleaning of data");
        try {
            btnDataSummary.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/dataSummary.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for summaryButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnDataSummary);

        JButton btnAnalysis = new JButton("Genomic Selection");
        btnAnalysis.setToolTipText("Genomic selection");
        try {
            btnAnalysis.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/analysis.png"))));
        } catch (IOException e) {
            sharedInformation.getLogger().debug("Issue when setting the image for analysisButton." + e.toString() + "\t" + e.getMessage());

        }
        toolBar.add(btnAnalysis);

        sharedInformation.getMainFrame().add(toolBar, BorderLayout.NORTH);
        btnNewProject.addActionListener(e -> mainFrameActionListeners.newProjectActionItem(e));
        btnOpenProject.addActionListener(e -> mainFrameActionListeners.openProjectActionItem(e));
        btnGenotype.addActionListener(e -> mainFrameActionListeners.selectGenotypeFileActionItem(e));
        btnPhenotype.addActionListener(e -> mainFrameActionListeners.selectPhenotypeFileActionItem(e));
        btnDataSummary.addActionListener(e -> mainFrameActionListeners.dataSummaryActionItem(e));
        btnAnalysis.addActionListener(e -> mainFrameActionListeners.analysisActionItem(e));
    }

    /**
     * Creates the body of the main-frame.
     * Main frame is a splitPane with a tree on the left side and tabbedPane on right-side.
     */
    void createBody() {
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
     */
    void createFooterStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(new CompoundBorder(new LineBorder(Color.BLACK),
                new EmptyBorder(10, 10, 10, 10)));
        final JLabel status = new JLabel();
        statusBar.add(status);
        sharedInformation.getMainFrame().add(statusBar, BorderLayout.SOUTH);
        columnSelection.createDialog(sharedInformation);
        dataSummary.createDialog();
        analysis.createDialog(columnSelectionPanel);
        aboutDialog.createDialog(sharedInformation.getMainFrame());
    }

    public static void setEnableComponenents(boolean value) {
        genotypeFileMenuItem.setEnabled(value);
        phenotypeFileMenuItem.setEnabled(value);
        btnGenotype.setEnabled(value);
        btnPhenotype.setEnabled(value);
    }

    void createLogger(URL path) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        String resultDir = sharedInformation.getWorkingDirectory() + "/";
        String logDir = resultDir + new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());
        new File(logDir).mkdir();
        String logName = "IsmuLogFile.txt";
        System.setProperty("log.dir", logDir);
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        try {
            jc.doConfigure(path);
        } catch (JoranException e) {
            e.printStackTrace();
        }
        Logger logger = LoggerFactory.getLogger(this.getClass());
        sharedInformation.setLogger(logger);
        logger.info("Logger Started");

        FileLocation logFile = new FileLocation(logName, logDir + "/" + logName);
        if (dynamicTree.getLogNode().getChildCount() == 0)
            dynamicTree.addObject(dynamicTree.getLogNode(), logFile, false);
    }

    public void initialize() {

        setFrameProperties();
        createLogger(getClass().getResource("/logback.xml"));
        createMenuBar();
        createToolBar();
        createFooterStatusBar();
        createBody();
        setEnableComponenents(false);
    }
}
