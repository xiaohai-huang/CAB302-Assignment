package ControlPanel;

import Viewer.BillboardXML;
import Viewer.Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ControlPanel.Model.createBillboardXML;

public class ControlPanelView extends JFrame {

    private static final int WIDTH = 800;

    private static final int HEIGHT = 520;
    private final CreateBillboardsPanel createBillboardsPanel;
    private final ListBillboardsPanel listBillboardsPanel;
    private final JPanel scheduleBillboardsPanel;
    private final JPanel editUsersPanel;
    private final LogoutPanel logoutPanel;
    private final JTabbedPane tabbedPane;

    public ControlPanelView() {
        super("Billboard Control Panel");
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        createBillboardsPanel = new CreateBillboardsPanel();
        listBillboardsPanel = new ListBillboardsPanel();
        scheduleBillboardsPanel = new JPanel();
        editUsersPanel = new JPanel();
        logoutPanel = new LogoutPanel();

        tabbedPane.add("Create Billboards", createBillboardsPanel);
        tabbedPane.add("List Billboards", listBillboardsPanel);
        tabbedPane.add("Schedule Billboards", scheduleBillboardsPanel);
        tabbedPane.add("Edit Users", editUsersPanel);
        tabbedPane.add("Log Out", logoutPanel);

        this.getContentPane().add(tabbedPane);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public CreateBillboardsPanel getCreateBillboardsPanel() {
        return createBillboardsPanel;
    }

    public ListBillboardsPanel getListBillboardsPanel() {
        return listBillboardsPanel;
    }

    public LogoutPanel getLogoutPanel() {
        return logoutPanel;
    }
}

class CreateBillboardsPanel extends JPanel {

    private final JButton newBtn;
    private final JButton importBtn;
    private final JButton exportBtn;
    private final JButton previewBtn;
    private final BillboardEditor editor;


    // left panel
    public JButton getNewBtn() {
        return newBtn;
    }

    public JButton getImportBtn() {
        return importBtn;
    }

    public JButton getExportBtn() {
        return exportBtn;
    }

    public JButton getPreviewBtn() {
        return previewBtn;
    }

    public BillboardEditor getEditor() {
        return editor;
    }


    public CreateBillboardsPanel() {
        this.setLayout(new BorderLayout());

        // buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1));
        newBtn = new JButton("Create New Billboard");
        importBtn = new JButton("Import XML");
        exportBtn = new JButton("Export XML");
        previewBtn = new JButton("Preview Billboard");

        // add to the left panel
        buttonsPanel.add(newBtn);
        buttonsPanel.add(importBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(previewBtn);

        editor = new BillboardEditor();
        this.add(buttonsPanel, BorderLayout.WEST);
        this.add(editor, BorderLayout.CENTER);
    }
}

class SmallEditor extends JFrame {

    private final int WIDTH = 500;
    private final int HEIGHT = 400;

    private final JButton saveBtn;
    private final JButton cancelBtn;
    private final JTextArea contentText;


    public SmallEditor(String title, String source) {
        super(title);
        this.setLayout(new BorderLayout());
        contentText = new JTextArea(source);
        contentText.setFont(new Font("Arial", Font.BOLD, 30));
        contentText.setLineWrap(true);
        // todo: set the cursor to the end
        JPanel btnPanel = new JPanel();
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        this.getContentPane().add(contentText, BorderLayout.CENTER);
        this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public String getUserInput() {
        return contentText.getText();
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }


}

class BillboardEditor extends JPanel {

    private final JTextField boardNameField;
    private final JTextField boardColourField;
    private final JButton messageContentBtn;
    private final JTextField messageColourField;
    private final JButton pictureContentBtn;
    private final JRadioButton urlBtn;
    private final JRadioButton dataBtn;
    private final JButton infoBtn;
    private final JTextField infoColourField;

    private String messageContent = "";
    private String pictureContent = "";
    private String informationContent = "";

    public String getBoardName() {
        return boardNameField.getText();
    }

    public void setBoardName(String boardName) {
        boardNameField.setText(boardName);
    }

    public String getBoardColour() {
        return boardColourField.getText();
    }

    public void setBoardColour(String colour) {
        boardColourField.setText(colour);
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String msgContent) {
        messageContent = msgContent;
    }

    public JButton getEditMessageContentBtn() {
        return messageContentBtn;
    }

    public String getMessageColour() {
        return messageColourField.getText();
    }

    public void setMessageColour(String colour) {
        messageColourField.setText(colour);
    }

    public String getPictureContent() {
        return pictureContent;
    }

    public void setPictureContent(String picContent) {
        pictureContent = picContent;
    }

    public JButton getEditPictureContentBtn() {
        return pictureContentBtn;
    }

    public JRadioButton getUrlBtn() {
        return urlBtn;
    }

    public JRadioButton getDataBtn() {
        return dataBtn;
    }

    public String getInfoContent() {
        return informationContent;
    }

    public void setInfoContent(String info) {
        informationContent = info;
    }

    public JButton getEditInfoBtn() {
        return infoBtn;
    }

    public String getInfoColour() {
        return infoColourField.getText();
    }

    public BillboardEditor() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel boardNameLabel = new JLabel("Billboard Name: ");
        boardNameField = new JTextField("Please enter a billboard name", 20);
        JLabel boardColourLabel = new JLabel("Billboard Colour: ");
        boardColourField = new JTextField("#00FF00", 7);

        JLabel messageContentLabel = new JLabel("Message Content: ");
        messageContentBtn = new JButton("Edit Message");
        JLabel messageColourLabel = new JLabel("Message Colour: ");
        messageColourField = new JTextField("#FF0000", 7);

        // picture
        JLabel pictureLabel = new JLabel("Picture: ");
        pictureContentBtn = new JButton("Edit Picture");
        urlBtn = new JRadioButton("URL");
        dataBtn = new JRadioButton("Data");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(urlBtn);
        buttonGroup.add(dataBtn);

        // information
        JLabel infoLabel = new JLabel("Information: ");
        infoBtn = new JButton("Edit Information");
        JLabel infoColourLabel = new JLabel("Information Colour: ");
        infoColourField = new JTextField("#0000FF", 7);

        // board name line
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 10;
        constraints.ipady = 15;

        constraints.gridx = 0;
        constraints.gridy = 0;
        this.add(boardNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        this.add(boardNameField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        this.add(boardColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        this.add(boardColourField, constraints);


        // message line
        constraints.gridx = 0;
        constraints.gridy = 1;
        this.add(messageContentLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        this.add(messageContentBtn, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        this.add(messageColourLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        this.add(messageColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 1;
        this.add(messageColourField, constraints);

        // picture line
        constraints.gridx = 0;
        constraints.gridy = 2;
        this.add(pictureLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        this.add(pictureContentBtn, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        this.add(urlBtn, constraints);

        constraints.gridx = 3;
        constraints.gridy = 2;
        this.add(dataBtn, constraints);

//        radioBtnPanel.add(urlBtn);
//        radioBtnPanel.add(dataBtn);
//        constraints.gridx = 2;
//        constraints.gridy = 2;
//        constraints.gridwidth = 2;
//        this.add(radioBtnPanel, constraints);
//        constraints.gridwidth = 1;

        // information line
        constraints.gridx = 0;
        constraints.gridy = 3;
        this.add(infoLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        this.add(infoBtn, constraints);

        constraints.gridx = 2;
        constraints.gridy = 3;
        this.add(infoColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 3;
        this.add(infoColourField, constraints);

        // add listeners for edit message, picture, info content buttons
        // edit message content button
        messageContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SmallEditor editor = new SmallEditor("Message Editor", messageContent);
                editor.getSaveBtn().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        messageContent = editor.getUserInput();
                    }
                });
            }
        });

        // edit picture content button
        pictureContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get picture from a URL
                if (urlBtn.isSelected()) {
                    String userInput = JOptionPane.showInputDialog("Input the URL of the image");
                    if (userInput == null) {
                        userInput = "";
                    }
                    pictureContent = userInput;
                } else if (dataBtn.isSelected()) {// get picture from base 64 string or a local image file
                    int o = JOptionPane.showConfirmDialog(null,
                            "Select the image from local files?",
                            "Edit Picture Content", JOptionPane.YES_NO_OPTION);

                    if (o == JOptionPane.OK_OPTION) {// get from local image file
                        // loads the image from a local file
                        JFileChooser chooser = new JFileChooser();
                        int option = chooser.showOpenDialog(null);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            BufferedImage image = null;
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            // encode the image to a base 64 string
                            pictureContent = Viewer.encodeImage(image);
                        }
                    } else if (o == JOptionPane.NO_OPTION) {// from a base 64 string
                        String userInput = JOptionPane.showInputDialog("Input a base 64 string");
                        if (userInput == null) {
                            userInput = "";
                        }
                        pictureContent = userInput;
                    }
                }

            }
        });

        // edit info content button
        infoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SmallEditor editor = new SmallEditor("Information Content Editor", informationContent);
                editor.getSaveBtn().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        informationContent = editor.getUserInput();
                    }
                });
            }
        });
    }

    public void setXML(String xmlStr) {
        BillboardXML xml = new BillboardXML(xmlStr);
        // populates the form
        boardColourField.setText(xml.getBackground());

        if (xml.isHasMessage()) {
            messageContent = xml.getMessageContent();
            messageColourField.setText(xml.getMessageColour());
        }
        if (xml.isHasPicture()) {
            if (xml.getPictureURL() != null) {
                pictureContent = xml.getPictureURL();
                urlBtn.setSelected(true);
            } else { // loads base 64 image
                pictureContent = xml.getPictureData();
                dataBtn.setSelected(true);
            }
        }
        if (xml.isHasInformation()) {
            informationContent = xml.getInformationContent();
            infoColourField.setText(xml.getInformationColour());
        }
    }

    public String getXML() {
        String bgColour = this.getBoardColour();
        String msgColour = this.getMessageColour();
        String msg = this.getMessageContent();
        String picContent = this.getPictureContent();
        // figure out picture type
        String picType = null;
        if (this.getDataBtn().isSelected()) {
            picType = "Data";
        } else if (this.getUrlBtn().isSelected()) {
            picType = "URL";
        }

        String info = this.getInfoContent();
        String infoColour = this.getInfoColour();

        String xml = createBillboardXML(bgColour, msg, msgColour,
                picContent, picType, info, infoColour);
        return xml;
    }

    public void clearXML() {
        // clear the form for the new billboard
        // populates the form
        boardNameField.setText("");
        boardColourField.setText("");
        messageContent = "";
        messageColourField.setText("");
        pictureContent = "";
        urlBtn.setSelected(false);
        pictureContent = "";
        dataBtn.setSelected(false);
        informationContent = "";
        infoColourField.setText("");
    }
}

class ListBillboardsPanel extends JPanel {

    private JPanel displayPanel;

    private JButton loadBillboardsBtn;
    private JButton previewBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private BillboardEditor editor;

    private CardLayout card;

    // board list
    private JTable table;

    public ListBillboardsPanel() {
        this.setLayout(new BorderLayout());
        // create left panel for holding buttons
        JPanel btnPanel = makeLeftPanel();
        displayPanel = new JPanel();
        card = new CardLayout();
        displayPanel.setLayout(card);
        editor = new BillboardEditor();

        displayPanel.add("list", makeBillboardsList());
        displayPanel.add("editor", editor);


        this.add(btnPanel, BorderLayout.WEST);
        this.add(displayPanel, BorderLayout.CENTER);
    }

    public JButton getLoadBillboardsBtn() {
        return loadBillboardsBtn;
    }

    public JButton getEditBtn() {
        return editBtn;
    }

    public JButton getPreviewBtn() {
        return previewBtn;
    }

    public JButton getDeleteBtn() {
        return deleteBtn;
    }

    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    public BillboardEditor getEditor() {
        return editor;
    }

    public CardLayout getCard() {
        return card;
    }

    public JTable getTable() {
        return table;
    }

    private JPanel makeLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(4, 1));

        loadBillboardsBtn = new JButton("Load Billboards");
        previewBtn = new JButton("Preview");
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        leftPanel.add(loadBillboardsBtn);
        leftPanel.add(previewBtn);
        leftPanel.add(editBtn);
        leftPanel.add(deleteBtn);

        return leftPanel;
    }

    public JScrollPane makeBillboardsList() {
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane panel = new JScrollPane(table);
        return panel;
    }

}

class LogoutPanel extends JPanel {

    private JButton logoutBtn;

    public LogoutPanel() {
        logoutBtn = new JButton("Log Out");
        this.add(logoutBtn);
    }

    public JButton getLogoutBtn() {
        return logoutBtn;
    }
}