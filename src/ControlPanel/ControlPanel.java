package ControlPanel;

import Server.BillboardDB;
import Server.Request;
import Server.Response;
import Viewer.ServerConnection;
import Viewer.Viewer;
import Viewer.BillboardXML;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

public class ControlPanel extends JFrame {

    private static final int WIDTH = 800;

    private static final int HEIGHT = 520;

    private String token;

    private BasicUser operator;

    public ControlPanel() {
        super("Billboard Control Panel");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        showLoginScreen();
        showMainScreen();


    }

    private Response sendRequestAndGetResponse(Request request) {
        try {
            Socket socket = ServerConnection.getSocket();
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            // get response
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Response response = (Response) objectInputStream.readObject();

            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            return response;
        } catch (ClassNotFoundException | IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Some errors occurred when communicating with server",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return null;
    }

    private void showLoginScreen() {
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Billboard Control Panel Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = username.getText();
            String word = password.getText();
            if (name == null) {
                name = "";
            }
            HashMap<String, String> content = new HashMap<>();
            content.put("userName", name);
            content.put("hashedPassword", BillboardDB.hashString(word));
            // communicate with server
            Request loginRequest = new Request(Request.RequestType.LOGIN);
            loginRequest.setContent(content);
            Response response = sendRequestAndGetResponse(loginRequest);
            // verify the userName and password
            assert response != null;
            boolean success = response.getResponseType() == Response.ResponseType.SUCCESS;



            this.token = (String) response.getResponseContent();
            if (!success) {
                JOptionPane.showMessageDialog(this, "Authentication failed!",
                        "Fail to login", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                JOptionPane.showMessageDialog(this, "Authentication success!");
            }
        } else {// press cancel or close the window
            System.exit(1);
        }
        // Get the current operator for permissions check
        Request getCurrentOperator = new Request(Request.RequestType.GET_CURRENT_OPERATOR, token);
        this.operator = (BasicUser) sendRequestAndGetResponse(getCurrentOperator).getResponseContent();
    }

    private void showMainScreen() {
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel createBillboardsPanel = makeCreateBillboardsPanel();
        JPanel listBillboardsPanel = new JPanel();
        JPanel scheduleBillboardsPanel = new JPanel();
        JPanel editUsersPanel = new JPanel();
        tabbedPane.add("Create Billboards", createBillboardsPanel);
        tabbedPane.add("List Billboards", listBillboardsPanel);
        tabbedPane.add("Schedule Billboards", scheduleBillboardsPanel);
        tabbedPane.add("Edit Users", editUsersPanel);

        // some users might not have the rights to browse these two tabs
        if (!operator.hasPermission(Permission.CREATE_BILLBOARDS)) {
            tabbedPane.setEnabledAt(0, false);
            // since first tab is disabled, user shouldn't be able to see the first tab
            tabbedPane.setSelectedIndex(1);
        }
        if (!operator.hasPermission(Permission.SCHEDULE_BILLBOARDS)) {
            tabbedPane.setEnabledAt(2, false);
        }
        this.getContentPane().add(tabbedPane);
        this.setVisible(true);
    }

    private JPanel makeCreateBillboardsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel boardNameLabel = new JLabel("Billboard Name: ");
        JTextField boardnameField = new JTextField("Please enter a billboard name", 20);
        JLabel boardColourLabel = new JLabel("Billboard Colour: ");
        JTextField boardColourField = new JTextField("#00FF00", 7);

        JLabel messageContentLabel = new JLabel("Message Content: ");
        JButton messageContentBtn = new JButton("Edit Message");
        JLabel messageColourLabel = new JLabel("Message Colour: ");
        JTextField messageColourField = new JTextField("#FF0000", 7);

        // picture
        JLabel pictureLabel = new JLabel("Picture: ");
        JButton pictureContentBtn = new JButton("Edit Picture Source");
        JRadioButton urlBtn = new JRadioButton("URL");
        JRadioButton dataBtn = new JRadioButton("Data");
        ButtonGroup buttonGroup = new ButtonGroup();
        urlBtn.setSelected(true);
        buttonGroup.add(urlBtn);
        buttonGroup.add(dataBtn);
        JTextField pictureField = new JTextField("URL or Base 64 data or select from file");

        // information
        JLabel infoLabel = new JLabel("Information: ");
        JButton infoBtn = new JButton("Edit Information");
        JLabel infoColourLabel = new JLabel("Information Colour: ");
        JTextField infoColourField = new JTextField("#0000FF", 7);

        // board name line
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 10;
        constraints.ipady = 15;

        constraints.gridx = 0;
        constraints.gridy = 0;
        centerPanel.add(boardNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        centerPanel.add(boardnameField, constraints);

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


        // inner window size
        final int INNER_WINDOW_WIDTH = 500;
        final int INNER_WINDOW_HEIGHT = 400;
        String[] messageContent = {""};
        messageContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame messageContentWindow = new JFrame("Message Content");
                JTextArea messageContentText = new JTextArea(messageContent[0]);
                messageContentText.setFont(new Font("Arial", Font.BOLD, 30));
                messageContentText.setLineWrap(true);
                messageContentWindow.getContentPane().add(messageContentText);
                messageContentWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        messageContent[0] = messageContentText.getText();
                    }
                });
                messageContentWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                messageContentWindow.setLocationRelativeTo(null);
                messageContentWindow.setVisible(true);

            }
        });

        String[] pictureContent = {""};
        pictureContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame pictureContentWindow = new JFrame("Picture Source");
                JTextArea pictureContentText = new JTextArea(pictureContent[0]);
                pictureContentText.setLineWrap(true);
                pictureContentWindow.getContentPane().add(pictureContentText);
                pictureContentWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        pictureContent[0] = pictureContentText.getText();
                    }
                });
                pictureContentWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                pictureContentWindow.setLocationRelativeTo(null);
                pictureContentWindow.setVisible(true);
            }
        });

        String[] informationContent = {""};
        infoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame infoWindow = new JFrame("Information Content");
                JTextArea infoText = new JTextArea(informationContent[0]);
                infoText.setFont(new Font("Arial", Font.BOLD, 30));
                infoText.setLineWrap(true);
                infoWindow.getContentPane().add(infoText);
                infoWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        informationContent[0] = infoText.getText();
                    }
                });
                infoWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                infoWindow.setLocationRelativeTo(null);
                infoWindow.setVisible(true);
            }
        });


        // buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1));
        JButton newBtn = new JButton("Create New Billboard");
        JButton importBtn = new JButton("Import XML");
        JButton exportBtn = new JButton("Export XML");
        JButton previewBtn = new JButton("Preview Billboard");


        // how can I avoid repeating myself in these handlers
        // left buttons
        newBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bgColour = boardColourField.getText();
                String msg = messageContent[0];
                String msgColour = messageColourField.getText();
                String picture = pictureContent[0];
                String picType = "URL";
                if (dataBtn.isSelected()) {
                    picType = "Data";
                }
                String info = informationContent[0];
                String infoColour = infoColourField.getText();

                String finalPicType = picType;
                String xml = createBillboardXML(bgColour, msg, msgColour,
                        picture, finalPicType, info, infoColour);
                Request request = new Request(Request.RequestType.CREATE_EDIT_BILLBOARD, token);
                request.setContent(new String[]{boardnameField.getText(), xml});
                Response response = sendRequestAndGetResponse(request);
                Response.ResponseType responseType = response.getResponseType();
                if (responseType == Response.ResponseType.SUCCESS) {
                    JOptionPane.showMessageDialog(panel, "Successfully created a billboard!");
                } else {
                    JOptionPane.showMessageDialog(panel, "Unable to create the billboard!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        previewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bgColour = boardColourField.getText();
                String msg = messageContent[0];
                String msgColour = messageColourField.getText();
                String picture = pictureContent[0];
                String picType = "URL";
                if (dataBtn.isSelected()) {
                    picType = "Data";
                }
                String info = informationContent[0];
                String infoColour = infoColourField.getText();

                String finalPicType = picType;
                Viewer preview = new Viewer() {
                    @Override
                    public String getXMLSource() {
                        return createBillboardXML(bgColour, msg, msgColour,
                                picture, finalPicType, info, infoColour);
                    }
                };
            }
        });

        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bgColour = boardColourField.getText();
                String msg = messageContent[0];
                String msgColour = messageColourField.getText();
                String picture = pictureContent[0];
                String picType = "URL";
                if (dataBtn.isSelected()) {
                    picType = "Data";
                }
                String info = informationContent[0];
                String infoColour = infoColourField.getText();

                String finalPicType = picType;

                String xml = createBillboardXML(bgColour, msg, msgColour,
                        picture, finalPicType, info, infoColour);
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showSaveDialog(panel);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    System.out.println(file.getPath());
                    try (PrintWriter out = new PrintWriter(file.getPath() + ".xml")) {
                        out.println(xml);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int o = JOptionPane.showConfirmDialog(panel, "All unsaved changes will be disposed!");
                if (o != JOptionPane.OK_OPTION) {
                    return;
                }
                // clear the form for the new billboard
                // populates the form
                boardnameField.setText("");
                boardColourField.setText("");

                messageContent[0] = "";
                messageColourField.setText("");
                pictureContent[0] = "";
                urlBtn.setSelected(false);
                pictureContent[0] = "";
                dataBtn.setSelected(false);
                informationContent[0] = "";
                infoColourField.setText("");

                BillboardXML xml = null;
                String fileName = null;
                // loads the xml from a file
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(panel);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    fileName = file.getName();
                    String xmlStr = Viewer.ReadTextFile(file.getPath());
                    xml = new BillboardXML(xmlStr);
                } else {
                    return;
                }

                // populates the form
                boardnameField.setText(fileName.split("\\.")[0]);
                boardColourField.setText(xml.getBackground());

                if (xml.isHasMessage()) {
                    messageContent[0] = xml.getMessageContent();
                    messageColourField.setText(xml.getMessageColour());
                }
                if (xml.isHasPicture()) {
                    // set
                    if (xml.getPictureURL() != null) {
                        pictureContent[0] = xml.getPictureURL();
                        urlBtn.setSelected(true);
                    } else { // loads base 64 image
                        pictureContent[0] = xml.getPictureData();
                        dataBtn.setSelected(true);
                    }
                }
                if (xml.isHasInformation()) {
                    informationContent[0] = xml.getInformationContent();
                    infoColourField.setText(xml.getInformationColour());
                }
            }
        });

        // add to the left panel
        buttonsPanel.add(newBtn);
        buttonsPanel.add(importBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(previewBtn);

        panel.add(buttonsPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeEditBillboardsPanel() {
        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel boardNameLabel = new JLabel("Billboard Name: ");
        JTextField boardnameField = new JTextField("Please enter a billboard name", 20);
        JLabel boardColourLabel = new JLabel("Billboard Colour: ");
        JTextField boardColourField = new JTextField("#00FF00", 7);

        JLabel messageContentLabel = new JLabel("Message Content: ");
        JButton messageContentBtn = new JButton("Edit Message");
        JLabel messageColourLabel = new JLabel("Message Colour: ");
        JTextField messageColourField = new JTextField("#FF0000", 7);

        // picture
        JLabel pictureLabel = new JLabel("Picture: ");
        JButton pictureContentBtn = new JButton("Edit Picture Source");
        JRadioButton urlBtn = new JRadioButton("URL");
        JRadioButton dataBtn = new JRadioButton("Data");
        ButtonGroup buttonGroup = new ButtonGroup();
        urlBtn.setSelected(true);
        buttonGroup.add(urlBtn);
        buttonGroup.add(dataBtn);
        JTextField pictureField = new JTextField("URL or Base 64 data or select from file");

        // information
        JLabel infoLabel = new JLabel("Information: ");
        JButton infoBtn = new JButton("Edit Information");
        JLabel infoColourLabel = new JLabel("Information Colour: ");
        JTextField infoColourField = new JTextField("#0000FF", 7);

        // board name line
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 10;
        constraints.ipady = 15;

        constraints.gridx = 0;
        constraints.gridy = 0;
        centerPanel.add(boardNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        centerPanel.add(boardnameField, constraints);

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


        // inner window size
        final int INNER_WINDOW_WIDTH = 500;
        final int INNER_WINDOW_HEIGHT = 400;
        String[] messageContent = {""};
        messageContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame messageContentWindow = new JFrame("Message Content");
                JTextArea messageContentText = new JTextArea(messageContent[0]);
                messageContentText.setFont(new Font("Arial", Font.BOLD, 30));
                messageContentText.setLineWrap(true);
                messageContentWindow.getContentPane().add(messageContentText);
                messageContentWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        messageContent[0] = messageContentText.getText();
                    }
                });
                messageContentWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                messageContentWindow.setLocationRelativeTo(null);
                messageContentWindow.setVisible(true);

            }
        });

        String[] pictureContent = {""};
        pictureContentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame pictureContentWindow = new JFrame("Picture Source");
                JTextArea pictureContentText = new JTextArea(pictureContent[0]);
                pictureContentText.setLineWrap(true);
                pictureContentWindow.getContentPane().add(pictureContentText);
                pictureContentWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        pictureContent[0] = pictureContentText.getText();
                    }
                });
                pictureContentWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                pictureContentWindow.setLocationRelativeTo(null);
                pictureContentWindow.setVisible(true);
            }
        });

        String[] informationContent = {""};
        infoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame infoWindow = new JFrame("Information Content");
                JTextArea infoText = new JTextArea(informationContent[0]);
                infoText.setFont(new Font("Arial", Font.BOLD, 30));
                infoText.setLineWrap(true);
                infoWindow.getContentPane().add(infoText);
                infoWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        informationContent[0] = infoText.getText();
                    }
                });
                infoWindow.setSize(new Dimension(INNER_WINDOW_WIDTH, INNER_WINDOW_HEIGHT));
                infoWindow.setLocationRelativeTo(null);
                infoWindow.setVisible(true);
            }
        });

        return centerPanel;
    }

    private String createBillboardXML(String billboardColour,
                                      String msg, String msgColour,
                                      String pic, String picType,
                                      String info, String infoColour) {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<billboard bg>\n" +
                        "msg\n" +
                        "pic\n" +
                        "info\n" +
                        "</billboard>";
        String msgTag = "";
        String picTag = "";
        String infoTag = "";

        // replace bg colour
        if (!billboardColour.isBlank()) {
            template = template.replace("bg", "background=\"" + billboardColour + "\"");
        }


        if (!msg.isBlank()) {
            msgTag = createXMLTag("message", "colour", msgColour, msg);
        }
        if (!pic.isBlank()) {
            if (picType.toLowerCase().equals("url")) {
                picTag = String.format("<picture url=\"%s\" />", pic);
            } else {
                picTag = String.format("<picture data=\"%s\" />", pic);
            }
        }
        if (!info.isBlank()) {
            infoTag = createXMLTag("information", "colour", infoColour, info);
        }

        template = template.replace("bg", "");
        template = template.replace("msg", msgTag);
        template = template.replace("pic", picTag);
        template = template.replace("info", infoTag);

        return template;
    }

    private static String createXMLTag(String tagName,
                                       String attributeName, String attributeValue,
                                       String text) {
        String xml = "";
        if (!attributeValue.isBlank()) {
            xml = String.format("<%s %s=\"%s\">%s</%s>", tagName, attributeName, attributeValue, text, tagName);
        } else {
            xml = String.format("<%s>%s</%s>", tagName, text, tagName);
        }
        return xml;
    }


    public static void main(String[] args) {

        new ControlPanel();
    }
}
