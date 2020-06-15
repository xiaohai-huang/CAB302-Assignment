package ControlPanel;

import Server.CannotCommunicateWithServerException;
import Viewer.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        CreateBillboardPanel createBillboardPanel = mainView.getCreateBillboardsPanel();

        // create billboard button
        createBillboardPanel.getNewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String xml = createBillboardPanel.getXML();

                // make sure the board has not existed yet
                String boardName = createBillboardPanel.getBoardName();
                String billboardContent = model.getBillboardContents(boardName);
                if (billboardContent == null || billboardContent.equals("")) {
                    model.createEditBillboard(boardName, xml);
                } else {
                    JOptionPane.showMessageDialog(mainView, "The billboard with name '" + boardName + "' " +
                            "has already existed!");
                }
            }
        });

        // import xml
        createBillboardPanel.getImportBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int o = JOptionPane.showConfirmDialog(mainView, "All unsaved changes will be disposed!");
                if (o != JOptionPane.OK_OPTION) {
                    return;
                }

                // clear the form
                createBillboardPanel.clearXML();

                String fileName;
                String xml;
                // loads the xml from a file
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(mainView);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    fileName = file.getName();
                    xml = Viewer.ReadTextFile(file.getPath());
                } else {
                    return;
                }

                // populates the form
                createBillboardPanel.setXML(xml);
                // use the file name to set the billboard name
                createBillboardPanel.setBoardName(fileName.split("\\.")[0]);
            }
        });

        // export xml
        createBillboardPanel.getExportBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xml = createBillboardPanel.getXML();

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
        createBillboardPanel.getPreviewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xml = createBillboardPanel.getXML();
                Viewer previewer = new Viewer();
                previewer.updatePanel(xml);
            }
        });

        // edit message content button
        createBillboardPanel.getEditMessageContentBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SmallEditor editor = new SmallEditor("Message Editor", createBillboardPanel.getMessageContent());
                editor.getSaveBtn().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createBillboardPanel.setMessageContent(editor.getUserInput());
                    }
                });
            }
        });

        // edit picture source button
        createBillboardPanel.getEditPictureContentBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get picture from a URL
                if (createBillboardPanel.getUrlBtn().isSelected()) {
                    String userInput = JOptionPane.showInputDialog("Input the URL of the image");
                    createBillboardPanel.setPictureContent(userInput);
                }
                else {// get picture from base 64 string or a local image file
                    int o = JOptionPane.showConfirmDialog(mainView, "Select the image from local files?",
                            "Edit Picture Content",JOptionPane.YES_NO_OPTION);

                    if (o == JOptionPane.OK_OPTION) {// get from local image file
                        // loads the image from a local file
                        JFileChooser chooser = new JFileChooser();
                        int option = chooser.showOpenDialog(mainView);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            BufferedImage image = null;
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            // encode the image to a base 64 string

                            createBillboardPanel.setPictureContent(Viewer.encodeImage(image));
                        }
                    } else if(o==JOptionPane.NO_OPTION) {// from a base 64 string
                        String userInput = JOptionPane.showInputDialog("Input a base 64 string");
                        createBillboardPanel.setPictureContent(userInput);
                    }
                }
            }
        });

        // edit info content button
        createBillboardPanel.getEditInfoBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SmallEditor editor = new SmallEditor("Information Content Editor",createBillboardPanel.getInfoContent());
                editor.getSaveBtn().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createBillboardPanel.setInfoContent(editor.getUserInput());
                    }
                });
            }
        });
        // 5. log out tab
        mainView.getLogoutPanel().getLogoutBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String acknowledgement;
                try {
                    acknowledgement = model.logout();
                } catch (CannotCommunicateWithServerException ex) {
                    JOptionPane.showMessageDialog(mainView, ex.getMessage());
                    return;
                }
                JOptionPane.showMessageDialog(mainView, acknowledgement);
                if (!acknowledgement.equals("Fail to logout")) {
                    System.exit(1);
                }
            }
        });
    }

}
