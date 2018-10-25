package com.icrisat.sbdm.ismu.ui.mainFrame;

import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DynamicTree extends MouseAdapter {

    private final JTree ismuTree;
    private final DefaultMutableTreeNode rootNode, genotypeNode, phenotypeNode, covariantNode, resultsNode, logNode;
    private SharedInformation sharedInformation;

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    /**
     * Creates the Dynamic Tree with the basic nodes required.
     */
    @Autowired
    public DynamicTree() {
        rootNode = new DefaultMutableTreeNode("ISMU");
        ismuTree = new JTree(rootNode);
        ismuTree.addMouseListener(this);
        ismuTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //These tree nodes are not to be edited. So made final hence initialized in constructor.
        genotypeNode = new DefaultMutableTreeNode("Genotype");
        phenotypeNode = new DefaultMutableTreeNode("Phenotype");
        covariantNode = new DefaultMutableTreeNode("Covariate");
        resultsNode = new DefaultMutableTreeNode("Result");
        logNode = new DefaultMutableTreeNode("Log");
        rootNode.add(genotypeNode);
        rootNode.add(phenotypeNode);
        rootNode.add(covariantNode);
        rootNode.add(resultsNode);
        rootNode.add(logNode);

        DefaultTreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer();
        try {
            treeCellRenderer.setLeafIcon(new ImageIcon(ImageIO.read(getClass().getResource("/treeFile.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ismuTree.setCellRenderer(treeCellRenderer);
    }

    JTree getTreePane() {
        return ismuTree;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public DefaultMutableTreeNode getGenotypeNode() {
        return genotypeNode;
    }

    public DefaultMutableTreeNode getPhenotypeNode() {
        return phenotypeNode;
    }

    private DefaultMutableTreeNode getCovariantNode() {
        return covariantNode;
    }

    public DefaultMutableTreeNode getResultsNode() {
        return resultsNode;
    }

    public DefaultMutableTreeNode getLogNode() {
        return logNode;
    }

    /**
     * Add child node at parent in tree with visibility set to false..
     *
     * @param parent Parent node
     * @param child  Node to be added.
     * @return child node.
     */

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
        return addObject(parent, child, false);
    }

    /**
     * Add child node at parent in tree.
     *
     * @param parent          Parent node
     * @param child           Node to be added.
     * @param shouldBeVisible should the node be visible after adding.
     * @return child node.
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if (parent == null) {
            parent = rootNode;
        }
        // Add this child at the end of the parent children list.
        ((DefaultTreeModel) ismuTree.getModel()).insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            ismuTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    /**
     * Handles mouse pressed event.
     * Highlight the tab if already file is opened in tabbedPane or open the file in tabbedPane if it is closed.
     * Right clicking  on child node shows a popup menu (remove).
     *
     * @param e Event generated.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        ClosableTabbedPane tabbedPane = sharedInformation.getTabbedPane();
        int nodeSelection = ismuTree.getRowForLocation(e.getX(), e.getY());
        // Ensures that we clicked on the tree.
        if (nodeSelection != -1) {
            // Going by lastSelected is not sufficient as selection might be from last time.
            DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode) ismuTree.getPathForLocation(e.getX(), e.getY()).getLastPathComponent());
            if (e.getButton() == 3) {
                // Check it is not any of the root node or log.txt file.
                showDeleteMenu(selectedNode, e.getX(), e.getY());
            } else {
                if (selectedNode.isLeaf()) {
                    if (!(isRootNode(selectedNode) || isPartOfDefaultNodes(selectedNode))) {
                        if (isFileInDisplay(selectedNode) != -1) {
                            tabbedPane.setSelectedIndex(isFileInDisplay(selectedNode));
                        } else {
                            FileLocation selectedFile = (FileLocation) selectedNode.getUserObject();
                            //TODO: FIll actual content. Based on the type of file.
                            if (Util.getFileExtension(selectedFile.getFileNameInApplication()).equalsIgnoreCase(Constants.CSV)) {
                                UtilCSV.addCSVToTabbedPanel(selectedFile, isGenoTypeFile(selectedFile.getFileNameInApplication()));
                            } else if (Util.getFileExtension(selectedFile.getFileNameInApplication()).equalsIgnoreCase(Constants.HTM)) {
                                UtilHTML.displayHTMLFile(selectedFile);
                            } else if (Util.getFileExtension(selectedFile.getFileNameInApplication()).equalsIgnoreCase(Constants.TXT)) {
                                UtilHTML.displayHTMLFile(selectedFile);
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean isGenoTypeFile(String fileName) {
        int childCount = genotypeNode.getChildCount();
        for (int i = 0; i < childCount; i++)
            if (fileName.equalsIgnoreCase(String.valueOf(genotypeNode.getChildAt(i)))) {
                return true;
            }
        return false;
    }

    /**
     * Checks whether the selected node is displayed or not.
     *
     * @param selectedNode clicked node.
     * @return tab no if opened else returns -1;
     */
    private int isFileInDisplay(DefaultMutableTreeNode selectedNode) {
        int tabNo = -1;
        ClosableTabbedPane tabbedPane = sharedInformation.getTabbedPane();
        String selectedNodeName = selectedNode.toString();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (selectedNodeName.equalsIgnoreCase(tabbedPane.getTitleAt(i).trim())) {
                tabNo = i;
                return tabNo;
            }
        }
        return tabNo;
    }

    /**
     * Checks whether the selected node is part of the root nodes.
     *
     * @param selectedNode Node to be verified
     * @return true if at-least one is equal.
     */
    private boolean isPartOfDefaultNodes(DefaultMutableTreeNode selectedNode) {
        if ((selectedNode.toString()).equalsIgnoreCase(genotypeNode.toString())
                || (selectedNode.toString()).equalsIgnoreCase(phenotypeNode.toString()) || (selectedNode.toString()).equalsIgnoreCase(covariantNode.toString())
                || (selectedNode.toString()).equalsIgnoreCase(logNode.toString()) || (selectedNode.toString()).equalsIgnoreCase(resultsNode.toString()))
            return true;
        else return false;
    }

    private boolean isRootNode(DefaultMutableTreeNode selectedNode) {
        return selectedNode.toString().equalsIgnoreCase(rootNode.toString());
    }

    /**
     * Display's popup menu to delete a node from tree.
     *
     * @param selectedNode node that is to be displayed.
     * @param x            x-coordinate
     * @param y            y-coordinate
     */
    private void showDeleteMenu(DefaultMutableTreeNode selectedNode, int x, int y) {
        JPopupMenu deletePopup = new JPopupMenu();
        JMenuItem menuItem;
        if (selectedNode.isLeaf()) {
            menuItem = new JMenuItem("Remove");
        } else
            menuItem = new JMenuItem("Delete all files from folder.");

        deletePopup.add(menuItem);
        deletePopup.show(ismuTree, x, y);
        menuItem.addActionListener(e -> remove(selectedNode, true));
    }

    /**
     * Delete the selected Node.
     * If it is a leaf node: Delete it
     * If it is a parent node: Delete the children
     * If it is the log node or log file: Don't delete them and pop up showing that it's a log file and dont delete it.
     * If it is the root node: Delete all the nodes except the parent nodes.
     *
     * @param selectedNode Node to be deleted.
     */
    public void remove(DefaultMutableTreeNode selectedNode, boolean deleteFileAlso) {
        String selectedNodeName = selectedNode.toString();
        if (isRootNode(selectedNode)) {
            remove(genotypeNode, deleteFileAlso);
            remove(phenotypeNode, deleteFileAlso);
            remove(covariantNode, deleteFileAlso);
            int noOfChildren = resultsNode.getChildCount();
            for (int i = 0; i < noOfChildren; i++)
                delete((DefaultMutableTreeNode) resultsNode.getChildAt(0), deleteFileAlso);

            noOfChildren = logNode.getChildCount();
            for (int i = 1; i < noOfChildren; i++)
                delete((DefaultMutableTreeNode) logNode.getChildAt(1), deleteFileAlso);
            return;
        }
        if (logNode.isNodeChild(selectedNode)) {
            Util.showMessageDialog("Let's not delete log file.");
            return;
        }
        if (selectedNode.isLeaf() && !isPartOfDefaultNodes(selectedNode)) { // Initially except root node all the nodes are default nodes.
            delete(selectedNode, deleteFileAlso);
            return;
        }
        if (selectedNodeName.equalsIgnoreCase(genotypeNode.toString()) || selectedNodeName.equalsIgnoreCase(phenotypeNode.toString()) || selectedNodeName.equalsIgnoreCase(covariantNode.toString())) {
            int childCount = selectedNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                delete((DefaultMutableTreeNode) selectedNode.getChildAt(0), deleteFileAlso);
            }
            return;
        }
        if (selectedNodeName.equalsIgnoreCase(logNode.toString())) {
            Util.showMessageDialog("Let's not delete log directory contents.");
            return;
        }
        if (selectedNodeName.equalsIgnoreCase(resultsNode.toString())) {
            Util.showMessageDialog("Let's not delete result directory contents.");
            return;
        }
        ((DefaultTreeModel) ismuTree.getModel()).reload();
    }

    /**
     * Delete the node from tree, tabbedPane, sharedInformation.
     * If it is a html delete the corresponding png files.
     *
     * @param selectedNode Node to be deleted.
     */
    private void delete(DefaultMutableTreeNode selectedNode, boolean deleteFileAlso) {
        PathConstants pathConstants = sharedInformation.getPathConstants();
        FileLocation selectedFileLocation = (FileLocation) selectedNode.getUserObject();
        if (isFileInDisplay(selectedNode) != -1) {
            sharedInformation.getTabbedPane().removeTabAt(isFileInDisplay(selectedNode));
        }
        if (pathConstants.phenotypeFiles.contains(selectedFileLocation)) {
            pathConstants.phenotypeFiles.remove(selectedFileLocation);
        }
        if (pathConstants.genotypeFiles.contains(selectedFileLocation)) {
            pathConstants.genotypeFiles.remove(selectedFileLocation);
            String summaryFile = pathConstants.summaryFilesMap.remove(selectedFileLocation.getFileNameInApplication());

            int noOfChildren = resultsNode.getChildCount();
            for (int i = 0; i < noOfChildren; i++) {
                DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) resultsNode.getChildAt(0);
                FileLocation childAtUserObject = (FileLocation) childAt.getUserObject();
                if (childAtUserObject.getFileNameInApplication().equals(summaryFile)) {
                    if (isFileInDisplay(childAt) != -1) {
                        sharedInformation.getTabbedPane().removeTabAt(isFileInDisplay(childAt));
                    }
                    pathConstants.resultFiles.remove(childAtUserObject);
                    ((DefaultTreeModel) ismuTree.getModel()).removeNodeFromParent(childAt);
                    if (deleteFileAlso) {
                        try {
                            Files.delete(Paths.get(pathConstants.resultDirectory + childAtUserObject.getFileNameInApplication()));
                        } catch (IOException ignored) {

                        }
                    }
                }
            }
        }
        if (pathConstants.resultFiles.contains(selectedFileLocation)) {
            pathConstants.resultFiles.remove(selectedFileLocation);
            pathConstants.summaryFilesMap.forEach((k, v) -> {
                System.out.println(k + "           " + v);
                if (v.equals(selectedFileLocation.getFileNameInApplication())) {
                    int noOfChildren = genotypeNode.getChildCount();
                    for (int i = 0; i < noOfChildren; i++) {
                        DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) genotypeNode.getChildAt(0);
                        FileLocation childAtUserObject = (FileLocation) childAt.getUserObject();
                        if (childAtUserObject.getFileNameInApplication().equals(k)) {
                            if (isFileInDisplay(childAt) != -1) {
                                sharedInformation.getTabbedPane().removeTabAt(isFileInDisplay(childAt));
                            }
                            pathConstants.genotypeFiles.remove(childAtUserObject);
                            ((DefaultTreeModel) ismuTree.getModel()).removeNodeFromParent(childAt);
                            if (deleteFileAlso) {
                                try {
                                    Files.delete(Paths.get(pathConstants.resultDirectory + childAtUserObject.getFileNameInApplication()));
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    }

                }
            });
        }
        ((DefaultTreeModel) ismuTree.getModel()).removeNodeFromParent(selectedNode);
        if (deleteFileAlso) {
            try {
                Files.delete(Paths.get(sharedInformation.getPathConstants().resultDirectory + selectedFileLocation.getFileNameInApplication()));
            } catch (IOException ignored) {

            }
        }
    }
}