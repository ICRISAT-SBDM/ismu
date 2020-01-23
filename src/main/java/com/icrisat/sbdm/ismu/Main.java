package com.icrisat.sbdm.ismu;

import com.icrisat.sbdm.ismu.ui.mainFrame.CreateMainFrameComponents;
import com.icrisat.sbdm.ismu.util.Project;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;
import java.util.Properties;

/**
 * Entry point for the Pipeline
 *
 * @author Chaitanya
 */
public class Main extends SingleFrameApplication {

    private static AnnotationConfigApplicationContext context;
    private JFrame mainFrame;
    private SharedInformation sharedInformation;
    private Project project;

    public Main() {
    }

    @Override
    protected void initialize(String[] args) {
        System.out.println("init");
        context = new AnnotationConfigApplicationContext(SpringConfig.class);
        sharedInformation = context.getBean("sharedInformation", SharedInformation.class);
        project = context.getBean("project", Project.class);
        super.initialize(args);
    }

    @Override
    protected void startup() {

        setLookAndFeelOfApplication();
        mainFrame = getMainFrame();
        sharedInformation.setMainFrame(getMainFrame());
        CreateMainFrameComponents createMainFrameComponents = context.getBean("createMainFrameComponents", CreateMainFrameComponents.class);
        createMainFrameComponents.initialize();


        // Exit Listener.
        addExitListener(new ExitListener() {
            public boolean canExit(EventObject e) {
                return Util.closeApplication(e, project);
            }

            public void willExit(EventObject event) {

            }
        });
        show(mainFrame);
    }

    @Override
    protected void shutdown() {
        context.close();
        super.shutdown();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE | WindowConstants.EXIT_ON_CLOSE);
        System.out.println("Close");
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    private void setLookAndFeelOfApplication() {
        Properties props = new Properties();
        props.put("logoString", "ISMU");
        props.put("dynamicLayout", "on");
        props.put("windowDecoration", "on");
        props.put("tooltipBackgroundColor", "");
        props.put("menuTextFont", "Arial BOLD 15");
        props.put("textAntiAliasing", "on");
        AcrylLookAndFeel.setCurrentTheme(props);
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
            UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 15));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            sharedInformation.getLogger().debug("Issue when setting the looking and feel of application." + ex.toString() + "\t" + ex.getMessage());
        }
    }
}
