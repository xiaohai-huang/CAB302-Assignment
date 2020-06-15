package Viewer;


import Server.Request;
import Server.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Base64;

public class Viewer {
    public JFrame viewer;
    private Dimension screenSize;
    protected DisplayPanel displayPanel;

    private static final String ERROR_BILLBOARD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<billboard background=\"#FF0000\">\n" +
                    "    <message>Cannot connect to the server!</message>\n" +
                    "</billboard>";
    private static final String ERROR_IMAGE = "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RRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFR1JUdADx0FLSDoKWgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKjqSo6AHjoKWkHQUtABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVHUlR0APHQUtXta/wCQ7qH/AF9S/wDoRqpQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMop9FADKKfRQAyin0UAMqOp69h0P/AJF/Tf8Ar1i/9AFAH//Z";

    private static final String START_UP_BILLBOARD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<billboard background=\"#00FF00\">\n" +
                    "    <message>Initializing the billboard...</message>\n" +
                    "</billboard>";

    public void close() {
        viewer.dispose();
    }

    private String xml = START_UP_BILLBOARD;
    public Viewer() {
        // makes the app full screen
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        viewer = new JFrame();
        //viewer.setSize(screenSize);
        viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        viewer.setUndecorated(true);


        // handle ESC press, exit the program
        viewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    viewer.setVisible(false);
                    viewer.dispose();
                }
            }
        });

        // handles mouse click, exit the program
        viewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               viewer.setVisible(false);
               viewer.dispose();
            }
        });

        // hides the cursor
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the viewer.
        viewer.getContentPane().setCursor(blankCursor);


        // loads the content to be displayed
        displayPanel = new DisplayPanel(getXML(), screenSize);

        // displays the content
        viewer.getContentPane().add(displayPanel);
        viewer.setVisible(true);
//        System.out.println("constructor finished");

    }

    // default
    public String getXML() {
        return xml;
    }

    public void setXML(String xml){
        this.xml = xml;
    }

    private static final int FIFTY_SECONDS_IN_MILLIS = 15000;

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        // background
//        JFrame baseFrame = new JFrame();
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        baseFrame.setUndecorated(true);
//        baseFrame.setSize(screenSize);
//        baseFrame.setBackground(Color.gray);
//        baseFrame.setVisible(true);

        Viewer viewer = new Viewer();

        while (true) {
            Socket socket = null;
            Response response = null;
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            String xml = null;
            try {
                socket = ServerConnection.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                oos = new ObjectOutputStream(outputStream);

                InputStream inputStream = socket.getInputStream();
                ois = new ObjectInputStream(inputStream);

                Request request = new Request(Request.RequestType.REQUEST_CURRENTLY_SHOWING_BILLBOARD);
                oos.writeObject(request);
                oos.flush();

                response = (Response) ois.readObject();
                xml = (String) response.getResponseContent();
            } catch (SocketException e) {
                // display 'cannot connect to server'
                xml = ERROR_BILLBOARD;
            }


            viewer.updatePanel(xml);

            // close all connections
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (NullPointerException ignore) {
                // error billboard will be displayed
            }

            Thread.sleep(FIFTY_SECONDS_IN_MILLIS);
        }
    }

    public void updatePanel(String xml) {

        DisplayPanel newPanel = new DisplayPanel(xml, screenSize);
        this.viewer.getContentPane().removeAll();
        this.viewer.getContentPane().invalidate();

        this.viewer.getContentPane().add(newPanel);
        this.viewer.getContentPane().revalidate();
    }


    // from https://grokonez.com/java/java-advanced/java-8-encode-decode-an-image-base64
    public static String encodeImage(BufferedImage image) {
        String base64Image = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = bos.toByteArray();

        base64Image = Base64.getEncoder().encodeToString(data);

        return base64Image;
    }

    public static BufferedImage decodeImage(String base64Image) {
        // Converting a Base64 String into Image byte array
        byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByteArray);
        BufferedImage image = null;
        try {
            image = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static String ReadTextFile(String filePath) {
        String everything = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();

        } catch (IOException e) {
            System.out.println("IO exception!");
        }
        return everything;
    }

    public static String getERROR_IMAGE() {
        return ERROR_IMAGE;
    }
}

class DisplayPanel extends JPanel {

    private final Dimension screenSize;
    private BillboardXML XML;

    public DisplayPanel(String xml, Dimension screenSize) {
        this.XML = new BillboardXML(xml);
        this.screenSize = screenSize;

        // loads background colour and draw the bg colour
        String backgroundColour = XML.getBackground();
        Color bg = Color.decode(backgroundColour);
        this.setBackground(bg);

        // loads image if exists
        BufferedImage image = null;
        if (XML.isHasPicture()) {
            // loads image from url
            if (XML.getPictureURL() != null) {
                try {
                    image = ImageIO.read(new URL(XML.getPictureURL()));
                } catch (IOException e) {
                    image = Viewer.decodeImage(Viewer.getERROR_IMAGE());
                    //e.printStackTrace();
                }
            } else { // loads base 64 image
                image = Viewer.decodeImage(XML.getPictureData());
            }
        }

        // loads message if exists
        String message = null;
        String messageColour = null;
        if (XML.isHasMessage()) {
            message = XML.getMessageContent();
            messageColour = XML.getMessageColour();
        }

        // loads information
        String information = null;
        String informationColour = null;
        if (XML.isHasInformation()) {
            information = XML.getInformationContent();
            informationColour = XML.getInformationColour();
        }


        // only message is present
        if (XML.isHasMessage() && (!XML.isHasPicture() && !XML.isHasInformation())) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(labelText);
            this.setLayout(new BorderLayout());
            this.add(msg, BorderLayout.CENTER);
        }
        // only picture is present
        else if (XML.isHasPicture() && (!XML.isHasMessage() && !XML.isHasInformation())) {
            //the image should be scaled up to half the width and height of the screen and displayed in the centre.
            // screenSize = 1000 x 750           ==>  500 x 375   1:1.33
            // imageSize =  100  x 100           ==>  375 x 375   1:1
            // imageSize =  100  x 50  (1:2)     ==>  500 x 250
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            Point centerDrawingPoint = getCenterDrawingPoint(screenSize, imageSize);
            //g.drawImage(image, centerDrawingPoint.x, centerDrawingPoint.y, imageSize.width, imageSize.height, null);
        }
        // only information is present
        else if (XML.isHasInformation() && (!XML.isHasMessage() && !XML.isHasPicture())) {
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 1;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(info);
//            System.out.println( "actual width"+msg.getPreferredSize().getWidth());
//            System.out.println("limited width"+limitedWidth);
//            System.out.println();
//            System.out.println("actual height"+ msg.getPreferredSize().getHeight());
//            System.out.println("limited height"+limitedHeight);
//            System.out.println();
        }
        // only message and picture are present
        else if (XML.isHasMessage() && XML.isHasPicture() && !XML.isHasInformation()) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);
            this.setLayout(null);
            JLabel msg = new JLabel(labelText);
            msg.setBounds(0, 50, screenSize.width, 300);
            this.add(msg);

            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // it should be drawn in the middle of the bottom 2/3 of the screen.
            Point drawingPoint = getBottomDrawingPoint(screenSize, imageSize);
            // draw the image
            // g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);


        }
        // only message and information are present
        else if (XML.isHasMessage() && XML.isHasInformation() && !XML.isHasPicture()) {
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);
            this.add(msg);


            int limitedWidth = (int) (0.75 * screenSize.width);

            int infoFontSize = (limitedWidth / information.length()) + 25;
            if (infoFontSize > messageFontSize) {
                infoFontSize = messageFontSize - 5;
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, infoFontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(0, 0, 0, 0));
            bottomPanel.add(info);
            this.add(bottomPanel);

            this.setLayout(new GridLayout(2, 1, 10, 100));


        }
        // only picture and information are present
        else if (XML.isHasPicture() && XML.isHasInformation() && !XML.isHasMessage()) {
            // 5.xml
            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // get top center section location
            Point drawingPoint = getTopDrawingPoint(screenSize, imageSize);
            // draw the image
            //  g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 4;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridLayout(3, 1));
            JPanel row1 = new JPanel();
            JPanel row2 = new JPanel();
            JPanel infoPanel = new JPanel();
            row1.setBackground(new Color(0, 0, 0, 0));
            row2.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.add(info);

            this.add(row1);
            this.add(row2);
            this.add(infoPanel);


//            System.out.println("75% of screen width" + screenSize.width * 0.75);
//            System.out.println("label's width" + info.getPreferredSize().getWidth());


        }
        //  message, picture and information are all present
        else if (XML.isHasMessage() && XML.isHasInformation() && XML.isHasPicture()) {
            // 6.xml
            // 10.xml
            // get new image size which fits to 1/3 of screen
//            System.out.println("all present");
            Dimension imageSize = getImageDimensionFitOneThirdScreen(screenSize,
                    new Dimension(image.getWidth(), image.getHeight()));

            // get center drawing point
            Point drawingPoint = getCenterDrawingPoint(screenSize, imageSize);

            // draw the image
            // g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

//            System.out.println(imageSize);
//            System.out.println(drawingPoint);
//            System.out.println(screenSize);

            // draw message
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length();
            if (fontSize > messageFontSize) {
                fontSize = (int) (messageFontSize * 0.60);
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);


            this.setLayout(new GridLayout(3, 1));
            JPanel messagePanel = new JPanel();
            JPanel imagePanel = new JPanel();
            JPanel infoPanel = new JPanel();
            // make them transparent
            messagePanel.setBackground(new Color(0, 0, 0, 0));
            imagePanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));

            messagePanel.setLayout(new BorderLayout());
            messagePanel.add(msg, BorderLayout.CENTER);

            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            infoPanel.add(info);

            this.add(messagePanel);
            this.add(imagePanel);
            this.add(infoPanel);
        }


    }

    /**
     * Draws the content on the panel
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // handle error
        if (XML.isHasError()) {
            try {
                BufferedImage errorImage = ImageIO.read(new URL(XML.getPictureURL()));
                g.drawImage(errorImage, 0, 0, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // loads image if exists
        BufferedImage image = null;
        if (XML.isHasPicture()) {
            // loads image from url
            if (XML.getPictureURL() != null) {
                try {
                    image = ImageIO.read(new URL(XML.getPictureURL()));
                } catch (IOException e) {
                    image = Viewer.decodeImage(Viewer.getERROR_IMAGE());// display error image
                }
            } else { // loads base 64 image
                image = Viewer.decodeImage(XML.getPictureData());
            }
        }

        // loads message if exists
        String message = null;
        String messageColour = null;
        if (XML.isHasMessage()) {
            message = XML.getMessageContent();
            messageColour = XML.getMessageColour();
        }

        // loads information
        String information = null;
        String informationColour = null;
        if (XML.isHasInformation()) {
            information = XML.getInformationContent();
            informationColour = XML.getInformationColour();
        }


        // only message is present
        if (XML.isHasMessage() && (!XML.isHasPicture() && !XML.isHasInformation())) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(labelText);
            this.setLayout(new BorderLayout());
            this.add(msg, BorderLayout.CENTER);
        }
        // only picture is present
        else if (XML.isHasPicture() && (!XML.isHasMessage() && !XML.isHasInformation())) {
            //the image should be scaled up to half the width and height of the screen and displayed in the centre.
            // screenSize = 1000 x 750           ==>  500 x 375   1:1.33
            // imageSize =  100  x 100           ==>  375 x 375   1:1
            // imageSize =  100  x 50  (1:2)     ==>  500 x 250
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            Point centerDrawingPoint = getCenterDrawingPoint(screenSize, imageSize);
            g.drawImage(image, centerDrawingPoint.x, centerDrawingPoint.y, imageSize.width, imageSize.height, null);
        }
        // only information is present
        else if (XML.isHasInformation() && (!XML.isHasMessage() && !XML.isHasPicture())) {
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 1;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(info);
//            System.out.println( "actual width"+msg.getPreferredSize().getWidth());
//            System.out.println("limited width"+limitedWidth);
//            System.out.println();
//            System.out.println("actual height"+ msg.getPreferredSize().getHeight());
//            System.out.println("limited height"+limitedHeight);
//            System.out.println();
        }
        // only message and picture are present
        else if (XML.isHasMessage() && XML.isHasPicture() && !XML.isHasInformation()) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);
            this.setLayout(null);
            JLabel msg = new JLabel(labelText);
            msg.setBounds(0, 50, screenSize.width, 300);
            this.add(msg);

            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // it should be drawn in the middle of the bottom 2/3 of the screen.
            Point drawingPoint = getBottomDrawingPoint(screenSize, imageSize);
            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);


        }
        // only message and information are present
        else if (XML.isHasMessage() && XML.isHasInformation() && !XML.isHasPicture()) {
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);
            this.add(msg);


            int limitedWidth = (int) (0.75 * screenSize.width);

            int infoFontSize = (limitedWidth / information.length()) + 25;
            if (infoFontSize > messageFontSize) {
                infoFontSize = messageFontSize - 5;
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, infoFontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(0, 0, 0, 0));
            bottomPanel.add(info);
            this.add(bottomPanel);

            this.setLayout(new GridLayout(2, 1, 10, 100));


        }
        // only picture and information are present
        else if (XML.isHasPicture() && XML.isHasInformation() && !XML.isHasMessage()) {
            // 5.xml
            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // get top center section location
            Point drawingPoint = getTopDrawingPoint(screenSize, imageSize);
            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 4;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridLayout(3, 1));
            JPanel row1 = new JPanel();
            JPanel row2 = new JPanel();
            JPanel infoPanel = new JPanel();
            row1.setBackground(new Color(0, 0, 0, 0));
            row2.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.add(info);

            this.add(row1);
            this.add(row2);
            this.add(infoPanel);


//            System.out.println("75% of screen width" + screenSize.width * 0.75);
//            System.out.println("label's width" + info.getPreferredSize().getWidth());


        }
        //  message, picture and information are all present
        else if (XML.isHasMessage() && XML.isHasInformation() && XML.isHasPicture()) {
            // 6.xml
            // 10.xml
            // get new image size which fits to 1/3 of screen
//            System.out.println("all present");
            Dimension imageSize = getImageDimensionFitOneThirdScreen(screenSize,
                    new Dimension(image.getWidth(), image.getHeight()));

            // get center drawing point
            Point drawingPoint = getCenterDrawingPoint(screenSize, imageSize);

            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

//            System.out.println(imageSize);
//            System.out.println(drawingPoint);
//            System.out.println(screenSize);

            // draw message
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length();
            if (fontSize > messageFontSize) {
                fontSize = (int) (messageFontSize * 0.60);
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);


            this.setLayout(new GridLayout(3, 1));
            JPanel messagePanel = new JPanel();
            JPanel imagePanel = new JPanel();
            JPanel infoPanel = new JPanel();
            // make them transparent
            messagePanel.setBackground(new Color(0, 0, 0, 0));
            imagePanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));

            messagePanel.setLayout(new BorderLayout());
            messagePanel.add(msg, BorderLayout.CENTER);

            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            infoPanel.add(info);

            this.add(messagePanel);
            this.add(imagePanel);
            this.add(infoPanel);
        }

    }

    private Dimension getImageDimensionFitHalfScreen(Dimension screenSize, Dimension imageSize) {
        Dimension halfScreenSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
        double imageRatio = (double) imageSize.width / imageSize.height;

        double y = 1;
        double x = imageRatio * y;

        double newImageHeight = halfScreenSize.width / imageRatio;
        if (newImageHeight > halfScreenSize.height) {
            newImageHeight = halfScreenSize.height;
        }
        double newImageWidth = imageRatio * newImageHeight;

        return new Dimension((int) newImageWidth, (int) newImageHeight);
    }

    private Dimension getImageDimensionFitOneThirdScreen(Dimension screenSize, Dimension imageSize) {
        Dimension smallScreenSize = new Dimension(screenSize.width / 3, screenSize.height / 3);
        double imageRatio = (double) imageSize.width / imageSize.height;

        double y = 1;
        double x = imageRatio * y;

        double newImageHeight = smallScreenSize.width / imageRatio;
        if (newImageHeight > smallScreenSize.height) {
            newImageHeight = smallScreenSize.height;
        }
        double newImageWidth = imageRatio * newImageHeight;

        return new Dimension((int) newImageWidth, (int) newImageHeight);
    }

    private Point getCenterDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point center = new Point(screenSize.width / 2, screenSize.height / 2);
        Point leftUpPoint = new Point(center.x - imageSize.width / 2, center.y - imageSize.height / 2);

        return leftUpPoint;
    }

    private Point getBottomDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point bottomCenter = new Point(screenSize.width / 2, (int) ((double) (screenSize.height) * 2 / 3));
        Point leftUpPoint = new Point(bottomCenter.x - imageSize.width / 2, bottomCenter.y - imageSize.height / 2);
        return leftUpPoint;
    }

    private Point getTopDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point topCenter = new Point(screenSize.width / 2, (int) ((double) (screenSize.height) * 1 / 3));
        Point leftUpPoint = new Point(topCenter.x - imageSize.width / 2, topCenter.y - imageSize.height / 2);
        return leftUpPoint;
    }


}