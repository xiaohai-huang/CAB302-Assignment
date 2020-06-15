package ControlPanel;

import Viewer.BillboardXML;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static ControlPanel.Model.createBillboardXML;

public class ControlPanelView extends JFrame {

    private static final int WIDTH = 800;

    private static final int HEIGHT = 520;
    private final CreateBillboardPanel createBillboardsPanel;
    private final JPanel listBillboardsPanel;
    private final JPanel scheduleBillboardsPanel;
    private final JPanel editUsersPanel;
    private final LogoutPanel logoutPanel;
    private final JTabbedPane tabbedPane;

    public ControlPanelView(){
        super("Billboard Control Panel");
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        createBillboardsPanel = new CreateBillboardPanel();
        listBillboardsPanel = new JPanel();
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

    public JTabbedPane getTabbedPane(){
        return tabbedPane;
    }

    public CreateBillboardPanel getCreateBillboardsPanel(){
        return createBillboardsPanel;
    }

    public LogoutPanel getLogoutPanel(){
        return logoutPanel;
    }
}

class CreateBillboardPanel extends JPanel{

    private final JButton newBtn;
    private final JButton importBtn;
    private final JButton exportBtn;
    private final JButton previewBtn;
    private final JTextField boardNameField;
    private final JTextField boardColourField;
    private final JButton messageContentBtn;
    private final JTextField messageColourField;
    private final JButton pictureContentBtn;
    private final JRadioButton urlBtn;
    private final JRadioButton dataBtn;
    private final JButton infoBtn;
    private final JTextField infoColourField;

    private String messageContent="";
    private String pictureContent="";
    private String informationContent="";

    // left panel
    public JButton getNewBtn(){
        return newBtn;
    }

    public JButton getImportBtn(){
        return importBtn;
    }

    public JButton getExportBtn(){
        return exportBtn;
    }

    public JButton getPreviewBtn(){
        return previewBtn;
    }

    // center
    public String getBoardName(){
        return boardNameField.getText();
    }

    public void setBoardName(String boardName){
        boardNameField.setText(boardName);
    }

    public String getBoardColour(){
        return boardColourField.getText();
    }

    public void setBoardColour(String colour){
        boardColourField.setText(colour);
    }

    public String getMessageContent(){
        return messageContent;
    }

    public void setMessageContent(String msgContent){
        messageContent = msgContent;
    }

    public JButton getEditMessageContentBtn(){
        return messageContentBtn;
    }

    public String getMessageColour(){
        return messageColourField.getText();
    }

    public void setMessageColour(String colour){
        messageColourField.setText(colour);
    }
    public String getPictureContent(){
        return pictureContent;
    }

    public void setPictureContent(String picContent){
        pictureContent = picContent;
    }

    public JButton getEditPictureContentBtn(){
        return pictureContentBtn;
    }

    public JRadioButton getUrlBtn(){
        return urlBtn;
    }

    public JRadioButton getDataBtn(){
        return dataBtn;
    }

    public String getInfoContent(){
        return informationContent;
    }

    public void setInfoContent(String info){
        informationContent = info;
    }

    public JButton getEditInfoBtn(){
        return infoBtn;
    }

    public String getInfoColour(){
        return infoColourField.getText();
    }

    public CreateBillboardPanel(){
        this.setLayout(new BorderLayout());
        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
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
        pictureContentBtn = new JButton("Edit Picture Source");
        urlBtn = new JRadioButton("URL");
        dataBtn = new JRadioButton("Data");
        ButtonGroup buttonGroup = new ButtonGroup();
        urlBtn.setSelected(true);
        buttonGroup.add(urlBtn);
        buttonGroup.add(dataBtn);
        JTextField pictureField = new JTextField("URL or Base 64 data or select from file");

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
        centerPanel.add(boardNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        centerPanel.add(boardNameField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        centerPanel.add(boardColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        centerPanel.add(boardColourField, constraints);


        // message line
        constraints.gridx = 0;
        constraints.gridy = 1;
        centerPanel.add(messageContentLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        centerPanel.add(messageContentBtn, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        centerPanel.add(messageColourLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        centerPanel.add(messageColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 1;
        centerPanel.add(messageColourField, constraints);

        // picture line
        constraints.gridx = 0;
        constraints.gridy = 2;
        centerPanel.add(pictureLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        centerPanel.add(pictureContentBtn, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        centerPanel.add(urlBtn, constraints);

        constraints.gridx = 3;
        constraints.gridy = 2;
        centerPanel.add(dataBtn, constraints);

        // information line
        constraints.gridx = 0;
        constraints.gridy = 3;
        centerPanel.add(infoLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        centerPanel.add(infoBtn, constraints);

        constraints.gridx = 2;
        constraints.gridy = 3;
        centerPanel.add(infoColourLabel, constraints);

        constraints.gridx = 3;
        constraints.gridy = 3;
        centerPanel.add(infoColourField, constraints);

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

        this.add(buttonsPanel, BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    public String getXML(){

        String bgColour = this.getBoardColour();
        String msgColour = this.getMessageColour();
        String msg = this.getMessageContent();
        String picContent = this.getPictureContent();
        // figure out picture type
        String picType = "URL";
        if (this.getDataBtn().isSelected()) {
            picType = "Data";
        }

        String info = this.getInfoContent();
        String infoColour = this.getInfoColour();

        String xml = createBillboardXML(bgColour, msg, msgColour,
                picContent, picType, info, infoColour);
        return xml;
    }

    public void setXML(String xmlStr){
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

    public void clearXML(){
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

class SmallEditor extends JFrame{

    private final int WIDTH =500;
    private final int HEIGHT = 400;

    private final JButton saveBtn;
    private final JButton cancelBtn;
    private final JTextArea contentText;


    public SmallEditor(String title, String source){
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

        this.getContentPane().add(contentText,BorderLayout.CENTER);
        this.getContentPane().add(btnPanel,BorderLayout.SOUTH);
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public String getUserInput(){
        return contentText.getText();
    }

    public JButton getSaveBtn(){
        return saveBtn;
    }



}

class LogoutPanel extends JPanel{

    private JButton logoutBtn;

    public LogoutPanel(){
        logoutBtn = new JButton("Log Out");
        this.add(logoutBtn);
    }

    public JButton getLogoutBtn(){
        return logoutBtn;
    }
}