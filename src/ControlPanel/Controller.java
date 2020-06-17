package ControlPanel;

import Common.CannotCommunicateWithServerException;
import Common.InvalidTokenException;
import Common.Permission;
import Common.PermissionException;
import Viewer.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static Common.Utility.ReadTextFile;

public class Controller {

    private View view;

    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        // add listeners
        addListeners();
    }

    private void addListeners() {
        LoginView loginView = this.view.getLoginView();
        ControlPanelView mainView = this.view.getControlPanelView();

        // login button
        loginView.getLoginBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean success;
                try {
                    success = model.verifyPassword(loginView.getUserName(), loginView.getPassword());

                } catch (CannotCommunicateWithServerException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage());
                    return;
                }
                loginView.displayDialog(success);

                // if fail to login exit the program, otherwise go to the main window
                if (!success) {
                    System.exit(1);
                } else {
                    loginView.dispose();
                    // modify the main window based on the user permissions
                    // some users might not have the rights to browse these two tabs
                    ArrayList<Permission> permissionList = model.getPermissions(loginView.getUserName());
                    JTabbedPane tabbedPane = mainView.getTabbedPane();
                    if (!permissionList.contains(Permission.CREATE_BILLBOARDS)) {
                        tabbedPane.setEnabledAt(0, false);
                        // since first tab is disabled, user shouldn't be able to see the first tab
                        tabbedPane.setSelectedIndex(1);
                    }
                    if (!permissionList.contains(Permission.SCHEDULE_BILLBOARDS)) {
                        tabbedPane.setEnabledAt(2, false);
                    }
                    mainView.setVisible(true);// open the main window
                }
            }
        });

        // exit button
        loginView.getExitBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        // main window

        // 1. create billboard tab
        CreateBillboardsPanel createBillboardsPanel = mainView.getCreateBillboardsPanel();

        // create billboard button
        createBillboardsPanel.getNewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BillboardEditor editor = createBillboardsPanel.getEditor();
                String xml = editor.getXML();

                // make sure the board has not existed yet
                String boardName = editor.getBoardName();
                String billboardContent;
                try {
                    billboardContent = model.getBillboardContents(boardName);
                } catch (InvalidTokenException | CannotCommunicateWithServerException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (billboardContent == null || billboardContent.equals("")) {
                    model.createEditBillboard(boardName, xml);
                    JOptionPane.showMessageDialog(mainView, "Successfully created a billboard with name '"
                            + boardName + "' !");
                } else {
                    JOptionPane.showMessageDialog(mainView, "The billboard with name '" + boardName + "' " +
                            "has already existed!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // import xml
        createBillboardsPanel.getImportBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int o = JOptionPane.showConfirmDialog(mainView, "All unsaved changes will be disposed!");
                if (o != JOptionPane.OK_OPTION) {
                    return;
                }
                BillboardEditor editor = createBillboardsPanel.getEditor();
                // clear the form
                editor.clearXML();

                String fileName;
                String xml;
                // loads the xml from a file
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(mainView);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    fileName = file.getName();
                    xml = ReadTextFile(file.getPath());
                } else {
                    return;
                }

                // populates the form
                editor.setXML(xml);
                // use the file name to set the billboard name
                editor.setBoardName(fileName.split("\\.")[0]);
            }
        });

        // export xml
        createBillboardsPanel.getExportBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xml = createBillboardsPanel.getEditor().getXML();

                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showSaveDialog(mainView);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (PrintWriter out = new PrintWriter(file.getPath() + ".xml")) {
                        out.println(xml);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // preview button
        createBillboardsPanel.getPreviewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xml = createBillboardsPanel.getEditor().getXML();
                Viewer previewer = new Viewer();
                previewer.updatePanel(xml);
            }
        });

        // 2. list billboard tab
        ListBillboardsPanel listBillboardsPanel = mainView.getListBillboardsPanel();

        // load billboards button
        listBillboardsPanel.getLoadBillboardsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton editBtn = listBillboardsPanel.getEditBtn();
                if (editBtn.getText().equals("Save Changes")) {
                    int o = JOptionPane.showConfirmDialog(mainView, "All unsaved changes will be disposed!",
                            "Discard changes", JOptionPane.YES_NO_OPTION);
                    if (o == JOptionPane.NO_OPTION) {
                        return;
                    } else {
                        // turn it back to normal mode
                        editBtn.setText("Edit");
                    }
                }
                String[][] data;
                try {
                    data = model.getBillboards();
                } catch (InvalidTokenException | CannotCommunicateWithServerException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                final String[] columns = {"Board Name", "Creator"};
                JPanel displayPanel = listBillboardsPanel.getDisplayPanel();
                CardLayout card = listBillboardsPanel.getCard();
                JTable table = listBillboardsPanel.getTable();

                table.setModel(new DefaultTableModel(data, columns));
                // select the first row
                table.setRowSelectionInterval(0, 0);
                card.show(displayPanel, "list");

            }
        });

        // preview button
        listBillboardsPanel.getPreviewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = listBillboardsPanel.getTable();
                // make sure the user has selected a billboard
                int rowIndex = table.getSelectedRow();
                String selectedBoardName;
                if (rowIndex != -1) {
                    // select the first row
                    table.setRowSelectionInterval(0, 0);
                    selectedBoardName = (String) table.getValueAt(rowIndex, 0);
                } else {
                    JOptionPane.showMessageDialog(mainView, "Please select a billboard!");
                    return;
                }

                String xml = model.getBillboardContents(selectedBoardName);
                ;
                Viewer previewer = new Viewer();
                previewer.updatePanel(xml);
            }
        });

        // edit button
        listBillboardsPanel.getEditBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = listBillboardsPanel.getEditBtn();
                JTable table = listBillboardsPanel.getTable();
                BillboardEditor editor = listBillboardsPanel.getEditor();

                if (source.getText().equals("Save Changes"))// user has finished the editing
                {
                    // make the changes to the billboard on database
                    try {
                        model.createEditBillboard(editor.getBoardName(), editor.getXML());
                        JOptionPane.showMessageDialog(mainView, "Successfully edit the billboard!");
                    } catch (PermissionException ex) {
                        JOptionPane.showMessageDialog(mainView, ex.getMessage(), "Permission required",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    source.setText("Edit");
                    // make the display panel to show billboard list
                    CardLayout card = listBillboardsPanel.getCard();
                    JPanel displayPanel = listBillboardsPanel.getDisplayPanel();
                    card.show(displayPanel, "list");
                    return;
                }

                // make sure the user has selected a billboard
                // and enter editor mode
                int rowIndex = table.getSelectedRow();
                String selectedBoardName = "";
                if (rowIndex != -1) {
                    table.clearSelection();
                    selectedBoardName = (String) table.getValueAt(rowIndex, 0);
                    String xml = model.getBillboardContents(selectedBoardName);
                    source.setText("Save Changes");

                    // make sure the editor is clean before populating
                    editor.clearXML();

                    // populates the form
                    editor.setXML(xml);
                    editor.setBoardName(selectedBoardName);

                    // make the display panel to show editor
                    CardLayout card = listBillboardsPanel.getCard();
                    JPanel displayPanel = listBillboardsPanel.getDisplayPanel();
                    card.show(displayPanel, "editor");


                } else {
                    JOptionPane.showMessageDialog(mainView, "Please select a billboard!");
                }
            }
        });

        // delete button
        listBillboardsPanel.getDeleteBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = listBillboardsPanel.getTable();
                // make sure the user has selected a billboard
                int rowIndex = table.getSelectedRow();
                String selectedBoardName = "";
                if (rowIndex != -1) {
                    table.clearSelection();
                    selectedBoardName = (String) table.getValueAt(rowIndex, 0);
                } else {
                    JOptionPane.showMessageDialog(mainView, "Please select a billboard!");
                    return;
                }
                try {
                    if (model.deleteBillboard(selectedBoardName)) {
                        JOptionPane.showMessageDialog(mainView, "Successfully deleted the billboard!" +
                                " Please reload the billboard list to see the change!");

                    } else {
                        JOptionPane.showMessageDialog(mainView, "Fail to delete the billboard", "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (CannotCommunicateWithServerException | InvalidTokenException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage());
                }

            }
        });

        // 5. log out tab
        mainView.getLogoutPanel().getLogoutBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String acknowledgement = null;
                try {
                    acknowledgement = model.logout();
                } catch (CannotCommunicateWithServerException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage());
                    return;
                } catch (InvalidTokenException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage());
                    System.exit(1);
                }

                JOptionPane.showMessageDialog(mainView, acknowledgement);
                if (!acknowledgement.equals("Fail to logout")) {
                    System.exit(1);
                }
            }
        });
    }

}
